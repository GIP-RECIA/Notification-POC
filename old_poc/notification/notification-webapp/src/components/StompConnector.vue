<template>
  <div>
    <b-badge variant="danger" v-if="errorState">{{errorState}}</b-badge>
    <b-badge variant="warning" v-if="connecting" class="font-weight-bold">Connecting ...</b-badge>
    <b-badge variant="success" v-if="connected && !connecting" class="font-weight-bold">Connected</b-badge>
    <b-badge variant="warning" v-if="!connected && !connecting" class="font-weight-bold">Not connected</b-badge>
    <template v-if="actionnable">
      <b-button size="sm" v-if="connected" @click="disconnectButtonClicked">
        <i class="fa fa-plug" aria-hidden="true"></i> Disconnect
      </b-button>
      <b-button size="sm" v-else @click="connectButtonClicked">
        <i class="fa fa-plug" aria-hidden="true"></i> Connect
      </b-button>
    </template>
  </div>
</template>

<script>
  import rabbitMQ from '../modules/rabbitmq'
  import {mapState, mapMutations} from 'vuex'

  export default {
    name: 'RabbitmqConnector',
    computed: {
      ...mapState('stomp', {
        'client': 'client',
        'connecting': 'connecting',
        'connected': 'connected',
        'errorState': 'error'
      }),
      ...mapState('auth', ['auth'])
    },
    props: {
      actionnable: false
    },
    methods: {
      connectButtonClicked () {
        rabbitMQ.connect(this, this.auth)
      },
      disconnectButtonClicked () {
        rabbitMQ.disconnect()
      },
      ...mapMutations('stomp', ['doConnect', 'doWillConnect', 'doDisconnect', 'doError'])
    },
    beforeDestroy () {
      if (this.client) {
        this.client.disconnect()
        this.client = null
      }
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
