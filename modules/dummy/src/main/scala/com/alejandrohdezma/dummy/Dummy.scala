/*
 * Copyright 2022-2023 Alejandro Hernández <https://github.com/alejandrohdezma>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alejandrohdezma.dummy

import java.time.Instant

import scala.language.dynamics

import org.ocpsoft.prettytime.nlp.PrettyTimeParser

/** Utility for creating dummy data for tests.
  *
  * @param creator
  *   The code that should be called to generate a new value of your dummy object
  *
  * @example
  *   {{{
  *   ```scala
  *   import com.alejandrohdezma.dummy.Dummy
  *
  *   import java.util.UUID
  *   import scala.util.Random
  *
  *   object dummy {
  *
  *     val dogs = new Dummy(UUID.randomUUID())
  *
  *     val cats = new Dummy(Random.alphanumeric.take(5).mkString)
  *
  *   }
  *   ```
  *
  *   And then use it in your tests with any value you want:
  *
  *   ```scala
  *   dummy.dogs.snoopy
  *
  *   dummy.dogs.`santa's-little-helper`
  *
  *   dummy.cats.garfield
  *
  *   dummy.cats.sylvester
  *   ```
  *   }}}
  */
class Dummy[A](creator: => A) extends Dynamic {

  /** The cache containing all the values created by this dummy object. */
  val cache: Cache[A] = Cache.fromConcurrentHashMap[A]

  def selectDynamic(name: String): A = cache.getOrSet(name, _ => creator)

  def apply(name: String): A = selectDynamic(name)

}

object Dummy {

  /** Creates a "dummy" object that allows generating dummy values for tests easily.
    *
    * @example
    *   {{{
    *   ```scala
    *   import com.alejandrohdezma.dummy.Dummy
    *
    *   import java.util.UUID
    *   import scala.util.Random
    *
    *   object dummy {
    *
    *     val dogs = Dummy(UUID.randomUUID())
    *
    *     val cats = Dummy(Random.alphanumeric.take(5).mkString)
    *
    *   }
    *   ```
    *
    *   And then use it in your tests with any value you want:
    *
    *   ```scala
    *   dummy.dogs.snoopy
    *
    *   dummy.dogs.`santa's-little-helper`
    *
    *   dummy.cats.garfield
    *
    *   dummy.cats.sylvester
    *   ```
    *   }}}
    */
  def apply[A](creator: => A): Dummy[A] = new Dummy(creator)

  /** Creates a "dummy" object that allows generating dummy values for tests easily.
    *
    * @param creator
    *   The code that should be called to generate a new value of your dummy object depending on the choosen name
    *
    * @example
    *   {{{
    *   ```scala
    *   import com.alejandrohdezma.dummy.Dummy
    *
    *   import java.util.UUID
    *   import scala.util.Random
    *
    *   object dummy {
    *
    *     val dogs = Dummy.withName(name => s"\$name-\${UUID.randomUUID()}")
    *
    *     val cats = Dummy.withName(name => s"\${Random.alphanumeric.take(5).mkString}-\$name")
    *
    *   }
    *   ```
    *
    *   And then use it in your tests with any value you want:
    *
    *   ```scala
    *   dummy.dogs.snoopy
    *
    *   dummy.dogs.`santa's-little-helper`
    *
    *   dummy.cats.garfield
    *
    *   dummy.cats.sylvester
    *   ```
    *   }}}
    */
  def withName[A](creator: String => A): Dummy.WithName[A] = new Dummy.WithName[A](creator)

  /** Creates a "dummy" object that allows generating dummy instant values from natural language for tests easily.
    *
    * @example
    *   {{{
    *   ```scala
    *   import com.alejandrohdezma.dummy.Dummy
    *
    *   object dummy {
    *
    *     val dates = Dummy.fromNaturalLanguageDate()
    *
    *   }
    *   ```
    *
    *   And then use it in your tests with any value you want:
    *
    *   ```scala
    *   dummy.dates.`5 days ago`
    *
    *   dummy.dates.`yesterday`
    *
    *   dummy.dates.`last year`
    *
    *   dummy.dates.`next tuesday`
    *   ```
    *   }}}
    */
  def fromNaturalLanguageDate(): Dummy.WithName[Instant] = withName[Instant] { text =>
    val date = new PrettyTimeParser().parse(text)

    if (date.isEmpty()) {
      sys.error(s"Unable to parse `$text` as a date")
    }

    date.get(0).toInstant()
  }

  /** Utility for creating dummy data for tests.
    *
    * @param creator
    *   The code that should be called to generate a new value of your dummy object depending on the choosen name
    *
    * @example
    *   {{{
    *   ```scala
    *   import com.alejandrohdezma.dummy.Dummy
    *
    *   import java.util.UUID
    *   import scala.util.Random
    *
    *   object dummy {
    *
    *     val dogs = Dummy.withName(name => s"\$name-\${UUID.randomUUID()}")
    *
    *     val cats = Dummy.withName(name => s"\${Random.alphanumeric.take(5).mkString}-\$name")
    *
    *   }
    *   ```
    *
    *   And then use it in your tests with any value you want:
    *
    *   ```scala
    *   dummy.dogs.snoopy
    *
    *   dummy.dogs.`santa's-little-helper`
    *
    *   dummy.cats.garfield
    *
    *   dummy.cats.sylvester
    *   ```
    *   }}}
    */
  final case class WithName[A](creator: String => A) extends Dynamic {

    /** The cache containing all the values created by this dummy object. */
    val cache: Cache[A] = Cache.fromConcurrentHashMap[A]

    def selectDynamic(name: String): A = cache.getOrSet(name, creator)

    def apply(name: String): A = selectDynamic(name)

  }

}
