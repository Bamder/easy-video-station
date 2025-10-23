package com.sylong.videostation.controller;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import com.sylong.videostation.util.ResourceUtils;

@RestController
@RequestMapping("/api/streams")
public class StreamController {

    @Value("${streams.dir:}")
    private String streamsDir;

    // 按视频名查询播放清单：存在则重定向到 /streams/<name>/playlist.m3u8，否则返回 404
    @GetMapping("/{name}")
    public ResponseEntity<Void> getPlaylist(@PathVariable String name) {
        // For the back end to check if the playlist file exists
        Path playlist = ResourceUtils.getResourcePath(streamsDir.trim(), new String[] { name, "playlist.m3u8" });

        if (Files.exists(playlist)) {
            // For the front end to request the playlist file with http
            String encodedName = UriUtils.encodePathSegment(name, StandardCharsets.UTF_8);
            URI location = URI.create("/streams/" + encodedName + "/playlist.m3u8");

            //返回重定向响应，指向/streams/<name>/playlist.m3u8
            return ResponseEntity.status(HttpStatus.FOUND).location(location).build();
        }
        return ResponseEntity.notFound().build();
    }
}