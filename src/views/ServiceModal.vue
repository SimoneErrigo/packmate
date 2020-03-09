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
						  label="Decompress GZIP?"
						  label-for="service-ungzip">
				<b-form-checkbox id="service-ungzip" required
								 v-model="service.ungzipHttp"/>
			</b-form-group>
			<b-form-group label-cols-sm="4"
						  label="Apply urldecode?"
						  label-for="service-urldecode">
				<b-form-checkbox id="service-urldecode" required
								 v-model="service.urldecodeHttpRequests"/>
			</b-form-group>
			<b-form-group label-cols-sm="4"
						  label="Merge adjacent packets??"
						  label-for="service-mergeAdjacent">
				<b-form-checkbox id="service-mergeAdjacent" required
								 v-model="service.mergeAdjacentPackets"/>
			</b-form-group>
		</b-form>
	</b-modal>
</template>

<script>
	import {objectAssignDeep,} from '../objectAssignDeep';

	export default {
		name: 'ServiceModal',
		props: {
			creating: Boolean,

			initialService: {
				name: String,
				port: Number,
				ungzipHttp: Boolean,
				urldecodeHttpRequests: Boolean,
				mergeAdjacentPackets: Boolean,
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
				if (!this.$refs.taskForm.reportValidity()) {
					return;
				}
				console.debug('Submitting service...', this.service, this.creating);
				this.$http.post('service/', this.service)
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
		},
	};
</script>

<style scoped>

</style>
