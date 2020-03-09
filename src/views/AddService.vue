<template>
	<b-modal @ok="createService" @cancel="reset" id="addServiceModal" title="Добавить сервис"
			 cancel-title="Отмена"
			 centered scrollable>
		<form ref="addServiceForm">
			<b-form-group
					label-cols-sm="4"
					label-cols-lg="3"
					label="Название"
					label-for="addSvc-name">
				<b-form-input @keydown.native.enter="createService" id="addSvc-name" required
							  v-model="svcName"/>
			</b-form-group>
			<b-form-group
					label-cols-sm="4"
					label-cols-lg="3"
					label="Порт"
					label-for="addSvc-port">
				<b-form-input @keydown.native.enter="createService" type="number" min="1" max="65535"
							  id="addSvc-port" required v-model.number="svcPort"/>
			</b-form-group>
			<b-form-group
					label-cols-sm="4"
					label-cols-lg="3"
					label="Разархивировать GZIP?"
					label-for="addSvc-ungzip">
				<b-form-checkbox id="addSvc-ungzip" required
								 v-model="ungzipHttp"/>
			</b-form-group>
			<b-form-group
					label-cols-sm="4"
					label-cols-lg="3"
					label="Применять urldecode?"
					label-for="addSvc-urldecode">
				<b-form-checkbox id="addSvc-urldecode" required
								 v-model="urldecodeHttpRequests"/>
			</b-form-group>
			<b-form-group
					label-cols-sm="4"
					label-cols-lg="3"
					label="Совмещать смежные пакеты?"
					label-for="addSvc-mergeAdjacent">
				<b-form-checkbox id="addSvc-mergeAdjacent" required
								 v-model="mergeAdjacentPackets"/>
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
				ungzipHttp: Boolean(),
				urldecodeHttpRequests: Boolean(),
				mergeAdjacentPackets: Boolean(),
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
				this.ungzipHttp = false;
				this.urldecodeHttpRequests = false;
				this.mergeAdjacentPackets = false;
			},

			createService(ev) {
				const name = this.svcName;
				const port = this.svcPort;
				const ungzipHttp = this.ungzipHttp;
				const urldecodeHttpRequests = this.urldecodeHttpRequests;
				const mergeAdjacentPackets = this.mergeAdjacentPackets;
				if (!this.checkValidity()) {
					ev.preventDefault();
					console.debug('Form is invalid');
					return;
				}
				console.debug(`Creating service ${name} (${port})...`);

				this.$http.post('service/', {
					name,
					port,
					ungzipHttp,
					urldecodeHttpRequests,
					mergeAdjacentPackets,
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
				ev.preventDefault();
			},
		},
	};
</script>

<style scoped>

</style>
