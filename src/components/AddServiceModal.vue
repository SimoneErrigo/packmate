<template>
	<div class="modal-mask">
		<div class="modal-wrapper">
			<div class="modal-container">
				<form>
					<div class="form-group">
						<label for="inputServiceName">Имя сервиса</label>
						<input type="text" class="form-control" id="inputServiceName" v-model="service.name">
					</div>
					<div class="form-group">
						<label for="inputServicePort">Порт</label>
						<input type="number" class="form-control" id="inputServicePort" v-model="service.port">
					</div>
					<button type="submit" @click.prevent="sendNewService()" class="btn btn-primary" style="margin-right: 10px;">
						Сохранить
					</button>
					<button type="submit" @click.prevent="$emit('close')" class="btn btn-secondary">
						Закрыть
					</button>
				</form>
			</div>
		</div>
	</div>
</template>

<script>
import axios from "axios";

export default {
	name: "AddServiceModal",
	data: function () {
		return {
			escapeKeyCallback: (e) => {
				if (e.key === 'Escape') this.$emit('close');
			},
			service: {
				name: '',
				port: 0,
			},
		}
	},
	mounted() {
		document.addEventListener('keyup', this.$data.escapeKeyCallback);
	},
	beforeDestroy() {
		document.removeEventListener('keyup', this.$data.escapeKeyCallback);
	},
	methods: {
		sendNewService() {
			console.debug(`creating service name: ${this.service.name}, port: ${this.service.port}`);
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
			instance.post('/service/', this.service)
				.then(response => this.$emit('serviceAddDone', response.data))
				.catch(error => console.error('Failed to add service', error));
		},
	},
}
</script>

<style scoped>
	.modal-mask {
		position: fixed;
		z-index: 9998;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		background-color: rgba(0, 0, 0, .5);
		display: table;
		transition: opacity .3s ease;
	}

	.modal-wrapper {
		display: table-cell;
		vertical-align: middle;
	}

	.modal-container {
		width: 300px;
		margin: 0 auto;
		padding: 20px 30px;
		background-color: #fff;
		border-radius: 2px;
		box-shadow: 0 2px 8px rgba(0, 0, 0, .33);
		transition: all .3s ease;
		font-family: Helvetica, Arial, sans-serif;
	}
</style>
