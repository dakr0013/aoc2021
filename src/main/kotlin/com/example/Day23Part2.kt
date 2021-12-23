package com.example

import com.example.Amphipod.*

fun main() {
  val (top, bottom) =
      readLinesString(23).dropLast(1).drop(2).map {
        it.trim().replace("^#+".toRegex(), "").replace("#+$".toRegex(), "").split("#").map {
            character ->
          when (character) {
            "A" -> AMBER
            "B" -> BRONZE
            "C" -> COPPER
            "D" -> DESERT
            else -> throw IllegalStateException("unexpected character: $character")
          }
        }
      }

  val hallway = Array<Amphipod?>(11) { null }
  val sideRooms =
      mapOf(
          AMBER to SideRoom(AMBER, 2, listOf(bottom[0], DESERT, DESERT, top[0])),
          BRONZE to SideRoom(BRONZE, 4, listOf(bottom[1], BRONZE, COPPER, top[1])),
          COPPER to SideRoom(COPPER, 6, listOf(bottom[2], AMBER, BRONZE, top[2])),
          DESERT to SideRoom(DESERT, 8, listOf(bottom[3], COPPER, AMBER, top[3])),
      )
  val initialConfig = Configuration(hallway, sideRooms, 0, 4)
  organizeAmphipods(initialConfig)

  println(leastEnergyUsed)
}
