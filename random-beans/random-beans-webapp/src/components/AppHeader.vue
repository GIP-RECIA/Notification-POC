<template>
  <b-navbar toggleable="md" type="dark" variant="info">

    <b-navbar-toggle target="nav_collapse"></b-navbar-toggle>

    <b-navbar-brand :to="{name: 'Main'}"><i class="fa fa-home" aria-hidden="true" title="Random Beans"></i></b-navbar-brand>

    <b-navbar-nav>
      <b-nav-item :to="{name: 'ExchangeDirect'}">Direct</b-nav-item>
      <b-nav-item :to="{name: 'ExchangeFanout'}">Fanout</b-nav-item>
      <b-nav-item :to="{name: 'ExchangeRouting'}">Routing</b-nav-item>
      <b-nav-item :to="{name: 'ExchangeTopic'}">Topic</b-nav-item>
    </b-navbar-nav>


    <b-collapse is-nav id="nav_collapse">

    </b-collapse>

    <rabbitmq-connector class="rabbitmq-connector"
                        username="admin"
                        password="admin"
                        websocketUrl="ws://localhost:8088/rabbitmq/ws"
                        sockJSUrl="http://localhost:8088/rabbitmq/stomp">
    </rabbitmq-connector>
  </b-navbar>
</template>

<script>
  import RabbitmqConnector from './RabbitmqConnector.vue'

  export default {
    components: {RabbitmqConnector},
    name: 'AppHeader'
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  .rabbitmq-connector {
    float: right;
  }
</style>
