package com.airguard.model.platform;

import com.airguard.model.dong.SensorAirKorDto;
import lombok.Getter;
import lombok.Setter;

/**
 * @FileName : AirMapKorDto.java
 * @Project : KIOT
 * @Date : 2021. 3. 12.
 * @Auth : Yoo, HS
 */
@Getter
@Setter
public class AirMapKorDto {

  private TimeStampDto service;
  private SensorAirKorDto data;

}
