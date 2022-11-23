Utility for creating dummy data for Scala tests

## Installation

Add the following line to your build.sbt file:

```sbt
libraryDependencies += "com.alejandrohdezma" %% "dummy" % "0.1.0" % Test
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
// res0: UUID = 11354173-e635-4f2f-a948-e759a46a00f9

dummy.dogs.`santa's-little-helper`
// res1: UUID = 163948ef-02d2-4468-a0ae-2b145bd217ac

dummy.cats.garfield
// res2: String = "nVTRT"

dummy.cats.sylvester
// res3: String = "fSSu1"
```

The key of these generators is that values are cached, so if we try to use the
same "key" twice, it will give us the same value:

```scala
dummy.dogs.snoopy
// res4: UUID = 11354173-e635-4f2f-a948-e759a46a00f9

dummy.dogs.`santa's-little-helper`
// res5: UUID = 163948ef-02d2-4468-a0ae-2b145bd217ac

dummy.cats.garfield
// res6: String = "nVTRT"

dummy.cats.sylvester
// res7: String = "fSSu1"
```

## Contributors to this project 

| <a href="https://github.com/alejandrohdezma"><img alt="alejandrohdezma" src="https://avatars.githubusercontent.com/u/9027541?v=4&s=120" width="120px" /></a> |
| :--: |
| <a href="https://github.com/alejandrohdezma"><sub><b>alejandrohdezma</b></sub></a> |
