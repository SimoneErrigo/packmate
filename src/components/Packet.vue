<template>
	<div class="packet" :class="{'packet-incoming': isIncoming}">
		<div>#{{id}} at {{new Date(timestamp).toLocaleDateString('ru-RU', {month: '2-digit', day: '2-digit', hour:'2-digit', minute: '2-digit', second: '2-digit'})}}</div>
		<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-2 pb-2 mb-3 border-bottom">
			<pre>{{ hexdata }}</pre>
		</div>
	</div>
</template>

<script>

export default {
	name: 'Packet',
	props: ['id', 'content', 'timestamp', 'isIncoming',],
	methods: {
		hexdump(buffer, blockSize, lineNumberBase) {
			blockSize = parseInt(blockSize, 10) || 16;
			lineNumberBase = parseInt(lineNumberBase, 10) || 10;
			let lines = [];
			const hex = "0123456789ABCDEF";
			for (let b = 0; b < buffer.length; b += blockSize) {
				let block = buffer.slice(b, Math.min(b + blockSize, buffer.length));
				let addr = ("0000000000" + b.toString(lineNumberBase)).slice(-10);
				let codes = block.split('').map(ch => {
					let code = ch.charCodeAt(0);
					return " " + hex[(0xF0 & code) >> 4] + hex[0x0F & code];
				}).join("");
				codes += " ..".repeat(blockSize - block.length);
				// eslint-disable-next-line no-control-regex
				let chars = block.replace(/[\x00-\x1F]/g, '.');
				chars += " ".repeat(blockSize - block.length);
				lines.push(addr + ":" + codes + " |" + chars + "|");
			}
			return lines.join("\n");
		},
	},
	computed: {
		hexdata() {
			// const dataString = decodeURIComponent(escape(atob(this.$props.content)));
			const dataString = atob(this.$props.content);
			return this.hexdump(dataString, this.$store.state.hexdumpBlockSize, this.$store.state.hexdumpLineNumberBase);
		},
	},
}
</script>
<style scoped>
	pre {
		font-family: "Ubuntu Mono", "Lucida Console", monospace;
		font-size: 100%;
		color: black;
		margin-bottom: 10px;
		padding-bottom: 5px;
	}
	.packet {
		background: rgba(224, 246, 255, 0.4);
		box-shadow: 0 0 5px 5px rgba(224, 246, 255, 0.4);
	}
	.packet-incoming {
		background: rgba(251, 233, 231, 0.4);
		box-shadow: 0 0 5px 5px rgba(251, 233, 231, 0.4);
	}
</style>
