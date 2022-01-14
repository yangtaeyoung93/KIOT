package com.airguard.model.dong;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @FileName : DongDetail.java
 * @Project : KIOT_ADMIN
 * @Date : 2021. 3. 18.
 * @Auth : Yoo, HS
 */
@Getter
@Setter
@ToString
public class DongDetail {

  private String regDate;

  private String pm10;
  private String pm25;

  private String co;
  private String o3;
  private String no2;
  private String so2;
  private String khai;

  private String khai_grade;
  private String so2_grade;
  private String co_grade;
  private String o3_grade;
  private String no2_grade;
  private String pm10_grade;
  private String pm25_grade;
}
