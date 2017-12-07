import Querystring from 'query-string'
import jwtDecode from 'jwt-decode'

import {rawClient} from '../api/client'
import config from '../config'

function getOpenidConfiguration (context) {
  if (context.state.openidConfiguration) {
    return Promise.resolve(context.state.openidConfiguration)
  } else if (config.openid.configuration) {
    return Promise.resolve(config.openid.configuration)
  } else {
    return rawClient.get(config.openid.issuerUrl + '/.well-known/openid-configuration').then((response) => {
      context.commit('setOpenidConfiguration', response.data)
      return response.data
    })
  }
}

export default {
  namespaced: true,
  state: {
    openidConfiguration: null,
    auth: null,
    accessTokenDecoded: null,
    refreshTokenDecoded: null
  },
  mutations: {
    setAuthPayload (state, payload) {
      if (payload) {
        state.auth = payload
        state.accessTokenDecoded = jwtDecode(payload.access_token)
        state.refreshTokenDecoded = payload.refresh_token ? jwtDecode(payload.refresh_token) : payload.refresh_token
      } else {
        state.auth = null
        state.accessTokenDecoded = null
        state.refreshTokenDecoded = null
      }
    },
    setOpenidConfiguration (state, payload) {
      state.openidConfiguration = payload
    }
  },
  actions: {
    login (context, payload) {
      return getOpenidConfiguration(context).then((openidConfiguration) => {
        const data = {
          'grant_type': 'password',
          'username': payload.username,
          'password': payload.password
        }

        const encodedData = Querystring.stringify(data)

        const auth = { username: config.openid.clientId, password: config.openid.clientSecret }
        return rawClient.post(openidConfiguration.token_endpoint, encodedData, {
          auth: auth,
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        })
      }).then((response) => {
        context.commit('setAuthPayload', response.data)
        return response.data
      })
    },
    refresh (context, payload) {
      return getOpenidConfiguration(context).then((openidConfiguration) => {
        const data = {
          'grant_type': 'refresh_token',
          'refresh_token': payload.refresh_token
        }

        const encodedData = Querystring.stringify(data)

        const auth = { username: config.openid.clientId, password: config.openid.clientSecret }
        return rawClient.post(openidConfiguration.token_endpoint, encodedData, {
          auth: auth,
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        }).then((response) => {
          context.commit('setAuthPayload', response.data)
          return response.data
        })
      })
    },
    logout (context) {
      return getOpenidConfiguration(context).then((openidConfiguration) => {
        let promise
        if (openidConfiguration.end_session_endpoint) {
          const data = {
            'refresh_token': context.state.auth.refresh_token
          }

          const encodedData = Querystring.stringify(data)

          const auth = { username: config.openid.clientId, password: config.openid.clientSecret }
          promise = rawClient.post(openidConfiguration.end_session_endpoint, encodedData, {
            auth: auth,
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
          })
        } else {
          promise = Promise.resolve()
        }

        return promise.then(() => {
          context.commit('setAuthPayload', null)
        })
      })
    }
  },
  getters: {
    auth (state) {
      return state.auth
    },
    accessToken (state) {
      if (state.auth) {
        return state.auth.access_token
      }
      return null
    },
    refreshToken (state) {
      if (state.auth) {
        const token = state.auth.refresh_token
        return token
      }
      return null
    }
  }
}
