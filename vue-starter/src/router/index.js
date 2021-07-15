import Vue from 'vue'
import VueRouter from 'vue-router'
import UserChart from '../views/UserChart.vue'
import AssigneeChart from '../views/AssigneeChart.vue'
import DayChart from '../views/DayChart.vue'
import IssuesList from '../views/IssuesList.vue'
import Header from '../views/Header.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    components: {
      userChart: UserChart,
      header: Header,
      assigneeChart : AssigneeChart,
      dayChart : DayChart,
      issuesList: IssuesList
    }
  },
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
