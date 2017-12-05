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
    state.client = client
    state.connected = true
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
  connected: state => state.connected,
  connecting: state => state.connecting
}

export default {
  namespaced: true,
  state,
  mutations,
  getters
}
