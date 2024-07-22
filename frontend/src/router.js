import Vue from 'vue';
import Router from 'vue-router';
import ContentStream from './views/ContentStream';
import Sidebar from './components/Sidebar';

Vue.use(Router);

export default new Router({
	routes: [
		{
			path: '/:servicePort?/:streamId?',
			name: 'stream',
			props: true,
			components: {
				sidebar: Sidebar,
				content: ContentStream,
			},
		},
	],
	linkActiveClass: 'active',
});
