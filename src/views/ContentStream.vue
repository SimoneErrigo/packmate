<template>
	<div>
		<p>
			<span style="background: rgba(224, 246, 255, 0.4); box-shadow: 0 0 5px 5px rgba(224, 246, 255, 0.4);">Ответ</span>,
			<span style="background: rgba(251, 233, 231, 0.4); box-shadow: 0 0 5px 5px rgba(251, 233, 231, 0.4);">запрос</span>.
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
				packets: [
					/*
					{
						"id": 17,
						"matches": [],
						"timestamp": 1574630325546,
						"incoming": false,
						"ungzipped": false,
						"content": "shit in b64=="
					} */
				],
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
				let startsFrom;
				if (packets && packets.length && packets[packets.length - 1]) {
					startsFrom = packets[packets.length - 1].id;
				} else {
					startsFrom = 0;
				}

				this.$http.post(`packet/${this.$route.params.streamId}`, {
					direction: 'ASC',
					startingFrom: startsFrom,
					pageSize: this.$store.state.pageSize,
				}).then(response => {
					const data = response.data;
					if (data.length === 0) return $state.complete();
					if (data[0] && this.packets[0] && data[0].id === this.packets[0].id) {
						$state.complete();
					} else {
						this.packets.push(...data);
						$state.loaded();
					}
				}).catch(e => {
					this.$bvToast.toast(`Не удалось загрузить пакеты: ${e}`, {
						title: 'Сбой',
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

<style scoped>

</style>
