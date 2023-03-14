package com.youtubedownloader.service;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class YoutubeDownloaderService {

    public String extractVideoId(String url) {
        String regex = "v=([^&]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException("Invalid YouTube URL");
        }
    }

    public String getDownloadLink(String videoId) throws IOException {
        String apiUrl = "https://www.youtube.com/get_video_info?video_id=" + videoId;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(apiUrl);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        Map<String, String> queryParams = parseQueryParams(responseBody);
        String downloadUrl = queryParams.get("url_encoded_fmt_stream_map");
        downloadUrl = URLDecoder.decode(downloadUrl, StandardCharsets.UTF_8);
        String[] downloadUrls = downloadUrl.split(",");
        for (String url : downloadUrls) {
            if (url.contains("itag=22")) {
                String[] urlParams = url.split("&");
                for (String param : urlParams) {
                    if (param.startsWith("url=")) {
                        return param.substring(4);
                    }
                }
            }
        }
        throw new RuntimeException("Unable to find download link");
    }

    private Map<String, String> parseQueryParams(String queryParams) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = queryParams.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                map.put(key, value);
            }
        }
        return map;
    }
}
