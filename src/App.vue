<template>
	<div id="app">
		<!--    <div id="nav">-->
		<!--      <router-link to="/">Home</router-link> |-->
		<!--      <router-link to="/about">About</router-link>-->
		<!--    </div>-->
		<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
			<router-link to="/" class="navbar-brand">Packmate</router-link>
			<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent"
					aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
				<span class="navbar-toggler-icon"></span>
			</button>

			<div class="collapse navbar-collapse" id="navbarSupportedContent">
				<ul class="navbar-nav mr-auto">
					<li class="nav-item active">
						<a class="nav-link" href="#">1337 (L33T)</a>
					</li>
					<li class="nav-item">
						<a class="nav-link" href="#">1241 (Shitty service)</a>
					</li>
					<li class="nav-item">
						<button type="button" class="btn btn-link"><svg id="i-plus" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 32 32" width="16" height="16" fill="none" stroke="#ffffff" stroke-linecap="round" stroke-linejoin="round" stroke-width="2">
							<path d="M16 2 L16 30 M2 16 L30 16"></path>
						</svg></button>
					</li>
				</ul>
				<div class="my-2 my-lg-0">
					<button type="button" class="btn btn-link"><svg id="i-settings" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 32 32" width="16" height="16" fill="none" stroke="#ffffff" stroke-linecap="round" stroke-linejoin="round" stroke-width="2">
						<path d="M13 2 L13 6 11 7 8 4 4 8 7 11 6 13 2 13 2 19 6 19 7 21 4 24 8 28 11 25 13 26 13 30 19 30 19 26 21 25 24 28 28 24 25 21 26 19 30 19 30 13 26 13 25 11 28 8 24 4 21 7 19 6 19 2 Z" />
						<circle cx="16" cy="16" r="4" />
					</svg></button>
				</div>
			</div>
		</nav>
		<router-view/>
	</div>
</template>

<style>

</style>
