Utility for creating dummy data for Scala tests

## Installation

Add the following line to your build.sbt file:

```sbt
libraryDependencies += "com.alejandrohdezma" %% "dummy" % "0.3.0" % Test
```

## Usage

Create a new `dummy` object and add some dummy cases you want to use:

```scala
import com.alejandrohdezma.dummy.Dummy
import java.util.UUID
import scala.util.Random

object dummy {

  val dogs = Dummy(UUID.randomUUID())

  val cats = Dummy.withName(name => s"${Random.alphanumeric.take(5).mkString}-$name")

}
```

And then use it in your tests with any value you want (uses
[Scala's Dynamic](https://www.scala-lang.org/api/2.13.3/scala/Dynamic.html)
under the hood):

```scala
dummy.dogs.snoopy
// res0: UUID = d1d49a7e-fa32-4a36-9945-bbd8782075e8

dummy.dogs.`santa's-little-helper`
// res1: UUID = b3c4d4b7-7d9b-4912-bb35-e8194a995439

dummy.cats.garfield
// res2: String = "GWlgO-garfield"

dummy.cats.sylvester
// res3: String = "U1wAG-sylvester"
```

The key of these generators is that values are cached, so if we try to use the
same "key" twice, it will give us the same value:

```scala
dummy.dogs.snoopy
// res4: UUID = d1d49a7e-fa32-4a36-9945-bbd8782075e8

dummy.dogs.`santa's-little-helper`
// res5: UUID = b3c4d4b7-7d9b-4912-bb35-e8194a995439

dummy.cats.garfield
// res6: String = "GWlgO-garfield"

dummy.cats.sylvester
// res7: String = "U1wAG-sylvester"
```

### Accessing the cache

You can access the internal `Dummy` cache to see the values in
store.

```scala
dummy.dogs.cache.all
// res8: Map[String, UUID] = Map(
//   "snoopy" -> d1d49a7e-fa32-4a36-9945-bbd8782075e8,
//   "santa's-little-helper" -> b3c4d4b7-7d9b-4912-bb35-e8194a995439
// )

dummy.cats.cache.all
// res9: Map[String, String] = Map(
//   "sylvester" -> "U1wAG-sylvester",
//   "garfield" -> "GWlgO-garfield"
// )
```

## Contributors to this project 

| <a href="https://github.com/alejandrohdezma"><img alt="alejandrohdezma" src="https://avatars.githubusercontent.com/u/9027541?v=4&s=120" width="120px" /></a> |
| :--: |
| <a href="https://github.com/alejandrohdezma"><sub><b>alejandrohdezma</b></sub></a> |
