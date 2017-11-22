import Vue from 'vue'
import Router from 'vue-router'
import RandomBeans from '../components/RandomBeans.vue'
import RoutingRandomBeans from '../components/RoutingRandomBeans.vue'
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
      path: '/exchange/direct',
      name: 'ExchangeDirect',
      component: RandomBeans,
      props: {
        destination: '/amq/queue/random-beans'
      }
    },
    {
      path: '/exchange/fanout',
      name: 'ExchangeFanout',
      component: RandomBeans,
      props: {
        destination: '/exchange/random-beans-fanout'
      }
    },
    {
      path: '/exchange/routing',
      name: 'ExchangeRouting',
      component: RoutingRandomBeans,
      props: {
        destination: '/exchange/random-beans-routing',
        routingKeys: ['short-title', 'long-title']
      }
    },
    {
      path: '/exchange/topic',
      name: 'ExchangeTopic',
      component: RoutingRandomBeans,
      props: {
        destination: '/exchange/event',
        routingKeys: ['random-beans.short-title', 'random-beans.long-title']
      }
    }
  ]
})
