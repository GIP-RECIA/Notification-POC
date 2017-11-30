<template>
  <b-navbar toggleable="md" type="dark" variant="info">

    <b-navbar-toggle target="nav_collapse"></b-navbar-toggle>

    <b-navbar-brand :to="{name: 'Main'}"><i class="fa fa-home" title="Notification" aria-hidden="true"></i></b-navbar-brand>
    <b-navbar-brand :to="{name: 'Login'}">Login</b-navbar-brand>
    <b-navbar-brand :to="{name: 'Notifications'}">Notifications</b-navbar-brand>

    <stomp-connector class="stomp-connector"
                        username="admin"
                        password="admin"
                        websocketUrl="ws://localhost:8082/stomp">
    </stomp-connector>
  </b-navbar>
</template>

<script>
  import StompConnector from './StompConnector.vue'

  export default {
    components: {StompConnector},
    name: 'AppHeader'
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  .rabbitmq-connector {
    float: right;
  }
</style>
