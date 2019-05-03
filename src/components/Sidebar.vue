<template>
	<nav class="col-md-2 d-none d-md-block bg-light sidebar">
		<div class="sidebar-sticky">
			<ul class="nav flex-column">
				<Stream
						v-for="stream in streams"
						:key="stream.id"
						:id="stream.id"
						:servicePort="stream.service.port"
						:time="stream.startTimestamp"></Stream>
				<infinite-loading @infinite="infiniteHandler"></infinite-loading>
			</ul>
		</div>
	</nav>
</template>

<script>
import Stream from "@/components/Stream.vue";
import InfiniteLoading from "vue-infinite-loading";
import axios from "axios";

export default {
	name: 'Sidebar',
	props: ['servicePort', 'streamId',],
	data: function () {
		return {
			streams: [
				// {
				// 	id: 111,
				// 	time: 1556411080,
				// },
			],
		}
	},
	methods: {
		infiniteHandler($state) {
			console.debug('getting next portion of streams...');
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
			instance.post(`/stream/${this.$props.servicePort || 'all'}`, {
				fetchLatest: true,
				direction: 'DESC',
				startingFrom: this.streams.length,
				pageSize: 50,
			}).then(response => {
				const data = response.data;
				if (data.length === 0) return $state.complete();
				if (data[0] && this.streams[0] && data[0].id === this.streams[0].id) { // FIXME: проверка довольно колхозная
					$state.complete();
				} else {
					this.streams.push(...data);
					$state.loaded();
				}
			}).catch((error) => {
				console.error('Failed to load new portion of streams', error);
				return $state.error();
			});
		},
	},
	components: {Stream, InfiniteLoading,},
}
</script>
<style>
	.sidebar {
		position: fixed;
		top: 0;
		bottom: 0;
		left: 0;
		z-index: 100; /* Behind the navbar */
		padding: 48px 0 0; /* Height of navbar */
		box-shadow: inset -1px 0 0 rgba(0, 0, 0, .1);
	}

	.sidebar-sticky {
		position: relative;
		top: 0;
		height: calc(100vh - 48px);
		padding-top: .5rem;
		overflow-x: hidden;
		overflow-y: auto; /* Scrollable contents if viewport is shorter than content. */
	}

	@supports ((position: -webkit-sticky) or (position: sticky)) {
		.sidebar-sticky {
			position: -webkit-sticky;
			position: sticky;
		}
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

	.sidebar-heading {
		font-size: .75rem;
		text-transform: uppercase;
	}
</style>
