package part1recap

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
  val incrementer = new Function1[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }

  def main(args: Array[String]): Unit = {}
}
