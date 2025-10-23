package com.sylong.videostation.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

public class ResourceUtils {

    @Value("${env.FFMPEG}")
    private static String FFMPEG_HOME;

    // location 为URI格式（Start with "file:///"）
    // 暴露给Spring以查找静态文件
    public static String getResourceLocation(String streamsDir) {
        Path base = Paths.get(streamsDir).toAbsolutePath().normalize();
        String location = base.toUri().toString(); //toUri() add "file:///" prefix
        if (!location.endsWith("/")) 
            location += "/"; 
        return location;
    }

    // path 为OS路径格式，指向直接的OS文件或目录
    public static Path getResourcePath(String streamsDir, String[] pathSegments) {
        Path path = Paths.get(streamsDir).toAbsolutePath().normalize();
        for(String seg : pathSegments){
            path = path.resolve(seg);
        }
        return path.toAbsolutePath().normalize();
    }

    public static void generateHlsTs(String sourceFile, String targetDir, int segmentSeconds)
            throws IOException, InterruptedException {
        Path source = Paths.get(sourceFile).toAbsolutePath().normalize();
        Path target = Paths.get(targetDir).toAbsolutePath().normalize();
        Files.createDirectories(target);

        String ffmpegPath = System.getenv(FFMPEG_HOME);
        if (ffmpegPath == null || ffmpegPath.isBlank()) {
            ffmpegPath = "ffmpeg";
        }

        // seg分段时间默认为4秒
        int seg = segmentSeconds > 0 ? segmentSeconds : 4;
        List<String> cmd = new ArrayList<>();
        cmd.add(ffmpegPath);
        cmd.add("-hide_banner");
        cmd.add("-y");
        cmd.add("-i");
        cmd.add(source.toString());
        cmd.add("-codec");
        cmd.add("copy");
        cmd.add("-start_number");
        cmd.add("0");
        cmd.add("-hls_time");
        cmd.add(String.valueOf(seg));
        cmd.add("-hls_playlist_type");
        cmd.add("vod");
        cmd.add("-hls_segment_filename");
        cmd.add(target.resolve("seg_%03d.ts").toString());
        cmd.add(target.resolve("playlist.m3u8").toString());


        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);

        Process process = pb.start();
        StringBuilder commandOutput = new StringBuilder();

        // 新建缓冲区outputReader，用于读取ffmpeg进程的标准输出流
        try (BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String outputLine;
            while ((outputLine = outputReader.readLine()) != null) {
                commandOutput.append(outputLine).append('\n');
            }
        }
        // 阻塞当前线程，直到process对应执行ffmpeg进程结束
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("ffmpeg failed (" + exitCode + "):\n" + commandOutput);
        }
    }
}
