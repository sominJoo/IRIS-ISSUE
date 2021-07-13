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

    private static final String END_DATE;
    private static final String START_DATE;
    private static final String OPEN_ULR;
    private static int TOTAL_COUNT = 0;
    private static List<List<Issue>> ISSUE_LIST = new ArrayList<>();

    static {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date now = new Date();
        END_DATE = format.format(now);

        Calendar mon = Calendar.getInstance();
        mon.add(Calendar.MONTH , -1);
        START_DATE = format.format(mon.getTime());

        OPEN_ULR ="/search/issues?q=repo%3Amobigen/IRIS+created%3A"+START_DATE+".."+END_DATE;
    }
    public ChartController() throws ParseException {
        TOTAL_COUNT=  IrisIssuesController.TotalCount(OPEN_ULR);

        int loopCount = TOTAL_COUNT/100+1;

        for (int i = 0 ; i< loopCount ; i++){
            String requestUrl = OPEN_ULR+"&per_page="+IrisIssuesController.MAX_PER_PAGE+"&page="+i;
            List<Issue> list =  IrisIssuesController.AllIssueList(requestUrl);
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
                for (Issue issue :issueList ){
                    String created = issue.getCreated();
                    String date = created.substring(0,10);

                    dataList.add(date);
                }
            }
            issueCountList =Statistics(dataList);

            //정렬 하기
            issueCountList.sort(new Comparator<IssueCount>() {
                @Override
                public int compare(IssueCount o1, IssueCount o2) {
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
                }
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
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (String str : list) {
            Integer count = map.get(str);
            if (count == null) {
                map.put(str, 1);
            } else {
                map.put(str, count + 1);
            }
        }
        // // Map 출력
        for (String key : map.keySet()) {
            IssueCount issueCount = new IssueCount(key, map.get(key));
            issueCountList.add(issueCount);
        }
        return issueCountList;
    }

}
