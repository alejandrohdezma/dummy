/*
 * Copyright 2022 Alejandro Hernández <https://github.com/alejandrohdezma>
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
  *     case object dogs extends Dummy(UUID.randomUUID())
  *
  *     case object cats extends Dummy(Random.alphanumeric.take(5).mkString)
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

  private val cache: ConcurrentHashMap[String, A] = new ConcurrentHashMap[String, A]

  def selectDynamic(name: String): A = cache.computeIfAbsent(name, _ => creator)

}
