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
import java.time.temporal.ChronoUnit._
import java.util.UUID

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

  test("Dummy.fromNaturalLanguageDate fails if provided expression is not correct") {
    val dummy = Dummy.fromNaturalLanguageDate()

    interceptMessage[Dummy.IllegalDateException]("Unable to convert `this is not valid` to a valid instant") {
      dummy.`this is not valid`
    }
  }

}
