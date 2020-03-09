<template>
	<nav class="navbar navbar-dark navbar-expand fixed-top bg-dark flex-md-nowrap p-0 shadow">
		<PatternsDropdown ref="patternsDropdown"/>

		<div class="navbar-collapse collapse">
			<ul class="navbar-nav px-3 mr-auto">
				<li class="nav-item text-nowrap">
					<router-link class="nav-link" to="/" exact>All</router-link>
				</li>
				<template v-for="service in services">
					<router-link :key="service.name" tag="li" class="nav-item text-nowrap edit-button"
								 :to="{name:'stream', params: {servicePort: service.port}, query: $route.query}">
						<a class="nav-link">{{service.name}} #{{service.port}}</a>

						<a class="nav-link" style="cursor: pointer" @click.stop.prevent="editService(service)">
							<i class="fas fa-pencil-alt"/>
						</a>
					</router-link>
				</template>
				<li class="nav-item text-nowrap" style="padding-left: 1em;">
					<div class="my-2 mr-3 navbar-cogs" style="cursor: pointer;"
						 @click.stop.prevent="showAddService">
						<i class="fas fa-plus-circle"/>
					</div>
				</li>
			</ul>

			<div class="my-2 my-lg-0 mr-3 navbar-cogs" style="cursor: pointer;"
				 @click.stop.prevent="showSettings">
				<i class="fas fa-cogs"/>
			</div>
		</div>
		<ServiceModal :creating="serviceModalIsCreating" :initial-service="serviceModalEditingService"
					  @service-update-needed="updateServices"/>
	</nav>
</template>

<script>
	import PatternsDropdown from './PatternsDropdown';
	import ServiceModal from '../views/ServiceModal';

	export default {
		name: 'Navbar',
		data() {
			return {
				services: Array(),

				serviceModalIsCreating: true,
				serviceModalEditingService: {},
			};
		},
		mounted() {
			this.updateServices();
		},
		methods: {
			editService(service) {
				this.serviceModalIsCreating = false;
				this.serviceModalEditingService = {};
				this.$nextTick(() => {
					this.serviceModalEditingService = service;
					this.$bvModal.show('serviceModal');
				});
			},
			showSettings() {
				this.$bvModal.show('settingsModal');
				console.debug('Showing settings...');
			},
			showAddService() {
				this.serviceModalIsCreating = true;
				this.serviceModalEditingService = {};
				this.$bvModal.show('serviceModal');
				console.debug('Showing addService...');
			},
			updateServices() {
				this.$http.get('service/')
					.then(r => this.services = r.data)
					.catch(e => {
						this.$bvToast.toast(`Failed to load services: ${e}`, {
							title: 'Error',
							variant: 'danger',
						});
						console.error('Failed to load services:', e);
					});
			},
			addServiceFromWs(service) {
				const foundIndex = this.services.findIndex(el => el.port === service.port);
				if (foundIndex === -1) {
					this.services.push(service);
					return;
				}
				this.services.splice(foundIndex, 1, service);
			},
			deleteServiceFromWs(port) {
				this.services = this.services.filter(o => o.port !== port);
			},
		},
		components: {
			ServiceModal,
			PatternsDropdown,
		},
	};
</script>

<style scoped>
	.nav-link {
		transition: all .3s;
	}

	.edit-button {
		display: inherit;
	}

	.navbar-cogs > i {
		transition: all .3s;
		color: rgba(255, 255, 255, 0.7);
	}

	.navbar-cogs > i:hover {
		color: #FFF;
	}

	nav {
		overflow-x: auto;
	}

	::-webkit-scrollbar {
		height: 0.2em
	}

	::-webkit-scrollbar-button {
		width: 0;
	}

	::-webkit-scrollbar-track-piece {
		background: #F1F1F1
	}

	::-webkit-scrollbar-thumb {
		background: #C1C1C1
	}
</style>
