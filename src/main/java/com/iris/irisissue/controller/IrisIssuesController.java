package com.iris.irisissue.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iris.irisissue.vo.Issue;
import com.iris.irisissue.vo.IssueCount;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kohsuke.github.*;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import sun.net.www.http.HttpClient;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@Slf4j
public class IrisIssuesController {
    public static String token ="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";

    public static final String GITHUB_API_URL ="https://api.github.com";
    public static final String HEADER ="token "+token;

    public static final int MAX_PER_PAGE = 100;

    String testDate ="2021-07-08";

    //라벨 조회
    @GetMapping("/iris-issues/issues/label")
    @ResponseBody
    public List<String> label(){
        List<String> labelList = new ArrayList<>();
        try {
            //1. label data
            String labelUrl ="/repos/mobigen/IRIS/labels?per_page=100";

            String labelResult = apiConnection(labelUrl);
            JSONParser jsonParser = new JSONParser();
            JSONArray labelJsonArray = (JSONArray)jsonParser.parse(labelResult);


            for (int i =0; i< labelJsonArray.size();i++){
                JSONObject jsonObject= (JSONObject) labelJsonArray.get(i);
                String name = (String) jsonObject.get("name");
                labelList.add(name);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return labelList;
    }



    //오늘 생성, CLOSE된 ISSUE
    @GetMapping("/iris-issues/count")
    @ResponseBody
    public List<IssueCount> IsseuCounting() {
        List<IssueCount> issueCountList = new ArrayList<>();
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date now = new Date();
            String nowDate = format.format(now);

            //1. 오늘 open된  issue
            String openUrl ="/search/issues?q=repo%3Amobigen/IRIS+created%3A"+nowDate;
            int open_total_count = TotalCount(openUrl);
            IssueCount openCount = new IssueCount("open",open_total_count);


            //1. 오늘 close된  issue
            String  closeUrl="/search/issues?q=repo%3Amobigen/IRIS+closed%3A"+nowDate;
            int close_total_count = TotalCount(closeUrl);
            IssueCount closedCount = new IssueCount("closed",close_total_count);


            issueCountList.add(openCount);
            issueCountList.add(closedCount);

        }catch (Exception e){
            e.printStackTrace();
        }
        return issueCountList;
    }

    @GetMapping("/iris-issues/issues")
    public List<Issue> IsseuList(
            @RequestParam(value = "page", required = false) int page
    ){
        List<Issue> issueList = new ArrayList<>();
        try {
            String totalUrl="/search/issues?q=repo%3Amobigen/IRIS";
            int issue_total_count = TotalCount(totalUrl);
            System.out.println("issue_total_count= "+issue_total_count);

        }catch (Exception e){
            e.printStackTrace();
        }

        return issueList;
    }



    //List에 items로 온 이슈 저장
    public static List<Issue> AllIssueList(String requestUrl) throws ParseException {
        List<Issue> issueList = new ArrayList<>();
        String result = apiConnection(requestUrl);  //api 연결

        JSONParser jsonParse = new JSONParser();
        JSONObject jsonObj = (JSONObject) jsonParse.parse(result);
        JSONArray itemsArray =(JSONArray) jsonObj.get("items");

        for (int i=0;i<itemsArray.size();i++){
            Issue issue = new Issue();
            JSONObject itemsObj = (JSONObject) itemsArray.get(i);

            //Label
            JSONArray labelsArray =(JSONArray) itemsObj.get("labels");
            List<String> labelList = new ArrayList<>();
            for (int j =0 ; j<labelsArray.size();j++){
                JSONObject labelObj = (JSONObject) labelsArray.get(j);
                String labelName= (String) labelObj.get("name");
                labelList.add(labelName);
            }

            //담당자
            JSONArray assigneesArray =(JSONArray) itemsObj.get("assignees");
            List<String> assigneeList = new ArrayList<>();
            for (int j =0 ; j<assigneesArray.size();j++){
                JSONObject assigneeObj = (JSONObject) assigneesArray.get(j);
                String assignee= (String) assigneeObj.get("login");
                assigneeList.add(assignee);
            }

            JSONObject userObj =(JSONObject) itemsObj.get("user");
            Object user = (Object) userObj.get("login");

            Object number = (Object) itemsObj.get("number");
            Object title = (Object) itemsObj.get("title");
            Object state = (Object) itemsObj.get("state");
            Object createdAt = (Object) itemsObj.get("created_at");
            Object closedAt = (Object) itemsObj.get("closed_at");

            issue.setNo(number.toString());
            issue.setUser(user.toString());
            issue.setTitle( title.toString());
            issue.setLabel(labelList);
            issue.setAssign(assigneeList);
            issue.setState( state.toString());
            issue.setCreated(createdAt.toString());
            issue.setCloased(closedAt == null ? null : closedAt.toString());

            issueList.add(issue);
        }
        return issueList;
    }



    //total Count data parsing
    public static int TotalCount(String url) throws ParseException {
        String result = apiConnection(url);

        JSONParser jsonParse = new JSONParser();
        JSONObject jsonObj = (JSONObject) jsonParse.parse(result);
        Object obj = jsonObj.get("total_count");

        int total_count = Integer.parseInt(obj.toString());

        return total_count;
    }



    //Url 연결후 JsonArray를 반환
    public static String apiConnection(String inputUrl){
        BufferedReader br;
        String resultStr = null;
        JSONArray jsonArray = null;
        try {
            URL url = new URL(GITHUB_API_URL+inputUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", HEADER);

            int responseCode = connection.getResponseCode();

            if (responseCode == 200){
                br= new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
            else{
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            resultStr =br.readLine();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultStr;
    }
}
