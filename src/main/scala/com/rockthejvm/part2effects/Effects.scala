package com.rockthejvm.part2effects

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global
import zio.Runtime.unsafe
object Effects {

  // think about that everything in Scala is an expression, e.g..
  //
  def combine(a: Int, b: Int): Int = {
    val result = a + b
    result
  }

  val five = combine(2, 3)

  val five_2 = 2 + 3
  val five_3 = 5

// side effects are inevitable

  /*
    Effects desires:
   - they type signature describes what KIND of computation it will perform
   - the type signature describes the type of VALUE  that it will produce
   - if side effect are required, construction must be separate from EXECUTION
   */


  val anOption: Option[Int] = Some(42)
  /*
    Example: Future
    - describes an asynchronous computatiopn
    - produces a value of type A, if it finishes and it's successful
    - side effects are needed to start the computation. Construction is NOT SEPARATE form execution
   */

  val aFurture: Future[Int] = Future {
    42
  }

  /*
   Example 3: MyIO
   - describes computation which might perform side effects A
   - produces a value of type A if the computation is successful
   - side effects are required, construction IS SEPARATE form execution

    MyIO IS AN EFFECT!
   */

  case class MyIO[A](unsafeRun: () => A) {
    def map[B](f: A => B): MyIO[B] = MyIO(() => f(unsafeRun()))
    def flatMap[B](f: A => MyIO[B]): MyIO[B] =
      MyIO(() => f(unsafeRun()).unsafeRun())
  }

  val anIOWithSIdeEffects: MyIO[Int] = MyIO(() => {
    println("I am printing something")
    42
  })

  /** Exercises - create some IO which
    *   1. measure the current time of the sytem 2. measure the duration of a
    *      computation
    *      - use exercise the previous section
    *      - use map, flatMap combinations of MyIO 3. read something from the
    *        console 4. print something to the console (e.g. your name), then
    *        read, then print again
    */

// 1. measure the current time of the sytem
  val currentTime: MyIO[Long] = MyIO(() => System.currentTimeMillis())

// 2. measure the duration of a computation
  def measure[A](computation: MyIO[A]): MyIO[(A, Long)] = for {
    start <- currentTime
    result <- computation
    end <- currentTime
  } yield (result, end - start)

  // 3. read something from the console
  val readSomething: MyIO[String] = MyIO(() => {
    scala.io.StdIn.readLine()
  })

  // 4. print something to the console (e.g. your name), then read, then print again
  val printReadPrint: MyIO[Unit] = for {
    _ <- MyIO(() => println("What's your name?"))
    _ <- readSomething
    _ <- MyIO(() => println("Thanks!"))
  } yield ()

  /** A simplified version of ZIO Effects - what if some exceptions are thrown?
    *
    * R is an environment, E is an error, A is a value return if all is fine
    *
    * R consumer of MyZIO so it's contravariant
    * A is producer of MyZIO so it's covariant
    * E is an error produced, so it's covariant
    */

  case class MyZIO[-R, +E, +A](unsafeRun: R => Either[E, A]) {
    def map[B](f: A => B): MyZIO[R, E, B] = MyZIO(r =>
      unsafeRun(r) match {
        case Left(e)  => Left(e)
        case Right(v) => Right(f(v))
      }
    )
    def flatMap[R1<: R, E1 >: E, B](f: A => MyZIO[R1, E1, B]): MyZIO[R1, E1, B] =
      MyZIO(r =>
        unsafeRun(r) match {
          case Left(e)  => Left(e)
          case Right(v) => f(v).unsafeRun(r)
        }
      )
  }

  def main(args: Array[String]): Unit = {
    println("Effects")
    anIOWithSIdeEffects.unsafeRun()
    println(measure(MyIO(() => 42)).unsafeRun())
    printReadPrint.unsafeRun()
  }
}
