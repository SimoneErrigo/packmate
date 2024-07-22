<template>
	<b-modal @ok="lookBack" id="lookBackModal"
					 title="Search into the past"
					 cancel-title="Cancel"
					 centered scrollable>
		<form ref="lookBackForm">
			<b-form-group
					label-cols-sm="4"
					label-cols-lg="3"
					label="Minutes"
					description="How far into the past do we want to look"
					label-for="lookback-minutes">
				<b-form-input @keydown.native.enter="lookBack" id="lookback-minutes" required v-model="minutes"/>
			</b-form-group>
		</form>
	</b-modal>
</template>

<script>
export default {
	name: 'LookBack',
	props: {
		patternId: Number,
	},
	data() {
		return {
			minutes: 5,
		};
	},
	methods: {
		checkValidity() {
			return this.$refs.lookBackForm.reportValidity();
		},

		lookBack(ev) {
			ev.preventDefault();
			if (!this.checkValidity()) {
				console.debug('Form is invalid');
				return;
			}
			console.debug(`Looking back with pattern ${this.patternId}`);

			this.$http.post(`pattern/${this.patternId}/lookback`, this.minutes)
					.then(() => {
						console.debug('Lookback started');
						this.$bvModal.hide('lookBackModal');
					}).catch(e => {
				this.$bvToast.toast(`Failed to start lookback: ${e}`, {
					title: 'Error',
					variant: 'danger',
				});
				console.error('Failed to start lookback', e);
			});
		},
	},
};
</script>
