import Vue from 'vue'
import App from './App.vue'
import router from './router'
import {BootstrapVue, IconsPlugin} from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

Vue.config.productionTip = false
Vue.use(BootstrapVue)
Vue.use(IconsPlugin)

new Vue({
    router,
    render: h => h(App)
}).$mount('#app')


export var options = {
    scales: {
        y: {
            beginAtZero: true
        }
    }
}


export function getBackgroundColor() {
    let r = (Math.random() * (256))
    let g = r - 10;
    let b = r + 10;
    let backgroundColor = 'rgb(' + r + ',' + g + ',' + b + ')'
    return backgroundColor;
}