<template>
	<nav class="col-sm-3 d-none d-sm-block bg-light sidebar">
		<div class="m-2">
			<button type="button" class="btn btn-sm btn-outline-success">
				<i class="fas fa-pause"></i> <!-- TODO! -->
			</button>
			<button type="button" class="btn btn-sm ml-1"
					:class="this.$store.state.displayFavoritesOnly ? 'btn-danger' : 'btn-outline-danger'"
					@click.stop.prevent="toggleFavorites">
				<i class="fas fa-star"></i>
			</button>
			<button type="button" class="btn btn-sm btn-outline-info" style="float: right;">
				<i class="fas fa-angle-double-up"></i>
			</button>
		</div>
		<div class="sidebar-sticky">
			<ul class="nav flex-column">
				<!--				<li class="nav-item">-->
				<!--					<a class="nav-link" href="#">SOME ITEM</a>-->
				<!--				</li>-->
				<!--				<li class="nav-item">-->
				<!--					<a class="nav-link" href="#">SOME ITEM</a>-->
				<!--				</li>-->
				<!--				<li class="nav-item">-->
				<!--					<a class="nav-link" href="#">SOME ITEM</a>-->
				<!--				</li>-->
				<!--				<li class="nav-item">-->
				<!--					<a class="nav-link" href="#">SOME ITEM</a>-->
				<!--				</li>-->
				<SidebarStream v-for="stream in streams"
							   :key="stream.id"
							   :stream="stream"></SidebarStream>
				<infinite-loading @infinite="infiniteLoadingHandler" ref="infiniteLoader"></infinite-loading>
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
		methods: {
			infiniteLoadingHandler($state) {
				const ourStreams = this.streams;
				let startsFrom;
				if (ourStreams?.length && ourStreams[ourStreams.length - 1]) {
					startsFrom = ourStreams[ourStreams.length - 1].id;
				} else {
					startsFrom = Number.MAX_SAFE_INTEGER; // FIXME: rewrite backend?
				}

				this.$http.post(`/stream/${this.$props.servicePort || 'all'}`, {
					direction: 'DESC',
					startingFrom: startsFrom,
					pageSize: this.$store.state.pageSize,
					pattern: null, // TODO
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
					console.error('Failed to load portion of streams:', e);
					return $state.error();
				});
			},
			toggleFavorites() {
				this.$store.commit('toggleDisplayFavoritesOnly');
			},
		},
		components: {
			SidebarStream,
			// InfiniteLoading,
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
