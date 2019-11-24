<template>
	<router-link tag="li" class="nav-item text-nowrap edit-button" :to="{name:'stream', params: {servicePort: port}}">
		<a class="nav-link">{{name}} #{{port}}</a> <!-- TODO: editing! -->

		<a class="nav-link" style="cursor: pointer" @click.stop.prevent="editService">
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
		methods: {
			editService() {
				// Костыль: нельзя запустить BModal с аргументом
				this.$store.commit('setServiceModalId', this.port);
				this.$store.commit('setServiceModalName', this.name);
				this.$bvModal.show('editServiceModal');
			},
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
