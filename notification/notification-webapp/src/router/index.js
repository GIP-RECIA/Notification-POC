import Vue from 'vue'
import Router from 'vue-router'
import Notifications from '../components/Notifications'
import Main from '../components/Main'
import Login from '../components/Login'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Main',
      component: Main
    },
    {
      path: '/login',
      name: 'Login',
      component: Login
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
