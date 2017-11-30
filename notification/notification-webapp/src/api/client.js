import axios from 'axios'

import store from '../state/store'

export const clientFactory = () => {
  const client = axios.create()
  return client
}

const requestInterceptor = (config) => {
  const accessToken = store.getters['auth/accessToken']
  if (accessToken) {
    config.headers['Authorization'] = 'Bearer ' + accessToken
  }
  return config
}

const error401ResponseHandler = (error) => {
  if (error.response.status !== 401 || !error.response || !error.response.data || error.response.data.error !== 'invalid_token') {
    throw error
  }
  const requestConfig = error.config
  requestConfig.baseURL = null
  const refreshToken = store.getters['auth/refreshToken']
  console.log('Refreshing JWT ...')
  return store.dispatch('auth/refresh', { refresh_token: refreshToken })
    .then((response) => {
      console.log('JWT Refreshed !')
      return client.request(requestConfig)
    }).catch((error) => {
      console.log('JWT Refresh Failed !')
      if (error.response.data.error === 'invalid_grant') {
        console.log(error.response.data)
        store.commit('auth/setAuthPayload', null)
      }
      throw error
    })
}

const client = clientFactory()
client.interceptors.request.use(requestInterceptor)
client.interceptors.response.use((response) => {
  return response
}, error401ResponseHandler)

export default client

export const rawClient = clientFactory()
