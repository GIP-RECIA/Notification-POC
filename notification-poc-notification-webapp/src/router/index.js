import Vue from 'vue'
import Router from 'vue-router'
import Notifications from '../components/Notifications.vue'
import Main from '../components/Main.vue'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Main',
      component: Main
    },
    {
      path: '/notifications',
      name: 'Notifications',
      component: Notifications,
      props: {
        destination: '/notifications'
      }
    }
  ]
})
