<template>
	<nav class="col-md-2 d-none d-md-block bg-light sidebar">
		<div class="sidebar-sticky">
			<ul class="nav flex-column">
				<li class="nav-item">
					<a class="nav-link active" href="#">
						#1<br/>
						13:37:01
					</a>
				</li>
				<li class="nav-item">
					<a class="nav-link" href="#">
						#2<br/>
						12:28:02
					</a>
				</li>
			</ul>
		</div>
	</nav>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";

    @Component
    export default class Sidebar extends Vue {
    }
</script>

<style scoped>

</style>
