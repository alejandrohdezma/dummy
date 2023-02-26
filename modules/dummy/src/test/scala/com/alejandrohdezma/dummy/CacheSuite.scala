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

class CacheSuite extends FunSuite {

  test("Cache.fromConcurrentHashMap creates a valid cache") {
    val cache = Cache.fromConcurrentHashMap[String]

    val a = List
      .fill(10)(cache.getOrSet("a", key => s"$key-${UUID.randomUUID()}"))
      .distinct

    val b = List
      .fill(10)(cache.getOrSet("b", key => s"$key-${UUID.randomUUID()}"))
      .distinct

    assertEquals(a.size, 1)
    assert(a.head.startsWith("a-"))

    assertEquals(b.size, 1)
    assert(b.head.startsWith("b-"))
  }

  test("Cache.all returns all values stored in the cache") {
    val cache = Cache.fromConcurrentHashMap[String]

    val a = List
      .fill(10)(cache.getOrSet("a", key => s"$key-${UUID.randomUUID()}"))
      .distinct

    val b = List
      .fill(10)(cache.getOrSet("b", key => s"$key-${UUID.randomUUID()}"))
      .distinct

    val expected = Map("a" -> a.head, "b" -> b.head)

    assertEquals(cache.all, expected)
  }

}
