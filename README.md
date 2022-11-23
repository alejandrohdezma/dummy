Utility for creating dummy data for Scala tests

## Installation

Add the following line to your build.sbt file:

```sbt
libraryDependencies += "com.alejandrohdezma" %% "dummy" % "0.0.0" % Test
```

## Usage

Create a new `dummy` object and add some dummy cases you want to use:

```scala
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

```scala
dummy.dogs.snoopy
// res0: UUID = 6bc8b106-04cf-4775-b904-38c3214ed3ce

dummy.dogs.`santa's-little-helper`
// res1: UUID = fb5d572b-3710-4d15-b99a-d0d875dc355d

dummy.cats.garfield
// res2: String = "KnhT0"

dummy.cats.sylvester
// res3: String = "auKCM"
```

The key of these generators is that values are cached, so if we try to use the
same "key" twice, it will give us the same value:

```scala
dummy.dogs.snoopy
// res4: UUID = 6bc8b106-04cf-4775-b904-38c3214ed3ce

dummy.dogs.`santa's-little-helper`
// res5: UUID = fb5d572b-3710-4d15-b99a-d0d875dc355d

dummy.cats.garfield
// res6: String = "KnhT0"

dummy.cats.sylvester
// res7: String = "auKCM"
```

## Contributors to this project 

| <a href="https://github.com/alejandrohdezma"><img alt="alejandrohdezma" src="https://avatars.githubusercontent.com/u/9027541?v=4&s=120" width="120px" /></a> |
| :--: |
| <a href="https://github.com/alejandrohdezma"><sub><b>alejandrohdezma</b></sub></a> |
