package com.rockthejvm.part2effects

import zio.*

import java.util.concurrent.TimeUnit

object ZIODependencies extends ZIOAppDefault {

  // Service definitions moved to ServiceModel.scala
  // reason: ZLayer.make and ZIO.provide use macros which mistakenly require the services to be in another source filer
  import ServiceModel.*
  /*
   * This is not the framework preffered approach - it's very general.
   */
  val subscriptionService =
    ZIO.succeed( // Dependency injection in ZIO (very general approach)
      UserSubscription.create( // we can use factory here
        EmailService.create(),
        UserDatabase.create(
          ConnectionPool.create(10)
        )
      )
    )

  /*
    drawbacks
    - risk leaking resources if you subscribe multiple user in hte same program
    - does not scale for many services
    - DI can be 100x worse
      - pass dependencies partially
      - not having all deps in the same place
      - passing dependencies multiple times
   */

  def subscribe(user: User): ZIO[Any, Throwable, Unit] = for {
    sub <- subscriptionService // service is instantiated at the point of call
    _ <- sub.subscribeUser(user)
  } yield ()

  // risk leaking resources if you subscribe multiple users in the same program
  val program = for {
    _ <- subscribe(User("Daniel", "daniel@rockthejvm.com"))
    _ <- subscribe(User("Bon Jovi", "jon@rockthejvm.com"))
  } yield ()

  // alternative
  def subscribe_v2(user: User): ZIO[UserSubscription, Throwable, Unit] = for {
    // ZIO.service returns a URIO
    //
    // difference comparing to subscribe - user if fetched from different place
    // end effect will have a different effect - here we return a ZIO
    // only able to call that when somone push user to it -> see program_v2
    sub <- ZIO.service[
      UserSubscription
    ] // ZIO[UserSubscription, Nothing, UserSubscription]
    _ <- sub.subscribeUser(user)
  } yield ()

  val program_v2 = for {
    _ <- subscribe_v2(User("Daniel", "daniel@rockthejvm.com"))
    _ <- subscribe_v2(User("Bon Jovi", "jon@rockthejvm.com"))
  } yield ()

  /*
    - we don't need to care about dependencies until the end of the world
    - all ZIOs requiring this dependency will use the same instance
    - can use different instances of the same type for different needs (e.g. testing)
    - layers can be created and composed much like regular ZIOs + rich API
   */

  /** ZLayers
    *
    * A cool construct to pass dependencies in nested dependencies graph
    * Usually, Zlayer requires some environment so Any could be more specific
    * but anyway, usually is not empty
    */
  val connectionPoolLayer: ZLayer[Any, Nothing, ConnectionPool] =
    ZLayer.succeed(ConnectionPool.create(10))
  // a layer that requires a dependency (higher layer) can be built with ZLayer.fromFunction
  // (and automatically fetch the function arguments and place them into the ZLayer's dependency/environment type argument)
  val databaseLayer: ZLayer[ConnectionPool, Nothing, UserDatabase] =
    // it auto fetches argument from the function and pushes it
    ZLayer.fromFunction(UserDatabase.create _)
  val emailServiceLayer: ZLayer[Any, Nothing, EmailService] =
    // we can pass the factory methods like that
    ZLayer.succeed(EmailService.create())

  // we can compose Zlayers (in contrast to ZIO api itself) so the final ZLayer will have all the dependencies and does not require any arguments
  val userSubscriptionServiceLayer
      : ZLayer[UserDatabase with EmailService, Nothing, UserSubscription] =
    ZLayer.fromFunction(UserSubscription.create _)

  // composing layers
  // vertical composition >>>
  val databaseLayerFull: ZLayer[Any, Nothing, UserDatabase] =
    connectionPoolLayer >>> databaseLayer
  // horizontal composition: combines the dependencies of both layers AND the values of both layers
  val subscriptionRequirementsLayer
      : ZLayer[Any, Nothing, UserDatabase with EmailService] =
    databaseLayerFull ++ emailServiceLayer
  // mix & match
  val userSubscriptionLayer: ZLayer[Any, Nothing, UserSubscription] =
    subscriptionRequirementsLayer >>> userSubscriptionServiceLayer

  // best practice: write "factory" methods exposing layers in the companion objects of the services
  // see ServiceModel.scala
  val runnableProgram = program_v2.provideLayer(userSubscriptionLayer)

  // magic - dependency graph is constructed automatically
  val runnableProgram_v2 = program_v2.provide(
    UserSubscription.live,
    EmailService.live,
    UserDatabase.live,
    ConnectionPool.live(10),
    // ZIO will tell you if you're missing a layer
    // and if you have multiple layers of the same type
    // and tell you the dependency graph!
    // ZLayer.Debug.tree,
    ZLayer.Debug.mermaid
    // can use either mermaind or tree at the same time
  )

  // magic v2
  val userSubscriptionLayer_v2: ZLayer[Any, Nothing, UserSubscription] =
    ZLayer.make[UserSubscription](
      UserSubscription.live,
      EmailService.live,
      UserDatabase.live,
      ConnectionPool.live(10)
    )

  // passthrough
  val dbWithPoolLayer
      : ZLayer[ConnectionPool, Nothing, ConnectionPool with UserDatabase] =
    UserDatabase.live.passthrough
  // service = take a dep and expose it as a value to further layers
  val dbService = ZLayer.service[UserDatabase]
  // launch = creates a ZIO that uses the services and never finishes
  val subscriptionLaunch
      : ZIO[EmailService with UserDatabase, Nothing, Nothing] =
    UserSubscription.live.launch
  // memoization

  /*
    Already provided services: Clock, Random, System, Console
   */
  val getTime = Clock.currentTime(TimeUnit.SECONDS)
  val randomValue = Random.nextInt
  val sysVariable = System.env("HADOOP_HOME")
  val printlnEffect = Console.printLine("This is ZIO")

  def run = runnableProgram_v2
}
