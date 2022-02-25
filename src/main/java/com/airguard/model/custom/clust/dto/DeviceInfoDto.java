package com.airguard.model.custom.clust.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceInfoDto {
    String deviceIdx;
    String serial;
    String stationName;
    String lat;
    String lon;
    String dcode;
    String dfname;
    Object sensor;
}
