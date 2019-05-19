<template>
	<nav class="col-md-2 d-md-block bg-light sidebar">
		<div class="sidebar-sticky">
			<ul class="nav flex-column">
				<Stream
						v-for="stream in streams"
						:key="stream.id"
						:id="stream.id"
						:proto="stream.protocol"
						:servicePort="stream.service.port"
						:foundPatterns="stream.foundPatterns"
						:startTime="stream.startTimestamp"
						:endTime="stream.endTimestamp"
						:favorite="stream.favorite"></Stream>
				<infinite-loading @infinite="infiniteHandler" spinner="waveDots" ref="infiniteLoading"></infinite-loading>
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
			streams: [],
			navigationKeysCallback: (e) => {
				if (e.key === 'ArrowUp') {
					e.preventDefault();
					const index = this.$data.streams.findIndex(e => e.id === this.$props.streamId);
					const newStream = this.$data.streams[index - 1];
					if (!newStream) return;
					const newId = newStream.id;
					this.$router.push({
						name: 'home',
						params: {servicePort: this.$props.servicePort, streamId: newId,},
					});
					document.getElementById(`stream-${newId}`).scrollIntoView({
						behavior: 'smooth',
						block: 'center',
						inline: 'center',
					});
				} else if (e.key === 'ArrowDown') {
					e.preventDefault();
					const index = this.$data.streams.findIndex(e => e.id === this.$props.streamId);
					const newStream = this.$data.streams[index + 1];
					if (!newStream) return;
					const newId = newStream.id;
					this.$router.push({
						name: 'home',
						params: {servicePort: this.$props.servicePort, streamId: newId,},
					});
					document.getElementById(`stream-${newId}`).scrollIntoView({
						behavior: 'smooth',
						block: 'center',
						inline: 'center',
					});
				} else if (e.key === 'Home') {
					const newStream = this.$data.streams[0];
					if (!newStream) return;
					const newId = newStream.id;
					this.$router.push({
						name: 'home',
						params: {servicePort: this.$props.servicePort, streamId: newId,},
					});
					document.getElementById(`stream-${newId}`).scrollIntoView({
						behavior: 'smooth',
						block: 'center',
						inline: 'center',
					});
				} else if (e.key === 'End') {
					const newStream = this.$data.streams[this.$data.streams.length - 1];
					if (!newStream) return;
					const newId = newStream.id;
					this.$router.push({
						name: 'home',
						params: {servicePort: this.$props.servicePort, streamId: newId,},
					});
					document.getElementById(`stream-${newId}`).scrollIntoView({
						behavior: 'smooth',
						block: 'center',
						inline: 'center',
					});
				}
			},
		}
	},
	watch: {
		'servicePort': function () {
			this.rerender();
		},
		'$route.query.pId': function () {
			this.rerender();
		},
	},
	methods: {
		infiniteHandler($state) {
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

			const streams = this.streams;
			let startsFrom;
			if (streams && streams.length && streams[streams.length - 1]) {
				startsFrom = streams[streams.length - 1].id;
			} else {
				startsFrom = Number.MAX_SAFE_INTEGER; // FIXME -_-
			}

			instance.post(`/stream/${this.$props.servicePort || 'all'}`, {
				direction: 'DESC',
				startingFrom: startsFrom,
				pageSize: parseInt(this.$store.state.pageSize),
				pattern: this.$route.query.pId ? {id: this.$route.query.pId,} : null,
				favorites: this.$store.state.displayFavOnly,
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
		onGotNewStream(stream) {
			if (this.streams.length > this.$store.state.pageSize * 2) {
				this.streams.pop();
			}
			this.streams.unshift(stream);
		},
		rerender() {
			this.streams = [];
			this.$refs.infiniteLoading.stateChanger.reset();
		},
	},
	components: {Stream, InfiniteLoading,},
	mounted() {
		document.addEventListener('keydown', this.$data.navigationKeysCallback);
	},
	beforeDestroy() {
		document.removeEventListener('keydown', this.$data.navigationKeysCallback);
	},
}
</script>
<style>
	.sidebar {
		display: block !important;
		flex: 0 0 16.666667%;
		max-width: 20.666667%;
		position: fixed !important;
		top: 0;
		bottom: 0;
		left: 0;
		z-index: 100; /* Behind the navbar */
		padding: 40px 0 0 0 !important;
		box-shadow: inset -1px 0 0 rgba(0, 0, 0, .1);
		height: 100%;
	}

	.sidebar-sticky {
		position: relative;
		top: 0;
		height: calc(100vh - 40px);
		/*padding-top: .5rem;*/
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
