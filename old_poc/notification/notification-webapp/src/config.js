export default {
  stomp: {
    username: 'admin',
    password: 'admin',
    url: process.env.NODE_ENV === 'development' ? 'ws://localhost:8082/stomp' : 'ws://' + window.location.host + '/emission/stomp'
  },
  emissionService: {
    url: process.env.NODE_ENV === 'development' ? 'http://localhost:8082' : '/emission'
  },
  openid: {
    // auth: { username: 'jwt', password: 'jwt' }, // Client ID/Secret
    configuration: {
      // token_endpoint: process.env.NODE_ENV === 'development' ? 'http://localhost:8080/oauth/token' : '/auth/oauth/token', // For Spring Authorization Server
      token_endpoint: process.env.NODE_ENV === 'development' ? 'http://localhost:11080/cas/oidc/accessToken?client_id=jwt' : '/cas/oidc/accessToken?client_id=jwt', // for CAS OpenID
      end_session_endpoint: null
    }
  }
}
