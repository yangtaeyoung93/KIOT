package com.airguard.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import com.airguard.mapper.readonly.ReadOnlyMapper;
import com.airguard.model.system.FileVo;

@Component
public class FileProcessUtil {

  @Value("${file-folder.path}")
  private String FILE_PATH;

  @Autowired
  private ReadOnlyMapper readMapper;

  private static final Logger logger = LoggerFactory.getLogger(FileProcessUtil.class);

  public static boolean fileSave(MultipartFile file, String filePath, String fileName)
      throws Exception {
    boolean result = false;

    File folder = new File(filePath);
    if (!folder.exists()) {
      try {
        folder.mkdirs();
      } catch (Exception e) {
        return result;
      }
    }

    File dest = new File(fileName);
    file.transferTo(dest);
    // Runtime.getRuntime().exec("chmod -R 644 " + dest);

    result = true;

    return result;
  }

  public static boolean fileDelete(String filePathName) {
    return new File(filePathName).delete();
  }

  public String encFileName(String fileName) {
    int asciCode = 0;
    for (char c : fileName.toCharArray())
      asciCode += (byte) c;
    return String.format("%02X", (asciCode % 256));
  }

  public List<FileVo> findAllFilesInFolder() throws Exception {
    List<FileVo> resultFileInfoList = new ArrayList<FileVo>();
    FileVo fileInfo;

    String[] directionArray = {"Main", "East", "West", "South", "North"};
    File deviceFolers = new File(FILE_PATH.concat("TEMP/")); // new File("D:\\air_temp\\south\\"); new File(FILE_PATH.concat("TEMP/"));

    for (File deviceFolder : deviceFolers.listFiles()) {
      String serial = deviceFolder.getName();
      String deviceIdx = readMapper.getSerialToDeviceIdx(serial);
      String memberIdx = readMapper.getDeviceIdxToUserIdx(deviceIdx);
      if (deviceIdx == null) {
        logger.error("=== SERIAL :: {} ===", serial);
        continue;

      } else if (memberIdx == null) {
        logger.error("=== SERIAL :: {}, MEMBER_IDX :: {} ===", serial, memberIdx);
        continue;
      }

      imageLoop : for (File imageFile : deviceFolder.listFiles()) {
        String clientFileName = imageFile.getName();
        int idx = (clientFileName.indexOf('(') == -1) ? 0 : (clientFileName.indexOf('(') + 1);
        String fileDirectionStr = clientFileName.substring(idx, idx + 1);

        String fileExt = clientFileName.substring(clientFileName.lastIndexOf(".") + 1);
        if (!fileExt.equals("jpg")) 
          continue imageLoop;

        String fileType = directionArray[Integer.parseInt(fileDirectionStr) - 1];
        String serverFilePath = FILE_PATH.concat("AIR_MAP_IMAGE").concat("/").concat(memberIdx).concat("/").concat(fileType).concat("/");
        String serverFileName = deviceIdx.concat("_").concat(encFileName(fileType.concat(clientFileName)));

        FileItem fileItem = new DiskFileItem(
            "mainFile", Files.probeContentType(imageFile.toPath()), false, clientFileName, 
            (int) imageFile.length(), imageFile.getParentFile());
        InputStream input = new FileInputStream(imageFile);
        OutputStream os = fileItem.getOutputStream(); IOUtils.copy(input, os);
        IOUtils.copy(new FileInputStream(imageFile), fileItem.getOutputStream());

        MultipartFile mFile = new CommonsMultipartFile(fileItem);

        if (fileSave(mFile, serverFilePath, serverFilePath.concat(serverFileName).concat(".").concat(fileExt))) {
          fileInfo = new FileVo();
          fileInfo.setDeviceIdx(deviceIdx);
          fileInfo.setClientFileName(clientFileName);
          fileInfo.setServerFileName(serverFileName);
          fileInfo.setServerFilePath(serverFilePath);
          fileInfo.setFileExt(fileExt);
          fileInfo.setFileType(fileType);

          resultFileInfoList.add(fileInfo);
        }

      }
    }

    return resultFileInfoList;
  }
}
