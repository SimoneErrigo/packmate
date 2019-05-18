<template>
	<li :id="`stream-${id}`" class="nav-item" :class="{highlight: favorite0}" style="transition: all .5s;">
		<router-link class="nav-link" active-class="active"
				:to="{ name: 'home', params: { servicePort: servicePort, streamId: id}, query: $route.query }">
			<button class="btn-text" @click.prevent="toggleStar">
				<svg xmlns="http://www.w3.org/2000/svg"
						viewBox="0 0 32 32" width="16" height="16" fill="none" class="star" stroke-linecap="round"
						stroke-linejoin="round" stroke-width="2">
					<path d="M16 2 L20 12 30 12 22 19 25 30 16 23 7 30 10 19 2 12 12 12 Z" style="transition: all .5s;" :fill="favorite0 ? 'red' : 'white'"></path>
				</svg>
			</button>
			#{{id}} {{proto}}<br/>
			{{new Date(startTime).toLocaleDateString('ru-RU', {month: '2-digit', day: '2-digit', hour:'2-digit', minute: '2-digit', second: '2-digit'})}}
			- {{new Date(endTime).toLocaleTimeString('ru-RU', {hour:'2-digit', minute: '2-digit', second: '2-digit'})}}
			<span v-for="pattern in foundPatterns"
					:key="pattern.id"
					:style="'color:' + pattern.color"
				>
				{{pattern.name}}
			</span>
		</router-link>
	</li>
</template>

<script>
import axios from "axios";

export default {
	name: "Stream",
	props: ['id', 'proto', 'startTime', 'endTime', 'servicePort', 'foundPatterns', 'favorite',],
	data: function () {
		return {
			favorite0: this.favorite,
		}
	},
	methods: {
		toggleStar() {
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
			instance.post(`/stream/${this.id}/${this.favorite ? 'unfavorite' : 'favorite'}`)
				.then(() => this.favorite0 = !this.favorite0)
				.catch(e => console.error('Failed to toggle favorite!', e));

		},
	},
}
</script>

<style scoped>
	button {
		padding: 0;
		top: -0.1em;
		position: relative;
	}

	.sidebar .nav-link {
		font-weight: 500;
		color: #333;
	}

	.sidebar .nav-link .feather {
		margin-right: 4px;
		color: #999;
	}

	.sidebar .nav-link.active {
		color: #007bff;
	}

	.sidebar .nav-link:hover .feather,
	.sidebar .nav-link.active .feather {
		color: inherit;
	}

	.btn-text {
		border: none;
		background-color: inherit;
		/*padding: 14px 28px;*/
		/*font-size: 16px;*/
		cursor: pointer;
		display: inline-block;
	}

	.star {
		stroke: #bfbfbf;
		transition: all .5s;
	}

	.star:hover {
		stroke: red;
	}

	.highlight {
		background: rgba(255, 119, 182, 0.3);
		box-shadow: 0 0 1px 1px rgba(255, 119, 182, 0.3);
	}
</style>
