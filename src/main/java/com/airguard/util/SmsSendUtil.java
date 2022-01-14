package com.airguard.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Component;
import com.airguard.exception.SQLException;

@Component
public class SmsSendUtil {

  public static int mailSend(String receivePhone, String message) throws SQLException {
    int resultCode;

    HttpPost post = new HttpPost(
        CommonConstant.SMS_SERVER_URL.concat(CommonConstant.SMS_SERVER_API_NAME));
    List<NameValuePair> urlParameters = new ArrayList<>();
    urlParameters.add(new BasicNameValuePair("key", "air365_service"));
    urlParameters.add(new BasicNameValuePair("sendPhone", "023602200"));
    urlParameters.add(new BasicNameValuePair("receiveName", "air365_customer"));
    urlParameters.add(new BasicNameValuePair("receivePhone", receivePhone));
    urlParameters.add(new BasicNameValuePair("message", message));

    try {

      CloseableHttpClient httpClient = HttpClients.createDefault();
      post.setEntity(new UrlEncodedFormEntity(urlParameters));
      httpClient.execute(post);

      resultCode = CommonConstant.R_SUCC_CODE;
      httpClient.close();

    } catch (Exception e) {
      throw new SQLException(SQLException.SQL_EXCEPTION);
    }

    return resultCode;
  }
}
