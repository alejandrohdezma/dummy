Utility for creating dummy data for Scala tests

## Installation

Add the following line to your build.sbt file:

```sbt
libraryDependencies += "com.alejandrohdezma" %% "dummy" % "0.7.0" % Test
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
// res0: UUID = ad8545df-5a9b-4344-bf54-4a6471bc3fc5

dummy.dogs.`santa's-little-helper`
// res1: UUID = 893e0fc5-58aa-4aca-8d6f-910389b60e76

dummy.cats.garfield
// res2: String = "kD6mQ-garfield"

dummy.cats.sylvester
// res3: String = "McRHT-sylvester"

dummy.dates.`3 days ago`
// res4: java.time.Instant = 2026-09-12T12:04:46.770991899Z

dummy.dates.yesterday
// res5: java.time.Instant = 2026-09-14T12:04:46.771807915Z

dummy.dates.`last monday`
// res6: java.time.Instant = 2026-09-14T12:04:46.772732543Z
```

Dates are relative to the current time; pass a `ZonedDateTime` to
`Dummy.fromNaturalLanguageDate` to pin them to a fixed one instead.

### Dates as other types

`fromNaturalLanguageDate` produces `Instant`s. Use `map` to get any other
representation, such as a `LocalDate`, a `ZonedDateTime` or a formatted
string. Combined with a pinned date this keeps tests that depend on the day
of the week deterministic:

```scala
import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime

object pinned {

  val anchor = ZonedDateTime.parse("2026-09-15T10:00:00Z")

  val days = Dummy.fromNaturalLanguageDate(anchor).map(_.atZone(UTC).toLocalDate)

  val stamps = Dummy.fromNaturalLanguageDate(anchor).map(_.atZone(UTC).toLocalDate.toString)

}
```

```scala
pinned.days.yesterday
// res7: java.time.LocalDate = 2026-09-14

pinned.days.`last monday`
// res8: java.time.LocalDate = 2026-09-14

pinned.stamps.`2 weeks ago`
// res9: String = "2026-09-01"
```

The key of these generators is that values are cached, so if we try to use the
same "key" twice, it will give us the same value:

```scala
dummy.dogs.snoopy
// res10: UUID = ad8545df-5a9b-4344-bf54-4a6471bc3fc5

dummy.dogs.`santa's-little-helper`
// res11: UUID = 893e0fc5-58aa-4aca-8d6f-910389b60e76

dummy.cats.garfield
// res12: String = "kD6mQ-garfield"

dummy.cats.sylvester
// res13: String = "McRHT-sylvester"

dummy.dates.`3 days ago`
// res14: java.time.Instant = 2026-09-12T12:04:46.770991899Z

dummy.dates.yesterday
// res15: java.time.Instant = 2026-09-14T12:04:46.771807915Z
```

### Accessing the cache

You can access the internal `Dummy` cache to see the values in
store.

```scala
dummy.dogs.cache.all
// res16: Map[String, UUID] = Map(
//   "snoopy" -> ad8545df-5a9b-4344-bf54-4a6471bc3fc5,
//   "santa's-little-helper" -> 893e0fc5-58aa-4aca-8d6f-910389b60e76
// )

dummy.cats.cache.all
// res17: Map[String, String] = Map(
//   "sylvester" -> "McRHT-sylvester",
//   "garfield" -> "kD6mQ-garfield"
// )

dummy.dates.cache.all
// res18: Map[String, java.time.Instant] = Map(
//   "yesterday" -> 2026-09-14T12:04:46.771807915Z,
//   "last monday" -> 2026-09-14T12:04:46.772732543Z,
//   "3 days ago" -> 2026-09-12T12:04:46.770991899Z
// )
```

There are also available some convenient methods to get values from the cache
based on their name or value:


```scala
dummy.dogs.withName("snoopy")
// res19: UUID = ad8545df-5a9b-4344-bf54-4a6471bc3fc5
dummy.cats.withValue(_.endsWith("garfield"))
// res20: String = "kD6mQ-garfield"
```

## Contributors to this project 

| <a href="https://github.com/alejandrohdezma"><img alt="alejandrohdezma" src="https://avatars.githubusercontent.com/u/9027541?v=4&s=120" width="120px" /></a> |
| :--: |
| <a href="https://github.com/alejandrohdezma"><sub><b>alejandrohdezma</b></sub></a> |
