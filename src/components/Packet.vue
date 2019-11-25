<template>
	<div class="packet" :class="{'packet-incoming': packet.incoming}">
		<div>#{{ packet.id }} at {{ dateToText(packet.timestamp) }}, {{ packet.ungzipped ? 'GZIP' : 'Non-GZIP' }}</div>
		<code class="d-flex pt-2 pb-2 mb-3 border-bottom"
			  v-html="base64ToHtml(packet.content)"/>
	</div>
</template>

<script>
	export default {
		name: 'Packet',
		props: {
			packet: {
				id: Number(),
				matches: Array(),
				timestamp: Number(),
				incoming: Boolean(),
				ungzipped: Boolean(),
				content: String(),
			},
		},
		methods: {
			dateToText(unixTimestamp) {
				return new Date(unixTimestamp).toLocaleDateString('ru-RU', {
					month: '2-digit',
					day: '2-digit',
					hour: '2-digit',
					minute: '2-digit',
					second: '2-digit',
				});
			},
			escapeHtml(in_) {
				return in_.replace(/[&<>"'/]/g, s => {
					const entityMap = {
						'&': '&amp;',
						'<': '&lt;',
						'>': '&gt;',
						'"': '&quot;',
						'\'': '&#39;',
						'/': '&#x2F;',
					};

					return entityMap[s];
				});
			},
			base64ToHtml(in_) {
				return this.escapeHtml(atob(in_)).split('\n').join('<br/>'); // Replace all \n to <br/>
			},
		},
	};
</script>

<style scoped>
	.packet {
		background: rgba(224, 246, 255, 0.4);
		box-shadow: 0 0 5px 5px rgba(224, 246, 255, 0.4);
	}

	/*noinspection CssUnusedSymbol*/
	.packet-incoming {
		background: rgba(251, 233, 231, 0.4);
		box-shadow: 0 0 5px 5px rgba(251, 233, 231, 0.4);
	}

	code {
		font-family: "Ubuntu Mono", "Lucida Console", monospace;
		font-size: 100%;
		color: black;
		margin-bottom: 10px;
		padding-bottom: 5px;
	}
</style>
