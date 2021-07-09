<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="http://code.jquery.com/jquery-latest.min.js"></script>
    <!-- bootstrap js: jquery load 이후에 작성할것.-->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>

    <!-- bootstrap css -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css" integrity="sha384-9gVQ4dYFwwWSjIDZnLEWnxCjeSWFphJiwGPXr1jddIhOegiu1FwO5qRGvFXOdJZ4" crossorigin="anonymous">

    <!-- chart js -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/3.4.0/chart.min.js"></script>

    <title>issue</title>
    <style>
        #table-container{
            font-size: 13px;
        }
        .chartWrap{
            display: inline-block;
        }
        .count{
            width: 500px;
            border: 3px solid gray;
            display: inline-block;
        }
        #title{
            width: 1000px;
            border: 2px solid gray;
            display: inline-block;
        }
        .form-dic{
            height: 55px;
        }
        .radio-div{
            display: inline-block;
        }
        .radio-div input{
            margin-left: 1rem;
        }
    </style>
</head>
<body>
<header class="text-center">
    <div id="title" class="p-3 m-3"><span>IRIS 이슈 통계 페이지</span></div>
    <div class="counting m-3">
        <div class="count p-3"><span>오늘 등록 이슈 : 0</span></div>
        <div class="count p-3"><span>오늘 해결 이슈 : 0</span></div>
    </div>
</header>
<div class="container ">
    <ul class="nav nav-tabs mb-3">
        <li class="nav-item"><a href="#chart-div" data-toggle="tab"
                                class="nav-link active">Chart</a></li>
        <li class="nav-item"><a href="#table-container" data-toggle="tab"
                                class="nav-link ">ISSUES</a></li>
    </ul>


    <div class="tab-content">
        <div id="chart-div"  class="tab-pane fade-in active" id="home" role="tabpanel" aria-labelledby="home-tab">
            <div class="chart-container text-center">
                <!-- 등록자별 이슈 개수 바차트 -->
                <h5 class=" mt-3">등록자별 이슈 개수</h5>
                <div style="width: 80%;" class="chartWrap">
                    <canvas id="userChart" class="chart-canvas mb-5" ></canvas>
                </div>
                <!-- 담당자별 이슈 개수 파이차트 -->
                <h5 class=" mt-3">담당자별 이슈 개수</h5>
                <div style="width: 70%;" class="chartWrap">
                    <canvas id="assignChart" class="chart-canvas mb-5" ></canvas>
                </div>

                <!-- 날짜별 이슈 개수 라인차트 -->
                <h5 class=" mt-3">날짜별 이슈 개수</h5>
                <div style="width: 80%;" class="chartWrap">
                    <canvas id="dayIssuesChart" class="chart-canvas mb-5"></canvas>
                </div>
            </div>
        </div>



        <div id="table-container"  class="tab-pane fade show" id="profile" role="tabpanel" aria-labelledby="profile-tab">
            <form
                    name="searchFrm"
                    id="searchFrm"
                    class="border mb-3">
                <div class="d-flex justify-content-center mb-3 form-dic" >
                    <h5 class=" m-4">필터조건</h5>
                    <div class="resolveStatus m-4">
                        <span class="status-title">해결상태 : </span>
                        <div class="radio-div">
                            <input type="radio" name="chk_info" value="all" checked="checked">All
                            <input type="radio" name="chk_info" value="open">Open
                            <input type="radio" name="chk_info" value="closed">Closed
                        </div>

                    </div>

                    <div class="labelFilter m-4">
                        <span class="status-title">라벨 필터링 : </span>
                        <select name="" id="">
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                        </select>
                    </div>


                    <div class="termFilter m-4">
                        <span class="status-title">기간 : </span>
                        <input type="date" name="startDate" value="2021-07-08" min="2021-06-08" max="2021-07-08">
                        <input type="date" name="endDate" value="2021-07-08" min="2021-06-08" max="2021-07-08">
                    </div>
                </div>
            </form>
            <table class="table table-bordered table-striped mb-0 table-light ">
                <thead>
                <tr>
                    <th scope="col">No</th>
                    <th scope="col">제목</th>
                    <th scope="col">등록자</th>
                    <th scope="col">등록일</th>
                    <th scope="col">해결일</th>
                    <th scope="col">상태</th>
                    <th scope="col">라벨</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>123</td>
                    <td>[ Provision ] Product의 WebURL만이 아닌, payload도 Web에 띄우기</td>
                    <td>ollehQ</td>
                    <td>2021-07-06</td>
                    <td>2021-07-07</td>
                    <td>closed</td>
                    <td>#Cloud | Assign: Backend | Plan: QA | Priority: P3 | Rating: Minor | Status: In-Progress</td>
                </tr>
                </tbody>
            </table>

            <!-- 페이징 -->
            <div class="text-center d-flex justify-content-center">
                <nav aria-label="Page navigation example">
                    <ul class="pagination">
                        <li class="page-item">
                            <a class="page-link" href="#" aria-label="Previous">
                                <span aria-hidden="true">&laquo;</span>
                            </a>
                        </li>
                        <li class="page-item"><a class="page-link" href="#">1</a></li>
                        <li class="page-item"><a class="page-link" href="#">2</a></li>
                        <li class="page-item"><a class="page-link" href="#">3</a></li>
                        <li class="page-item">
                            <a class="page-link" href="#" aria-label="Next">
                                <span aria-hidden="true">&raquo;</span>
                            </a>
                        </li>
                    </ul>
                </nav>
            </div>
        </div>
    </div>



</div>

<script>

    const option ={
        scales: {
            y: {
                beginAtZero: true
            }
        }
    }


    const labels = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17];
    const data1 = {
        labels: labels,
        datasets: [{
            label: 'My First Dataset',
            data: [65, 59, 80, 81, 56, 55, 40],
            backgroundColor: ['rgb(48,87,164)'],
            borderWidth: 1
        }]
    };
    const ctx1 =  document.getElementById('userChart').getContext('2d');
    const config1 = {
        type: 'bar',
        data: data1,
        options: option
    };

    var myChart1 = new Chart(ctx1, config1)


    // Doughnut Chart
    var ctx = document.getElementById('assignChart').getContext('2d');
    const config2={
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                label: '# of Votes',
                data: [12, 19, 3, 5, 2, 3],
                backgroundColor: [
                    'rgba(255, 99, 132, 0.2)',
                    'rgba(54, 162, 235, 0.2)',
                    'rgba(255, 206, 86, 0.2)',
                    'rgba(75, 192, 192, 0.2)',
                    'rgba(153, 102, 255, 0.2)',
                    'rgba(255, 159, 64, 0.2)'
                ],
                borderColor: [
                    'rgba(255, 99, 132, 1)',
                    'rgba(54, 162, 235, 1)',
                    'rgba(255, 206, 86, 1)',
                    'rgba(75, 192, 192, 1)',
                    'rgba(153, 102, 255, 1)',
                    'rgba(255, 159, 64, 1)'
                ],
                borderWidth: 1
            }]
        },
        options: option
    }
    var myChart = new Chart(ctx,config2);

    // Line Chart
    const data = {
        labels: labels,
        datasets: [{
            label: "PEOPLE",
            data: [65, 59, 80, 81, 56, 55, 40],
            fill: false,
            borderColor: 'rgb(75, 192, 192)',
            tension: 0.1
        }]
    };
    const config = {
        type: 'line',
        data: data,
        options: option
    };
    var ctx3= document.getElementById('dayIssuesChart').getContext('2d');
    var myChart = new Chart(ctx3,config);
</script>
</body>
</html>