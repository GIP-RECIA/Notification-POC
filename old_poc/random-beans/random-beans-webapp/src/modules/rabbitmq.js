import SockJS from 'sockjs-client'
import webstompClient from 'webstomp-client'

class RabbitMQ {
  connect (vm, websocketUrl, sockJSUrl, username, password) {
    this.vm = vm

    this.useSockJS = !window.WebSocket
    this.url = this.useSockJS ? sockJSUrl : websocketUrl

    if (this.useSockJS) {
      this.client = webstompClient.over(() => {
        return new SockJS(this.url, {debug: false})
      }, {debug: false})
    } else {
      this.client = webstompClient.client(this.url, {debug: false})
    }

    this.client.reconnect_delay = 5000

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

const rabbitMQ = new RabbitMQ()
export default rabbitMQ
