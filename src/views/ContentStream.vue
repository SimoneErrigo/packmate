<template>
	<div>
		<p>
			<span style="background: var(--request-packet-color); box-shadow: 0 0 3px 3px var(--request-packet-color);">Request</span>,
			<span style="background: var(--response-packet-color); box-shadow: 0 0 3px 3px var(--response-packet-color);">response</span>.
			<ThemeButton class="d-inline float-right" style="margin-top: -0.25rem" />
		</p>
		<Packet v-for="packetWithOffset in packetsWithOffsets"
				:key="packetWithOffset.packet.id"
				:packet="packetWithOffset.packet"
				:offset="packetWithOffset.offset" />
		<infinite-loading @infinite="infiniteLoadingHandler" ref="infiniteLoader">
			<span slot="no-results"></span>
		</infinite-loading>
	</div>
</template>

<script>
	import Packet from '../components/Packet';
	import ThemeButton from '@/components/ThemeButton.vue';

	export default {
		name: 'ContentStream',
		props: ['servicePort', 'streamId',],
		data() {
			return {
				packets: [],
			};
		},
		computed: {
			packetsWithOffsets: function() {
				return this.packets.map((el, i) => {
					let offset = null;

					if (i !== 0) {
						offset = el.timestamp - this.packets[i - 1].timestamp;
					}

					return {packet: el, offset: offset,}
				})
			},
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
			ThemeButton,
			Packet,
		},
	};
</script>

<style>
	:root {
		--response-packet-color: rgb(231, 248, 253);
		--request-packet-color: rgb(254, 234, 231);
	}

	:root [data-theme=dark] {
		color-scheme: dark;
		--response-packet-color: rgb(43, 83, 120);
		--request-packet-color: rgb(52, 58, 64);
	}
</style>
