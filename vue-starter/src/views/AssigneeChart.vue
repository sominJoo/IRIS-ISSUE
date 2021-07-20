<canvas id="assignChart" class="chart-canvas mb-5"></canvas>


<script>
    import axios from "axios";
    import {Pie} from 'vue-chartjs'
    import {getBackgroundColor} from "../main.js";


    const labels = [];
    const dataList = [];
    const colorList = [];


    const options = {
        legend: {
            display: true,
            position: 'right',
            labels: {
                boxWidth: 10,
                padding: 10
            }
        },
        scales: {
            y: {
                beginAtZero: true
            }
        }
    }

    export default {
        name: 'assignee-chart',
        extends: Pie,
        data() {
            return {
                chartdata: null,
                issueCountList: [],
                isLoading: true
            };
        },
        mounted() {
            axios
                .get('/iris-issues/chart/assignee')
                .then(response => {
                    this.issueCountList = response.data.list;
                    this.issueCountList.forEach(function (value) {
                        labels.push(value.name)
                        dataList.push(value.total)
                        let backgroundColor = getBackgroundColor();
                        colorList.push(backgroundColor)
                    })

                    this.chartdata = {
                        labels: labels,
                        datasets: [{
                            label: '담당자별 이슈',
                            data: dataList,
                            backgroundColor: colorList,
                            borderWidth: 1
                        }]
                    };
                    this.isLoading = false;

                    this.renderChart(this.chartdata, options)
                });
        }
    }
</script>