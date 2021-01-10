<template>
	<b-dropdown no-flip text="Patterns" block variant="dark" class="col-sm-2 mr-0 p-0">
		<li role="presentation" style="padding-left: 0.5em; padding-right: 0.5em;">
			<button role="menuitem" type="button" class="btn btn-sm btn-primary btn-block"
					@click.stop.prevent="showAddService">
				<i class="fas fa-plus"/>
			</button>
		</li>
		<b-dropdown-divider/>
		<b-dropdown-item-button @click.stop.prevent="resetPatternSelection">
			<strong>All streams</strong>
		</b-dropdown-item-button>
		<b-dropdown-item-button v-for="pattern in this.$store.state.patterns"
								:key="pattern.id" @click.stop.prevent="openPattern(pattern)"
								:class="{ 'ignore-pattern' : pattern.actionType === 'IGNORE' }">

			<strong v-if="pattern.enabled" :style="getPatternColor">{{ pattern.name }}</strong>
			<s v-else :style="`color: ${pattern.color};`">{{ pattern.name }}</s>:

			<code>{{ getSearchTypeValue(pattern.searchType, pattern.value) }}</code>;

      <template v-if="pattern.actionType === 'FIND'">search </template>
      <template v-else>ignore </template>

			<template v-if="pattern.directionType === 'BOTH'">anywhere</template>
			<template v-else-if="pattern.directionType === 'INPUT'">in request</template>
			<template v-else>in response</template>

			<div class="float-right" style="margin-left: 2em;">
				<button v-if="pattern.actionType === 'FIND'" type="button" class="btn btn-outline-warning btn-sm mr-1"
								@click.stop.prevent="showLookBack(pattern)"
								title="Apply pattern to older streams">
					<i class="fas fa-backward"></i>
				</button>

				<button v-if="pattern.enabled" type="button" class="btn btn-outline-danger btn-sm"
						@click.stop.prevent="togglePattern(pattern)"
						title="Stop matching streams with this pattern">
					<i class="fas fa-pause"></i>
				</button>
				<button v-else type="button" class="btn btn-outline-success btn-sm"
						@click.stop.prevent="togglePattern(pattern)"
						title="Start matching streams with this pattern again">
					<i class="fas fa-play"></i>
				</button>
			</div>
		</b-dropdown-item-button>
	</b-dropdown>
</template>
<script>
	export default {
		name: 'PatternsDropdown',
		mounted() {
			this.updatePatterns();
		},
		methods: {
			getSearchTypeValue(searchType, value) {
				if (searchType === 'REGEX') return `/${value}/`;
				else if (searchType === 'SUBSTRING') return `'${value}'`;
				else return `0x${value}`;
			},
    	getPatternColor(pattern) {
				if (pattern.actionType === 'FIND') {
					return `color: ${pattern.color};`;
				} else {
					return `color: inherit;`;
				}
    	},
			showAddService() {
				this.$bvModal.show('addPatternModal');
			},
			showLookBack(pattern) {
				// Не знаю, как сделать это без костылей
				this.$root.$children[0].$refs.lookBack.patternId = pattern.id;
				this.$bvModal.show('lookBackModal');
			},
			updatePatterns() {
				this.$http.get('pattern/')
					.then(r => this.$store.commit('setPatterns', r.data))
					.catch(e => {
						this.$bvToast.toast(`Failed to load patterns: ${e}`, {
							title: 'Error',
							variant: 'danger',
						});
						console.error('Failed to load patterns:', e);
					});
			},
			openPattern(pattern) {
				if (pattern.actionType === 'IGNORE') {
					return;
				}

				console.debug('Opening pattern w/ id', pattern.id);
				this.$router.push({
					name: 'stream',
					params: this.$route.params,
					query: {pattern: pattern.id,},
				}, () => {});
			},
			resetPatternSelection() {
				console.debug('Resetting pattern selection');
				this.$router.push({
					name: 'stream',
					params: this.$route.params,
				}, () => {});
			},
			togglePattern(pattern) {
				const enabled = !pattern.enabled;
				console.debug('Toggling pattern', pattern);
				this.$http.post(`pattern/${pattern.id}/`, null, {
					params: {
						enabled,
					},
				})
					.then(response => {
						const data = response.data;
						console.debug('Done toggling pattern', data);
						this.$emit('patternAddComplete');
					}).catch(e => {
					this.$bvToast.toast(`Failed to toggle pattern: ${e}`, {
						title: 'Error',
						variant: 'danger',
					});
					console.error('Failed to toggle pattern', e);
				});
			},
			addPatternFromWs(pattern) {
				this.$store.commit('addPattern', pattern);
			},
			deletePatternFromWs(id) {
				this.$store.commit('setPatterns', this.$store.state.patterns.filter(o => o.id !== id));
			},
			togglePatternFromWs(id, enabled) {
				this.$store.state.patterns.forEach(pattern => {
					if (pattern.id === id) {
						pattern.enabled = enabled;
					}
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
<style>
	.ignore-pattern button {
		cursor: default !important;
	}
</style>
