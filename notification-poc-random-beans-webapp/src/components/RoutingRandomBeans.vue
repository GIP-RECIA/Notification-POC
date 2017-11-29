<template>
  <div class="random-beans-routing">
    <div class="pb-3" v-for="effectiveDestination in effectiveDestinations">
      <random-beans :destination="effectiveDestination"></random-beans>
    </div>
  </div>
</template>

<script>
  import RandomBeans from './RandomBeans'

  export default {
    name: 'AppHeader',
    components: {RandomBeans},
    props: {
      destination: String,
      routingKeys: Array
    },
    computed: {
      effectiveDestinations () {
        return this.routingKeys.map((routingKey) => this.destination + '/' + routingKey)
      }
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
