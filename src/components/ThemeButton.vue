<template>
	<div>
		<input
				@change="toggleTheme"
				id="checkbox"
				type="checkbox"
				class="switch-checkbox"
		/>
		<label for="checkbox" class="switch-label">
			<span class="checkbox-emoji">🌙</span>
			<span class="checkbox-emoji">☀️</span>
			<div
					class="switch-toggle"
					:class="{ 'switch-toggle-checked': chosenTheme === 'dark' }"
			></div>
		</label>
	</div>
</template>

<script>

export default {
	mounted() {
		this.initTheme();
	},

	computed: {
		chosenTheme: {
			get() {
				return this.$store.state.theme || this.detectTheme();
			},

			set(theme) {
				this.$store.commit('setTheme', theme);
				this.displayTheme(theme);
			},
		},
	},

	methods: {
		toggleTheme() {
			const activeTheme = this.chosenTheme;

			if (activeTheme === "light") {
				this.chosenTheme = 'dark';
			} else {
				this.chosenTheme = 'light';
			}

			console.debug('Toggling theme from ', activeTheme, ' to ', this.chosenTheme);
		},

		initTheme() {
			this.displayTheme(this.chosenTheme);
		},

		detectTheme() {
			return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
		},

		displayTheme(theme) {
			document.body.setAttribute('data-theme', theme);
		},
	},
};
</script>

<style scoped>
.switch-checkbox {
	display: none;
}

.switch-label {
	align-items: center;
	background: var(--button-text-primary-color);
	border: calc(var(--button-element-size) * 0.025) solid var(--button-accent-color);
	border-radius: var(--button-element-size);
	cursor: pointer;
	display: flex;
	font-size: calc(var(--button-element-size) * 0.3);
	height: calc(var(--button-element-size) * 0.35);
	position: relative;
	padding: calc(var(--button-element-size) * 0.1);
	transition: background 0.5s ease;
	justify-content: space-between;
	width: var(--button-element-size);
	z-index: 1;
	box-sizing: content-box;
}

.switch-toggle {
	position: absolute;
	background-color: var(--button-background-color-primary);
	border-radius: 50%;
	top: calc(var(--button-element-size) * 0.07);
	left: calc(var(--button-element-size) * 0.07);
	height: calc(var(--button-element-size) * 0.4);
	width: calc(var(--button-element-size) * 0.4);
	transform: translateX(0);
	transition: transform 0.3s ease, background-color 0.5s ease;
}

.switch-toggle-checked {
	transform: translateX(calc(var(--button-element-size) * 0.6)) !important;
}

.checkbox-emoji {
	color: transparent;
	text-shadow: 0 0 0 var(--button-background-color-primary);
}

>>> {
	--button-element-size: 3rem;

	--button-background-color-primary: #ebebeb;
	--button-background-color-secondary: #fafafa;
	--button-accent-color: #cacaca;
	--button-text-primary-color: #222;
}

[data-theme=dark] * {
	--button-background-color-primary: #1e1e1e;
	--button-background-color-secondary: #2d2d30;
	--button-accent-color: #3f3f3f;
	--button-text-primary-color: #ddd;
}
</style>
