<template>
	<main class="col-md-9 ml-sm-auto col-lg-10 px-4">
		<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
			<h1>Content</h1>
		</div>
	</main>
</template>

<script lang="ts">
    import {Component, Prop, Vue} from "vue-property-decorator";

    @Component
    export default class Content extends Vue {
        @Prop() private servicePort?: string;
        @Prop() private streamId?: string;
    }
</script>

<style scoped>

</style>
