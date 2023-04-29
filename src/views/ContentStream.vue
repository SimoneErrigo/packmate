<template>
	<div>
		<p>
			<span style="background: rgba(224, 246, 255, 0.4); box-shadow: 0 0 5px 5px rgba(224, 246, 255, 0.4);">Response</span>,
			<span style="background: rgba(251, 233, 231, 0.4); box-shadow: 0 0 5px 5px rgba(251, 233, 231, 0.4);">request</span>.
		</p>
		<Packet v-for="packet in packets"
				:key="packet.id"
				:packet="packet"/>
		<infinite-loading @infinite="infiniteLoadingHandler" ref="infiniteLoader"/>
	</div>
</template>

<script>
	import Packet from '../components/Packet';

	export default {
		name: 'ContentStream',
		props: ['servicePort', 'streamId',],
		data() {
			return {
				packets: [],
			};
		},
		watch: {
			'$route.params.streamId': function () {
				this.packets = [];
				this.$refs.infiniteLoader.stateChanger.reset();
			},
		},
		methods: {
			infiniteLoadingHandler($state) {
				if (!this.$route.params.streamId) return $state.complete();

				const packets = this.packets;
				const pageSize = this.$store.state.pageSize;
				let startsFrom;
				if (packets && packets.length && packets[packets.length - 1]) {
					startsFrom = packets[packets.length - 1].id;
				} else {
					startsFrom = null;
				}

				this.$http.post(`packet/${this.$route.params.streamId}`, {
					startingFrom: startsFrom,
					pageSize: pageSize,
				}).then(response => {
					const data = response.data;
					if (data.length === 0) {
						console.log('Finished loading packets (empty page)');
						return $state.complete();
					}

					if (data[0] && this.packets[0] && data[0].id === this.packets[0].id) {
						console.log('Finished loading packets (overlap detected)');
						return $state.complete();
					}

					this.packets.push(...data);

					if (data.length < pageSize) {
						// this was the last page
						console.log('Finished loading packets (last page was not full)');
						$state.complete();
					} else {
						console.log('Loaded another page of packets');
						$state.loaded();
					}
				}).catch(e => {
					this.$bvToast.toast(`Failed to load portion of packets: ${e}`, {
						title: 'Error',
						variant: 'danger',
					});
					console.error('Failed to load portion of packets:', e);
					return $state.error();
				});
			},
		},
		components: {
			Packet,
		},
	};
</script>
