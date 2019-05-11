<template>
	<div class="dropdown-item" :style="'color:' + color">
		{{name}}: {{value}}
	</div>
</template>

<script>
export default {
	name: "FlagPattern",
	props: ['name', 'value', 'color',],
}
</script>

<style scoped>

</style>
