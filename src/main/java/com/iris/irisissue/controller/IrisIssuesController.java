package com.iris.irisissue.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iris.irisissue.vo.Issue;
import com.iris.irisissue.vo.IssueCount;
import com.iris.irisissue.vo.IssuesExt;
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
public class IrisIssuesController {
    public static String token ="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";

    public static final String GITHUB_API_BASE_URL ="https://api.github.com";
    public static final String HEADER ="token "+token;

    public static final int MAX_PER_PAGE = 100;
    public static final int LIST_PER_PAGE = 20;

    public static int PER_PAGE =0;


    //라벨 조회
    @GetMapping("/iris-issues/issues/label")
    @ResponseBody
    public List<String> label(){
        List<String> labelList = new ArrayList<>();
        try {
            //1. label data
            String labelUrl ="/repos/mobigen/IRIS/labels?per_page="+MAX_PER_PAGE;

            //2. api 연결
            String labelResult = ApiConnection(labelUrl);

            //3. parsing 처리
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

            //2. 오늘 close된  issue
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


    //Issue pagination
    @GetMapping("/iris-issues/issues")
    @ResponseBody
    public List<IssuesExt> IsseuList(
            @RequestParam(value = "page",defaultValue = "1", required = false) int page,
            @RequestParam(value = "filter",defaultValue = "all", required = false) String filter,
            @RequestParam(value = "label", required = false) String label,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate
    ){
        List<IssuesExt> issueList = new ArrayList<>();
        try {
            String requestUrl = "/search/issues?q=repo%3Amobigen/IRIS";

            //filter query String
            if (filter.equals("open")){
                requestUrl += "+is%3Aopen";
            }
            else if (filter.equals("closed")){
                requestUrl += "+is%3Aclosed";
            }

            //label query String
            if (label !=null){
                if (label.length()>0){
                    String labelName =null;
                    if (label.contains("#") ){
                        labelName =label.replace("#","%23");
                    }
                    else if(label.contains(":")){
                        String temp = label.replace(":","%3A").replace(" ","+");
                        labelName = "\""+temp+"\"";
                    }
                    System.out.println("labelName = "+labelName);
                    requestUrl += "+label%3A"+labelName;
                }
            }

            //date query String
            if ((startDate!=null) && (endDate !=null )){
                if (startDate.length()>0 && endDate.length()>0 ){
                requestUrl += "+created%3A"+startDate+".."+endDate;
                }
            }

            //paging
            String pageStr = "&per_page="+LIST_PER_PAGE+"&page="+page;
            requestUrl += pageStr;

            System.out.println("최종 url = "+requestUrl);
            issueList = AllIssueList(requestUrl);

            IssuesExt issuesExt = new IssuesExt();
            issuesExt.setPerPage(PER_PAGE);
            issuesExt.setCurrentPage(page);

            issueList.add(issuesExt);
        }catch (Exception e){
            e.printStackTrace();
        }

        return issueList;
    }

    //Issue Items  저장
    public static List<IssuesExt> AllIssueList(String requestUrl){
        List<IssuesExt> issueList = new ArrayList<>();
        try{
            String result = ApiConnection(requestUrl);

            JSONParser jsonParse = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParse.parse(result);
            JSONArray itemsArray =(JSONArray) jsonObj.get("items");

            Object obj = jsonObj.get("total_count");
            PER_PAGE = Integer.parseInt(obj.toString());

            for (int i=0;i<itemsArray.size();i++){
                IssuesExt issue = new IssuesExt();
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

                String createDate = createdAt.toString().replace("T"," ").replace("Z","");

                issue.setNo(number.toString());
                issue.setUser(user.toString());
                issue.setTitle( title.toString());
                issue.setLabel(labelList);
                issue.setAssign(assigneeList);
                issue.setState( state.toString());
                issue.setCreated(createDate);
                issue.setCloased(closedAt == null ? null : closedAt.toString().replace("T"," ").replace("Z",""));

                issueList.add(issue);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return issueList;
    }




    //total Count data parsing
    public static int TotalCount(String url){
        int total_count=0;
        try {
            String result = ApiConnection(url);

            JSONParser jsonParse = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParse.parse(result);
            Object obj = jsonObj.get("total_count");

            total_count = Integer.parseInt(obj.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return total_count;
    }



    //Url 연결
    public static String ApiConnection(String inputUrl){
        BufferedReader br;
        String resultStr = null;
        JSONArray jsonArray = null;
        try {
            URL url = new URL(GITHUB_API_BASE_URL+inputUrl);

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
