<template>
	<nav class="navbar navbar-dark fixed-top bg-dark flex-md-nowrap p-0 shadow navbar-expand-lg navbar-expand">

<!--		<router-link to="/" class="navbar-brand col-sm-3 col-md-2 mr-0">Packmate</router-link>-->
		<div class="dropdown">
			<button class="btn btn-dark dropdown-toggle" type="button" data-toggle="dropdown">
				Паттерны
			</button>
			<div class="dropdown-menu">
				<div class="input-group">
					<input type="text" class="form-control" placeholder="Name" v-model="newPattern.name">
					<input type="text" class="form-control" placeholder="Value" v-model="newPattern.value">
					<input type="text" class="form-control" placeholder="#Color" v-model="newPattern.color">
					<div class="input-group-append">
						<button @click.prevent="createPattern()" class="btn btn-outline-primary" type="button">+</button>
					</div>
				</div>
				<FlagPattern v-for="pattern in patterns"
					:key="pattern.id"
					:name="pattern.name"
					:value="pattern.value"
					:color="pattern.color"></FlagPattern>
			</div>
		</div>

		<div class="collapse navbar-collapse" id="navbarSupportedContent">
			<ul class="navbar-nav mr-auto">
				<li class="nav-item" :class="{ 'active': !this.$route.params.servicePort }">
					<router-link class="nav-link" to="/">Все</router-link>
				</li>
				<CtfService
						v-for="service in ctfServices"
						:key="service.port"
						:port="service.port"
						:name="service.name"></CtfService>

				<li class="nav-item" style="margin-left: 10px;">
					<button type="button" class="btn btn-link" @click="$emit('addServiceButtonClicked')">
						<svg id="i-plus" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 32 32" width="16" height="16"
								fill="none" stroke="#ffffff" stroke-linecap="round" stroke-linejoin="round"
								stroke-width="2">
							<path d="M16 2 L16 30 M2 16 L30 16"></path>
						</svg>
					</button>
				</li>
			</ul>
			<div class="my-2 my-lg-0" style="margin-right: 10px;">
				<button type="button" class="btn btn-link" @click="$emit('settingsButtonClicked')">
					<svg id="i-settings" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 32 32" width="16" height="16"
							fill="none" stroke="#ffffff" stroke-linecap="round" stroke-linejoin="round"
							stroke-width="2">
						<path d="M13 2 L13 6 11 7 8 4 4 8 7 11 6 13 2 13 2 19 6 19 7 21 4 24 8 28 11 25 13 26 13 30 19 30 19 26 21 25 24 28 28 24 25 21 26 19 30 19 30 13 26 13 25 11 28 8 24 4 21 7 19 6 19 2 Z"></path>
						<circle cx="16" cy="16" r="4"></circle>
					</svg>
				</button>
			</div>
		</div>
	</nav>
</template>

<script>
import CtfService from "@/components/CtfService.vue";
import axios from "axios";
import $ from "jquery";
import FlagPattern from "@/components/FlagPattern.vue";

export default {
	name: "Navbar",
	data: function () {
		return {
			ctfServices: [],
			patterns: [],
			newPattern: {
				name: '',
				value: '',
				color: '',
			},
		}
	},
	methods: {
		getCtfServices() {
			const instance = axios.create({
				baseURL: this.$store.state.apiUrl,
				auth: {
					username: this.$store.state.apiLogin,
					password: this.$store.state.apiPassword,
				},
				headers: {
					'Accept': 'application/json',
					'Content-Type': 'application/json',
				},
			});
			instance.get('/service/')
				.then(response => (this.ctfServices = response.data))
				.catch(error => console.error('Failed to load CTF services!', error))
		},
		getPatterns() {
			const instance = axios.create({
				baseURL: this.$store.state.apiUrl,
				auth: {
					username: this.$store.state.apiLogin,
					password: this.$store.state.apiPassword,
				},
				headers: {
					'Accept': 'application/json',
					'Content-Type': 'application/json',
				},
			});
			instance.get('/pattern/')
				.then(response => (this.patterns = response.data))
				.catch(error => console.error('Failed to load patterns!', error));
		},
		createPattern() {
			const instance = axios.create({
				baseURL: this.$store.state.apiUrl,
				auth: {
					username: this.$store.state.apiLogin,
					password: this.$store.state.apiPassword,
				},
				headers: {
					'Accept': 'application/json',
					'Content-Type': 'application/json',
				},
			});
			instance.post('/pattern/', this.newPattern)
				.then(response => {
					console.debug('got r', response.data);
					this.getPatterns();
				})
				.catch(error => console.error('failed to create pattern', error));
		},
	},
	mounted() {
		this.getCtfServices();
		this.getPatterns();

		const dropdown = $('.dropdown');
		dropdown.on('show.bs.dropdown', function () {
			$(this).find('.dropdown-menu').first().stop(true, true).slideDown();
		});
		dropdown.on('hide.bs.dropdown', function () {
			$(this).find('.dropdown-menu').first().stop(true, true).slideUp();
		});
		$('.dropdown-menu').on("click.bs.dropdown", function (e) {
			e.stopPropagation();
			e.preventDefault();
		});
	},
	components: {FlagPattern, CtfService,},
}
</script>

<style scoped>
	li {
		margin-left: 10px;
	}

	svg {
		position: absolute;
		top: 50%;
		bottom: 50%;
		transform: translate(-50%, -50%);
	}

	.dropdown-menu {
		min-width: 300px;
	}

	.input-group {
		padding-left: 10px;
		padding-right: 10px;
		padding-bottom: 5px;
	}
</style>
