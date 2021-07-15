package com.iris.irisissue.controller;

import com.iris.irisissue.vo.Issue;
import com.iris.irisissue.vo.IssueCount;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class ChartController {

    private static  String END_DATE;
    private static  String START_DATE;
    private static  String OPEN_ULR;
    private static int TOTAL_COUNT = 0;
    private static List<List<Issue>> ISSUE_LIST = new ArrayList<>();

    //전역 변수 초기화
    public ChartController() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date now = new Date();
        END_DATE = format.format(now);

        Calendar mon = Calendar.getInstance();
        mon.add(Calendar.MONTH , -1);
        START_DATE = format.format(mon.getTime());

        OPEN_ULR ="/search/issues?q=repo%3Amobigen/IRIS+created%3A"+START_DATE+".."+END_DATE;

        //chart data 출력을 위한 issueList 초기화
        TOTAL_COUNT=  IrisIssuesController.TotalCount(OPEN_ULR);
        int loopCount = TOTAL_COUNT/100+1;
        for (int i = 0 ; i< loopCount ; i++){
            String requestUrl = OPEN_ULR+"&per_page="+IrisIssuesController.MAX_PER_PAGE+"&page="+i;
            List<Issue> list =  AllIssueList(requestUrl);
            ISSUE_LIST.add(list);
        }
    }


    //등록자별 이슈 조회
    @GetMapping("/iris-issues/chart/author")
    @ResponseBody
    public List<IssueCount> UserIssueCounting() {
        List<IssueCount> issueCountList = new ArrayList<>();
        try{
            List<List<Issue>> issueUserList = ISSUE_LIST;

            List<String> userList= new ArrayList<>();
            for (List<Issue> issueList : issueUserList){
                for (Issue issue :issueList ){
                    userList.add(issue.getUser());
                }
            }
            issueCountList =Statistics(userList);
        }catch (Exception e){
            e.printStackTrace();
        }

        return issueCountList;
    }


    //담당자별 이슈 조회
    @GetMapping("/iris-issues/chart/assignee")
    @ResponseBody
    public List<IssueCount> AssineeIssueCounting() {
        List<IssueCount> issueCountList = new ArrayList<>();
        try{
            List<List<Issue>> issueAssignList = ISSUE_LIST;

            //2. 전체 Issues 리스트값
            List<String> assigneeList= new ArrayList<>();
            for (List<Issue> issueList : issueAssignList){
                for (Issue issue :issueList){
                    for (String assignee : issue.getAssign())
                    assigneeList.add(assignee);
                }
            }

            issueCountList =Statistics(assigneeList);
        }catch (Exception e){
            e.printStackTrace();
        }

        return issueCountList;
    }


    //날짜별 이슈 조회
    @GetMapping("/iris-issues/chart/open-date")
    @ResponseBody
    public List<IssueCount> DateIssueCounting() {
        List<IssueCount> issueCountList = new ArrayList<>();
        try{
            List<List<Issue>> issueDateList = ISSUE_LIST;

            List<String> dataList= new ArrayList<>();
            for (List<Issue> issueList : issueDateList){
                for (Issue issue :issueList){
                    String created = issue.getCreated();
                    String date = created.substring(0,10);
                    dataList.add(date);
                }
            }
            issueCountList =Statistics(dataList);

            //정렬 => custom 정렬
            issueCountList.sort((o1, o2) -> {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date1 = null;
                Date date2 = null;
                try {
                    date1 = dateFormat.parse(o1.getName());
                    date2 = dateFormat.parse(o2.getName());
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
                int compare = date1.compareTo(date2);

                if(compare>0) return 1;
                else if(compare<0) return -1;
                else return 0;
            });
        }catch (Exception e){
            e.printStackTrace();
        }

        return issueCountList;
    }

    //통계
    public List<IssueCount> Statistics(List<String> list){
        List<IssueCount> issueCountList = new ArrayList<>();

        // List 원소 빈도수를 Map에 저장
        Map<String, Integer> map = new HashMap<>();
        for (String name : list) {
            Integer count = map.get(name);
            //count 값이 없으면 해당 str 저장
            if (count == null) {
                map.put(name, 1);
            }
            // name 값이 map에 존재시 count +1 해서 put => map은 중복 금지지
             else {
                map.put(name, count + 1);
            }
        }
         // Map 출력
        for (String key : map.keySet()) {
            IssueCount issueCount = new IssueCount(key, map.get(key));
            issueCountList.add(issueCount);
        }
        return issueCountList;
    }

    //Issue items 저장
    public static List<Issue> AllIssueList(String requestUrl) throws ParseException {
        List<Issue> issueList = new ArrayList<>();
        try{
            String result = IrisIssuesController.ApiConnection(requestUrl);  //api 연결

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
        }catch (Exception e){
            e.printStackTrace();

        }
        return issueList;
    }
}
