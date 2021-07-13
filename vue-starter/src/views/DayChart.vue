<h5 class=" mt-3">날짜별 이슈 개수</h5>
<canvas id="dayIssuesChart" class="chart-canvas mb-5" ></canvas>


<script src="https://unpkg.com/vue"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.4.0/Chart.min.js"></script>
<script>
    import { Line } from 'vue-chartjs'
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
        extends: Line,
        data() {
            return {
                chartdata: null,
                issueCountList : [],
                backColor :null
            };
        },
        mounted () {
            instance
                .get('/iris-issues/chart/open-date')
                .then(response => {
                    this.issueCountList = response.data;
                    this.issueCountList.forEach(function(value,index){
                        labels.push(value.name)
                        dataList.push(value.total)
                    })

                    let r = (Math.random() * (256))
                    let g = r-10
                    let b =r-5;
                    this.backColor = 'rgb('+r+','+g+','+b+')';

                    this.chartdata = {
                        labels: labels,
                        datasets: [{
                            label: '날짜별 이슈',
                            data:dataList,
                            borderWidth: 1,
                            fill: false,
                            borderColor: this.backColor,
                            tension: 0.1
                        }]
                    };
                    this.renderChart(this.chartdata, this.options)
                });
        }
    }

</script>

<style>
    #dayIssuesChart{
        width: 300px;
        height: 300px;
        display: inline-block;
    }
</style>

