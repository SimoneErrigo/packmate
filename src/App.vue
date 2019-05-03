<template>
	<div id="app">
		<router-view :key="$route.path"></router-view>
	</div>
</template>

<style>
	body {
		font-size: .875rem;
	}
</style>
