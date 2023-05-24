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

  // inheritance model: exted <=1 class but inherit >= 0 traits
  //
  class Crocodile extends Animal with Carnivore {
    override def eat(animal: Animal): Unit = println("Crunch!")
  }


  def main(args: Array[String]): Unit = {
  }
}
