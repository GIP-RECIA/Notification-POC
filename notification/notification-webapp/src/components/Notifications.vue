<template>
  <div class="notifications">
    <b-container>
      <b-row>
        <b-col>
          <b-card v-for="notification in notifications" class="mt-2 mb-2">
            <div slot="header">
              {{notification.content.title}}
            </div>
            <div>
              {{notification.content.message}}
            </div>
          </b-card>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>
<script>
  import {mapState} from 'vuex'

  export default {
    name: 'Notifications',
    data: function () {
      return {
        subscription: null,
        notifications: []
      }
    },
    props: {
      destination: String
    },
    computed: {
      ...mapState('stomp', ['error', 'client', 'connected'])
    },
    watch: {
      'connected': {
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
          this.notifications.push(JSON.parse(message.body))
        } else {
          throw new Error('Unknown content type: ' + message.headers['content-type'])
        }
      }
    }
  }
</script>
<style scoped>
</style>
