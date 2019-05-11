<template>
	<li class="nav-item">
		<router-link class="nav-link" active-class="active" :to="{ name: 'home', params: { servicePort: servicePort, streamId: id} }">
			#{{id}} {{proto}}<br/>
			{{new Date(startTime).toLocaleDateString('ru-RU', {month: '2-digit', day: '2-digit', hour:'2-digit', minute: '2-digit', second: '2-digit'})}}
			- {{new Date(endTime).toLocaleTimeString('ru-RU', {hour:'2-digit', minute: '2-digit', second: '2-digit'})}}
			<span v-for="pattern in foundPatterns"
					:key="pattern.id"
					:style="'color:' + pattern.color"
				>
				{{pattern.name}}
			</span>
		</router-link>
	</li>
</template>

<script>
export default {
	name: "Stream",
	props: ['id', 'proto', 'startTime', 'endTime', 'servicePort', 'foundPatterns',],
}
</script>

<style scoped>
	.sidebar .nav-link {
		font-weight: 500;
		color: #333;
	}

	.sidebar .nav-link .feather {
		margin-right: 4px;
		color: #999;
	}

	.sidebar .nav-link.active {
		color: #007bff;
	}

	.sidebar .nav-link:hover .feather,
	.sidebar .nav-link.active .feather {
		color: inherit;
	}
</style>
