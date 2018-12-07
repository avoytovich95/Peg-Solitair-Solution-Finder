package solver

import java.util.concurrent.RecursiveTask

class Fibonacci(private val n: Int) : RecursiveTask<Int>() {
  override fun compute(): Int {
    if (n <= 1)
      return n
    val f1 = Fibonacci(n - 1)
    f1.fork()
    val f2 = Fibonacci(n - 2)

    val toReturn = f2.invoke() + f1.join()
    println(toReturn)
    return toReturn
  }
}

fun main(args: Array<String>) {
  val f = Fibonacci(30)
  f.invoke()
  println("Result: ${f.rawResult}")
}
