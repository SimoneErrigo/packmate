<template>
	<div>
		<Navbar @settingsButtonClicked="showSettingsModal = true" @addServiceButtonClicked="showAddServiceModal = true" ref="navbar"></Navbar>
		<div class="container-fluid">
			<div class="row">
				<Sidebar ref="sidebar" :servicePort="servicePort" :streamId="streamId"></Sidebar>
				<!--suppress HtmlDeprecatedTag -->
				<Content :servicePort="servicePort" :streamId="streamId" @displayFavChanged="reloadSidebar()"></Content>
			</div>
		</div>

		<transition name="modal">
			<SettingsModal v-if="showSettingsModal" @close="showSettingsModal = false"></SettingsModal>
		</transition>
		<transition name="modal">
			<AddServiceModal v-if="showAddServiceModal"
					@serviceAddDone="$refs.navbar.ctfServices.push($event); showAddServiceModal = false"
					@close="showAddServiceModal = false"></AddServiceModal>
		</transition>
	</div>
</template>

<script>

import SettingsModal from "@/components/SettingsModal.vue";
import Navbar from "@/components/Navbar.vue";
import Sidebar from "@/components/Sidebar.vue";
import Content from "@/components/Content.vue";
import AddServiceModal from "@/components/AddServiceModal.vue";
import SockJS from "sockjs-client";

export default {
	name: 'Home',
	data: function () {
		return {
			showSettingsModal: false,
			showAddServiceModal: false,
			socket: null,
			navbarTimer: null,
		}
	},
	props: ['servicePort', 'streamId',],
	methods: {
		reloadSidebar() {
			this.$refs.sidebar.rerender();
		},
		reloadNavbar() {
			this.$refs.navbar.getCtfServices();
			this.$refs.navbar.getPatterns();
		},
		connectWs() {
			const wsUrl = this.$store.state.apiUrl + '/ws';
			// FIXME: нельзя посылать заголовок авторизации (может появиться окно с авторизацией, что не круто),
			// FIXME: нужно либо убрать его на эндпоинте, либо использовать другой способ
			// FIXME: (https://groups.google.com/forum/#!topic/sockjs/4gmZy8XZFIY)
			this.socket = new SockJS(wsUrl);
			this.socket.onopen = function () {
				console.info('WS connected');
			};
			this.socket.onclose = function (ev) {
				console.info('WS disconnected', ev.code, ev.reason);
				if (ev.code === 1008) { // FIXME: выключить security timeout (This connection was established under an authenticated HTTP session that has ended.)
					console.info('Security timeout, reconnecting...');
					this.connectWs();
				}
			};
			this.socket.onmessage = ev => {
				const parsed = JSON.parse(ev.data);
				const currentPort = parseInt(this.$route.params.servicePort, 10);
				if (!(currentPort === undefined || currentPort === parsed.service.port)) {
					return;
				}
				this.$refs.sidebar.onGotNewStream(parsed);
			};
			this.socket.onerror = function (ev) {
				console.warn('WS err', ev);
			};
		},
	},
	mounted() {
		this.connectWs();
		this.navbarTimer = setInterval(this.reloadNavbar, 5000); // TODO: некрасиво, лучше ловить через WS
	},
	beforeDestroy() {
		this.socket.close();
		clearInterval(this.navbarTimer);
	},
	components: {
		AddServiceModal,
		SettingsModal,
		Navbar,
		Sidebar,
		Content,
	},
}
</script>

<style scoped>
	.modal-enter {
		opacity: 0;
	}

	.modal-leave-active {
		opacity: 0;
	}

	.modal-enter .modal-container,
	.modal-leave-active .modal-container {
		-webkit-transform: scale(1.1);
		transform: scale(1.1);
	}
</style>
