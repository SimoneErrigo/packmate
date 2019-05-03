<template>
	<main class="col-md-9 ml-sm-auto col-lg-10 px-4">

			<Packet v-for="packet in packets"
					:key="packet.id"
					:id="packet.id"
					:content="packet.content"
					:timestamp="packet.timestamp"></Packet>
	</main>
</template>

<script>
import Packet from "@/components/Packet.vue";

export default {
	name: 'Content',
	props: ['servicePort', 'streamId',],
	data: function () {
		return {
			packets: [
				{
					id: 39,
					timestamp: 1556555231394,
					content: 'wuAAUEtPbKUAAAAAoAL///jUAAACBAW0BAIIChMcPpEAAAAAAQMDBw==',
				},
				{
					id: 40,
					timestamp: 1556555231392,
					content: 'AFDC4CVNkpxLT2ymoBJxIIKHAAACBAW0BAIICoTZHMUTHD6RAQMDBw==',
				},
			],
		}
	},
	components: {Packet,},
}
</script>

<style scoped>
	main {
		padding-top: 60px; /* Space for fixed navbar */
	}
</style>
