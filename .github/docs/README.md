@DESCRIPTION@

## Installation

Add the following line to your build.sbt file:

```sbt
libraryDependencies += "@ORGANIZATION@" %% "@NAME@" % "@VERSION@" % Test
```

## Usage

Create a new `dummy` object and add some dummy cases you want to use:

```scala mdoc:silent
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

```scala mdoc
dummy.dogs.snoopy

dummy.dogs.`santa's-little-helper`

dummy.cats.garfield

dummy.cats.sylvester

dummy.dates.`3 days ago`

dummy.dates.yesterday

dummy.dates.`last monday`
```

Dates are relative to the current time; pass a `ZonedDateTime` to
`Dummy.fromNaturalLanguageDate` to pin them to a fixed one instead.

### Dates as other types

`fromNaturalLanguageDate` produces `Instant`s. Use `map` to get any other
representation, such as a `LocalDate`, a `ZonedDateTime` or a formatted
string. Combined with a pinned date this keeps tests that depend on the day
of the week deterministic:

```scala mdoc:silent
import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime

object pinned {

  val anchor = ZonedDateTime.parse("2026-09-15T10:00:00Z")

  val days = Dummy.fromNaturalLanguageDate(anchor).map(_.atZone(UTC).toLocalDate)

  val stamps = Dummy.fromNaturalLanguageDate(anchor).map(_.atZone(UTC).toLocalDate.toString)

}
```

```scala mdoc
pinned.days.yesterday

pinned.days.`last monday`

pinned.stamps.`2 weeks ago`
```

The key of these generators is that values are cached, so if we try to use the
same "key" twice, it will give us the same value:

```scala mdoc
dummy.dogs.snoopy

dummy.dogs.`santa's-little-helper`

dummy.cats.garfield

dummy.cats.sylvester

dummy.dates.`3 days ago`

dummy.dates.yesterday
```

### Accessing the cache

You can access the internal `Dummy` cache to see the values in
store.

```scala mdoc
dummy.dogs.cache.all

dummy.cats.cache.all

dummy.dates.cache.all
```

There are also available some convenient methods to get values from the cache
based on their name or value:


```scala mdoc
dummy.dogs.withName("snoopy")
dummy.cats.withValue(_.endsWith("garfield"))
```

## Contributors to this project 

@CONTRIBUTORS_TABLE@
