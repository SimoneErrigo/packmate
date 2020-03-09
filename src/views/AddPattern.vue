<template>
	<b-modal @ok="addPattern" @cancel="reset" id="addPatternModal"
			 title="Добавить паттерн"
			 cancel-title="Отмена"
			 centered scrollable>
		<form ref="addPatternForm">
			<b-form-group
					label-cols-sm="4"
					label-cols-lg="3"
					label="Название"
					description="Отображается в списке и при успешном поиске"
					label-for="pattern-name">
				<b-form-input @keydown.native.enter="addPattern" id="pattern-name" required v-model="newPattern.name"/>
			</b-form-group>
			<b-form-group
					label-cols-sm="4"
					label-cols-lg="3"
					label="Паттерн"
					description="Подстрока или регулярное выражение"
					label-for="pattern-value">
				<b-form-input @keydown.native.enter="addPattern" id="pattern-value" required
							  v-model="newPattern.value"/>
			</b-form-group>
			<b-form-group
					label-cols-sm="4"
					label-cols-lg="3"
					label="Цвет"
					description="Как выделять найденный паттерн в пакете и стриме"
					label-for="pattern-color">
				<b-form-input id="pattern-color" required type="color" v-model="newPattern.color"/>
			</b-form-group>
			<b-form-group
					label-cols-sm="4"
					label-cols-lg="3"
					label="Способ поиска"
					description="Каким способом искать указанное выражение?"
					label-for="pattern-searchType">
				<b-form-select id="pattern-searchType" required v-model="newPattern.searchType">
					<option value="REGEX" selected>Регулярное выражение</option>
					<option value="SUBSTRING">Подстрока</option>
					<option value="SUBBYTES">В байтах (WIP)</option>
				</b-form-select>
			</b-form-group>
			<b-form-group
					label-cols-sm="4"
					label-cols-lg="3"
					label="Тип поиска"
					description="В каких пакетах искать паттерн"
					label-for="pattern-type">
				<b-form-select id="pattern-type" required v-model="newPattern.directionType">
					<option value="INPUT">Запрос</option>
					<option value="OUTPUT">Ответ</option>
					<option value="BOTH" selected>Везде</option>
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
					this.$bvToast.toast(`Не удалось добавить паттерн: ${e}`, {
						title: 'Сбой',
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
