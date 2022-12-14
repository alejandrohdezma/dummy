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

  case object dogs extends Dummy(UUID.randomUUID())

  case object cats extends Dummy(Random.alphanumeric.take(5).mkString)

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
```

The key of these generators is that values are cached, so if we try to use the
same "key" twice, it will give us the same value:

```scala mdoc
dummy.dogs.snoopy

dummy.dogs.`santa's-little-helper`

dummy.cats.garfield

dummy.cats.sylvester
```

## Contributors to this project 

@CONTRIBUTORS_TABLE@
