<template>
	<div>
		<Navbar @settingsButtonClicked="showSettingsModal = true" @addServiceButtonClicked="showAddServiceModal = true" ref="navbar"></Navbar>
		<div class="container-fluid">
			<div class="row">
				<Sidebar :servicePort="servicePort" :streamId="streamId"></Sidebar>
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

export default {
	name: 'Home',
	data: function () {
		return {
			showSettingsModal: false,
			showAddServiceModal: false,
		}
	},
	props: ['servicePort', 'streamId',],
	methods: {
		reloadNavbar() {
			this.$refs.navbar.getCtfServices();
		},
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
