package cluster

object Server {

  @JvmStatic
  fun main(args: Array<String>) {
    Thread(PegServer()).start()
    Thread.sleep(60000)
    System.exit(0)
  }
}