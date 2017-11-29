<template>
  <b-navbar toggleable="md" type="dark" variant="info">

    <b-navbar-toggle target="nav_collapse"></b-navbar-toggle>

    <b-navbar-brand :to="{name: 'Main'}">Notification</b-navbar-brand>

    <stomp-connector class="stomp-connector"
                        username="admin"
                        password="admin"
                        websocketUrl="ws://localhost:8082/notifications">
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
