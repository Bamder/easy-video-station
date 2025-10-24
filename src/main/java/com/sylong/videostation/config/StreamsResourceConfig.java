package com.sylong.videostation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.sylong.videostation.util.ResourceUtils;

@Configuration
public class StreamsResourceConfig implements WebMvcConfigurer {
  @Value("${videostation.streams.dir:${streams.dir:}}")
  private String streamsDir;

  // When the front end requests for video resources
  // 重定向至配置文件中设置的streams.dir目录下的资源
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String location = ResourceUtils.getResourceLocation(streamsDir);
    registry.addResourceHandler("/streams/**") // 前端请求向/streams/**请求静态资源
            .addResourceLocations(location) // 重定向至streams.dir下对应资源
            .setCacheControl(CacheControl.noCache())
            .resourceChain(true);
  }
}