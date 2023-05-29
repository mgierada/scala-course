package com.rockthejvm.part2effects

import zio._

object ZIOEffects {

  // Notation similar as for simplified ZIO IO
  val meaningOfLife: ZIO[Any, Nothing, Int] = ZIO.succeed(42)
  val aFailure: ZIO[Any, String, Nothing] = ZIO.fail("SWW")
  val aSuspendedZIO: ZIO[Any, Throwable, Int] = ZIO.suspend(meaningOfLife)

  // map and flatMap
  val improvedMOL = meaningOfLife.map(_ * 2)
  val printingMOL = meaningOfLife.flatMap(v => ZIO.succeed(println(v)))

  // and because be have map and flatMap, we also have for comprehensions
  val smallProgram = for {
    _ <- ZIO.succeed(println("What's your name?"))
    name <- ZIO.succeed(scala.io.StdIn.readLine())
    _ <- ZIO.succeed(println(s"Hello, $name!"))
  } yield ()

  // A LOT of cobminators
  // zip
  val anotherMOL = ZIO.succeed(100)
  val tupledZIO = meaningOfLife.zip(anotherMOL) // ZIO[Any, Nothing, (Int, Int)]
  val conbimedZIO =
    meaningOfLife.zipWith(anotherMOL)(_ + _) // ZIO[Any, Nothing, Int]

  /** Type aliases for ZIO
    */

  // UIO - ZIO[Any, Nothing, A] - no error, no environment, no requirements,cannot failed, produces A
  val aUIO: UIO[Int] = ZIO.succeed(42)

  // URIO[R, A] = ZIO[R, Nothing, A] - no error, requires an environment of type R, produces A
  val aURIO: URIO[String, Int] = ZIO.succeed(42)

  // TASK [A] = ZIO[Any, Throwable, A] - no environment, can fail with Throwable, produces A
  val aSuccessfulTask: Task[Int] = ZIO.succeed(82)
  val aFailedTask: Task[Int] = ZIO.fail(new RuntimeException("SWW"))

  // IO[E, A] = ZIO[Any, E, A] - no environment, can fail with E, produces A
  // val aSuccessfulIO = IO[String, Int](42)
  // val aFailedIO = IO[String, Int](new RuntimeException("SWW"))

  // RIO[R, A] = ZIO[R, Throwable, A] - requires an environment of type R, can fail with Throwable, produces AS
  val anRIO: RIO[Int, String] = ZIO.succeed("4")
  val aFailedRIO: RIO[Int, String] = ZIO.fail(new RuntimeException("SWW"))

  /** ZIO Exercises
    */

  // 1. Sequence two ZIOs and take the value of the second one
  def seqnecneTakeLast[R, E, A, B](
      zioa: ZIO[R, E, A],
      ziob: ZIO[R, E, B]
  ): ZIO[R, E, B] = zioa.flatMap(a => ziob.map(b => b))

  // 2. Sequence two ZIOs and take the value of the first one
  def sequenceTeakeFirst[R, E, A, B](
      first: ZIO[R, E, A],
      second: ZIO[R, E, B]
  ): ZIO[R, E, A] = first.flatMap(a => second.map(_ => a))

  // 3. Run a ZIO forever
  // def runForever[R, E, A](zio: ZIO[R, E, A]): ZIO[R, E, A] = ???
  // val endlessLoop = runForever {
  //   ZIO.succeed {
  //     Thread.sleep(100)
  //   }
  // }

  // 4. Convert the value of a ZIO to something else
  // def conver[R, E, A, B](zio: ZIO[R, A, B], value: B): ZIO[R, E, B] = ???

  // 5. Discard the value of a ZIO
  // def asUnit[R, A, E](zio: ZIO[R, E, A]): ZIO[R, E, Unit] = ???

  def main(args: Array[String]): Unit = {
    val runtime = Runtime.default

    val firstEffect = ZIO.succeed {
      println("Computing first effect")
      Thread.sleep(1000)
      1
    }

    val secondEffect = ZIO.succeed {
      println("Computing second effect")
      Thread.sleep(1000)
      2
    }

    val program =
      for result <- seqnecneTakeLast(firstEffect, secondEffect)
      yield result

    val programResult =  program.runtime.unsafe()
    println(programResult)

  }

}
