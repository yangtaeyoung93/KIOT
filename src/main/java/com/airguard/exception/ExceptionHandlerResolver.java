package com.airguard.exception;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @FileName : ExceptionHandlerResolver.java
 * @Project : KIOT
 * @Date : 2021. 4. 13.
 * @Auth : Yoo, HS
 */
@Slf4j
@Configuration
public class ExceptionHandlerResolver extends DefaultHandlerExceptionResolver {

  @Autowired
  private ErrorMessageHandler errorMessageHandler;

  @Override
  protected ModelAndView doResolveException(@NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response, Object handler, @NotNull Exception ex) {

    try {
      if (ex instanceof CommonException) {
        return this.handleException((CommonException) ex, request, response);
      }
      return this.handleException(ex);
    } catch (Exception handlerException) {
      log.error("handling of [ {} ] resulted in Exception", ex.getClass().getName());
    }
    return super.doResolveException(request, response, handler, ex);
  }

  private ModelAndView handleException(CommonException ex, HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    int errorCode = ex.getErrorCode();

    String message = this.errorMessageHandler.getMessage(ex);
    switch (errorCode) {

      // 401
      case HttpURLConnection.HTTP_UNAUTHORIZED:
        break;

      // 405
      case HttpURLConnection.HTTP_BAD_METHOD:
        break;

      // 500
      case HttpURLConnection.HTTP_INTERNAL_ERROR:
        break;

    }
    response.setStatus(200);
    log.error("call uri exception - url:{} , error_message:{}", request.getRequestURI(), message);

    JSONObject jsonData = new JSONObject();
    jsonData.put("message", message);
    jsonData.put("result", 0);
    jsonData.put("errorCode", errorCode);
    View view = new AbstractView() {
      @Override
      protected void renderMergedOutputModel(@NotNull Map model,
          @NotNull HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json; charset=UTF-8");
        FileCopyUtils.copy(jsonData.toString(),
            new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8));
      }
    };
    return new ModelAndView(view);

  }

  private ModelAndView handleException(Exception ex) {
    log.error("Controller Error({})", ex.getMessage());
    return new ModelAndView("errors/common");
  }
}
