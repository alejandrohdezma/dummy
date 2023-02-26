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

import java.util.UUID

import munit.FunSuite

class DummySuite extends FunSuite {

  test("Dummy always return the same value from the same key") {
    val dummy = Dummy(s"${UUID.randomUUID()}")

    val a = List.fill(10)(dummy.a).distinct

    val b = List.fill(10)(dummy.b).distinct

    assertEquals(a.size, 1)
    assertEquals(s"${UUID.fromString(a.head)}", a.head)

    assertEquals(b.size, 1)
    assertEquals(s"${UUID.fromString(b.head)}", b.head)
  }

  test("Dummy.WithName always return the same value from the same key") {
    val dummy = new Dummy.WithName(key => s"$key-${UUID.randomUUID()}")

    val a = List.fill(10)(dummy.a).distinct

    val b = List.fill(10)(dummy.b).distinct

    assertEquals(a.size, 1)
    assertEquals(s"a-${UUID.fromString(a.head.drop(2))}", a.head)

    assertEquals(b.size, 1)
    assertEquals(s"b-${UUID.fromString(b.head.drop(2))}", b.head)
  }

}
