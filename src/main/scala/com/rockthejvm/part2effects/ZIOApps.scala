package com.rockthejvm.part2effects

import zio._

object ZIOApps {

  val meaningOfLife: UIO[Int] = ZIO.succeed(42)

  def main(args: Array[String]): Unit = {
    val runtime = Runtime.default
    given trace: Trace = Trace.empty
    Unsafe.unsafeCompat { unsafe =>
      given u: Unsafe = unsafe

      BetterApp.main(Array.empty)

      // println(runtime.unsafe.run(meaningOfLife))
    }
  }
}

object BetterApp extends ZIOAppDefault {
  // provides runtime, trace, ...
  // It provides a single method that we can override: run
  // It is a runnable application that evaluates to a single effect

  override def run = ZIOApps.meaningOfLife.flatMap(mol => ZIO.succeed(println(mol)))
}

// not needed for 99.999% of the cases
object ManualApp extends ZIOApp {
  override implicit def environmentTag = ???

  override type Environment = this.type

  override def bootstrap = ???

  override def run = ???
}
