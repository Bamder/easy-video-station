package com.sylong.videostation.config;

import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MimeTypeConfig implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);

        // 前端请求HLS/DASH相关静态文件时，为后端返回的请求头设置最合适的MIME类型信息
        mappings.add("m3u8", "application/vnd.apple.mpegurl");
        mappings.add("ts", "video/mp2t");
        mappings.add("m4s", "video/mp4");
        mappings.add("mpd", "application/dash+xml");
        factory.setMimeMappings(mappings);
    }
}