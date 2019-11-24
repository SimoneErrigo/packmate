<!--suppress JSUnresolvedVariable -->
<template>
	<b-modal id="settingsModal" title="Настройки" centered scrollable ok-only>
		<b-form-group
				label-cols-sm="4"
				label-cols-lg="3"
				description="Логин для подключения к API"
				label="Логин"
				label-for="settings-apiLogin">
			<b-form-input id="settings-apiLogin" v-model="apiLogin"/>
		</b-form-group>
		<b-form-group
				label-cols-sm="4"
				label-cols-lg="3"
				description="Пароль для подключения к API"
				label="Пароль"
				label-for="settings-apiPassword">
			<b-form-input type="password" id="settings-apiPassword" v-model="apiPassword"/>
		</b-form-group>
		<b-form-group
				label-cols-sm="4"
				label-cols-lg="3"
				description="Количество байт для отображения в бинарном представлении пакета"
				label="Ширина блока HEX"
				label-for="settings-hexdumpBlockSize">
			<b-form-input type="number" id="settings-hexdumpBlockSize" v-model.number="hexdumpBlockSize"/>
		</b-form-group>
		<b-form-group
				label-cols-sm="4"
				label-cols-lg="3"
				description="Система счисления, используемая в номерах строк бинарного представления пакета"
				label="Нумерация строк"
				label-for="settings-hexdumpLineNumberBase">
			<b-form-select id="settings-hexdumpLineNumberBase" v-model="hexdumpLineNumberBase">
				<option :value="10" selected>Десятичная</option>
				<option :value="16">Шестнадцатеричная</option>
			</b-form-select>
		</b-form-group>
		<b-form-group
				label-cols-sm="4"
				label-cols-lg="3"
				description="Количество стримов для загрузки за раз"
				label="Размер страницы"
				label-for="settings-pageSize">
			<b-form-input type="number" id="settings-pageSize" v-model.number="pageSize"/>
		</b-form-group>
	</b-modal>
</template>

<script>
	export default {
		name: 'Settings',
		computed: {
			apiLogin: {
				get() {
					return this.$store.state.apiLogin;
				},
				set(v) {
					this.$store.commit('setApiLogin', v);
					this.$http.defaults.auth.username = v;
				},
			},
			apiPassword: {
				get() {
					return this.$store.state.apiPassword;
				},
				set(v) {
					this.$store.commit('setApiPassword', v);
					this.$http.defaults.auth.password = v;
				},
			},
			hexdumpBlockSize: {
				get() {
					return this.$store.state.hexdumpBlockSize;
				},
				set(v) {
					this.$store.commit('setHexdumpBlockSize', v);
				},
			},
			hexdumpLineNumberBase: {
				get() {
					return this.$store.state.hexdumpLineNumberBase;
				},
				set(v) {
					this.$store.commit('setHexdumpLineNumberBase', v);
				},
			},
			pageSize: {
				get() {
					return this.$store.state.pageSize;
				},
				set(v) {
					this.$store.commit('setPageSize', v);
				},
			},
		},
	};
</script>

<style scoped>

</style>
