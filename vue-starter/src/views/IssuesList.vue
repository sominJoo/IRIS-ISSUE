<template>
    <div class="IssueList">
        <div id="table-container">
            <form
                    name="searchFrm"
                    id="searchFrm"
                    class="border mb-3"
                    @submit.prevent="onSubmitFrm">
                <div class="d-flex justify-content-center form-dic">
                    <h6 class=" m-2">필터조건</h6>
                    <div class="resolveStatus m-2">
                        <span class="status-title">해결상태 : </span>
                        <div class="radio-div">
                            <input type="radio" name="filter" value="all" checked="checked" v-model="filter">All
                            <input type="radio" name="filter" value="open" v-model="filter">Open
                            <input type="radio" name="filter" value="closed" v-model="filter">Closed
                        </div>

                    </div>

                    <div class="labelFilter m-2">
                        <span class="status-title">라벨 필터링 : </span>
                        <select name="label" id="label" v-model="labelName">
                            <option value="">선택하세요</option>
                            <option :key="i" :value="label" v-for="(label, i) in labelList">{{ label }}</option>
                        </select>
                    </div>


                    <div class="termFilter m-2">
                        <span class="status-title">기간 : </span>
                        <input type="date" name="startDate" value="2021-07-08" v-model="startDate">
                        <input type="date" name="endDate" value="2021-07-08" v-model="endDate">
                    </div>
                    <button type="submit" class="btn btn-outline-success mt-1" style="height: 65%"> 검색</button>
                    <button type="button" class="btn btn-outline-danger mt-1" style="height: 65%" @click="onReset">
                        초기화
                    </button>
                </div>
            </form>

            <table class="table table-bordered table-striped mb-0 table-light " id="issue-table">
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
                <tr v-if="issueList.length<=1">
                    <td colspan="7"> 데이터없음</td>
                </tr>

                <tr v-for="(issue, index) in issueList" :key="index">
                    <td>{{ issue.no }}</td>
                    <td>{{ issue.title }}</td>
                    <td>{{ issue.user }}</td>
                    <td>{{ issue.created }}</td>
                    <td>{{ issue.cloased }}</td>
                    <td>{{ issue.state }}</td>
                    <td>
                        <span v-for="(label, i) in issue.label" :key="i">{{ label }} <span> | </span> </span>
                    </td>
                </tr>
                </tbody>
            </table>

            <!-- 페이징 -->
            <div id="pagination" class="text-center mt-3">
                <div class="pagination-div overflow-auto">
                    <b-pagination v-model="currentPage" :total-rows="totalRows" align="center"
                                  @page-click="pageList"></b-pagination>
                </div>
            </div>
        </div>
    </div>
</template>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>

<script>
    import axios from "axios";

    let temp = 0;


    export default {
        data() {
            return {
                labelList: [],
                issueList: [],//나중에
                totalRows: 0,
                currentPage: 1,//default

                filter: 'all',
                labelName: '',
                startDate: '',
                endDate: ''
            }
        },
        mounted() {
            //label data
            axios
                .get('/iris-issues/issues/label')
                .then(response => {
                    this.labelList = response.data;
                });

            this.getIssueList();
        },
        methods: {
            //page 이동
            pageList: function (button, page) {
                this.currentPage = page;
                this.getIssueList();
            },

            //search form 제출
            onSubmitFrm: function (event) {
                event.preventDefault();
                this.currentPage = 1;

                if (this.validationCheck())
                    this.getIssueList();
            },

            //reset button
            onReset: function () {
                this.filter = 'all';
                this.labelName = '';
                this.startDate = '';
                this.endDate = '';
                this.currentPage = 1;

                this.getIssueList();
            },

            //날짜값 체크
            validationCheck() {
                this.errors = [];
                if (this.startDate && !this.endDate) {
                    alert("마지막날을 입력해주세요");
                    return false;
                } else if (this.endDate && !this.startDate) {
                    alert("시작날을 입력해주세요");
                    return false;
                } else if (this.startDate > this.endDate) {
                    alert("시작날이 마지막날보다 클 수 없습니다.");
                    return false;
                }
                return true;
            },

            //issue 데이터 가져오기
            getIssueList: function () {
                axios
                    .get('/iris-issues/issues', {
                        params: {
                            page: this.currentPage,
                            filter: this.filter,
                            label: this.labelName,
                            startDate: this.startDate,
                            endDate: this.endDate
                        }
                    })
                    .then(response => {
                        this.issueList = response.data.issueList
                        this.totalRows = response.data.totalCount;
                    });
            }
        }
    };
</script>
<style>
    .pagination-div {
        display: inline-block;
    }
</style>
