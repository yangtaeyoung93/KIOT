package com.airguard.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Component
public class WeatherApiUtil {

  private static final Logger logger = LoggerFactory.getLogger(WeatherApiUtil.class);

  private static final String WEATHER_API_DOMAIN = "http://kapi.kweather.co.kr";
  private static final String FCAST = "/getXML_air_fcast_3times_area.php";
  private static final String TODAY = "/getXML_today.php";

  public static Map<String, Object> weatherAirCastApi(String region, String[] parsers)
      throws Exception {
    Map<String, Object> result = new LinkedHashMap<String, Object>();

    String weatherApiParam = "?mode=n&region=" + region;
    URI url = URI.create(
        new StringBuilder(WEATHER_API_DOMAIN).append(FCAST).append(weatherApiParam).toString());

    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Content-Type",
        MediaType.APPLICATION_FORM_URLENCODED_VALUE.concat(";charset=UTF-8"));
    RequestEntity<String> req = new RequestEntity<>(headers, HttpMethod.GET, url);
    ResponseEntity<String> res = new RestTemplate().exchange(req, String.class);
    JSONObject resDataObj = new JSONObject(res.getBody().toString());

    JSONObject mainData = resDataObj.getJSONObject("main");
    for (int i = 0; i < parsers.length; i++)
      result.put(new StringBuilder("P_").append(String.valueOf(i + 1)).toString(),
         mainData.get(parsers[i]).toString());

    return result;
  }

  public static Map<String, Object> weatherTodayApi(String region)
      throws Exception {

    Map<String, Object> result = new LinkedHashMap<>();

    String weatherApiParam = "?mode=n&region=" + region;
    String url = new StringBuilder(WEATHER_API_DOMAIN).append(TODAY).append(weatherApiParam).toString();
    HttpPost post = new HttpPost(url);

    String resData;
    try (CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(post)) {
      resData = EntityUtils.toString(response.getEntity());

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(new InputSource(new StringReader(resData)));
      NodeList nList = doc.getChildNodes();
        Node nNode = nList.item(0);
        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
          Element eElement = (Element) nNode;
          result.put(new StringBuilder("P_1").toString(), getTagValue("dong_ko", eElement));
          result.put(new StringBuilder("P_2").toString(), getTagValue("icon", eElement));
          result.put(new StringBuilder("P_3").toString(), getTagValue("wd_ws", eElement));
          result.put(new StringBuilder("P_4").toString(), getTagValue("temp", eElement));
          result.put(new StringBuilder("P_5").toString(), getTagValue("humi", eElement));
          result.put(new StringBuilder("P_6").toString(), getTagValue("snowf", eElement));
          result.put(new StringBuilder("P_7").toString(), getTagValue("rainf", eElement));
          result.put(new StringBuilder("P_8").toString(), getTagValue("date", eElement));

        }
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
}
