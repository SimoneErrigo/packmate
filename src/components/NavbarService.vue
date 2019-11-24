<template>
	<router-link tag="li" class="nav-item text-nowrap edit-button" :to="{name:'stream', params: {servicePort: port}}">
		<a class="nav-link">{{name}} #{{port}}</a> <!-- TODO: editing! -->

		<a class="nav-link" style="cursor: pointer"> <!-- TODO -->
			<i class="fas fa-pencil-alt"/>
		</a>
	</router-link>
</template>

<script>
	export default {
		name: 'NavbarService',
		props: {
			name: String(),
			port: Number(),
		},
	};
</script>

<style scoped>
	.nav-link {
		transition: all .3s;
	}

	.edit-button {
		display: inherit;
	}
</style>
