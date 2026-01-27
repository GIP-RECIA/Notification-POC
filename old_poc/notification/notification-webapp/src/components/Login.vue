<template>
<div class="login">
  <b-container>
    <b-row>
      <b-col class="mx-auto" md="4">
        <b-card class="login__card" :class="{'login__card--error': error}">
          <h5 slot="header" class="mb-0">Connexion</h5>
          <b-form @submit.prevent="submit">
            <b-form-group class="login__username-group">
              <b-form-input type="text" v-model="form.username" required placeholder="Nom d'utilisateur"></b-form-input>
            </b-form-group>
            <b-form-group class="login__password-group">
              <b-form-input type="password" v-model="form.password" required placeholder="Mot de passe"></b-form-input>
            </b-form-group>
            <b-button variant="primary" class="login__submit-button" type="submit">Se connecter</b-button>
          </b-form>
          <div slot="footer" v-if="error" class="login__footer"><span class="login__message--error">{{error}}</span></div>
        </b-card>
      </b-col>
    </b-row>
  </b-container>
</div>
</template>
<script>
  import {mapState, mapActions} from 'vuex'

  export default {
    name: 'Login',
    data: function () {
      return {
        error: null,
        form: {
          username: null,
          password: null
        }
      }
    },
    computed: mapState('auth', ['auth']),
    methods: {
      ...mapActions('auth', ['login']),
      submit () {
        this.login(this.form).then(() => {
          this.error = null
          this.$router.push({ name: 'Notifications' })
        }).catch((err) => {
          if (err.response && err.response.data && err.response.data.error_description) {
            this.error = err.response.data.error_description
          } else if (err.response && err.response.data && err.response.data.error) {
            this.error = err.response.data.error
          } else {
            this.error = err.message
          }
          throw err
        })
      }
    }
  }
</script>
<style lang="scss" scoped>
  @import '~bootstrap/scss/functions';
  @import '~bootstrap/scss/variables';
  @import '~bootstrap/scss/mixins';
  @import '~bootstrap/scss/utilities';
  @import '~bootstrap/scss/alert';

  .login__card {
    &.login__card {
      .card-header {
        @extend .alert-primary
      }
    }

    &.login__card--error {
      .card-footer {
        @extend .alert-danger
      }
    }
  }
</style>
