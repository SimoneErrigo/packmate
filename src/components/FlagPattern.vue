<template>
	<div class="dropdown-item" :style="'color:' + color"
			@click="openPattern">
		{{name}}: {{regex ? `/${value}/` : `'${value}'`}}
		<div class="float-right">
			<button @click.prevent.stop="deleteSelf()" class="btn btn-outline-danger btn-block btn-sm" type="button">x</button>
		</div>
	</div>
</template>

<script>
import axios from "axios";
import $ from "jquery";

export default {
	name: "FlagPattern",
	props: ['name', 'value', 'color', 'id', 'regex',],
	methods: {
		openPattern() {
			this.$router.push({name: 'home', params: this.$route.params, query: {pId: this.id,},});
			// $('.dropdown').trigger('hide.bs.dropdown')
			$('.dropdown-toggle').dropdown('toggle')
		},
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
				.then(() => {
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
