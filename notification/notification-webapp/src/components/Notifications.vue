<template>
  <div>Notifications</div>
</template>
<script>
  import { mapState } from 'vuex'

  export default {
    name: 'Notifications',
    data: function () {
      return {
        subscription: null
      }
    },
    props: {
      destination: String
    },
    computed: {
      ...mapState('stomp', ['error', 'client'])
    },
    watch: {
      'client.connected': {
        immediate: true,
        handler (client) {
          this.updateSubscription(this.destination)
        }
      },
      destination: function (destination) {
        this.updateSubscription(destination)
      }
    },
    methods: {
      updateSubscription (destination) {
        if (this.client && this.client.connected) {
          if (this.subscription) {
            this.subscription.unsubscribe()
            this.subscription = null
          }

          this.subscription = this.client.subscribe(destination, this.messageHandler)
        } else {
          this.subscription = null
        }
      },
      messageHandler: function (message) {
        if (message.headers['content-type'] === 'application/json' ||
          message.headers['content-type'] === 'application/json;charset=UTF-8') {
          console.log(message)
        } else {
          throw new Error('Unknown content type: ' + message.headers['content-type'])
        }
      }
    }
  }
</script>
<style scoped>
</style>
