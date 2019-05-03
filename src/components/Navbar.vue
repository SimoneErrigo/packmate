<template>
	<nav class="navbar navbar-dark fixed-top bg-dark flex-md-nowrap p-0 shadow navbar-expand-lg navbar-expand">
		<router-link to="/" class="navbar-brand col-sm-3 col-md-2 mr-0">Packmate</router-link>

		<div class="collapse navbar-collapse" id="navbarSupportedContent">
			<ul class="navbar-nav mr-auto">
				<CtfService
						v-for="service in ctfServices"
						:key="service.port"
						:port="service.port"
						:name="service.name"></CtfService>

				<li class="nav-item" style="margin-left: 10px;">
					<button type="button" class="btn btn-link">
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

export default {
	name: "Navbar",
	data: function () {
		return {
			ctfServices: [
				{
					port: 1337,
					name: 'ShittyService',
				},
			],
		}
	},
	components: {CtfService,},
}
</script>

<style scoped>
	.navbar-brand {
		padding-top: .75rem;
		padding-bottom: .75rem;
		font-size: 1rem;
		background-color: rgba(0, 0, 0, .25);
		box-shadow: inset -1px 0 0 rgba(0, 0, 0, .25);
	}

	.navbar .form-control {
		padding: .75rem 1rem;
		border-width: 0;
		border-radius: 0;
	}

	.form-control-dark {
		color: #fff;
		background-color: rgba(255, 255, 255, .1);
		border-color: rgba(255, 255, 255, .1);
	}

	.form-control-dark:focus {
		border-color: transparent;
		box-shadow: 0 0 0 3px rgba(255, 255, 255, .25);
	}

	svg {
		position: absolute;
		top: 50%;
		bottom: 50%;
		transform: translate(-50%, -50%);
	}
</style>
