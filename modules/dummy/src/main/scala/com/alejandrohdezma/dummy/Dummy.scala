/*
 * Copyright 2022-2024 Alejandro Hernández <https://github.com/alejandrohdezma>
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
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit._
import java.time.temporal.TemporalUnit

import scala.language.dynamics

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

  def map[B](f: A => B): Dummy[B] = Dummy(f(creator))

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
    * Allowed values are:
    *
    *   - `yesterday` / `tomorrow`
    *   - `N UNIT ago/forward`
    *   - `next/last UNIT`
    *
    * N will always be a positive number
    *
    * UNIT will always be a Java `ChronoUnit` in lowercase (singular or plural)
    *
    * @example
    *   {{{
    *    ```scala
    *    import com.alejandrohdezma.dummy.Dummy
    *
    *    object dummy {
    *
    *      val dates = Dummy.fromNaturalLanguageDate()
    *
    *    }
    *
    *    dummy.dates.`5 days ago`
    *    dummy.dates.`yesterday`
    *    dummy.dates.`last year`
    *    ```
    *   }}}
    */
  def fromNaturalLanguageDate(): Dummy.WithName[Instant] = withName {
    case s"${Number(quantity)} ${TimeUnit(unit)} ago"     => ZonedDateTime.now().minus(quantity, unit)
    case s"${Number(quantity)} ${TimeUnit(unit)} forward" => ZonedDateTime.now().plus(quantity, unit)
    case "yesterday"                                      => ZonedDateTime.now().minus(1, DAYS)
    case "tomorrow"                                       => ZonedDateTime.now().plus(1, DAYS)
    case s"next ${TimeUnit(unit)}"                        => ZonedDateTime.now().plus(1, unit)
    case s"last ${TimeUnit(unit)}"                        => ZonedDateTime.now().minus(1, unit)
    case string                                           => throw IllegalDateException(string) // scalafix:ok
  }.map(_.toInstant())

  final case class IllegalDateException(string: String)
      extends RuntimeException(s"Unable to convert `$string` to a valid instant")

  object Number {

    def unapply(string: String): Option[Long] = string.toLongOption

  }

  object TimeUnit {

    def unapply(string: String): Option[TemporalUnit] = string.toLowerCase match {
      case "nanos" | "nano"     => Some(ChronoUnit.NANOS)
      case "micros" | "micro"   => Some(ChronoUnit.MICROS)
      case "millis" | "milli"   => Some(ChronoUnit.MILLIS)
      case "seconds" | "second" => Some(ChronoUnit.SECONDS)
      case "minutes" | "minute" => Some(ChronoUnit.MINUTES)
      case "hours" | "hour"     => Some(ChronoUnit.HOURS)
      case "days" | "day"       => Some(ChronoUnit.DAYS)
      case "weeks" | "week"     => Some(ChronoUnit.WEEKS)
      case "months" | "month"   => Some(ChronoUnit.MONTHS)
      case "years" | "year"     => Some(ChronoUnit.YEARS)
      case _                    => None
    }

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

    def map[B](f: A => B): Dummy.WithName[B] = Dummy.WithName(creator.andThen(f))

  }

}
