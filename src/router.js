import Vue from 'vue';
import Router from 'vue-router';
import ContentStream from './views/ContentStream';
import Sidebar from './components/Sidebar';

Vue.use(Router);

export default new Router({
	routes: [
		// {
		// 	path: '/',
		// 	name: 'nothing',
		// 	component: ContentNothing,
		// },
		{
			path: '/:servicePort?/:streamId?',
			name: 'stream',
			props: true,
			// route level code-splitting
			// this generates a separate chunk (about.[hash].js) for this route
			// which is lazy-loaded when the route is visited.
			// component: () => import(/* webpackChunkName: "stream" */ './views/ContentStream'),

			// component: ContentStream,
			components: {
				sidebar: Sidebar,
				content: ContentStream,
			},
		},
	],
	linkActiveClass: 'active',
});
