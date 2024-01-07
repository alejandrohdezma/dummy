Utility for creating dummy data for Scala tests

## Installation

Add the following line to your build.sbt file:

```sbt
libraryDependencies += "com.alejandrohdezma" %% "dummy" % "0.6.0" % Test
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

  val dates = Dummy.fromNaturalLanguageDate()

}
```

And then use it in your tests with any value you want (uses
[Scala's Dynamic](https://www.scala-lang.org/api/2.13.3/scala/Dynamic.html)
under the hood):

```scala
dummy.dogs.snoopy
// res0: UUID = f4e5da9e-fb51-4d0d-900d-2559bd781b1d

dummy.dogs.`santa's-little-helper`
// res1: UUID = cd6ac753-3d3c-4414-a5e6-d371484803b0

dummy.cats.garfield
// res2: String = "2DtX6-garfield"

dummy.cats.sylvester
// res3: String = "c9Z0R-sylvester"

dummy.dates.`3 days ago`
// res4: java.time.Instant = 2024-01-04T14:12:08.716113988Z

dummy.dates.yesterday
// res5: java.time.Instant = 2024-01-06T14:12:08.716382231Z
```

The key of these generators is that values are cached, so if we try to use the
same "key" twice, it will give us the same value:

```scala
dummy.dogs.snoopy
// res6: UUID = f4e5da9e-fb51-4d0d-900d-2559bd781b1d

dummy.dogs.`santa's-little-helper`
// res7: UUID = cd6ac753-3d3c-4414-a5e6-d371484803b0

dummy.cats.garfield
// res8: String = "2DtX6-garfield"

dummy.cats.sylvester
// res9: String = "c9Z0R-sylvester"

dummy.dates.`3 days ago`
// res10: java.time.Instant = 2024-01-04T14:12:08.716113988Z

dummy.dates.yesterday
// res11: java.time.Instant = 2024-01-06T14:12:08.716382231Z
```

### Accessing the cache

You can access the internal `Dummy` cache to see the values in
store.

```scala
dummy.dogs.cache.all
// res12: Map[String, UUID] = Map(
//   "snoopy" -> f4e5da9e-fb51-4d0d-900d-2559bd781b1d,
//   "santa's-little-helper" -> cd6ac753-3d3c-4414-a5e6-d371484803b0
// )

dummy.cats.cache.all
// res13: Map[String, String] = Map(
//   "sylvester" -> "c9Z0R-sylvester",
//   "garfield" -> "2DtX6-garfield"
// )

dummy.dates.cache.all
// res14: Map[String, java.time.Instant] = Map(
//   "yesterday" -> 2024-01-06T14:12:08.716382231Z,
//   "3 days ago" -> 2024-01-04T14:12:08.716113988Z
// )
```

## Contributors to this project 

| <a href="https://github.com/alejandrohdezma"><img alt="alejandrohdezma" src="https://avatars.githubusercontent.com/u/9027541?v=4&s=120" width="120px" /></a> |
| :--: |
| <a href="https://github.com/alejandrohdezma"><sub><b>alejandrohdezma</b></sub></a> |
