<template>
</template>
<script>
  import {mapState} from 'vuex'

  export default {
    name: 'Main',
    computed: mapState('auth', ['auth']),
    mounted () {
      if (!this.auth) {
        return this.$router.push({name: 'Login'})
      }
    }
  }
</script>
<style scoped>
</style>
<style>
  .fade-enter-active, .fade-leave-active {
    transition: opacity .5s
  }

  .fade-enter, .fade-leave-to /* .fade-leave-active below version 2.1.8 */
  {
    opacity: 0
  }
</style>
