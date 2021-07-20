package com.iris.irisissue.controller;

import com.iris.irisissue.IrisIssuesService;
import com.iris.irisissue.vo.Issue;
import com.iris.irisissue.vo.IssueCount;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class IrisIssuesController {

    @Autowired
    private IrisIssuesService issuesService;

    //라벨 조회
    @GetMapping("/iris-issues/issues/label")
    @ResponseBody
    public Object label() {
        return issuesService.labelList();
    }


    //오늘 생성, CLOSE된 ISSUE
    @GetMapping("/iris-issues/count")
    @ResponseBody
    public Object IsseuCounting() {
        return issuesService.IsseuCounting();
    }


    //Issue pagination
    @GetMapping("/iris-issues/issues")
    @ResponseBody
    public Object IsseuList(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "filter", defaultValue = "all", required = false) String filter,
            @RequestParam(value = "label", required = false) String label,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate
    ) {
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        map.put("filter", filter);
        map.put("label", label);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        return issuesService.IsseuList(map);
    }


    //등록자별 이슈 조회
    @GetMapping("/iris-issues/chart/author")
    @ResponseBody
    public Object UserIssueCounting() {
        return issuesService.UserChartIssueCounting();
    }


    //담당자별 이슈 조회
    @GetMapping("/iris-issues/chart/assignee")
    @ResponseBody
    public Object AssineeIssueCounting() {
        return issuesService.AssineeIssueCounting();
    }


    //날짜별 이슈 조회
    @GetMapping("/iris-issues/chart/open-date")
    @ResponseBody
    public Object DateIssueCounting() {
        return issuesService.DateIssueCounting();
    }

}
