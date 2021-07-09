package com.iris.irisissue.controller;

import org.kohsuke.github.*;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.text.AbstractDocument;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GitApiController {
    private static String GITHUB_API_BASE_URL ="https://api.github.com/";
    private static String GITHUB_SEARCH_ISSUES_QUERY ="search/issues?q=repo%3Amobigen/IRIS";


    public static String GITHUB_API_HOST ="https://api.github.com";
    public static String GITHUB_API_REPOSITORY ="/repos/mobigen/IRIS";


    @GetMapping("/iris/labels")
    public String getIssuesCount(String token, String repoName) throws IOException {        //Header
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization","token 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");
//        headers.add("Content-type", "aapplication/json; charset=utf-8");

        //Labels 정보 조회
//        URL labelsUrl = new URL(GITHUB_API_HOST + GITHUB_API_REPOSITORY + "/labels");
//
//        HttpsURLConnection connection = (HttpsURLConnection) labelsUrl.openConnection();
//        connection.setRequestMethod("GET");
//        connection.setRequestProperty("Authorization", "token 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");
//        connection.setDoOutput(true);
//
//        StringBuilder response = new StringBuilder ();
//        BufferedReader in = new BufferedReader(
//                new InputStreamReader(connection.getInputStream()));
//
//        String line;
//
//        while ((line = in.readLine()) != null) {
//            response.append(line);
//        }
//        in.close();
//
//        return response.toString();
        return "안녕";
    }
}
