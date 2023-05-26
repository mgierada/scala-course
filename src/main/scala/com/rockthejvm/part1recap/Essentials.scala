package part1recap

import scala.util.Try
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.util.Success
import scala.util.Failure
import java.util.concurrent.Executors

object Essentials {

  // values
  val aBoolean: Boolean = false

  // expressions are EVALUATED to a value
  val anIfExpression = if (2 > 3) "bigger" else "smaller"

  // instructions - represented as Unit type
  val theUnit = println("Hello Scala") // Unit

  // OOP
  class Animal
  class Cat extends Animal

  // Traits are like interfaces in JavaScript
  trait Carnivore {
    def eat(a: Animal): Unit
  }

  // inheritance model: extend <=1 class but inherit >= 0 traits
  class Crocodile extends Animal with Carnivore {
    override def eat(animal: Animal): Unit = println("Crunch!")
  }

  // singleton pattern Singleton can contain only one value
  object MySingleton // the only instance of the MySingleton type

  // companions - similar to JAVA static methods
  object Carnivore // companion object of the Carnivore trait

  // generics
  class MyList[A] // a list of elements of type A

  // method notation
  val three = 1 + 2
  val anotherThree = 1.+(2) // equivalent infix notation

  // functional programming
  val incrementer_old_pattern_anonymous_fn = new Function1[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }

  val incremeter = (x: Int) => x + 1 // anonymous function (lambda) new patern

  // higher order functions (map and flatMap or filter)
  val processedList = List(1, 2, 3).map(incremeter) // List(2, 3, 4)

  val aLongerList =
    List(1, 2, 3).flatMap(x => List(x, x + 1)) // List(1, 2, 2, 3, 3, 4)

  // options and try

  val anOption: Option[Int] = Option(
    2
  ) // Two subtypes Some and None. Can use as List
  val doubledOption = anOption.map(_ * 2) // Some(4) but if none provided, None

  val onAttempt =
    Try(
      2
    ) // Try is a subtype of Success and Failure
  // Try works similar as List and Options

  val aModifiedAttempt: Try[Int] = onAttempt.map(_ * 6) // Success or Failure

  // patern matching - switch case on steroids
  val anUnknown: Any = 2
  val ordinal = anUnknown match {
    case 1 => "first"
    case 2 => "second"
    case _ => "unknown"
  }

  val optionDescription: String = anOption match {
    case Some(value) => s"the option is not empty: $value"
    case None        => "the option is empty"
  }

  // async as Futures - has to implement implicit ExecutionContext
  //
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(
    Executors.newFixedThreadPool(8)
  )

  val aFuture = Future {
    42
  }

  // wait for completion (async - await) with some pattern matching - this is called partial functions
  aFuture.onComplete {
    case Success(value)     => println(s"the meaning of life is $value")
    case Failure(exception) => println(s"I have failed: $exception")
  }

  // map a Future easily
  val anotherFuture = aFuture.map(_ + 1) // Future(43)

  // for comprehensions usuful for chaining Futures
  // instead
  val checkerBoard =
    List(1, 2, 3).flatMap(n => List('a', 'b', 'c').map(c => (n, c)))

  // we can use for comprehension - this is equivalent - available for Options, Try, Futures, Lists
  val anouterChecker = for {
    n <- List(1, 2, 3)
    c <- List('a', 'b', 'c')
  } yield (n, c)



  // PARTIAL FUNCTIONS also in pattern matching in Futures
  // Do not accept argument of type int byt just pattern matching
  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 65
    case 5 => 999
  }


  // some more advanced stuff
  //
  trait HigherKindedType[F[_]] // a type constructor which takes a type
  trait SequenceChecker[F[_]] {
    // def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
    def isSequential = true
  }
  

  def main(args: Array[String]): Unit = {}
}
