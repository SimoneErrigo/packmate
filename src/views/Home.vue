<template>
	<div>
		<Navbar></Navbar>
		<div class="container-fluid">
			<div class="row">
				<Sidebar :servicePort="servicePort" :streamId="streamId"/>
				<Content :servicePort="servicePort" :streamId="streamId"/>
			</div>
		</div>
	</div>
</template>

<script lang="ts">
    import {Component, Prop, Vue} from "vue-property-decorator";
    import Sidebar from "@/components/Sidebar.vue";
    import Content from "@/components/Content.vue";
    import Navbar from "@/components/Navbar.vue";

    @Component({
        components: {
            Navbar,
            Sidebar,
            Content,
        },
    })
    export default class Home extends Vue {
        @Prop() private servicePort?: string;
        @Prop() private streamId?: string;
    }
</script>
