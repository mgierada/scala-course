package com.rockthejvm.part1recap

object ContextualAbstractions {

  // given/using combo
  //
  // typiical way of doing that
  def increment(x: Int)(amount: Int) = x + amount

  // using a function as a parameter that when specified works as default valueA
  //
  def increment_with_using(x: Int)(using amount: Int) = x + amount
  given defaultAmount: Int = 10
  val incremented = increment(2) // 12

  // more comples usageA
  //
  trait Combiner[A] {
    def combine(x: A, y: A): A
    def empty: A
  }

  def combineAll[A](values: List[A])(using combiner: Combiner[A]): A =
    values.foldLeft(combiner.empty)(combiner.combine)

  given intCombiner: Combiner[Int] with {
    override def combine(x: Int, y: Int) = x + y
    override def empty: Int = 0
  }

  val numbers = (1 to 10).toList
  val sum10 = combineAll(numbers) // 55

  // How to deal Options
  given optionCombiner[T](using combiner: Combiner[T]): Combiner[Option[T]]
  with {
    override def empty: Option[T] = Some(combiner.empty)
    override def combine(x: Option[T], y: Option[T]): Option[T] = for {
      vx <- x
      vy <- y
    } yield combiner.combine(vx, vy)

  }

  val sumOption: Option[Int] = combineAll(
    List(Some(2), None, Some(4))
  ) // Some(9)

  // extentions methods
  case class Person(name: String) {
    def greet(): String = s"Hi, my name is $name"
  }

  extension (name: String) def greet(): String = Person(name).greet()

  val aliceGreeting = "Alice".greet() // "Hi, my name is Alice"

  // generic extension methods
  extension [A](list: List[A])
    def reduceAll(using combiner: Combiner[A]): A =
      list.foldLeft(combiner.empty)(combiner.combine)

  // reduceAll now exists on List because it was extended
  val sum10_v2 = numbers.reduceAll

  // typed classes

  def main(args: Array[String]): Unit = {}
}
