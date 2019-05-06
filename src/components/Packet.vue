<template>
	<div>
		<div>#{{id}} at {{new Date(timestamp * 1000).toLocaleDateString('ru-RU', {month: '2-digit', day: '2-digit', hour:'2-digit', minute: '2-digit', second: '2-digit'})}}</div>
		<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
			<pre>{{ hexdata }}</pre>
		</div>
	</div>
</template>

<script>

// import {Hexdump,} from '@/components/hexdump.js' // Why the fuck is this working, Babel?!?!

export default {
	name: 'Packet',
	props: ['id', 'content', 'timestamp',],
	methods: {
		hexdump(buffer, blockSize) {
			blockSize = parseInt(blockSize, 10) || 16;
			let lines = [];
			const hex = "0123456789ABCDEF";
			for (let b = 0; b < buffer.length; b += blockSize) {
				let block = buffer.slice(b, Math.min(b + blockSize, buffer.length));
				let addr = ("0000" + b.toString(10)).slice(-4);
				let codes = block.split('').map(function (ch) {
					let code = ch.charCodeAt(0);
					return " " + hex[(0xF0 & code) >> 4] + hex[0x0F & code];
				}).join("");
				codes += "   ".repeat(blockSize - block.length);
				// eslint-disable-next-line no-control-regex
				let chars = block.replace(/[\x00-\x1F\x20]/g, '.');
				chars += " ".repeat(blockSize - block.length);
				lines.push(addr + " " + codes + "  " + chars);
			}
			return lines.join("\n");
		},
	},
	computed: {
		hexdata() {
			// const dataString = decodeURIComponent(escape(atob(this.$props.content)));
			const dataString = atob(this.$props.content);
			return this.hexdump(dataString, this.$store.state.hexdumpBlockSize);
		},
	},
	// mounted() {
	//
	// 	this.hex = new Hexdump(dataString, { // TODO: в настройки
	// 		container: 'hexdump-' + this.$props.id,
	// 		base: 'hex',
	// 		width: 16,
	// 		ascii: false,
	// 		byteGrouping: 1,
	// 		html: false,
	// 		// endian: 'little',
	// 		lineNumber: true,
	// 		nonPrintable: '?',
	// 		style: {
	// 			lineNumberLeft: '',
	// 			lineNumberRight: ':',
	// 			stringLeft: '|',
	// 			stringRight: '|',
	// 			hexLeft: '',
	// 			hexRight: '',
	// 			hexNull: '.g',
	// 			stringNull: ' ',
	// 		},
	// 	});
	// },
	// beforeDestroy() {
	// 	delete this.hex;
	// },
}
</script>
<style scoped>
	pre {
		font-family: "Ubuntu Mono", "Lucida Console", monospace;
		font-size: 100%;
		color: black;
	}
</style>
