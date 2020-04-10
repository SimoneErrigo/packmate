<template>
	<b-modal @show="loadData" @ok="editService" @cancel="reset" id="editServiceModal"
			 title="Редактировать сервис"
			 cancel-title="Отмена"
			 centered scrollable>
		<form ref="editServiceForm">
			<b-form-group
					label-cols-sm="4"
					label-cols-lg="3"
					label="Название"
					label-for="addSvc-name">
				<b-form-input @keydown.native.enter="editService" id="addSvc-name" required v-model="svcName"/>
			</b-form-group>
			<button @click.stop.prevent="deleteService" class="btn btn-danger" style="width: 100%">Удалить</button>
		</form>
	</b-modal>
</template>

<script>
	export default {
		name: 'EditService',
		data() {
			return {
				svcName: String(),
				svcPort: Number(),
			};
		},
		methods: {
			loadData() {
				this.svcName = this.$store.state.serviceModalName;
				this.svcPort = this.$store.state.serviceModalId;
			},

			checkValidity() {
				return this.$refs.editServiceForm.reportValidity();
			},

			reset() {
				this.svcName = '';
			},

			editService(ev) {
				const name = this.svcName;
				const port = this.svcPort;
				if (!this.checkValidity()) {
					ev.preventDefault();
					console.debug('Form is invalid');
					return;
				}
				console.debug(`Editing service ${name} (${port})...`);

				this.$http.post('service/', {
					name,
					port,
				}).then(response => {
					const data = response.data;
					console.debug('Done editing service', data);
					this.$emit('serviceEditComplete');
					this.reset();
					this.$bvModal.hide('editServiceModal');
				}).catch(e => {
					this.$bvToast.toast(`Failed to edit service: ${e}`, {
						title: 'Сбой',
						variant: 'danger',
					});
					console.error('Failed to edit service', e);
				});
				ev.preventDefault();
			},

			deleteService() {
				this.$http.delete(`service/${this.svcPort}`)
					.then(response => {
						const data = response.data;
						console.debug('Done deleting service', data);
						this.$emit('serviceEditComplete');
						this.reset();
						this.$bvModal.hide('editServiceModal');
					}).catch(e => {
					this.$bvToast.toast(`Failed to remove service: ${e}`, {
						title: 'Сбой',
						variant: 'danger',
					});
					console.error('Failed to delete service', e);
				});
			},
		},
	};
</script>

<style scoped>

</style>
