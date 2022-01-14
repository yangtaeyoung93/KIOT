package com.airguard.model.platform;

import com.fasterxml.jackson.annotation.JsonProperty;


public class IaoData {
  @JsonProperty("device")
  String device;

  @JsonProperty("timestamp")
  long timestamp;

  @JsonProperty("data")
  // InspectData inspectData;
  AutoData autoData;



}
