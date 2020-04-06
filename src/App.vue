<template>
	<div id="app">
		<Navbar ref="navbar"/>
		<div class="container-fluid">
			<div class="row">
				<transition name="fade" mode="out-in">
					<router-view name="sidebar" ref="sidebar"/>
				</transition>
				<main role="main" class="col-sm-9 ml-sm-auto px-4">
					<transition name="fade" mode="out-in">
						<router-view name="content"/>
					</transition>
				</main>
			</div>
		</div>
		<Settings/>
		<AddService/>
		<EditService/>
		<AddPattern/>
	</div>
</template>

<script>
	export const DB_OBJSTORE_COUNTERS_HISTORY = 'countersHistory';

	import Navbar from './components/Navbar';
	import Settings from './views/Settings';
	import AddService from './views/AddService';
	import EditService from './views/EditService';
	import AddPattern from './views/AddPattern';
	import SockJS from 'sockjs-client';
	import {openDB,} from 'idb';

	export default {
		data() {
			return {
				websocket: null,
				db: null,
			};
		},
		mounted() {
			this.connectWs();
			openDB('packmate', 1, {
				upgrade(db, oldVersion, newVersion) {
					console.info('[IDB] Creating new database! Old rev %d, new rev %d',
						oldVersion, newVersion);
					db.createObjectStore(DB_OBJSTORE_COUNTERS_HISTORY, {
						autoIncrement: false,
						keyPath: null,
					});
				},
			})
				.then(db => {
					this.db = db;
				})
				.catch(e => {
					console.error('[IDB] Failed to open DB!', e);
				});
		},
		beforeDestroy() {
			this.websocket?.close();
		},
		methods: {
			connectWs() {
				if (this.websocket !== null) return;
				this.websocket = new SockJS(this.$http.defaults.baseURL + '/ws');
				this.websocket.onopen = () => {
					console.info('[WS] Connected');
				};
				this.websocket.onclose = (ev) => {
					console.info('[WS] Disconnected', ev.code, ev.reason);
					this.websocket = null;
					if (ev.code === 1008) {
						console.info('[WS] Security timeout, reconnecting...');
						this.connectWs();
					}
					if (ev.code !== 1000) { // Normal closure
						setTimeout(this.connectWs, 3000);
						console.info('[WS] Reconnecting...');
					}
				};
				this.websocket.onmessage = (ev) => {
					const parsed = JSON.parse(ev.data);
					// console.debug('[WS] New message', parsed);

					switch (parsed.type) {
						case 'NEW_STREAM': {
							this.$refs.sidebar.addStreamFromWs(parsed.value);
							break;
						}
						case 'SAVE_SERVICE': {
							this.$refs.navbar.addServiceFromWs(parsed.value);
							break;
						}
						case 'DELETE_SERVICE': {
							this.$refs.navbar.deleteServiceFromWs(parsed.value);
							break;
						}
						case 'SAVE_PATTERN': {
							this.$refs.navbar.$refs.patternsDropdown.addPatternFromWs(parsed.value);
							break;
						}
						case 'DELETE_PATTERN': {
							this.$refs.navbar.$refs.patternsDropdown.deletePatternFromWs(parsed.value);
							break;
						}
						case 'COUNTERS_UPDATE': {
							console.debug('Adding new counters to DB', parsed.value);
							const tx = this.db.transaction(DB_OBJSTORE_COUNTERS_HISTORY, 'readwrite');
							const data = parsed.value;
							// noinspection JSUnresolvedVariable,JSCheckFunctionSignatures
							tx.store.add({
								newPacketsCount: data.totalPackets,
								newStreamsCount: data.totalStreams,
								servicesPackets: data.servicesPackets,
								servicesStreams: data.servicesStreams,
							}, Date.now()).then(() => {
								console.debug('[IDB] Added entry');
							}).catch(e => {
								console.error('[IDB] Failed to add entry!', e);
							});
							break;
						}
						case 'ENABLE_PATTERN': {
							this.$refs.navbar.$refs.patternsDropdown.togglePatternFromWs(parsed.value, true);
							break;
						}
						case 'DISABLE_PATTERN': {
							this.$refs.navbar.$refs.patternsDropdown.togglePatternFromWs(parsed.value, false);
							break;
						}
						default: {
							console.error('[WS] Event is not implemented!', parsed);
							break;
						}
					}
				};
				this.websocket.onerror = (ev) => {
					console.warn('[WS] Error', ev);
				};
			},
		},
		components: {
			AddPattern,
			AddService,
			EditService,
			Settings,
			Navbar,
		},
	};
</script>

<style>
	/* cyrillic-ext */
	@font-face {
		font-family: 'Open Sans';
		font-style: normal;
		font-weight: 400;
		font-display: swap;
		src: local('Open Sans Regular'), local('OpenSans-Regular'), url(https://fonts.gstatic.com/s/opensans/v16/mem8YaGs126MiZpBA-UFWJ0bbck.woff2) format('woff2');
		unicode-range: U+0460-052F, U+1C80-1C88, U+20B4, U+2DE0-2DFF, U+A640-A69F, U+FE2E-FE2F;
	}

	/* cyrillic */
	@font-face {
		font-family: 'Open Sans';
		font-style: normal;
		font-weight: 400;
		font-display: swap;
		src: local('Open Sans Regular'), local('OpenSans-Regular'), url(https://fonts.gstatic.com/s/opensans/v16/mem8YaGs126MiZpBA-UFUZ0bbck.woff2) format('woff2');
		unicode-range: U+0400-045F, U+0490-0491, U+04B0-04B1, U+2116;
	}

	/* greek-ext */
	@font-face {
		font-family: 'Open Sans';
		font-style: normal;
		font-weight: 400;
		font-display: swap;
		src: local('Open Sans Regular'), local('OpenSans-Regular'), url(https://fonts.gstatic.com/s/opensans/v16/mem8YaGs126MiZpBA-UFWZ0bbck.woff2) format('woff2');
		unicode-range: U+1F00-1FFF;
	}

	/* greek */
	@font-face {
		font-family: 'Open Sans';
		font-style: normal;
		font-weight: 400;
		font-display: swap;
		src: local('Open Sans Regular'), local('OpenSans-Regular'), url(https://fonts.gstatic.com/s/opensans/v16/mem8YaGs126MiZpBA-UFVp0bbck.woff2) format('woff2');
		unicode-range: U+0370-03FF;
	}

	/* vietnamese */
	@font-face {
		font-family: 'Open Sans';
		font-style: normal;
		font-weight: 400;
		font-display: swap;
		src: local('Open Sans Regular'), local('OpenSans-Regular'), url(https://fonts.gstatic.com/s/opensans/v16/mem8YaGs126MiZpBA-UFWp0bbck.woff2) format('woff2');
		unicode-range: U+0102-0103, U+0110-0111, U+1EA0-1EF9, U+20AB;
	}

	/* latin-ext */
	@font-face {
		font-family: 'Open Sans';
		font-style: normal;
		font-weight: 400;
		font-display: swap;
		src: local('Open Sans Regular'), local('OpenSans-Regular'), url(https://fonts.gstatic.com/s/opensans/v16/mem8YaGs126MiZpBA-UFW50bbck.woff2) format('woff2');
		unicode-range: U+0100-024F, U+0259, U+1E00-1EFF, U+2020, U+20A0-20AB, U+20AD-20CF, U+2113, U+2C60-2C7F, U+A720-A7FF;
	}

	/* latin */
	@font-face {
		font-family: 'Open Sans';
		font-style: normal;
		font-weight: 400;
		font-display: swap;
		src: local('Open Sans Regular'), local('OpenSans-Regular'), url(https://fonts.gstatic.com/s/opensans/v16/mem8YaGs126MiZpBA-UFVZ0b.woff2) format('woff2');
		unicode-range: U+0000-00FF, U+0131, U+0152-0153, U+02BB-02BC, U+02C6, U+02DA, U+02DC, U+2000-206F, U+2074, U+20AC, U+2122, U+2191, U+2193, U+2212, U+2215, U+FEFF, U+FFFD;
	}

	html * {
		font-family: 'Open Sans', Helvetica, Arial, sans-serif;
		-webkit-font-smoothing: antialiased;
		-moz-osx-font-smoothing: grayscale;
		font-size: .875rem;
	}

	body {
		overflow-y: scroll;
	}

	/*noinspection CssUnusedSymbol*/
	.fade-enter-active, .fade-leave-active {
		transition: opacity .3s;
	}

	/*noinspection CssUnusedSymbol*/
	.fade-enter, .fade-leave-to {
		opacity: 0;
	}

	/* Patterns dropdown fix */
	/*noinspection CssUnusedSymbol*/
	.dropdown-menu.show {
		position: fixed !important;
		/*transform: translate3d(15px, 38px, 0) !important;*/
		transform: none !important;
		left: 15px !important;
		top: 38px !important;
	}

	[role="main"] {
		padding-top: 55px; /* Space for fixed navbar */
	}
</style>
