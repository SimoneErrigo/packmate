<template>
	<div>
		<div>#{{id}} at {{new Date(timestamp * 1000).toLocaleDateString('ru-RU', {month: '2-digit', day: '2-digit', hour:'2-digit', minute: '2-digit', second: '2-digit'})}}</div>
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
		new Hexdump(atob(this.$props.content), { // TODO: в настройки
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
	},
}
</script>
