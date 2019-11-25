import Vue from 'vue';
import Vuex from 'vuex';
import createMutationsSharer from 'vuex-shared-mutations';
import createPersistedState from 'vuex-persistedstate';

Vue.use(Vuex);

// noinspection JSUnusedLocalSymbols,JSUnusedGlobalSymbols
export default new Vuex.Store({
	state: {
		apiLogin: 'BinaryBears',
		apiPassword: '123456',
		hexdumpBlockSize: 16,
		hexdumpLineNumberBase: 10,
		pageSize: 25,
		displayFavoritesOnly: false,
		pause: false,

		hexdumpMode: false,

		serviceModalName: '',
		serviceModalId: 0,
	},
	mutations: {
		setApiLogin: (s, p) => s.apiLogin = p,
		setApiPassword: (s, p) => s.apiPassword = p,
		setHexdumpBlockSize: (s, p) => s.hexdumpBlockSize = p,
		setHexdumpLineNumberBase: (s, p) => s.hexdumpLineNumberBase = p,
		setPageSize: (s, p) => s.pageSize = p,
		// eslint-disable-next-line no-unused-vars
		toggleDisplayFavoritesOnly: (s, p) => s.displayFavoritesOnly = !s.displayFavoritesOnly,
		// eslint-disable-next-line no-unused-vars
		togglePause: (s, p) => s.pause = !s.pause,
		// eslint-disable-next-line no-unused-vars
		toggleHexdumpMode: (s, p) => s.hexdumpMode = !s.hexdumpMode,

		setServiceModalName: (s, p) => s.serviceModalName = p,
		setServiceModalId: (s, p) => s.serviceModalId = p,
	},
	actions: {},
	plugins: [
		createPersistedState(),
		createMutationsSharer({
			// eslint-disable-next-line no-unused-vars
			predicate: (mutation, state) => {
				console.debug('Got mutation:', mutation);
				const mName = mutation?.type;
				const process = mName !== 'toggleDisplayFavoritesOnly'
					&& mName !== 'setServiceModalName'
					&& mName !== 'setServiceModalId'
					&& mName !== 'togglePause'
					&& mName !== 'toggleHexdumpMode';
				console.debug('Processing?', process);
				return process;
			},
		}),
	],
});
