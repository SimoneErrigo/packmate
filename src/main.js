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

Vue.config.productionTip = false;
Vue.use(BootstrapVue);
Vue.use(InfiniteLoading, {
	slots: {
		'no-results': 'Нет результатов...',
		'no-more': 'Данных больше нет',
		error: 'Произошла ошибка!',
		errorBtnText: 'Повторить',
	},
	props: {
		spinner: 'waveDots',
	},
});

const axiosInstance = Axios.create({
	// baseURL: '/api', // TODO: edit for release!
	baseURL: 'http://192.168.79.131:65000/api',
	auth: {
		username: store.state.apiLogin,
		password: store.state.apiPassword,
	},
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
