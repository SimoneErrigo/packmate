<template>
	<li :id="`stream-${stream.id}`" class="nav-item stream-item" :class="{highlight: favorite0}">
		<router-link class="nav-link"
					 :to="{name: 'stream', params: {servicePort: this.stream.service, streamId: this.stream.id}, query: this.$route.query}">
			<a @click.stop.prevent="toggleFavorite">
				<i class="fa-star" style="color: #DC3545" :class="favorite0 ? 'fas' : 'far'"/>
			</a>
			{{ stream.id }} {{ stream.protocol }}
			<template v-if="stream.ttl">TTL {{ stream.ttl }}</template>
      <template v-if="shouldShowServiceName"><br/>{{ getServiceName(stream.service) }} #{{stream.service}}</template>
			<br/>
			{{ dateToText(stream.startTimestamp) }}
			<template v-if="!dateMatches(stream.startTimestamp, stream.endTimestamp)">
				- {{ dateToText(stream.endTimestamp, dayMatches(stream.startTimestamp, stream.endTimestamp)) }}
			</template>
			<template v-if="stream.userAgentHash"><br/>UA: {{ stream.userAgentHash }}</template>
			<br/>
			{{ stream.sizeBytes }} bytes in {{ stream.packetsCount }} packets
			<br/>
			<span v-for="pattern in notDeletedFoundPatterns"
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
				foundPatternsIds: Array(),
				favorite: Boolean(),
				ttl: Number(),
				userAgentHash: String(),
				sizeBytes: Number(),
				packetsCount: Number(),
			},
		},
    computed: {
		  shouldShowServiceName: function () {
        return this.$route?.params?.servicePort === undefined;
      },
			foundPatterns: function () {
				return this.$store.state.patterns.filter(p => this.stream.foundPatternsIds.includes(p.id));
			},
			notDeletedFoundPatterns: function () {
				return this.foundPatterns.filter(p => !p.deleted);
			},
    },
		data: function () {
			return {
				favorite0: this.stream.favorite,
        getServiceName: function (port) {
          return this.$store.state.services.find(o => o.port === port)?.name ?? '<Deleted service>'
        },
			};
		},
		methods: {
			dateToText(unixTimestamp, short = false) {
				const date = new Date(unixTimestamp);
				const options = {
					hour: '2-digit',
					minute: '2-digit',
					second: '2-digit',
				};
				if (!short && !this.isToday(date)) {
					options.month = '2-digit';
					options.day = '2-digit';
					return date.toLocaleDateString('ru-RU', options);
				}
				return date.toLocaleTimeString('ru-RU', options);
			},

			isToday(someDate) {
				const today = new Date();
				return someDate.getDate() === today.getDate() &&
					someDate.getMonth() === today.getMonth() &&
					someDate.getFullYear() === today.getFullYear();
			},

			dayMatches(rFirst, rSecond) {
				const first = new Date(rFirst);
				const second = new Date(rSecond);

				return first.getDate() === second.getDate() &&
					first.getMonth() === second.getMonth() &&
					first.getFullYear() === second.getFullYear();
			},

			dateMatches(rFirst, rSecond) {
				const first = new Date(rFirst);
				const second = new Date(rSecond);
				return first.getDate() === second.getDate() &&
					first.getMonth() === second.getMonth() &&
					first.getFullYear() === second.getFullYear() &&
					first.getHours() === second.getHours() &&
					first.getMinutes() === second.getMinutes() &&
					first.getSeconds() === second.getSeconds();
			},

			toggleFavorite() {
				this.$http.post(`stream/${this.stream.id}/${this.favorite0 ? 'unfavorite' : 'favorite'}`)
					.then(() => this.favorite0 = !this.favorite0)
					.catch(e => {
						this.$bvToast.toast(`Failed to fav service: ${e}`, {
							title: 'Error',
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

	[data-theme=dark] .nav-link {
		color: #f8f9fa;
	}

	/*noinspection CssUnusedSymbol*/
	.nav-link.active {
		color: #007BFF;
	}

	.highlight {
		background: rgba(220, 53, 69, 0.3);
		box-shadow: 0 0 1px 1px rgba(255, 119, 182, 0.3);
	}

	.stream-item {
		transition: all .5s;
		box-shadow: 0 24px 3px -25px #4b4e51;
	}

	[data-theme=dark] .stream-item {
		box-shadow: 0 24px 3px -25px #84898E;
	}
</style>
