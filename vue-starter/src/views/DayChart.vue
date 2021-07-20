<canvas id="dayIssuesChart" class="chart-canvas mb-5"></canvas>


<script>
    import axios from "axios";
    import {Line} from 'vue-chartjs'
    import {options} from "../main.js";


    const labels = [];
    const dataList = [];

    export default {
        name: 'day-chart',
        extends: Line,
        data() {
            return {
                chartdata: null,
                issueCountList: [],
                backColor: null
            };
        },
        mounted() {
            axios
                .get('/iris-issues/chart/open-date')
                .then(response => {
                    this.issueCountList = response.data.list;
                    this.issueCountList.forEach(function (value) {
                        labels.push(value.name)
                        dataList.push(value.total)
                    })
                    this.backColor = 'rgb(123,113,138)';

                    this.chartdata = {
                        labels: labels,
                        datasets: [{
                            label: '날짜별 이슈(1개월)',
                            data: dataList,
                            borderWidth: 1,
                            fill: false,
                            borderColor: this.backColor,
                            tension: 0.1
                        }]
                    };
                    this.renderChart(this.chartdata, options)
                });
        }
    }

</script>

<style>
    #dayIssuesChart {
        width: 300px;
        height: 300px;
        display: inline-block;
    }
</style>

