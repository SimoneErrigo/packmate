<template>
	<nav class="navbar navbar-dark navbar-expand fixed-top bg-dark flex-md-nowrap p-0 shadow">
		<div class="navbar-brand col-sm-3 mr-0" style="cursor: pointer;">
			Паттерны
			<i class="fas fa-angle-down" style="margin-left: 0.5em"></i>
		</div>

		<div class="navbar-collapse collapse">
			<ul class="navbar-nav px-3 mr-auto">
				<li class="nav-item text-nowrap">
					<router-link class="nav-link" to="/" exact>Все</router-link>
				</li>
				<NavbarService v-for="service in services"
							   :key="service.name"
							   :name="service.name"
							   :port="service.port"/>
			</ul>

			<div class="my-2 my-lg-0 mr-3 navbar-cogs" style="cursor: pointer;"
				 @click.stop.prevent="showSettings">
				<i class="fas fa-cogs"></i>
			</div>
		</div>
	</nav>
</template>

<script>
	import NavbarService from './NavbarService';

	export default {
		name: 'Navbar',
		data() {
			return {
				services: Array(),
			};
		},
		mounted() {
			this.updateServices();
		},
		methods: {
			showSettings() {
				this.$bvModal.show('settingsModal');
				console.debug('Showing settings...');
			},
			updateServices() {
				this.$http.get('service/')
					.then(r => this.services = r.data)
					.catch(e => {
						this.$bvToast.toast(`Не удалось загрузить сервисы: ${e}`, {
							title: 'Сбой',
							variant: 'danger',
						});
						console.error('Failed to load services:', e);
					});
			},
		},
		components: {
			NavbarService,
		},
	};
</script>

<style scoped>
	.nav-link {
		transition: all .3s;
	}

	.navbar-cogs > i {
		transition: all .3s;
		color: rgba(255, 255, 255, 0.7);
	}

	.navbar-cogs > i:hover {
		color: #FFF;
	}

	.navbar-brand {
		padding-top: .5rem;
		padding-bottom: .5rem;

		font-size: 1rem;
		background-color: rgba(0, 0, 0, .25);
		box-shadow: inset -1px 0 0 rgba(0, 0, 0, .25);
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
