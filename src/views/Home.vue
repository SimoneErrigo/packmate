<template>
	<!--  <div class="home">-->
	<!--    <img alt="Vue logo" src="../assets/logo.png">-->
	<!--    <HelloWorld msg="Welcome to Your Vue.js + TypeScript App"/>-->
	<!--  </div>-->
	<div class="container-fluid">
		<div class="row">
			<Sidebar/>
			<Content/>
		</div>
	</div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import Sidebar from '@/components/Sidebar.vue';
    import Content from '@/components/Content.vue';

    @Component({
        components: {
            // HelloWorld,
			Sidebar,
			Content,
        },
    })
    export default class Home extends Vue {
    }
</script>
