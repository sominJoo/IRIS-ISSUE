package com.iris.irisissue;

import com.iris.irisissue.vo.Issue;
import com.iris.irisissue.vo.IssueCount;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class IrisIssuesService {
    @Value("${github.token}")
    private String token;

    public String HEADER;

    //token 값으로 header 값 초기화
    @PostConstruct
    public void init() {
        HEADER = "token " + token;
    }


    public static final String GITHUB_API_BASE_URL = "https://api.github.com";
    public static final int MAX_PER_PAGE = 100;
    public static final int LIST_PER_PAGE = 20;

    int listTotalCount = 0;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public List<String> labelList() {
        List<String> labelList = new ArrayList<>();
        try {
            String labelUrl = "/repos/mobigen/IRIS/labels?per_page=" + MAX_PER_PAGE;

            String labelResult = ApiConnection(labelUrl);

            JSONParser jsonParser = new JSONParser();
            JSONArray labelJsonArray = (JSONArray) jsonParser.parse(labelResult);

            for (int i = 0; i < labelJsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) labelJsonArray.get(i);
                String name = (String) jsonObject.get("name");
                labelList.add(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return labelList;
    }

    public Map<String, Object> IsseuCounting() {
        Map<String, Object> map = new HashMap<>();
        List<IssueCount> issueCountList = new ArrayList<>();
        try {
            Date now = new Date();
            String nowDate = format.format(now);

            //1. 오늘 open된  issue
            String openUrl = "/search/issues?q=repo:mobigen/IRIS+created:" + nowDate;
            int open_total_count = TotalCount(openUrl);
            IssueCount openCount = new IssueCount("open", open_total_count);
            issueCountList.add(openCount);

            //2. 오늘 close된  issue
            String closeUrl = "/search/issues?q=repo:mobigen/IRIS+closed:" + nowDate;
            int close_total_count = TotalCount(closeUrl);
            IssueCount closedCount = new IssueCount("closed", close_total_count);
            issueCountList.add(closedCount);

            map.put("list", issueCountList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map<String, Object> IsseuList(Map<String, Object> map) {
        Map<String, Object> issueMap = new HashMap<>();
        try {
            String searchUrl = "/search/issues?q=repo:mobigen/IRIS";

            //filter query String
            if (map.get("filter").toString().equals("open")) {
                searchUrl += "+is:open";
            } else if (map.get("filter").toString().equals("closed")) {
                searchUrl += "+is:closed";
            }

            //label query String
            if (map.get("label") != null) {
                if (map.get("label").toString().length() > 0) {
                    String labelName = null;
                    if (map.get("label").toString().contains(":")) {
                        labelName = URLEncoder.encode(map.get("label").toString(), "UTF-8");
                        labelName = "\"" + labelName + "\"";
                        labelName = URLEncoder.encode(":", "UTF-8") + labelName;
                    } else {
                        labelName = URLEncoder.encode(":" + map.get("label").toString(), "UTF-8");
                    }
                    searchUrl += "+label" + labelName;
                }
            }

            //date query String
            if ((map.get("startDate") != null) && (map.get("startDate") != null)) {
                String startDate = map.get("startDate").toString();
                String endDate = map.get("endDate").toString();
                if (startDate.length() > 0 && endDate.length() > 0) {
                    searchUrl += "+created:" + startDate + ".." + endDate;
                }
            }
            String pageStr = "&per_page=" + LIST_PER_PAGE + "&page=" + map.get("page").toString();
            searchUrl += pageStr;

            List<Issue> issueList = SearchIssueList(searchUrl) == null ? new ArrayList<>() : SearchIssueList(searchUrl);

            issueMap.put("issueList", issueList);
            issueMap.put("totalCount", listTotalCount);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return issueMap;
    }


    //등록자별 chart
    public Map<String, Object> UserChartIssueCounting() {
        Map<String, Object> map = new HashMap<>();
        try {
            String chartUrl = "/repos/mobigen/IRIS/issues" + "?per_page=" + MAX_PER_PAGE + "&state=all";
            List<List<Issue>> issueUserList = setChartData(chartUrl);

            List<String> userList = new ArrayList<>();
            for (List<Issue> issueList : issueUserList) {
                for (Issue issue : issueList) {
                    userList.add(issue.getUser());
                }
            }

            List<IssueCount> issueCountList = Statistics(userList);
            map.put("list", issueCountList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    //담당자별 chart
    public Map<String, Object> AssineeIssueCounting() {
        Map<String, Object> map = new HashMap<>();
        try {
            String chartUrl = "/repos/mobigen/IRIS/issues" + "?per_page=" + MAX_PER_PAGE + "&state=all";
            List<List<Issue>> issueAssignList = setChartData(chartUrl);

            //2. 전체 Issues 리스트값
            List<String> assigneeList = new ArrayList<>();
            for (List<Issue> issueList : issueAssignList) {
                for (Issue issue : issueList) {
                    for (String assignee : issue.getAssign())
                        assigneeList.add(assignee);
                }
            }

            List<IssueCount> issueCountList = Statistics(assigneeList);
            map.put("list", issueCountList);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return map;
    }


    //날짜별 Chart
    public Map<String, Object> DateIssueCounting() {
        Map<String, Object> map = new HashMap<>();
        try {
            Calendar mon = Calendar.getInstance();
            String end_date = format.format(mon.getTime());

            mon.add(Calendar.MONTH, -1);
            String start_date = format.format(mon.getTime());

            List<List<Issue>> issueDateList = new ArrayList<>();

            String chartUrl = "/search/issues?q=repo:mobigen/IRIS+created:" + start_date + ".." + end_date + "&per_page=" + MAX_PER_PAGE;
            int i = 0;
            while (true) {
                String requestUrl = chartUrl + "&page=" + ++i;
                List<Issue> list = SearchIssueList(requestUrl);
                if (list == null)
                    break;
                issueDateList.add(list);
            }


            List<String> dateList = new ArrayList<>();
            for (List<Issue> issueList : issueDateList) {
                for (Issue issue : issueList) {
                    String created = issue.getCreated();
                    dateList.add(created);
                }
            }
            List<IssueCount> issueCountList = Statistics(dateList);

            //정렬
            issueCountList.sort((o1, o2) -> {
                Date date1 = null;
                Date date2 = null;
                try {
                    date1 = format.parse(o1.getName());
                    date2 = format.parse(o2.getName());
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
                int compare = date1.compareTo(date2);

                if (compare > 0) return 1;
                else if (compare < 0) return -1;
                else return 0;
            });

            map.put("list", issueCountList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    //total Count data parsing
    public int TotalCount(String url) {
        int total_count = 0;
        try {
            String result = ApiConnection(url + "&per_page=1");

            JSONParser jsonParse = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParse.parse(result);
            Object obj = jsonObj.get("total_count");

            total_count = Integer.parseInt(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total_count;
    }

    //Chart Data
    public List<List<Issue>> setChartData(String chartUrl) {
        List<List<Issue>> issue_list = new ArrayList<>();
        int i = 0;
        while (true) {
            String requestUrl = chartUrl + "&page=" + ++i;
            List<Issue> list = ReposIssueList(requestUrl);
            if (list == null)
                break;
            issue_list.add(list);
        }
        return issue_list;
    }

    //Url 연결
    public String ApiConnection(String inputUrl) {
        BufferedReader br;
        String resultStr = null;
        try {
            System.out.println("ApiConnection = " + GITHUB_API_BASE_URL + inputUrl);
            URL url = new URL(GITHUB_API_BASE_URL + inputUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", HEADER);

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            resultStr = br.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultStr;
    }


    //통계
    public List<IssueCount> Statistics(List<String> list) {
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


    //List Issue Items  저장
    public List<Issue> SearchIssueList(String requestUrl) {
        List<Issue> issueList = new ArrayList<>();
        try {
            String result = ApiConnection(requestUrl);

            JSONParser jsonParse = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParse.parse(result);

            Object obj = jsonObj.get("total_count");
            listTotalCount = Integer.parseInt(obj.toString());

            JSONArray itemsArray = (JSONArray) jsonObj.get("items");
            if (itemsArray.size() <= 0) {
                return null;
            }

            for (int i = 0; i < itemsArray.size(); i++) {
                Issue issue = new Issue();
                JSONObject itemsObj = (JSONObject) itemsArray.get(i);

                //담당자
                JSONArray assigneesArray = (JSONArray) itemsObj.get("assignees");
                List<String> assigneeList = new ArrayList<>();
                String assignee = null;
                if (assigneesArray.size() == 0) {
                    assignee = "No Assignee";
                    assigneeList.add(assignee);
                } else {
                    for (int j = 0; j < assigneesArray.size(); j++) {
                        JSONObject assigneeObj = (JSONObject) assigneesArray.get(j);
                        assignee = (String) assigneeObj.get("login");
                        assigneeList.add(assignee);
                    }
                }

                //Label
                JSONArray labelsArray = (JSONArray) itemsObj.get("labels");
                List<String> labelList = new ArrayList<>();
                for (int j = 0; j < labelsArray.size(); j++) {
                    JSONObject labelObj = (JSONObject) labelsArray.get(j);
                    String labelName = (String) labelObj.get("name");
                    labelList.add(labelName);
                }

                JSONObject userObj = (JSONObject) itemsObj.get("user");
                Object user = (Object) userObj.get("login");

                Object number = (Object) itemsObj.get("number");
                Object title = (Object) itemsObj.get("title");
                Object state = (Object) itemsObj.get("state");
                Object createdAt = (Object) itemsObj.get("created_at");
                Object closedAt = (Object) itemsObj.get("closed_at");


                Date createDate = dateFormat.parse(createdAt.toString());
                String createDateStr = format.format(createDate);

                String closedDateStr = null;
                if (closedAt != null) {
                    Date closeDate = dateFormat.parse(createdAt.toString());
                    closedDateStr = format.format(closeDate);
                }

                issue.setNo(number.toString());
                issue.setTitle(title.toString());
                issue.setUser(user.toString());
                issue.setCreated(createDateStr);
                issue.setCloased(closedDateStr);
                issue.setState(state.toString());
                issue.setLabel(labelList);
                issue.setAssign(assigneeList);

                issueList.add(issue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return issueList;
    }


    //Chart Day Issue Items  저장
    public List<Issue> ReposIssueList(String requestUrl) {
        String result = null;
        List<Issue> chartIssueList = new ArrayList<>();
        try {
            result = ApiConnection(requestUrl);

            JSONParser jsonParse = new JSONParser();
            JSONArray itemsArray = (JSONArray) jsonParse.parse(result);
            if (itemsArray.size() <= 0) {
                return null;
            }

            for (int i = 0; i < itemsArray.size(); i++) {
                Issue issue = new Issue();
                JSONObject itemsObj = (JSONObject) itemsArray.get(i);

                //담당자
                JSONArray assigneesArray = (JSONArray) itemsObj.get("assignees");
                List<String> assigneeList = new ArrayList<>();
                String assignee = null;
                if (assigneesArray.size() == 0) {
                    assignee = "No Assignee";
                    assigneeList.add(assignee);
                } else {
                    for (int j = 0; j < assigneesArray.size(); j++) {
                        JSONObject assigneeObj = (JSONObject) assigneesArray.get(j);
                        assignee = (String) assigneeObj.get("login");
                        assigneeList.add(assignee);
                    }
                }

                //등록자
                JSONObject userObj = (JSONObject) itemsObj.get("user");
                Object user = (Object) userObj.get("login");

                //생성날짜
                Object createdAt = (Object) itemsObj.get("created_at");

                Date createDate = dateFormat.parse(createdAt.toString());
                String createDateStr = format.format(createDate);

                issue.setUser(user.toString());
                issue.setAssign(assigneeList);
                issue.setCreated(createDateStr);

                chartIssueList.add(issue);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ChartIssueList" + result);
        }
        return chartIssueList;
    }
}
