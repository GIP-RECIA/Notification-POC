<template>
  <b-navbar toggleable="md" type="dark" variant="info">
    <b-row>
      <b-col></b-col>
      <b-col></b-col>
    </b-row>
    <b-navbar-toggle target="nav_collapse"></b-navbar-toggle>

    <b-navbar-brand>Notifications</b-navbar-brand>

    <div class="ml-auto">
      <b-navbar-brand v-if="auth">
          <stomp-connector></stomp-connector>
      </b-navbar-brand>
      <b-navbar-brand>
        <login-header class="mx-2"></login-header>
      </b-navbar-brand>
    </div>
  </b-navbar>
</template>

<script>
  import StompConnector from './StompConnector.vue'
  import LoginHeader from './LoginHeader.vue'

  import {mapState} from 'vuex'

  export default {
    components: {StompConnector, LoginHeader},
    computed: mapState('auth', ['auth']),
    name: 'AppHeader'
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
