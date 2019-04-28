<template>
	<nav class="col-md-2 d-none d-md-block bg-light sidebar">
		<div class="sidebar-sticky">
			<ul class="nav flex-column">
				<!--				<li class="nav-item">-->
				<!--					<a class="nav-link active" href="#">-->
				<!--						#1<br/>-->
				<!--						13:37:01-->
				<!--					</a>-->
				<!--				</li>-->
				<!--				<li class="nav-item">-->
				<!--					<a class="nav-link" href="#">-->
				<!--						#2<br/>-->
				<!--						12:28:02-->
				<!--					</a>-->
				<!--				</li>-->
				<Stream
						v-for="stream in streams"
						:key="stream.id"
						:id="stream.id"
						:time="stream.time"></Stream>
			</ul>
		</div>
	</nav>
</template>

<script lang="ts">
    import {Component, Prop, Vue} from "vue-property-decorator";
    import Stream from "@/components/Stream.vue";

    @Component({
        components: {Stream},
        data() {
            return {
                streams: [
                    {
                        id: 111,
                        time: 1556411080,
                    },
                ]
            };
        }
    })
    export default class Sidebar extends Vue {
        @Prop() private servicePort?: string;
        @Prop() private streamId?: string;
    }
</script>

<style scoped>

</style>
