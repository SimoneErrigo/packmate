import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate'

Vue.use(Vuex);

export default new Vuex.Store({
	state: {
		apiUrl: 'http://127.0.0.1/api',
	},
	mutations: {
		setApiUrl: (state, payload) => state.apiUrl = payload,
	},
	// actions: {
	// 	setApiUrl: async (injectee, payload) => injectee.commit('setApiUrl', payload),
	// },
	plugins: [createPersistedState(),],
})
