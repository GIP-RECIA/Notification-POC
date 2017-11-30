import Vue from 'vue'
import Vuex from 'vuex'
import auth from './auth'
import stomp from './stomp'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {},
  modules: {
    auth,
    stomp
  }
})
