<template>
	<div class="modal-mask">
		<div class="modal-wrapper">
			<div class="modal-container">
				<form>
					<div class="form-group">
						<label for="inputApiUrl">URL бэкенда</label>
						<input type="url" class="form-control" id="inputApiUrl" placeholder="URL" v-model="apiUrl">
					</div>

					<div class="form-row">
						<div class="form-group col-md-6">
							<label for="inputApiLogin">Логин</label>
							<input type="text" class="form-control" id="inputApiLogin" v-model="apiLogin">
						</div>
						<div class="form-group col-md-6">
							<label for="inputApiPassword">Пароль</label>
							<input type="password" class="form-control" id="inputApiPassword" v-model="apiPassword">
						</div>
					</div>

					<div class="form-row">
						<div class="form-group col-md-5">
							<label for="inputHexdumpBlockSize">Ширина блока HEX</label>
							<input type="number" class="form-control" id="inputHexdumpBlockSize" v-model="hexdumpBlockSize">
						</div>
						<div class="form-group col-md-7">
							<label for="inputHexdumpLineNumberBase">Номер строки</label>
							<select id="inputHexdumpLineNumberBase" class="form-control" v-model="hexdumpLineNumberBase">
								<option selected value="10">Десятичный</option>
								<option value="16">Шестнадцатеричный</option>
							</select>
						</div>
					</div>

					<div class="form-group">
						<label for="inputPageSize">Размер страницы</label>
						<input type="number" class="form-control" id="inputPageSize" v-model="pageSize">
					</div>

					<button type="submit" @click.prevent="$emit('close')" class="btn btn-primary">
						Закрыть
					</button>
				</form>
			</div>
		</div>
	</div>
</template>

<script>
export default {
	name: "SettingsModal",
	data: function () {
		return {
			escapeKeyCallback: (e) => {
				if (e.key === 'Escape') this.$emit('close');
			},
		}
	},
	mounted() {
		document.addEventListener('keyup', this.$data.escapeKeyCallback);
	},
	beforeDestroy() {
		document.removeEventListener('keyup', this.$data.escapeKeyCallback);
	},
	computed: {
		apiUrl: {
			get() {
				return this.$store.state.apiUrl;
			},
			set(value) {
				this.$store.commit('setApiUrl', value);
			},
		},
		apiLogin: {
			get() {
				return this.$store.state.apiLogin;
			},
			set(value) {
				this.$store.commit('setApiLogin', value);
			},
		},
		apiPassword: {
			get() {
				return this.$store.state.apiPassword;
			},
			set(value) {
				this.$store.commit('setApiPassword', value);
			},
		},
		hexdumpBlockSize: {
			get() {
				return this.$store.state.hexdumpBlockSize;
			},
			set(value) {
				this.$store.commit('setHexdumpBlockSize', value);
			},
		},
		hexdumpLineNumberBase: {
			get() {
				return this.$store.state.hexdumpLineNumberBase;
			},
			set(value) {
				this.$store.commit('setHexdumpLineNumberBase', value);
			},
		},
		pageSize: {
			get() {
				return this.$store.state.pageSize;
			},
			set(value) {
				this.$store.commit('setPageSize', value);
			},
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
		width: 500px;
		margin: 0 auto;
		padding: 20px 30px;
		background-color: #fff;
		border-radius: 2px;
		box-shadow: 0 2px 8px rgba(0, 0, 0, .33);
		transition: all .3s ease;
		font-family: Helvetica, Arial, sans-serif;
	}
</style>
