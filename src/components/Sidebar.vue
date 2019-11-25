<template>
	<nav class="col-sm-3 d-none d-sm-block bg-light sidebar">
		<div class="m-2">
			<button type="button" class="btn btn-sm"
					:class="this.$store.state.pause ? 'btn-danger' : 'btn-outline-success'"
					@click.stop.prevent="togglePause">
				<i class="fas fa-pause"/>
			</button>
			<button type="button" class="btn btn-sm ml-1"
					:class="this.$store.state.displayFavoritesOnly ? 'btn-danger' : 'btn-outline-danger'"
					@click.stop.prevent="toggleFavorites">
				<i class="fas fa-star"/>
			</button>
			<button type="button" class="btn btn-sm btn-outline-info" style="float: right;">
				<i class="fas fa-angle-double-up"/>
			</button>
		</div>
		<div class="sidebar-sticky">
			<ul class="nav flex-column">
				<SidebarStream v-for="stream in streams"
							   :key="stream.id"
							   :stream="stream"/>
				<infinite-loading @infinite="infiniteLoadingHandler" ref="infiniteLoader"/>
			</ul>
		</div>
	</nav>
</template>

<script>
	import SidebarStream from './SidebarStream';

	export default {
		name: 'Sidebar',
		props: ['servicePort', 'streamId',],
		data() {
			return {
				streams: [],
			};
		},
		watch: {
			'$route.params.servicePort': function () {
				console.debug('Port reselected');
				this.streams = [];
				this.$refs.infiniteLoader.stateChanger.reset();
			},
			'$route.query.pattern': function () {
				console.debug('Pattern selected');
				this.streams = [];
				this.$refs.infiniteLoader.stateChanger.reset();
			},
		},
		methods: {
			infiniteLoadingHandler($state) {
				const ourStreams = this.streams;
				let startsFrom;
				if (ourStreams?.length && ourStreams[ourStreams.length - 1]) {
					startsFrom = ourStreams[ourStreams.length - 1].id;
				} else {
					startsFrom = Number.MAX_SAFE_INTEGER; // FIXME: rewrite backend?
				}

				this.$http.post(`/stream/${this.$route?.params?.servicePort || 'all'}`, {
					direction: 'DESC',
					startingFrom: startsFrom,
					pageSize: this.$store.state.pageSize,
					pattern: this.$route.query.pattern ? {id: this.$route.query.pattern,} : null,
					favorites: this.$store.state.displayFavoritesOnly,
				}).then(r => {
					const data = r.data;
					if (data?.length === 0) return $state.complete();
					if (data[0]?.id === this.streams[0]?.id) {
						return $state.complete();
					}

					this.streams.push(...data);
					return $state.loaded();
				}).catch(e => {
					this.$bvToast.toast(`Не удалось загрузить стримы: ${e}`, {
						title: 'Сбой',
						variant: 'danger',
					});
					console.error('Failed to load portion of streams:', e);
					return $state.error();
				});
			},
			toggleFavorites() {
				this.$store.commit('toggleDisplayFavoritesOnly');
				this.streams = [];
				this.$refs.infiniteLoader.stateChanger.reset();
			},
			togglePause() {
				this.$store.commit('togglePause');
			},
			addStreamFromWs(stream) {
				const currentPort = parseInt(this.$route?.params?.servicePort, 10);
				if (currentPort && currentPort !== stream.service) return;
				if (this.$store.state.displayFavoritesOnly || this.$store.state.pause) return;
				if (this.$route.query.pattern && !stream.foundPatterns.some(e => e.id === this.$route.query.pattern)) {
					return;
				}

				this.streams.unshift(stream);
			},
		},
		components: {
			SidebarStream,
		},
	};
</script>

<style scoped>
	.sidebar {
		position: fixed;
		top: 0;
		bottom: 0;
		left: 0;
		z-index: 100; /* Behind the navbar */
		padding: 40px 0 0; /* Height of navbar */
		box-shadow: inset -1px 0 0 rgba(0, 0, 0, .1);
	}

	.sidebar-sticky {
		position: relative;
		top: 0;
		height: calc(100vh - 85px);
		/*padding-top: .5rem;*/
		overflow-x: hidden;
		overflow-y: auto; /* Scrollable contents if viewport is shorter than content. */
	}

	@supports (position: sticky) {
		.sidebar-sticky {
			position: sticky;
		}
	}

</style>
