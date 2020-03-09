<template>
	<b-modal @ok="addPattern" @cancel="reset" id="addPatternModal"
			 title="Add pattern"
			 cancel-title="Cancel"
			 centered scrollable>
		<form ref="addPatternForm">
			<b-form-group
					label-cols-sm="4"
					label-cols-lg="3"
					label="Name"
					description="It is displayed in the list and highlighted in the packet contents"
					label-for="pattern-name">
				<b-form-input @keydown.native.enter="addPattern" id="pattern-name" required v-model="newPattern.name"/>
			</b-form-group>
			<b-form-group
					label-cols-sm="4"
					label-cols-lg="3"
					label="Pattern"
					description="Substring, RegEx or bytes"
					label-for="pattern-value">
				<b-form-input @keydown.native.enter="addPattern" id="pattern-value" required
							  v-model="newPattern.value"/>
			</b-form-group>
			<b-form-group
					label-cols-sm="4"
					label-cols-lg="3"
					label="Color"
					description="The highlight color of the pattern in the packets and streams"
					label-for="pattern-color">
				<b-form-input id="pattern-color" required type="color" v-model="newPattern.color"/>
			</b-form-group>
			<b-form-group
					label-cols-sm="4"
					label-cols-lg="3"
					label="Search method"
					description="The way to search for patterns in packets"
					label-for="pattern-searchType">
				<b-form-select id="pattern-searchType" required v-model="newPattern.searchType">
					<option value="REGEX" selected>Regular expression</option>
					<option value="SUBSTRING">Substring</option>
					<option value="SUBBYTES">Bytes (WIP)</option>
				</b-form-select>
			</b-form-group>
			<b-form-group
					label-cols-sm="4"
					label-cols-lg="3"
					label="Search type"
					description="in which packets to search for a pattern"
					label-for="pattern-type">
				<b-form-select id="pattern-type" required v-model="newPattern.directionType">
					<option value="INPUT">Request</option>
					<option value="OUTPUT">Response</option>
					<option value="BOTH" selected>Anywhere</option>
				</b-form-select>
			</b-form-group>
		</form>
	</b-modal>
</template>

<script>
	export default {
		name: 'AddPattern',
		data() {
			return {
				newPattern: {
					name: String(),
					value: String(),
					color: String(),
					searchType: String(),
					directionType: String(),
				},
			};
		},
		methods: {
			checkValidity() {
				return this.$refs.addPatternForm.reportValidity();
			},

			reset() {
				this.newPattern = {};
			},

			addPattern(ev) {
				ev.preventDefault();
				if (!this.checkValidity()) {
					console.debug('Form is invalid');
					return;
				}
				console.debug('Adding pattern...', this.newPattern);

				this.$http.post('pattern/', this.newPattern)
					.then(response => {
						const data = response.data;
						console.debug('Done adding pattern', data);
						this.$emit('patternAddComplete');
						this.reset();
						this.$bvModal.hide('addPatternModal');
					}).catch(e => {
					this.$bvToast.toast(`Failed to add pattern: ${e}`, {
						title: 'Error',
						variant: 'danger',
					});
					console.error('Failed to add pattern', e);
				});
			},
		},
	};
</script>

<style scoped>

</style>
