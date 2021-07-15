
<h5 class=" mt-3">담당자별 이슈 개수</h5>
<canvas id="assignChart" class="chart-canvas mb-5" ></canvas>


<script src="https://unpkg.com/vue"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.4.0/Chart.min.js"></script>
<script>
    import { Pie } from 'vue-chartjs'
    import axios from "axios";


    const labels = [];
    const dataList = [];
    const colorList = [];

    const instance = axios.create({
        baseURL: 'http://localhost:8080',
        timeout: 100000,
        headers:
            {'content-type': 'application/json'}
    });

    const options ={
        scales: {
            y: {
                beginAtZero: true
            }
        }
    }

    export default {
        extends: Pie,
        data() {
            return {
                chartdata: null,
                issueCountList : []
            };
        },
        mounted () {
            instance
                .get('/iris-issues/chart/assignee')
                .then(response => {
                    this.issueCountList = response.data;
                    this.issueCountList.forEach(function(value,index){
                        labels.push(value.name)
                        dataList.push(value.total)
                        let r = (Math.random() * (256))
                        let g = r-10;
                        let b =r+10;
                        let backgroundColor = 'rgb('+r+','+g+','+b+')'
                        colorList.push(backgroundColor)
                    })

                    this.chartdata = {
                        labels: labels,
                        datasets: [{
                            label: '담당자별 이슈',
                            data:dataList,
                            backgroundColor: colorList,
                            borderWidth: 1
                        }]
                    };
                    console.log(this.chartdata);
                    this.renderChart(this.chartdata, this.options)
                });
        }
    }
</script>