<template>
	<b-modal id="serviceModal" size="lg"
			 :title="creating ? 'Creating service' : 'Editing service'"
			 centered scrollable @ok.prevent="submit">
		<b-form ref="serviceForm">
			<b-form-group label-cols-sm="4"
						  label="Name"
						  label-for="service-name">
				<b-form-input id="service-name" required v-model="service.name"
							  @keyup.enter.stop.prevent="submit"/>
			</b-form-group>

			<b-form-group v-if="creating"
						  label-cols-sm="4"
						  label="Port"
						  label-for="service-port">
				<b-form-input id="service-port" required
							  type="number" min="1" max="65535" v-model.number="service.port"
							  @keyup.enter.stop.prevent="submit"/>
			</b-form-group>

			<b-form-group label-cols-sm="4"
						  label="Is an HTTP service"
						  label-for="service-is-http">
				<b-form-checkbox id="service-is-http" required
                  v-model="service.http" />
			</b-form-group>

			<b-form-group label-cols-sm="4"
						  label="Apply urldecode"
						  label-for="service-urldecode">
				<b-form-checkbox id="service-urldecode" required
								 v-model="service.urldecodeHttpRequests"/>
			</b-form-group>

			<b-form-group label-cols-sm="4"
						  label="Merge adjacent packets"
						  label-for="service-mergeAdjacent">
				<b-form-checkbox id="service-mergeAdjacent" required
								 v-model="service.mergeAdjacentPackets"/>
			</b-form-group>

			<b-form-group label-cols-sm="4"
						label="Inflate WebSockets"
						label-for="service-inflateWS">
				<b-form-checkbox id="service-inflateWS" required
								 v-model="service.parseWebSockets"/>
			</b-form-group>

			<b-form-group label-cols-sm="4"
						  label="Decrypt TLS (TLS_RSA_WITH_AES only)"
						  label-for="service-decryptTls">
				<b-form-checkbox id="service-decryptTls" required
								 v-model="service.decryptTls"/>
			</b-form-group>

			<b-button v-if="!creating" variant="danger" @click="deleteService">Delete</b-button>
		</b-form>
	</b-modal>
</template>

<script>
	import {objectAssignDeep,} from '@/objectAssignDeep';

	export default {
		name: 'ServiceModal',
		props: {
			creating: Boolean,

			initialService: {
				name: String,
				port: Number,
				http: Boolean,
				urldecodeHttpRequests: Boolean,
				mergeAdjacentPackets: Boolean,
				parseWebSockets: Boolean,
				decryptTls: Boolean,
			},
		},
		data() {
			return {
				service: {},
			};
		},
		watch: {
			initialService() {
				console.debug('initialService changed, reassigning...', this.initialService);
				this.service = objectAssignDeep({}, this.initialService);
			},
		},
		methods: {
			submit() {
				if (!this.$refs.serviceForm.reportValidity()) {
					return;
				}
				console.debug('Submitting service...', this.service, this.creating);

				let url;

				if (this.creating) {
					url = 'service/';
				} else {
					url = 'service/' + this.initialService.port
				}

				this.$http.post(url, this.service)
					.then(response => {
						console.info('Done editing/creating service', response.data);
						this.$emit('service-update-needed');
						this.$bvModal.hide('serviceModal');
					})
					.catch(e => {
						this.$bvToast.toast(`Failed to ${this.creating ? 'create' : 'edit'} service: ${e}`, {
							title: 'Error',
							variant: 'danger',
						});
						console.error('Failed to edit/create service', e);
					});
			},
			deleteService() {
				this.$http.delete(`service/${this.service.port}`)
						.then(() => {
							console.info('Done deleting service', this.service);
							this.$emit('service-update-needed');
							this.$bvModal.hide('serviceModal');
						}).catch(e => {
					this.$bvToast.toast(`Failed to delete service: ${e}`, {
						title: 'Error',
						variant: 'danger',
					});
					console.error('Failed to delete service', e);
				});
			},
		},
	};
</script>
