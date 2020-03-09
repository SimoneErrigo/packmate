<template>
	<b-dropdown no-flip text="Паттерны" block variant="dark" class="col-sm-3 mr-0">
		<li role="presentation" style="padding-left: 0.5em; padding-right: 0.5em;">
			<button role="menuitem" type="button" class="btn btn-sm btn-primary btn-block"
					@click.stop.prevent="showAddService">
				<i class="fas fa-plus"/>
			</button>
		</li>
		<b-dropdown-divider/>
		<b-dropdown-item-button v-for="pattern in this.$store.state.patterns"
								:key="pattern.id" @click.stop.prevent="openPattern(pattern.id)">
			<strong :style="`color: ${pattern.color};`">{{ pattern.name }}</strong>:
			<code>{{ getSearchTypeValue(pattern.searchType, pattern.value) }}</code>;
			search
			<template v-if="pattern.directionType === 'BOTH'">anywhere</template>
			<template v-else-if="pattern.directionType === 'INPUT'">in request</template>
			<template v-else>in response</template>
			<div class="float-right" style="margin-left: 2em;">
				<button type="button" class="btn btn-outline-danger btn-block btn-sm"
						@click.stop.prevent="deletePattern(pattern.id)">
					<i class="far fa-trash-alt"/>
				</button>
			</div>
		</b-dropdown-item-button>
	</b-dropdown>
</template>
<script>
	export default {
		name: 'PatternsDropdown',
		data() {
			return {
				// patterns: Array(),
			};
		},
		mounted() {
			this.updatePatterns();
		},
		methods: {
			getSearchTypeValue(searchType, value) {
				if (searchType === 'REGEX') return `/${value}/`;
				else if (searchType === 'SUBSTRING') return `'${value}'`;
				else return `0x${value}`;
			},
			showAddService() {
				this.$bvModal.show('addPatternModal');
			},
			updatePatterns() {
				this.$http.get('pattern/')
					.then(r => this.$store.commit('setPatterns', r.data))
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
			deletePattern(patternId) {
				console.debug('Deleting pattern w/ id', patternId);
				this.$http.delete(`pattern/${patternId}`)
					.then(response => {
						const data = response.data;
						console.debug('Done deleting pattern', data);
						this.$emit('patternAddComplete');
					}).catch(e => {
					this.$bvToast.toast(`Не удалось удалить паттерн: ${e}`, {
						title: 'Сбой',
						variant: 'danger',
					});
					console.error('Failed to delete pattern', e);
				});
			},

			addPatternFromWs(pattern) {
				this.$store.commit('addPattern', pattern);
			},

			deletePatternFromWs(id) {
				this.$store.commit('setPatterns', this.$store.state.patterns.filter(o => o.id !== id));
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
