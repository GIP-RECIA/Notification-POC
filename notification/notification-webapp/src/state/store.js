import Vue from 'vue'
import Vuex from 'vuex'
import auth from './auth'
import stomp from './stomp'
import createPersistedState from 'vuex-persistedstate'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {},
  modules: {
    auth,
    stomp
  },
  plugins: [createPersistedState({
  })]
})
