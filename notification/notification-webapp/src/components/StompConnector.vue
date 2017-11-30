<template>
  <div class="rabbitmq-connector">
    <b-badge variant="danger" v-if="errorState">{{errorState}}</b-badge>
    <b-badge variant="warning" v-if="connecting" class="font-weight-bold">Connecting ...</b-badge>
    <b-badge variant="success" v-if="connected && !connecting" class="font-weight-bold">Connected</b-badge>
    <b-badge variant="warning" v-else class="font-weight-bold">Not connected</b-badge>
    <b-button size="sm" v-if="connected" @click="disconnectButtonClicked">
      <i class="fa fa-plug" aria-hidden="true"></i> Disconnect
    </b-button>
    <b-button size="sm" v-else @click="connectButtonClicked">
      <i class="fa fa-plug" aria-hidden="true"></i> Connect
    </b-button>
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
    methods: {
      connectButtonClicked () {
        const accessToken = this.auth ? this.auth.access_token : undefined
        let url = this.websocketUrl
        if (accessToken) {
          url += '?access_token=' + accessToken
        }
        rabbitMQ.connect(this, url, this.sockJSUrl, this.username, this.password)
      },
      disconnectButtonClicked () {
        rabbitMQ.disconnect()
      },
      ...mapMutations('stomp', ['doConnect', 'doWillConnect', 'doDisconnect', 'doError'])
    },
    props: {
      username: String,
      password: String,
      websocketUrl: String,
      sockJSUrl: String
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
