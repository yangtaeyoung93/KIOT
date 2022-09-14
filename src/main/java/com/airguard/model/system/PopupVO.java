package com.airguard.model.system;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PopupVO {
    private String articleIdx;
    private String title;
    private String content;
    private String endDt;
}
