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

import java.time.DayOfWeek
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit._
import java.time.temporal.TemporalAdjusters
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

  /** Returns a dummy value with the provided name without creating a new one.
    *
    * Throws a [[Dummy.NameNotFound]] if no value has previously been created with that name.
    */
  def withName(name: String): A =
    cache.all.get(name).getOrElse(throw Dummy.NameNotFound) // scalafix:ok

  /** Returns a dummy value from the cache that satisfies the provided predicate.
    *
    * Throws a [[Dummy.ValueNotFound]] if no value has previously been created that passes the predicate.
    */
  def withValue(predicate: A => Boolean): A =
    cache.all.find { case (_, value) => predicate(value) }.getOrElse(throw Dummy.ValueNotFound)._2 // scalafix:ok

  def selectDynamic(name: String): A = cache.getOrSet(name, _ => creator)

  def apply(name: String): A = selectDynamic(name)

  def map[B](f: A => B): Dummy[B] = Dummy(f(creator))

}

object Dummy {

  case object NameNotFound extends RuntimeException("Unable to find value with the provided name")

  case object ValueNotFound extends RuntimeException("Unable to find value that satisfies the provided predicate")

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
    *   - `now` / `today`
    *   - `yesterday` / `tomorrow`
    *   - `N UNIT ago/forward`
    *   - `next/last UNIT`
    *   - `next/last WEEKDAY`
    *
    * N will always be a positive number
    *
    * UNIT will always be a Java `ChronoUnit` in lowercase (singular or plural)
    *
    * WEEKDAY will always be an English day of the week in lowercase (`monday`, `tuesday`...). `next monday` is the
    * first Monday strictly after the current date and `last monday` the last one strictly before it, so on a Monday
    * both are a week away.
    *
    * Values are relative to the current date and time. Use the overload taking a `ZonedDateTime` to pin them to a fixed
    * one instead, so tests that depend on the day of the week stay deterministic.
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
    *    dummy.dates.`next monday`
    *    ```
    *   }}}
    */
  def fromNaturalLanguageDate(): Dummy.WithName[Instant] = fromNaturalLanguageDate(ZonedDateTime.now())

  /** Creates a "dummy" object that allows generating dummy instant values from natural language for tests easily,
    * relative to the provided date and time instead of the current one. See the parameterless overload for the accepted
    * expressions.
    *
    * @param now
    *   The date and time every expression is relative to. Evaluated each time a new value is created.
    *
    * @example
    *   {{{
    *    ```scala
    *    import com.alejandrohdezma.dummy.Dummy
    *    import java.time.ZonedDateTime
    *
    *    object dummy {
    *
    *      val dates = Dummy.fromNaturalLanguageDate(ZonedDateTime.parse("2026-09-15T10:00:00Z"))
    *
    *    }
    *
    *    dummy.dates.`yesterday`   // 2026-09-14T10:00:00Z
    *    dummy.dates.`last monday` // 2026-09-14T10:00:00Z
    *    ```
    *   }}}
    */
  def fromNaturalLanguageDate(now: => ZonedDateTime): Dummy.WithName[Instant] = withName {
    case "now" | "today"                                  => now
    case s"${Number(quantity)} ${TimeUnit(unit)} ago"     => now.minus(quantity, unit)
    case s"${Number(quantity)} ${TimeUnit(unit)} forward" => now.plus(quantity, unit)
    case "yesterday"                                      => now.minus(1, DAYS)
    case "tomorrow"                                       => now.plus(1, DAYS)
    case s"next ${Weekday(day)}"                          => now.`with`(TemporalAdjusters.next(day))
    case s"last ${Weekday(day)}"                          => now.`with`(TemporalAdjusters.previous(day))
    case s"next ${TimeUnit(unit)}"                        => now.plus(1, unit)
    case s"last ${TimeUnit(unit)}"                        => now.minus(1, unit)
    case string                                           => throw IllegalDateException(string) // scalafix:ok
  }.map(_.toInstant())

  final case class IllegalDateException(string: String)
      extends RuntimeException(
        s"Unable to convert `$string` to a valid instant. Accepted expressions are `now`, `today`, `yesterday`, " +
          "`tomorrow`, `N UNIT ago`, `N UNIT forward`, `next UNIT`, `last UNIT`, `next WEEKDAY` and `last WEEKDAY`, " +
          "where N is a positive number, UNIT is one of nanos, micros, millis, seconds, minutes, hours, days, weeks, " +
          "months or years (singular or plural) and WEEKDAY is an English day of the week"
      )

  object Number {

    def unapply(string: String): Option[Long] = string.toLongOption

  }

  object Weekday {

    def unapply(string: String): Option[DayOfWeek] = string.toLowerCase match {
      case "monday"    => Some(DayOfWeek.MONDAY)
      case "tuesday"   => Some(DayOfWeek.TUESDAY)
      case "wednesday" => Some(DayOfWeek.WEDNESDAY)
      case "thursday"  => Some(DayOfWeek.THURSDAY)
      case "friday"    => Some(DayOfWeek.FRIDAY)
      case "saturday"  => Some(DayOfWeek.SATURDAY)
      case "sunday"    => Some(DayOfWeek.SUNDAY)
      case _           => None
    }

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

    /** Returns a dummy value with the provided name without creating a new one.
      *
      * Throws a [[Dummy.NameNotFound]] if no value has previously been created with that name.
      */
    def withName(name: String): A =
      cache.all.get(name).getOrElse(throw Dummy.NameNotFound) // scalafix:ok

    /** Returns a dummy value from the cache that satisfies the provided predicate.
      *
      * Throws a [[Dummy.ValueNotFound]] if no value has previously been created that passes the predicate.
      */
    def withValue(predicate: A => Boolean): A =
      cache.all.find { case (_, value) => predicate(value) }.getOrElse(throw Dummy.ValueNotFound)._2 // scalafix:ok

    def selectDynamic(name: String): A = cache.getOrSet(name, creator)

    def apply(name: String): A = selectDynamic(name)

    def map[B](f: A => B): Dummy.WithName[B] = Dummy.WithName(creator.andThen(f))

  }

}
