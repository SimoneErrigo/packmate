<template>
	<li class="nav-item" :class="{ 'active': subIsActive('/' + port) }">
		<router-link class="nav-link" :to="'/' + port">{{ name }} ({{ port }})</router-link>
	</li>
</template>

<script lang="ts">
    import {Component, Prop, Vue} from "vue-property-decorator";

    @Component({
        methods: {
            subIsActive(input) {
                const paths = Array.isArray(input) ? input : [input];
                return paths.some(path => {
                    return this.$route.path.indexOf(path) === 0; // current path starts with this path string
                });
            }
        }
    })
    export default class CrfService extends Vue {
        @Prop() private port!: number;
        @Prop() private name!: string;
    }
</script>

<style scoped>

</style>
