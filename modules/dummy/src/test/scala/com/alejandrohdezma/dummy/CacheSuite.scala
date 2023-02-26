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

}
