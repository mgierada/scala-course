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

  /*
    Example: Options=  possible absetn valuees:
    - type signature describes the kind of computations = a possibly absent value
    - type signature says that the computations returns an A, if the computations produce something
    - no side effects are needed.
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

   MyIO is the most general effect type in Scala that we can imagine - desire in pure functional programming.
   */

  case class MyIO[A](unsafeRun:()=> A) {
    def map[B](f: A => B): MyIO[B] = MyIO(() => f(unsafeRun()))
    def flatMap[B](f: A => MyIO[B]): MyIO[B] = MyIO(() => f(unsafeRun()).unsafeRun())
  }

  val anIOWithSIdeEffects : MyIO[Int] = MyIO(() => {
    println("I am printing something")
    42
  })

  def main(args: Array[String]): Unit = {
    println("Effects")
    anIOWithSIdeEffects.unsafeRun()
  }
}
