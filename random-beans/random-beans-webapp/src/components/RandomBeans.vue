<template>
  <div class="random-beans">
    <div class="container">
      <div class="row">
        <div class="col">
          <b-card>
            <div slot="header" class="text-info">
              <div class="float-left">
                <div>
                  <span class="font-weight-bold">{{ destination }}</span>
                </div>
                <div>
                  <span><span class="font-weight-bold">{{ eventsCount }}</span> messages</span>
                  <span v-if="!isNaN(eventsPerSecond)">({{ eventsPerSecond }}/s)</span>
                </div>
              </div>
              <div class="float-right">
                <div class="text-right">
                  <span v-if="subscription">Subscribed: {{ subscription.id }}</span>
                  <span v-else class="text-danger">Not subscribed</span>
                </div>
                <div class="text-right">
                  <span>{{ randomBeans.length }} element<template
                    v-if="randomBeans.length > 1">s</template></span>
                </div>
              </div>
            </div>
            <div class="row py-3" v-if="lastDeletedBean">
              <div class="col">
                <b-card no-body class="card-synchronized">
                  <b-card-body class="bg-danger py-1 px-2 text-light">
                      <span :title="lastDeletedBean.uuid">
                        <span class="font-weight-bold">[deleted]</span>
                        <span class="font-weight-bold">{{lastDeletedBean.title}}: </span>
                        <span>{{lastDeletedBean.description}}</span>
                        <span>({{lastDeletedBean.random}})</span>
                      </span>
                  </b-card-body>
                </b-card>
              </div>
            </div>
            <div class="container-fluid px-0">
              <div class="row">
                <div class="col">
                  <b-card no-body v-for="randomBean in randomBeans" :key="randomBean.uuid"
                          :class="{'bg-success': randomBean === lastCreatedBean, 'bg-warning': randomBean === lastUpdatedBean}">
                    <b-card-body class="py-1 px-2">
                    <span :title="randomBean.uuid">
                      <span class="font-weight-bold">{{randomBean.title}}: </span>
                      <span>{{randomBean.description}}</span>
                      <span>({{randomBean.random}})</span>
                    </span>
                    </b-card-body>
                  </b-card>
                </div>
              </div>
            </div>
          </b-card>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
  import { mapState } from 'vuex'

  const randomBeansMap = {}

  export default {
    name: 'RandomBeans',
    data: function () {
      return {
        randomBeans: [],
        lastCreatedBean: null,
        lastUpdatedBean: null,
        lastDeletedBean: null,
        eventsCount: 0,
        startTime: null,
        lastTime: null,
        subscription: null
      }
    },
    props: {
      destination: String
    },
    computed: {
      eventsPerSecond: function () {
        return ((this.eventsCount / (this.lastTime - this.startTime)) * 1000).toFixed(2)
      },
      ...mapState(['error', 'client'])
    },
    watch: {
      'client.connected': {
        immediate: true,
        handler (client) {
          this.updateSubscription(this.destination)
        }
      },
      destination: function (destination) {
        this.randomBeans = []
        this.lastCreatedBean = null
        this.lastUpdatedBean = null
        this.lastDeletedBean = null
        this.eventsCount = 0
        this.startTime = null
        this.lastTime = null

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
        if (message.headers['content-type'] === 'application/json') {
          const json = JSON.parse(message.body)
          this.eventsCount++
          this.lastTime = new Date().getTime()
          if (!this.startTime) {
            this.startTime = this.lastTime
          }
          console.log(`Event ${message.headers['type']} received for RandomBean{uuid='${json['uuid']}'}`)

          if (message.headers['type'] === 'CREATED') {
            randomBeansMap[json.uuid] = json
            this.randomBeans.push(json)
            this.lastCreatedBean = json
          } else if (message.headers['type'] === 'DELETED') {
            const oldValue = randomBeansMap[json.uuid]
            const indexOf = this.randomBeans.indexOf(oldValue)
            if (indexOf > -1) {
              this.randomBeans.splice(indexOf, 1)
              this.lastDeletedBean = oldValue
              delete randomBeansMap[oldValue.uuid]
            }
          } else if (message.headers['type'] === 'UPDATED') {
            const oldValue = randomBeansMap[json.uuid]
            const indexOf = this.randomBeans.indexOf(oldValue)
            if (indexOf > -1) {
              this.randomBeans[indexOf] = json
            } else {
              this.randomBeans.push(json)
            }
            this.lastUpdatedBean = json
            randomBeansMap[json.uuid] = json
          }
        } else {
          throw new Error('Unknown content type: ' + message.headers['content-type'])
        }
      }
    }
  }
</script>
<style scoped>
</style>
