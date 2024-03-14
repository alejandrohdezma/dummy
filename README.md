Utility for creating dummy data for Scala tests

## Installation

Add the following line to your build.sbt file:

```sbt
libraryDependencies += "com.alejandrohdezma" %% "dummy" % "0.6.1" % Test
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
// res0: UUID = 8e8cae16-a1e0-4ced-bb30-70b962c9d08a

dummy.dogs.`santa's-little-helper`
// res1: UUID = d6829a6f-b34f-429f-ae44-04c0fa17ba7a

dummy.cats.garfield
// res2: String = "uNaql-garfield"

dummy.cats.sylvester
// res3: String = "kKVkq-sylvester"

dummy.dates.`3 days ago`
// res4: java.time.Instant = 2024-03-11T12:09:04.354561159Z

dummy.dates.yesterday
// res5: java.time.Instant = 2024-03-13T12:09:04.354902729Z
```

The key of these generators is that values are cached, so if we try to use the
same "key" twice, it will give us the same value:

```scala
dummy.dogs.snoopy
// res6: UUID = 8e8cae16-a1e0-4ced-bb30-70b962c9d08a

dummy.dogs.`santa's-little-helper`
// res7: UUID = d6829a6f-b34f-429f-ae44-04c0fa17ba7a

dummy.cats.garfield
// res8: String = "uNaql-garfield"

dummy.cats.sylvester
// res9: String = "kKVkq-sylvester"

dummy.dates.`3 days ago`
// res10: java.time.Instant = 2024-03-11T12:09:04.354561159Z

dummy.dates.yesterday
// res11: java.time.Instant = 2024-03-13T12:09:04.354902729Z
```

### Accessing the cache

You can access the internal `Dummy` cache to see the values in
store.

```scala
dummy.dogs.cache.all
// res12: Map[String, UUID] = Map(
//   "snoopy" -> 8e8cae16-a1e0-4ced-bb30-70b962c9d08a,
//   "santa's-little-helper" -> d6829a6f-b34f-429f-ae44-04c0fa17ba7a
// )

dummy.cats.cache.all
// res13: Map[String, String] = Map(
//   "sylvester" -> "kKVkq-sylvester",
//   "garfield" -> "uNaql-garfield"
// )

dummy.dates.cache.all
// res14: Map[String, java.time.Instant] = Map(
//   "yesterday" -> 2024-03-13T12:09:04.354902729Z,
//   "3 days ago" -> 2024-03-11T12:09:04.354561159Z
// )
```

There are also available some convenient methods to get values from the cache
based on their name or value:


```scala
dummy.dogs.withName("snoopy")
// res15: UUID = 8e8cae16-a1e0-4ced-bb30-70b962c9d08a
dummy.cats.withValue(_.endsWith("garfield"))
// res16: String = "uNaql-garfield"
```

## Contributors to this project 

| <a href="https://github.com/alejandrohdezma"><img alt="alejandrohdezma" src="https://avatars.githubusercontent.com/u/9027541?v=4&s=120" width="120px" /></a> |
| :--: |
| <a href="https://github.com/alejandrohdezma"><sub><b>alejandrohdezma</b></sub></a> |
