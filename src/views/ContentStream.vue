<template>
	<div>
		Some stream #{{ streamId }} at port #{{ servicePort }}
	</div>
</template>

<script>
	export default {
		props: ['servicePort', 'streamId',],
		name: 'ContentStream',
	};
</script>

<style scoped>

</style>
