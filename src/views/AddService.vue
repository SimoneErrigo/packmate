<template>
	<b-modal @ok="createService" @cancel="reset" id="addServiceModal" title="Добавить сервис" cancel-title="Отмена"
			 centered scrollable>
		<form ref="addServiceForm">
			<b-form-group
					label-cols-sm="4"
					label-cols-lg="3"
					label="Название"
					label-for="addSvc-name">
				<b-form-input @keydown.native.enter="createService" id="addSvc-name" required v-model="svcName"/>
			</b-form-group>
			<b-form-group
					label-cols-sm="4"
					label-cols-lg="3"
					label="Порт"
					label-for="addSvc-port">
				<b-form-input @keydown.native.enter="createService" type="number" id="addSvc-port" required
							  v-model.number="svcPort"/>
			</b-form-group>
		</form>
	</b-modal>
</template>

<script>
	export default {
		name: 'AddService',
		data() {
			return {
				svcName: String(),
				svcPort: Number(),
			};
		},
		methods: {
			checkValidity() {
				return this.$refs.addServiceForm.reportValidity()
					&& this.svcPort >= 1
					&& this.svcPort <= 65535;
			},

			reset() {
				this.svcName = '';
				this.svcPort = 0;
			},

			createService(e) {
				const name = this.svcName;
				const port = this.svcPort;
				if (!this.checkValidity()) {
					e.preventDefault();
					console.debug('Form is invalid');
					return;
				}
				console.debug(`Creating service ${name} (${port})...`);

				this.$http.post('service/', {
					name,
					port,
				}).then(response => {
					const data = response.data;
					console.debug('Done adding service', data);
					this.$emit('serviceAddComplete');
					this.reset();
					this.$bvModal.hide('addServiceModal');
				}).catch(e => {
					this.$bvToast.toast(`Не удалось добавить сервис: ${e}`, {
						title: 'Сбой',
						variant: 'danger',
					});
					console.error('Failed to add service', e);
				});
			},
		},
	};
</script>

<style scoped>

</style>
