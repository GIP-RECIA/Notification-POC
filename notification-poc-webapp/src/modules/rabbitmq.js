import SockJS from 'sockjs-client'
import webstompClient from 'webstomp-client'

export default class RabbitMQ {
  constructor (vm, websocketUrl, sockJSUrl, useSockJS = null) {
    this.vm = vm
    this.useSockJS = (useSockJS !== null && useSockJS !== undefined) ? useSockJS : !window.WebSocket
    this.url = this.useSockJS ? sockJSUrl : websocketUrl

    if (this.useSockJS) {
      this.client = webstompClient.over(() => {
        return new SockJS(this.url, {debug: false})
      }, {debug: false})
    } else {
      this.client = webstompClient.client(this.url, {debug: false})
    }

    this.client.reconnect_delay = 5000
  }

  connect (username, password) {
    return new Promise((resolve, reject) => {
      this.vm.doWillConnect(this.client)
      this.client.connect(username, password, (success) => {
        this.vm.doConnect(this.client)
        resolve(this.client, success)
      }, (error) => {
        this.vm.doError(error)
        reject(error)
      })
      return this.client
    })
  }

  disconnect () {
    this.client.disconnect()
    this.vm.doDisconnect()
  }
}
