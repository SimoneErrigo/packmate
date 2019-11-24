<template>
	<li class="nav-item" :class="{highlight: favorite0}" style="transition: all .5s;">
		<router-link class="nav-link"
					 :to="{name: 'stream', params: {servicePort: stream.service, streamId: stream.id}}">
			<a @click.stop.prevent="toggleFavorite">
				<i class="fa-star" style="color: #DC3545" :class="favorite0 ? 'fas' : 'far'"/>
			</a>
			{{ stream.id }} {{ stream.protocol }}
			<template v-if="stream.ttl">TTL {{ stream.ttl }}</template>
			<br/>
			{{ dateToText(stream.startTimestamp) }} - {{ dateToText(stream.endTimestamp) }}
			<template v-if="stream.userAgentHash"><br/>UA: {{ stream.userAgentHash }}</template>

			<span v-for="pattern in stream.foundPatterns"
				  :key="pattern.id"
				  :style="`color: ${pattern.color};`">
				{{ pattern.name }}
			</span>
		</router-link>
	</li>
</template>

<script>
	export default {
		name: 'SidebarStream',
		props: {
			stream: {
				id: Number(),
				protocol: String(),
				service: Number(),
				startTimestamp: Number(),
				endTimestamp: Number(),
				foundPatterns: Array(),
				favorite: Boolean(),
				ttl: Number(),
				userAgentHash: String(),
			},
		},
		data: function () {
			return {
				favorite0: this.stream.favorite,
			};
		},
		methods: {
			dateToText(unixTimestamp) {
				return new Date(unixTimestamp).toLocaleDateString('ru-RU', {
					month: '2-digit',
					day: '2-digit',
					hour: '2-digit',
					minute: '2-digit',
					second: '2-digit',
				});
			},

			toggleFavorite() {
				this.$http.post(`stream/${this.stream.id}/${this.favorite0 ? 'unfavorite' : 'favorite'}`)
					.then(() => this.favorite0 = !this.favorite0)
					.catch(e => {
						this.$bvToast.toast(`Не удалось пометить сервис: ${e}`, {
							title: 'Сбой',
							variant: 'danger',
						});
						console.error('Failed to fav service', e);
					});
			},
		},
	};
</script>

<style scoped>
	.nav-link {
		font-weight: 500;
		color: #333;
	}

	/*noinspection CssUnusedSymbol*/
	.nav-link.active {
		color: #007BFF;
	}

	/*noinspection CssUnusedSymbol*/
	.highlight {
		/*background: rgb(220, 53, 69)*/
		background: rgba(220, 53, 69, 0.3);
		box-shadow: 0 0 1px 1px rgba(255, 119, 182, 0.3);
	}
</style>
