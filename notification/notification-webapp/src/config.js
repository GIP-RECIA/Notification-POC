export default {
  openid: {
    clientId: 'jwt',
    clientSecret: 'jwt',
    configuration: {
      token_endpoint: 'http://localhost:8080/oauth/token',
      end_session_endpoint: null
    }
  }
}
