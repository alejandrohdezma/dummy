/*
 * Copyright 2022-2026 Alejandro Hernández <https://github.com/alejandrohdezma>
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
import java.time.temporal.ChronoUnit._
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

import scala.util.Random

import munit.FunSuite
import munit.Location

class DummySuite extends FunSuite {

  test("Dummy always return the same value from the same key") {
    val dummy = Dummy(s"${UUID.randomUUID()}")

    val a = List.fill(10)(dummy.a).distinct

    val b = List.fill(10)(dummy.b).distinct

    assertEquals(a.size, 1)
    assertEquals(s"${UUID.fromString(a.head)}", a.head)
    assertEquals(dummy("a"), a.head)

    assertEquals(b.size, 1)
    assertEquals(s"${UUID.fromString(b.head)}", b.head)
    assertEquals(dummy("b"), b.head)
  }

  test("Dummy#map allows transforming the returned type") {
    val dummy = Dummy(Random.alphanumeric.take(5).mkString).map(_ + "42")

    val a = List.fill(10)(dummy.a).distinct

    val b = List.fill(10)(dummy.b).distinct

    assertEquals(a.size, 1)
    assert(a.head.endsWith("42"))
    assertEquals(dummy("a"), a.head)

    assertEquals(b.size, 1)
    assert(b.head.endsWith("42"))
    assertEquals(dummy("b"), b.head)
  }

  test("Dummy#withName returns the value with the provided name") {
    val dummy = Dummy(Random.alphanumeric.take(5).mkString)

    val a = dummy.`a`

    assertEquals(dummy.withName("a"), a)
  }

  test("Dummy#withName throws error if no value can be found with that name") {
    val dummy = Dummy(Random.alphanumeric.take(5).mkString)

    interceptMessage[Dummy.NameNotFound.type]("Unable to find value with the provided name") {
      dummy.withName("a")
    }
  }

  test("Dummy.withValue returns the value that satisfies the predicate") {
    val dummy = Dummy(2)

    val a = dummy.`a`

    assertEquals(dummy.withValue(_ >= 2), a)
  }

  test("Dummy#withValue throws error if no value can be found with that name") {
    val dummy = Dummy(5)

    interceptMessage[Dummy.ValueNotFound.type]("Unable to find value that satisfies the provided predicate") {
      dummy.withValue(_ >= 2)
    }
  }

  test("Dummy.WithName always return the same value from the same key") {
    val dummy = new Dummy.WithName(key => s"$key-${UUID.randomUUID()}")

    val a = List.fill(10)(dummy.a).distinct

    val b = List.fill(10)(dummy.b).distinct

    assertEquals(a.size, 1)
    assertEquals(s"a-${UUID.fromString(a.head.drop(2))}", a.head)
    assertEquals(dummy("a"), a.head)

    assertEquals(b.size, 1)
    assertEquals(s"b-${UUID.fromString(b.head.drop(2))}", b.head)
    assertEquals(dummy("b"), b.head)
  }

  test("Dummy.fromNaturalLanguageDate allows creating dummy values from natural language") {
    val dummy = Dummy.fromNaturalLanguageDate()

    def assertInstant(obtained: Instant, expected: Instant)(implicit location: Location) =
      assertEquals(obtained.truncatedTo(DAYS), expected.truncatedTo(DAYS))

    val now = Instant.now()

    assertInstant(dummy.`5 days ago`, now.minus(5, DAYS))
    assertInstant(dummy("5 days ago"), now.minus(5, DAYS))
    assertInstant(dummy.`yesterday`, now.minus(1, DAYS))
    assertInstant(dummy("yesterday"), now.minus(1, DAYS))
    assertInstant(dummy.`last year`, ZonedDateTime.now().minusYears(1).toInstant)
    assertInstant(dummy("last year"), ZonedDateTime.now().minusYears(1).toInstant)
  }

  test("Dummy.fromNaturalLanguageDate resolves weekdays and `today` relative to the provided date") {
    val tuesday = ZonedDateTime.parse("2026-09-15T10:00:00Z")

    val dummy = Dummy.fromNaturalLanguageDate(tuesday)

    assertEquals(dummy.`today`, tuesday.toInstant)
    assertEquals(dummy.`now`, tuesday.toInstant)
    assertEquals(dummy.`yesterday`, Instant.parse("2026-09-14T10:00:00Z"))
    assertEquals(dummy.`last monday`, Instant.parse("2026-09-14T10:00:00Z"))
    assertEquals(dummy.`last tuesday`, Instant.parse("2026-09-08T10:00:00Z"))
    assertEquals(dummy.`next tuesday`, Instant.parse("2026-09-22T10:00:00Z"))
    assertEquals(dummy.`next Friday`, Instant.parse("2026-09-18T10:00:00Z"))
    assertEquals(dummy.`2 weeks ago`, Instant.parse("2026-09-01T10:00:00Z"))
    assertEquals(dummy.`last month`, Instant.parse("2026-08-15T10:00:00Z"))
  }

  test("Dummy.fromNaturalLanguageDate evaluates the provided date every time a value is created") {
    val calls = new AtomicInteger(0)

    val dummy = Dummy.fromNaturalLanguageDate {
      calls.incrementAndGet()
      ZonedDateTime.parse("2026-09-15T10:00:00Z")
    }

    val yesterday = List.fill(2)(dummy.`yesterday`).distinct
    val tomorrow  = dummy.`tomorrow`

    assertEquals(yesterday, List(Instant.parse("2026-09-14T10:00:00Z")))
    assertEquals(tomorrow, Instant.parse("2026-09-16T10:00:00Z"))
    assertEquals(calls.get(), 2)
  }

  test("Dummy.WithName#map allows transforming the returned type") {
    val dummy = Dummy.fromNaturalLanguageDate().map(_.truncatedTo(DAYS))

    val now = Instant.now()

    assertEquals(dummy.`5 days ago`, now.minus(5, DAYS).truncatedTo(DAYS))
    assertEquals(dummy("5 days ago"), now.minus(5, DAYS).truncatedTo(DAYS))
    assertEquals(dummy.`yesterday`, now.minus(1, DAYS).truncatedTo(DAYS))
    assertEquals(dummy("yesterday"), now.minus(1, DAYS).truncatedTo(DAYS))
    assertEquals(dummy.`last year`, ZonedDateTime.now().minusYears(1).toInstant.truncatedTo(DAYS))
    assertEquals(dummy("last year"), ZonedDateTime.now().minusYears(1).toInstant.truncatedTo(DAYS))
  }

  test("Dummy.WithName#withName returns the value with the provided name") {
    val dummy = Dummy.withName(_ => Random.alphanumeric.take(5).mkString)

    val a = dummy.`a`

    assertEquals(dummy.withName("a"), a)
  }

  test("Dummy.WithName#withName throws error if no value can be found with that name") {
    val dummy = Dummy.withName(_ => Random.alphanumeric.take(5).mkString)

    interceptMessage[Dummy.NameNotFound.type]("Unable to find value with the provided name") {
      dummy.withName("a")
    }
  }

  test("Dummy.WithName.withValue returns the value that satisfies the predicate") {
    val dummy = Dummy.withName(_ => 2)

    val a = dummy.`a`

    assertEquals(dummy.withValue(_ >= 2), a)
  }

  test("Dummy.WithName#withValue throws error if no value can be found with that name") {
    val dummy = Dummy.withName(_ => 5)

    interceptMessage[Dummy.ValueNotFound.type]("Unable to find value that satisfies the provided predicate") {
      dummy.withValue(_ >= 2)
    }
  }

  test("Dummy.fromNaturalLanguageDate fails if provided expression is not correct") {
    val dummy = Dummy.fromNaturalLanguageDate()

    val expected = "Unable to convert `this is not valid` to a valid instant. Accepted expressions are `now`, " +
      "`today`, `yesterday`, `tomorrow`, `N UNIT ago`, `N UNIT forward`, `next UNIT`, `last UNIT`, `next WEEKDAY` " +
      "and `last WEEKDAY`, where N is a positive number, UNIT is one of nanos, micros, millis, seconds, minutes, " +
      "hours, days, weeks, months or years (singular or plural) and WEEKDAY is an English day of the week"

    interceptMessage[Dummy.IllegalDateException](expected) {
      dummy.`this is not valid`
    }
  }

}
