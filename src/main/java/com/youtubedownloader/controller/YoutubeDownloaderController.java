package com.youtubedownloader.controller;

import com.youtubedownloader.dto.YoutubeDownloadRequest;
import com.youtubedownloader.dto.YoutubeDownloadResponse;
import com.youtubedownloader.service.YoutubeDownloaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class YoutubeDownloaderController {

    @Autowired
    private YoutubeDownloaderService youtubeDownloaderService;

    @PostMapping("/download")
    public ResponseEntity<?> downloadVideo(@RequestBody YoutubeDownloadRequest request) throws IOException {
        String videoId = youtubeDownloaderService.extractVideoId(request.getUrl());
        String downloadLink = youtubeDownloaderService.getDownloadLink(videoId);
        return ResponseEntity.ok(new YoutubeDownloadResponse(downloadLink));
    }
}
