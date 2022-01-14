package com.airguard.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KweatherElemeniUtil {

  private static final Logger logger = LoggerFactory.getLogger(KweatherElemeniUtil.class);

  private static int[] CI_RANGE = {100, 90, 80, 50, 0};

  private static int[] RAW_PM10_SECTION = {0, 30, 80, 150, 600};
  private static int[] RAW_PM25_SECTION = {0, 15, 35, 75, 500};
  private static int[] RAW_CO2_SECTION = {0, 500, 1000, 1500, 10000};
  private static int[] RAW_VOC_SECTION = {0, 200, 400, 1000, 10000};
  private static int[] RAW_NOISE_SECTION = {0, 30, 55, 70, 100};

  public static int elementCiCalculator(int val, String element) {
    int[] elSection;
    double ciIndexLow, ciIndexHigh, valLow, valHigh;

    switch (element.toUpperCase()) {
      case "PM10":
        elSection = RAW_PM10_SECTION;
        break;
      case "PM25":
        elSection = RAW_PM25_SECTION;
        break;
      case "CO2":
        elSection = RAW_CO2_SECTION;
        break;
      case "VOC":
        elSection = RAW_VOC_SECTION;
        break;
      case "NOISE":
        elSection = RAW_NOISE_SECTION;
        break;
      default:
        return -999;
    }

    if (elSection[0] <= val && val < elSection[1]) {
      ciIndexLow = CI_RANGE[1];
      ciIndexHigh = CI_RANGE[0];
      valLow = elSection[0];
      valHigh = elSection[1];

    } else if (elSection[1] <= val && val < elSection[2]) {
      ciIndexLow = CI_RANGE[2];
      ciIndexHigh = CI_RANGE[1] - 1;
      valLow = elSection[1] + 1;
      valHigh = elSection[2];

    } else if (elSection[2] <= val && val < elSection[3]) {
      ciIndexLow = CI_RANGE[3];
      ciIndexHigh = CI_RANGE[2] - 1;
      valLow = elSection[2] + 1;
      valHigh = elSection[3];

    } else if (elSection[3] <= val && val <= elSection[4]) {
      ciIndexLow = CI_RANGE[4];
      ciIndexHigh = CI_RANGE[3] - 1;
      valLow = elSection[3] + 1;
      valHigh = elSection[4];

    } else {
      return -999;
    }

    return (int) Math.round(ciIndexHigh - ((ciIndexHigh - ciIndexLow) * ((val - valLow) / (valHigh - valLow))));
  }
}
