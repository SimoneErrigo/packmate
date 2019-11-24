<template>
	<div>
		<p>
			Some stream #{{ $route.params.streamId }} at port #{{ $route.params.servicePort }} <br/>
			<span style="background: rgba(224, 246, 255, 0.4); box-shadow: 0 0 5px 5px rgba(224, 246, 255, 0.4);">Ответ</span>,
			<span style="background: rgba(251, 233, 231, 0.4); box-shadow: 0 0 5px 5px rgba(251, 233, 231, 0.4);">запрос</span>.
		</p>
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
