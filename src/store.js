import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate'

Vue.use(Vuex);

export default new Vuex.Store({
	state: {
		apiUrl: 'http://127.0.0.1/api',
		apiLogin: 'BinaryBears',
		apiPassword: '123456',
		hexdumpBlockSize: 16,
		hexdumpLineNumberBase: 10,
		pageSize: 50,
		displayFavOnly: false,
	},
	mutations: {
		setApiUrl: (state, payload) => state.apiUrl = payload,
		setApiLogin: (state, payload) => state.apiLogin = payload,
		setApiPassword: (state, payload) => state.apiPassword = payload,
		setHexdumpBlockSize: (state, payload) => state.hexdumpBlockSize = payload,
		setHexdumpLineNumberBase: (state, payload) => state.hexdumpLineNumberBase = payload,
		setPageSize: (state, payload) => state.pageSize = payload,
		setDisplayFavOnly: (state, payload) => state.displayFavOnly = payload,
	},
	plugins: [createPersistedState(),],
})
