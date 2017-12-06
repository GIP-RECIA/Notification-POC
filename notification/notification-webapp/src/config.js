export default {
  openid: {
    clientId: 'jwt',
    clientSecret: 'jwt',
    configuration: {
      token_endpoint: process.env.NODE_ENV === 'development' ? 'http://localhost:8080/oauth/token' : '/auth/oauth/token',
      end_session_endpoint: null
    }
  }
}
