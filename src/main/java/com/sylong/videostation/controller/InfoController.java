package com.sylong.videostation.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/info")
public class InfoController {
    @Value("${videostation.meta.eng-name:}")
    private String engName;
    @Value("${videostation.meta.chn-name:}")
    private String chnName;
    @Value("${videostation.meta.version:}")
    private String version;

    @GetMapping("/map")
    public Map<String, String> appInfo() {
        return Map.of(
            "engName", engName,
            "chnName", chnName,
            "version", version
        );
    }
}
