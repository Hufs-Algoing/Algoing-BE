package com.hufs.algoing.aisolved.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AISolvedDTO {

    private Long readLevel;
    private Long optLevel;
    private Long dupLevel;
    private String readTip;
    private String optTip;
    private String dupTip;
    private String pattern;
}
