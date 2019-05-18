<template>
	<main class="col-md-9 ml-sm-auto col-lg-10 px-4">
		<div class="custom-control custom-switch" style="margin-bottom: 10px;">
			<input type="checkbox" class="custom-control-input" id="favSwitch" v-model="displayFavOnly">
			<label class="custom-control-label" for="favSwitch">Только избранные</label>
		</div>
		<p>
			<span style="background: rgba(224, 246, 255, 0.4); box-shadow: 0 0 5px 5px rgba(224, 246, 255, 0.4);">Ответ</span>, <span style="background: rgba(251, 233, 231, 0.4); box-shadow: 0 0 5px 5px rgba(251, 233, 231, 0.4);">запрос</span>.
		</p>
			<Packet v-for="packet in packets"
					:key="packet.id"
					:id="packet.id"
					:content="packet.content"
					:timestamp="packet.timestamp"
					:isIncoming="packet.incoming"></Packet>
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
		'streamId': function () {
			this.packets = [];
			this.$refs.infiniteLoading.stateChanger.reset();
		},
	},
	methods: {
		infiniteHandler($state) {
			if (!this.streamId) return $state.complete();
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
	computed: {
		displayFavOnly: {
			get() {
				return this.$store.state.displayFavOnly;
			},
			set(value) {
				this.$store.commit('setDisplayFavOnly', value);
				this.$emit('displayFavChanged');
			},
		},
	},
	components: {Packet, InfiniteLoading,},
}
</script>

<style scoped>
	main {
		padding-top: 50px; /* Space for fixed navbar */
		max-width: 80%;
		margin-left: auto !important;
	}
</style>
