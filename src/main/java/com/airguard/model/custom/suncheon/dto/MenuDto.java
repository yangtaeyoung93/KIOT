package com.airguard.model.custom.suncheon.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuDto {

  int idx;
  String name;
  String link;
  int degree;
  int parent;
  int userRight;

}
