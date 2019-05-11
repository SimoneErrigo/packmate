<template>
	<div class="dropdown-item" :style="'color:' + color">
		{{name}}: {{value}}
		<div class="float-right">
			<button @click.prevent="deleteSelf()" class="btn btn-outline-danger btn-block btn-sm" type="button">x</button>
		</div>
	</div>
</template>

<script>
import axios from "axios";

export default {
	name: "FlagPattern",
	props: ['name', 'value', 'color', 'id',],
	methods: {
		deleteSelf() {
			const instance = axios.create({
				baseURL: this.$store.state.apiUrl,
				auth: {
					username: this.$store.state.apiLogin,
					password: this.$store.state.apiPassword,
				},
				headers: {
					'Accept': 'application/json',
					'Content-Type': 'application/json',
				},
			});
			instance.delete(`/pattern/${this.id}`)
				.then(response => {
					console.debug('got r', response.data);
					this.$destroy();
					this.$el.parentNode.removeChild(this.$el); // FIXME: может сломать Vue, лучше бы это убрать...
				})
				.catch(error => console.error('failed to delete pattern', error));
		},
	},
}
</script>

<style scoped>
	button {
		position: relative;
		top: -4px;
	}
</style>
