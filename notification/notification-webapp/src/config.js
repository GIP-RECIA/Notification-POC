export default {
  stomp: {
    username: 'admin',
    password: 'admin',
    url: process.env.NODE_ENV === 'development' ? 'ws://localhost:8082/stomp' : 'ws://localhost:8082/stomp'
  },
  emissionService: {
    url: process.env.NODE_ENV === 'development' ? 'http://localhost:8082' : '/emission'
  },
  openid: {
    clientId: 'jwt',
    clientSecret: 'jwt',
    configuration: {
      token_endpoint: process.env.NODE_ENV === 'development' ? 'http://localhost:8080/oauth/token' : '/auth/oauth/token',
      end_session_endpoint: null
    }
  }
}
