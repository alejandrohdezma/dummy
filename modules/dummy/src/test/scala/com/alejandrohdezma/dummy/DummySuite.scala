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
