package com.airguard.model.datacenter;

import lombok.Data;

/**
 * 서울시 모든 디바이스(국가관측망 포함) 메타데이터
 */

@Data
public class SeoulMetaData {
    private String serial;
    private String type;
    private Double lat;
    private Double lon;
}
