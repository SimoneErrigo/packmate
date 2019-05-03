<template>
	<li class="nav-item" :class="{ 'active': subIsActive('/' + port) }">
		<router-link class="nav-link" :to="{name: 'home', params: {servicePort: port}}">{{ name }} ({{ port }})
		</router-link>
	</li>
</template>

<script>
export default {
	name: "CtfService",
	methods: {
		subIsActive(prefix) {
			const paths = Array.isArray(prefix) ? prefix : [prefix,];
			return paths.some(path => {
				return this.$route.path.indexOf(path) === 0;
			})
		},
	},
	props: ['port', 'name',],
}
</script>

<style scoped>
li {
	margin-left: 10px;
}
</style>
