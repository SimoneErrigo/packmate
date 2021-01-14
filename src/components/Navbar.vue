<template>
	<nav class="navbar navbar-dark navbar-expand fixed-top bg-dark flex-md-nowrap p-0 shadow">
		<span class="navbar-brand mb-0 ml-2">Packmate</span>
		<span class="navbar-text navbar-nowrap">
      {{ this.$store.state.currentStreamsCount }}
			<u title="Streams per minute">SPM</u>
			, {{ packetsPerStream }}
			<u title="Packets per stream (average)">PPS</u>
    </span>

		<PatternsDropdown ref="patternsDropdown"/>

    <span v-if="this.$route.query.pattern" class="navbar-text navbar-nowrap">
      {{ selectedPatternText }}
    </span>

		<div class="navbar-collapse collapse">
			<ul class="navbar-nav px-1 mr-auto">
				<li class="nav-item text-nowrap">
					<router-link class="nav-link" :to="{name:'stream', params: {}, query: $route.query}" exact>All</router-link>
				</li>
				<template v-for="service in this.$store.state.services">
					<router-link :key="service.port" tag="li" class="nav-item text-nowrap edit-button"
								 :to="{name:'stream', params: {servicePort: service.port}, query: $route.query}">
						<a class="nav-link">
							{{service.name}} #{{service.port}}
							({{ getSpmForService(service.port) }}
							<u title="Streams per minute">SPM</u>)
						</a>

						<a class="nav-link pl-0" style="cursor: pointer" @click.stop.prevent="editService(service)">
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
		computed: {
			packetsPerStream: function() {
				let streams = this.$store.state.currentStreamsCount;
				let packets = this.$store.state.currentPacketsCount;

				if (streams === 0) {
					return 0;
				} else {
					let pps = packets / streams;
					return Math.round((pps + Number.EPSILON) * 10) / 10;
				}
			},
      selectedPatternText: function () {
			  let selected = this.$route.query.pattern;
			  if (typeof selected === 'string') {
			    selected = parseInt(selected);
        }

        let pattern = this.$store.state.patterns.find(o => o.id === selected);
        if (pattern) {
          return `[Selected: ${pattern.name}]`;
        } else {
          return '[Invalid pattern]';
        }
      },
		},
		data() {
			return {
				serviceModalIsCreating: true,
				serviceModalEditingService: {},
			};
		},
		mounted() {
			this.updateServices();
		},
		methods: {
			getSpmForService(port) {
				return this.$store.state.currentServicesStreamsCount[port] ?? 0;
			},
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
					.then(r => this.$store.commit('setServices', r.data))
					.catch(e => {
						this.$bvToast.toast(`Failed to load services: ${e}`, {
							title: 'Error',
							variant: 'danger',
						});
						console.error('Failed to load services:', e);
					});
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

	.navbar-nowrap {
		white-space: nowrap;
	}

	nav {
		overflow-x: auto;
	}

	u {
		text-underline-position: under;
		text-decoration-style: dotted;
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
