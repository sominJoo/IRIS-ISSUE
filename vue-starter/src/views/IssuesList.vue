<template>
    <div class="IssueList">
        <div id="table-container" >
            <form
                    name="searchFrm"
                    id="searchFrm"
                    class="border mb-3">
                <div class="d-flex justify-content-center mb-3 form-dic" >
                    <h5 class=" m-4">필터조건</h5>
                    <div class="resolveStatus m-4">
                        <span class="status-title">해결상태 : </span>
                        <div class="radio-div">
                            <input type="radio" name="filter" value="all" checked="checked">All
                            <input type="radio" name="filter" value="open">Open
                            <input type="radio" name="filter" value="closed">Closed
                        </div>

                    </div>

                    <div class="labelFilter m-4">
                        <span class="status-title">라벨 필터링 : </span>
                        <select name="label" id="label">
                            <option value="">선택하세요</option>
                            <option :key="i" :value="label" v-for="(label, i) in labelList ">{{ label }}</option>
                        </select>
                    </div>


                    <div class="termFilter m-4">
                        <span class="status-title">기간 : </span>
                        <input type="date" name="startDate" value="2021-07-08" min="2021-06-08" max="2021-07-08">
                        <input type="date" name="endDate" value="2021-07-08" min="2021-06-08" max="2021-07-08">
                    </div>
                    <button type="submit" class="btn btn-outline-success"> 검색</button>
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
            <div class="text-center d-flex justify-content-center mt-3">
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
</template>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>

<script>
    import axios from "axios";


    export default {
        data(){
            return{
                labelList : [],
                issueList : []//나중에
            }
        },
        mounted() {
            axios
                .get('/iris-issues/issues/label')
                .then(response => {
                    this.labelList = response.data;
                });
            axios
                .get('/iris-issues/issues')
                .then(response => {
                    console.log(response.data)
                });

        }
    };
</script>
