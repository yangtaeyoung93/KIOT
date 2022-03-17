package com.airguard.service.system;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import com.airguard.mapper.main.system.MemberMapper;
import com.airguard.model.common.Search;
import com.airguard.model.system.Member;
import com.airguard.mapper.readonly.ReadOnlyMapper;
import com.airguard.util.Sha256EncryptUtil;

@Service
public class MemberService {

  private static final Logger logger = LoggerFactory.getLogger(MemberService.class);

  @Autowired
  MemberMapper mapper;

  @Autowired
  ReadOnlyMapper readOnlyMapper;

  public Member findMemberByLoginId(String loginId) {
    return readOnlyMapper.findMemberByLoginId(loginId);
  }

  public Long findMemberIdx(String memberId) {
    return readOnlyMapper.findMemberIdx(memberId);
  }

  public int loginCheckMemberId(Member member) {
    return readOnlyMapper.loginCheckMemberId(member);
  }

  public void memberLoginInfoUpdate(Member member) {
    mapper.memberLoginInfoUpdate(member);
  }

  public List<Member> selectMemberList(Search search) {
    return readOnlyMapper.selectMemberList(search);
  }

  public Member selectMemberOne(String idx) {
    return readOnlyMapper.selectMemberOne(idx);
  }

  public List<Member> selectMemberAppDevice(String idx) {
    return readOnlyMapper.selectMemberAppDevice(idx);
  }

  public void restLoginCount(Member reqBody){
   mapper.restLoginCount(reqBody);
  }

  public void insertMember(Member member) {
    String encPwd = Sha256EncryptUtil.ShaEncoder(member.getUserPw());
    member.setUserPw(encPwd);
    mapper.insertMember(member);
  }

  public void updateMember(Member member) {
    if (member.getUserPw().equals("") || member.getUserPw() == null) {
      member.setPwCheck("0");
    } else {
      member.setPwCheck("1");
    }
    String encPwd = Sha256EncryptUtil.ShaEncoder(member.getUserPw());
    member.setUserPw(encPwd);
    mapper.updateMember(member);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public int deleteMember(String idx) {

    if (readOnlyMapper.memberDeviceCheck(idx, null) != 0) {
      return 2;

    } else if (readOnlyMapper.deleteMemberGroupCheck(idx) != 0) {
      return 3;

    } else {
      mapper.deleteMember(idx);
      mapper.deleteAppDeviceInfo(idx);
      return 1;
    }
  }

  public void updateMemberPassword(String memberIdx, String password) {
    mapper.updateMemberPassword(memberIdx, Sha256EncryptUtil.ShaEncoder(password));
  }

  public int checkUserId(String userId) {
    int resultCode = 0;
    int result = readOnlyMapper.checkUserId(userId);

    if (result == 1) {
      resultCode = 1;
    }

    return resultCode;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public String xlsxMemberExcelReader(MultipartHttpServletRequest req) {
    List<Member> memberList = new ArrayList<>();
    String result = "SUCCESS";
    MultipartFile file = req.getFile("excel");
    XSSFWorkbook workbook;

    try {

      assert file != null;
      workbook = new XSSFWorkbook(file.getInputStream());

      XSSFSheet curSheet;
      XSSFRow curRow;
      XSSFCell curCell;
      Member vo;

      curSheet = workbook.getSheetAt(0);

      String lat = "";
      String lon = "";

      for (int rowIndex = 1; rowIndex <= curSheet.getLastRowNum(); rowIndex++) {
        curRow = curSheet.getRow(rowIndex);
        vo = new Member();
        String value;

        if (curRow.getCell(0) != null) {
          if (!"".equals(curRow.getCell(0).getStringCellValue())) {
            for (int cellIndex = 0; cellIndex < curSheet.getRow(0).getLastCellNum(); cellIndex++) {

              curCell = curRow.getCell(cellIndex);
              switch (curCell.getCellType()) {
                case XSSFCell.CELL_TYPE_FORMULA:
                  value = curCell.getCellFormula();
                  break;
                case XSSFCell.CELL_TYPE_NUMERIC:
                  return "TYPE_CHECK";
                case XSSFCell.CELL_TYPE_STRING:
                  value = curCell.getStringCellValue() + "";
                  break;
                case XSSFCell.CELL_TYPE_BLANK:
                  value = curCell.getBooleanCellValue() + "";
                  break;
                case XSSFCell.CELL_TYPE_ERROR:
                  value = curCell.getErrorCellValue() + "";
                  break;
                default:
                  value = "";
                  break;
              }

              switch (cellIndex) {
                case 0:
                  if (value.length() > 51) {
                    return "ID_SIZE_CHECK";
                  }
                  if (!"".equals(value.trim())) {
                    if (checkUserId(value) != 1) {
                      return value;
                    }
                  }
                  vo.setUserId(value);
                  break;
                case 1:
                  vo.setUserPw(Sha256EncryptUtil.ShaEncoder(value));
                  break;
                case 2:
                  lat = value;
                  // 위도 lat
                  break;
                case 3:
                  lon = value;
                  // 경도 lon
                  break;
                default:
                  break;
              }

              List<Map<String, Object>> regionInfos = getRegionInfo(lon, lat);

              vo.setRegion(regionInfos.get(0).get("region_id").toString());
              vo.setRegionName(regionInfos.get(0).get("address").toString());
              vo.setStationShared("Y");
            }

            memberList.add(vo);
          }
        }
      }


    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return "FAIL";
    }

    for (Member data : memberList) {
      mapper.insertMember(data);
    }

    return result;
  }

  private static String getTagValue(String tag, Element eElement) {
    NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
    Node nValue = nlList.item(0);
    if (nValue == null) {
      return null;
    }
    return nValue.getNodeValue();
  }

  public List<Map<String, Object>> getRegionInfo(String lon, String lat) throws Exception {
    List<Map<String, Object>> datas = new ArrayList<>();

    Map<String, Object> data;
    HttpPost post = new HttpPost("https://was.kweather.co.kr/kapi/airguardk/getXML_lonlat_new.php");
    String resData;
    List<NameValuePair> urlParameters = new ArrayList<>();
    urlParameters.add(new BasicNameValuePair("lon", lon));
    urlParameters.add(new BasicNameValuePair("lat", lat));

    post.setEntity(new UrlEncodedFormEntity(urlParameters));

    try (CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(post)) {
      resData = EntityUtils.toString(response.getEntity());
      logger.info("resData : " + resData);
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(new InputSource(new StringReader(resData)));

      NodeList nList = doc.getElementsByTagName("srch_pt");

      for (int temp = 0; temp < nList.getLength(); temp++) {
        data = new LinkedHashMap<>();
        Node nNode = nList.item(temp);
        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
          Element eElement = (Element) nNode;
          data.put("region_id", getTagValue("region_id", eElement));
          data.put("address",
              getTagValue("city_ko", eElement) + " " + getTagValue("dong_ko", eElement));
        }

        datas.add(data);
      }
    }

    return datas;
  }
}
