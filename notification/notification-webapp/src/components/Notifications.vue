<template>
  <div class="notifications">
    <b-container>
      <b-row>
        <b-col class="text-center">
          <b-button variant="primary" @click="refresh"><i class="fa fa-refresh" aria-hidden="true"></i> Refresh</b-button>
        </b-col>
      </b-row>
      <transition name="fade">
        <b-row v-if="err" class="py-2">
          <b-col cols="6" class="m-auto text-center">
            <b-card bg-variant="danger" text-variant="white" title="An error has occured">
              {{ err.message }}
            </b-card>
          </b-col>
        </b-row>
      </transition>
      <transition name="fade">
        <b-row v-if="loading" class="py-2">
          <b-col cols="6" class="m-auto text-center">
            <b-card bg-variant="info" text-variant="white">
              <i class="fa fa-spinner fa-spin" aria-hidden="true"></i> Loading ...
            </b-card>
          </b-col>
        </b-row>
      </transition>
      <b-row v-if="!loading && !err">
        <b-col>
          <transition-group name="fade">
            <b-card class="mt-2 mb-2" v-for="(notification, index) in notifications" :key="index">
              <div slot="header">
                {{notification.title}}
              </div>
              <div>
                {{notification.message}}
              </div>
            </b-card>
          </transition-group>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>
<script>
  import {mapState, mapMutations} from 'vuex'
  import axios from '../api/client'
  import config from '../config'

  import rabbitMQ from '../modules/rabbitmq'

  export default {
    name: 'Notifications',
    data: function () {
      return {
        subscription: null,
        notifications: [],
        loading: false,
        err: null
      }
    },
    props: {
      destination: String
    },
    computed: {
      ...mapState('stomp', ['error', 'client', 'connected']),
      ...mapState('auth', ['auth'])
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
    mounted () {
      if (!this.auth) {
        return this.$router.push({name: 'Login'})
      }
      this.refresh()
      let client = rabbitMQ.connect(this, this.auth)
      console.log(client)
    },
    methods: {
      ...mapMutations('stomp', ['doConnect', 'doWillConnect', 'doDisconnect', 'doError']),
      refresh () {
        this.loading = true
        this.err = null
        return axios.get(config.emissionService.url + '/web/notifications').then(response => {
          this.notifications = response.data
        }).catch(err => {
          this.err = err
        }).finally(() => {
          this.loading = false
        })
      },
      updateSubscription (destination) {
        if (this.client && this.client.connected && this.client.subscribe) {
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
          this.notifications.splice(0, 0, JSON.parse(message.body))
        } else {
          throw new Error('Unknown content type: ' + message.headers['content-type'])
        }
      }
    }
  }
</script>
<style scoped>
  .fade-enter-active, .fade-leave-active {
    transition: opacity .5s
  }

  .fade-enter, .fade-leave-to /* .fade-leave-active below version 2.1.8 */
  {
    opacity: 0
  }
</style>
