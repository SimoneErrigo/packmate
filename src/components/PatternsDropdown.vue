<template>
	<b-dropdown no-flip text="Паттерны" block variant="dark" class="col-sm-3 mr-0">
		<li role="presentation" style="padding-left: 0.5em; padding-right: 0.5em;">
			<button role="menuitem" type="button" class="btn btn-sm btn-primary btn-block"
					@click.stop.prevent="showAddService">
				<i class="fas fa-plus"/>
			</button>
		</li>
		<b-dropdown-divider/>
		<b-dropdown-item-button v-for="pattern in patterns"
								:key="pattern.id" @click.stop.prevent="openPattern(pattern.id)">
			<strong :style="`color: ${pattern.color};`">{{ pattern.name }}</strong>:
			<code>{{ pattern.regex ? `/${pattern.value}/` : `'${pattern.value}'` }}</code>;
			search
			<template v-if="pattern.type === 'BOTH'">anywhere</template>
			<template v-else-if="pattern.type === 'INPUT'">in request</template>
			<template v-else>in response</template>
		</b-dropdown-item-button>
	</b-dropdown>
</template>
<script>
	export default {
		name: 'PatternsDropdown',
		data() {
			return {
				patterns: Array(),
			};
		},
		mounted() {
			this.updatePatterns();
		},
		methods: {
			showAddService() {
				this.$bvModal.show('addPatternModal');
			},
			updatePatterns() {
				this.$http.get('pattern/')
					.then(r => this.patterns = r.data)
					.catch(e => {
						this.$bvToast.toast(`Не удалось загрузить паттерны: ${e}`, {
							title: 'Сбой',
							variant: 'danger',
						});
						console.error('Failed to load patterns:', e);
					});
			},
			openPattern(patternId) {
				console.debug('Opening pattern w/ id', patternId);
				this.$router.push({
					name: 'stream',
					params: this.$route.params,
					query: {pattern: patternId,},
				});
			},
		},
	};
</script>
<style scoped>
	code {
		font-family: "Ubuntu Mono", "Lucida Console", monospace;
		font-size: 100%;
	}
</style>
