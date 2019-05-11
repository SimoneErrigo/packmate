<template>
	<main class="col-md-9 ml-sm-auto col-lg-10 px-4">
			<Packet v-for="packet in packets"
					:key="packet.id"
					:id="packet.id"
					:content="packet.content"
					:timestamp="packet.timestamp"></Packet>
		<infinite-loading @infinite="infiniteHandler" spinner="waveDots" ref="infiniteLoading"></infinite-loading>
	</main>
</template>

<script>
import Packet from "@/components/Packet.vue";
import InfiniteLoading from "vue-infinite-loading";
import axios from "axios";

export default {
	name: 'Content',
	props: ['servicePort', 'streamId',],
	data: function () {
		return {
			packets: [],
		}
	},
	watch: {
		'streamId': function (streamId) {
			console.debug('Content.vue: streamId changed! new:', streamId);
			this.packets = [];
			this.$refs.infiniteLoading.stateChanger.reset();
		},
	},
	methods: {
		infiniteHandler($state) {
			if (!this.streamId) return $state.complete();
			console.debug('getting next portion of packets...');
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

			const packets = this.packets;
			let startsFrom;
			if (packets && packets.length && packets[packets.length - 1]) {
				startsFrom = packets[packets.length - 1].id;
			} else {
				startsFrom = 0;
			}

			instance.post(`/packet/${this.streamId}`, {
				fetchLatest: false,
				direction: 'ASC',
				startingFrom: startsFrom,
				pageSize: parseInt(this.$store.state.pageSize),
			}).then(response => {
				const data = response.data;
				if (data.length === 0) return $state.complete();
				if (data[0] && this.packets[0] && data[0].id === this.packets[0].id) {
					$state.complete();
				} else {
					this.packets.push(...data);
					$state.loaded();
				}
			}).catch(error => {
				console.error('Failed to load new portion of packets', error);
				return $state.error();
			})
		},
	},
	components: {Packet, InfiniteLoading,},
}
</script>

<style scoped>
	main {
		padding-top: 50px; /* Space for fixed navbar */
		margin: 0 !important;
		max-width: 83%;
	}
</style>
