package com.alejandrohdezma.dummy

import java.util.concurrent.ConcurrentHashMap

/** Cache for a `Dummy` or `Dummy.WithName` value. */
trait Cache[A] {

  /** Gets the value for key `name` from the cache or creates one using the provided `creator`. */
  def getOrSet(name: String, creator: String => A): A

}

object Cache {

  def fromConcurrentHashMap[A]: Cache[A] = new Cache[A] {

    private val internal = new ConcurrentHashMap[String, A]

    override def getOrSet(name: String, creator: String => A): A = internal.computeIfAbsent(name, creator(_))

  }

}
