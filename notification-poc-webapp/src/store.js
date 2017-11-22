import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const state = {
  client: null,
  connecting: false,
  connected: false,
  error: null
}

const mutations = {
  doWillConnect (state, client) {
    state.error = null
    state.connecting = true
    state.connected = false
    state.client = client
  },
  doConnect (state, client) {
    state.error = null
    state.connecting = false
    state.connected = true
    state.client = client
  },
  doDisconnect (state) {
    state.error = null
    state.connecting = false
    state.connected = false
    state.client = null
  },
  doError (state, error) {
    if (!state.error) {
      state.error = error.body ? error.body : error
    }
    state.connecting = false
    state.connected = false
    state.client = null
  }
}

const getters = {
  client: state => state.client,
  error: state => state.error,
  connected: state => !!state.client
}

export default new Vuex.Store({
  state,
  mutations,
  getters
})
