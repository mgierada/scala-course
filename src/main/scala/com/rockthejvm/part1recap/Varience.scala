package com.rockthejvm.part1recap

object Varience {

  // OOP substitution principle
  //
  class Animal
  class Dog(name: String) extends Animal

  // Varience question/problem for List: if Dog <: Animal, does List[Dog] <: List[Animal]?
  //
  //
  // 1. Yes, List[Dog] <: List[Animal] is called COVARIANCE
  val lassie = new Dog("Lassie")
  val hachi = new Dog("Hachi")
  val laika = new Dog("Laika")

  val anAnimal: Animal = lassie // widening (upcasting) = OK
  val someAnimal: List[Animal] = List(lassie, hachi, laika) // widening = OK

  class MyList[+A] // Covariant type specified by adding +
  val myAnimalList: MyList[Animal] = new MyList[Dog] // Covariance = OK

  // 2. No is called INVARIANCE
  //
  trait semiGroup[A] {
    def combine(x: A, y: A): A
  }

  // all generic in Java
  // val aJavaList: java.util.ArrayList[Animal] =
  //   new java.util.ArrayList[Dog] // Java is always covariant

  // 3. Hell, no! List[Dog] <: List[Animal] is called CONTRAVARIANCE

  trait Vet[-A] {
    def heal(animal: A): Boolean
  }

  // counterintuitive
  // Vet[Animal] is "better" then Vet[Dog]: she/he can treat ANY animal, therefore my dog as well
  // Dog <: Animal => Vet[Dogs] >: Vet[Animal]
  val myVet: Vet[Dog] = new Vet[Animal] {
    override def heal(animal: Animal): Boolean = {
      println("I'm healing your animal")
      true
    }
  }

  val healingLassie = myVet.heal(lassie) // widening = OK

  // How to pick the right variance?
  /*
   * Rule of thumb:
   *  - if the type PRODUCES or RETRIEVES values of type A (e.g. lists), then use COVARIANCE
   *  - if the type ACTS ON or CONSUMES the values of type A (e.g. functions, vet), then use CONTRAVARIANCE
   *  - otherwise, INVARIANCE
   */

  /** Variance positions
    */

  // class ver2[-A](
  //     val favouriteAnimal: A
  // ) // contravariant type  <-- the types of val fields are COVARIANT

  /*
    class Cat extends Animal
    val garfield = new Cat
    val theVet: Vet2[Animal] = new Vet2[Animal](garfield) // OK
    val dogVEgt: Vet2[Dog] = theVet // OK
    val favAnimal:Dog = dogVEgt.favouriteAnimal // widening = OK - must be a Dog type confict
   */

  // // ver fileds are COVARIANT positions (same)
  // class MutableContainer[+A](
  //     var content: A
  // ) // COVARIANT position <--- types of vars are in CONTRAVARIANCE position

  /*
    val container: MutableContainer[Animal] = new MutableContainer[Dog](new Dog)
    container.content= new Cat // widening = OK type conflict
    val cat: Cat = container.value // widening = OK
   */

  // var are always INVERIANT types
  // types of method arguments are in CONTRAVARIANCE position
  //
  // this will throw compile error
  // class Mylist2[+A] {
  //   def addAnimal(animal: A): Mylist2[A]
  // }
  //
  // this will work -> new type B that is a superset of A
  class Mylist2[+A] {
    def addAnimal[B >: A](animal: B): Mylist2[B] = ???
  }
  // types of method return types are in COVARIANCE position
  //
  // this will throw compile error
  // class Vet2[-A] {
  //   def rescueAnimal(): A
  //
  //
  // this will work - narrow the scope of type
  abstract class Vet2[-A] {
    def rescueAnimal[B<:A](): B
    
  }

  def main(args: Array[String]): Unit = {
    println("ZIO is set up, let's go!")
  }
}
