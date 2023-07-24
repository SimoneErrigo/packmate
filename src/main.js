import Vue from 'vue';
import BootstrapVue from 'bootstrap-vue';
import Axios from 'axios';
import InfiniteLoading from 'vue-infinite-loading';
import App from './App.vue';
import router from './router';

import store from './store';
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue/dist/bootstrap-vue.css';
import '@fortawesome/fontawesome-free/css/all.css';
import "bootstrap-darkmode/scss/darktheme.scss";

Vue.config.productionTip = false;
Vue.use(BootstrapVue);
Vue.use(InfiniteLoading, {
	slots: {
		noResults: 'No results...',
		noMore: 'No more data',
		error: 'An error has occurred!',
		errorBtnText: 'Retry',
	},
	props: {
		spinner: 'waveDots',
	},
});

const axiosInstance = Axios.create({
	baseURL: '/api', // TO!DO: edit for release!
	// baseURL: 'http://192.168.79.131:65000/api',
	headers: {
		'Accept': 'application/json',
		'Content-Type': 'application/json',
	},
	timeout: 5000,
	withCredentials: true,
	maxContentLength: 20000,
});
// noinspection JSUnusedGlobalSymbols
Vue.prototype.$http = axiosInstance;

// noinspection JSUnusedGlobalSymbols
new Vue({
	router,
	store,
	render: h => h(App),
}).$mount('#app');
