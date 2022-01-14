package com.airguard.model.app;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionModel {

  private long result;

  private long error_code;

  private String error_message;
}
