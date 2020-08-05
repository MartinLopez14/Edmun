import Vue from 'vue'
import App from './App'
import VueLogger from 'vuejs-logger';
import Vuelidate from 'vuelidate'
import VueRouter from 'vue-router'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import {BootstrapVue, IconsPlugin, NavbarPlugin, BootstrapVueIcons } from 'bootstrap-vue'
import routes from './routes';

Vue.use(VueRouter)
Vue.use(Vuelidate)
// Install BootstrapVue
Vue.use(BootstrapVue)
Vue.use(BootstrapVueIcons)
// Optionally install the BootstrapVue icon components plugin
Vue.use(IconsPlugin)


Vue.config.productionTip = false;

const options = {
    isEnabled: true,
    logLevel: 'debug',
    stringifyArguments: false,
    showLogLevel: true,
    showMethodName: false,
    separator: '|',
    showConsoleColors: true
};

const router = new VueRouter({
    routes
});

Vue.use(VueLogger, options);
Vue.use(NavbarPlugin);
Vue.use(BootstrapVue);
Vue.use(IconsPlugin);


Vue.config.productionTip = false;


/* eslint-disable no-new */
new Vue({
    el: '#app',
    template: '<App/>',
    components: {App},
    router,
    render: h => h(App),
}).$mount('#app');
