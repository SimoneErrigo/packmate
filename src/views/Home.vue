<template>
	<div>
		<Navbar @settingsButtonClicked="showSettingsModal = true" @addServiceButtonClicked="showAddServiceModal = true" ref="navbar"></Navbar>
		<div class="container-fluid">
			<div class="row">
				<Sidebar ref="sidebar" :servicePort="servicePort" :streamId="streamId"></Sidebar>
				<Content :servicePort="servicePort" :streamId="streamId"></Content>
			</div>
		</div>

		<transition name="modal">
			<SettingsModal v-if="showSettingsModal" @close="showSettingsModal = false"></SettingsModal>
		</transition>
		<transition name="modal">
			<AddServiceModal v-if="showAddServiceModal" @serviceAddDone="reloadNavbar(); showAddServiceModal = false" @close="showAddServiceModal = false"></AddServiceModal>
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
		}
	},
	props: ['servicePort', 'streamId',],
	methods: {
		reloadNavbar() {
			this.$refs.navbar.getCtfServices();
		},
		connectWs() {
			const wsUrl = this.$store.state.apiUrl + '/ws';
			this.socket = new SockJS(wsUrl);
			this.socket.onopen = function () {
				console.debug('WS connected');
			};
			this.socket.onclose = function (ev) {
				console.debug('WS disconnected', ev.code, ev.reason);
				if (ev.code === 1008) {
					console.info('Security timeout, reconnecting...');
					this.connectWs();
				}
			};
			this.socket.onmessage = ev => {
				const parsed = JSON.parse(ev.data);
				console.debug('WS got message', parsed);
				const currentPort = this.$route.params.servicePort;
				if (!(currentPort === undefined || currentPort === parsed.service.port)) {
					console.debug('not related port, skipping...');
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
	},
	beforeDestroy() {
		this.socket.close();
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
