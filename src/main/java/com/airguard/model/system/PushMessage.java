package com.airguard.model.system;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PushMessage {

  int rowNum;

  int idx;

  String element;
  String preStep;
  String curStep;
  String message;
  String deviceType;
}
