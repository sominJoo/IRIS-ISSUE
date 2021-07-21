<canvas id="userChart" class="chart-canvas mb-5"></canvas>

<script>
    import axios from "axios";
    import {Bar} from 'vue-chartjs'
    import {options} from "../main.js";
    import {getBackgroundColor} from "../main.js";

    const labels = [];
    const dataList = [];
    const colorList = [];


    export default {
        extends: Bar,
        data() {
            return {
                chartData: null,
                issueCountList: []
            };
        },
        mounted() {
            axios
                .get('/iris-issues/chart/author')
                .then(response => {
                    this.issueCountList = response.data.list;
                    this.issueCountList.forEach(function (value) {
                        labels.push(value.name)
                        dataList.push(value.total)
                        let backgroundColor = getBackgroundColor()
                        colorList.push(backgroundColor)
                    })

                    this.chartData = {
                        labels: labels,
                        datasets: [{
                            label: '등록자별 이슈',
                            data: dataList,
                            backgroundColor: colorList,
                            borderWidth: 1
                        }]
                    };
                    this.renderChart(this.chartData, options)
                });
        }

    }

</script>

