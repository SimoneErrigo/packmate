<template>
	<li class="nav-item">
		<!--		<a class="nav-link active" href="#">-->
		<!--			#1<br/>-->
		<!--			13:37:01-->
		<!--		</a>-->
		<router-link class="nav-link" active-class="active" :to="'/' + $route.params.servicePort + '/' + id">
			#{{id}}<br/>
			{{time}}
		</router-link>
	</li>
</template>

<script lang="ts">
    import {Component, Prop, Vue} from "vue-property-decorator";

    @Component
    export default class Stream extends Vue {
        @Prop() private id!: number;
        @Prop() private time!: number;
    }
</script>

<style scoped>

</style>
