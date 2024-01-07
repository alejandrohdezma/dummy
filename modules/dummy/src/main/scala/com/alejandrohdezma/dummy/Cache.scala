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

import java.util.concurrent.ConcurrentHashMap

import scala.jdk.CollectionConverters._

/** Cache for a `Dummy` or `Dummy.WithName` value. */
trait Cache[A] {

  /** Gets the value for key `name` from the cache or creates one using the provided `creator`. */
  def getOrSet(name: String, creator: String => A): A

  /** Returns all values stored in the cache */
  def all: Map[String, A]

}

object Cache {

  def fromConcurrentHashMap[A]: Cache[A] = new Cache[A] {

    private val internal = new ConcurrentHashMap[String, A]

    override def getOrSet(name: String, creator: String => A): A = internal.computeIfAbsent(name, creator(_))

    override def all: Map[String, A] = internal.asScala.toMap

  }

}
