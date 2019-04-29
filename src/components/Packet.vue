<template>
	<div>
		<div>#{{id}} at {{new Date(timestamp * 1000)}}</div>
		<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
			<pre :id="'hexdump-' + id"></pre>
		</div>
	</div>
</template>

<script>

import {Hexdump,} from '@/components/hexdump.js' // Why the fuck is this working, Babel?!?!

export default {
	name: 'Packet',
	props: ['id', 'content', 'timestamp',],
	mounted() {
		const instance = new Hexdump(atob(this.$props.content), {
			container: 'hexdump-' + this.$props.id,
			base: 'hex',
			width: 16,
			ascii: false,
			byteGrouping: 1,
			html: false,
			lineNumber: true,
			style: {
				lineNumberLeft: '',
				lineNumberRight: ':',
				stringLeft: '|',
				stringRight: '|',
				hexLeft: '',
				hexRight: '',
				hexNull: '.g',
				stringNull: '.',
			},
		});
		console.trace('created hexview', instance);
	},
}
</script>
