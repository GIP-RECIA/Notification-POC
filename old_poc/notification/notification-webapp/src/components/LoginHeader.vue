<template>
  <div v-if="auth">
    <b-badge variant="success">{{accessTokenDecoded.user_name}}</b-badge>
    <b-badge variant="success">{{accessTokenDecoded.user_uuid}}</b-badge>
    <b-button variant="warning" size="sm" @click="doLogout()">Logout</b-button>
  </div>
  <div v-else>
    <b-badge>Anonymous</b-badge>
    <b-button variant="warning" size="sm" @click="doLogin()">Login</b-button>
  </div>
</template>

<script>
  import {mapActions, mapState} from 'vuex'

  export default {
    name: 'LoginHeader',
    computed: mapState('auth', ['auth', 'accessTokenDecoded']),
    methods: {
      ...mapActions('auth', ['logout']),
      doLogout () {
        this.logout().then(() => {
          return this.$router.push({name: 'Login'})
        })
      },
      doLogin () {
        this.$router.push({name: 'Login'})
      }
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
