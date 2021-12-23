package com.example

import com.example.Amphipod.*
import kotlin.math.absoluteValue

val hallwayRoomEntryIndices = listOf(2, 4, 6, 8)

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
          AMBER to SideRoom(AMBER, 2, listOf(bottom[0], top[0])),
          BRONZE to SideRoom(BRONZE, 4, listOf(bottom[1], top[1])),
          COPPER to SideRoom(COPPER, 6, listOf(bottom[2], top[2])),
          DESERT to SideRoom(DESERT, 8, listOf(bottom[3], top[3])),
      )
  val initialConfig = Configuration(hallway, sideRooms, 0, 2)
  organizeAmphipods(initialConfig)

  println(leastEnergyUsed)
}

enum class Amphipod(val requiredEnergy: Int) {
  AMBER(1),
  BRONZE(10),
  COPPER(100),
  DESERT(1000),
}

class Configuration(
    val hallway: Array<Amphipod?>,
    val sideRooms: Map<Amphipod, SideRoom>,
    val currentEnergyUsed: Long,
    val maxRoomSize: Int
) {
  fun isOrganized(): Boolean {
    return hallway.filterNotNull().isEmpty() &&
        sideRooms.all { (roomType, room) -> room.content.all { amphipod -> amphipod == roomType } }
  }
}

class SideRoom(val type: Amphipod, val hallwayIndex: Int, val content: List<Amphipod>) {
  fun canEnter() = !containsWrongAmphipod()

  fun containsWrongAmphipod() = content.any { it != type }
}

var leastEnergyUsed = Long.MAX_VALUE

fun organizeAmphipods(config: Configuration) {
  if (config.currentEnergyUsed > leastEnergyUsed) {
    return
  }
  if (config.isOrganized()) {
    if (config.currentEnergyUsed < leastEnergyUsed) {
      leastEnergyUsed = config.currentEnergyUsed
    }
    return
  }

  var hasMoved = tryMoveAmphipodFromWrongRoomToCorrectRoom(config)
  if (!hasMoved) {
    hasMoved = tryMoveAmphipodFromHallwayToRoom(config)
  }
  if (!hasMoved) {
    tryMoveAmphipodFromWrongRoomToHallway(config)
  }
}

fun tryMoveAmphipodFromWrongRoomToHallway(config: Configuration): Boolean {
  var hasMovedAmphipod = false

  for ((roomType, room) in config.sideRooms) {
    if (!room.containsWrongAmphipod()) {
      continue
    }
    val amphipod = room.content.last()
    val spacesToHallway = (config.maxRoomSize - room.content.size) + 1
    // move left in hallway
    for (i in room.hallwayIndex - 1 downTo 0) {
      if (config.hallway[i] != null) {
        break
      }
      if (i in hallwayRoomEntryIndices) {
        continue
      }
      val movedSpacesInHallway = (room.hallwayIndex - i).absoluteValue
      val overallSpaces = spacesToHallway + movedSpacesInHallway
      val requiredEnergy = overallSpaces * amphipod.requiredEnergy

      val newHallway = config.hallway.copyOf()
      newHallway[i] = amphipod
      val newSideRooms = config.sideRooms.toMutableMap()
      newSideRooms[roomType] = SideRoom(roomType, room.hallwayIndex, room.content.dropLast(1))
      val newConfig =
          Configuration(
              newHallway,
              newSideRooms,
              config.currentEnergyUsed + requiredEnergy,
              config.maxRoomSize)
      organizeAmphipods(newConfig)
      hasMovedAmphipod = true
    }

    // move right in hallway
    for (i in room.hallwayIndex + 1..config.hallway.lastIndex) {
      if (config.hallway[i] != null) {
        break
      }
      if (i in hallwayRoomEntryIndices) {
        continue
      }
      val movedSpacesInHallway = (i - room.hallwayIndex).absoluteValue
      val overallSpaces = spacesToHallway + movedSpacesInHallway
      val requiredEnergy = overallSpaces * amphipod.requiredEnergy

      val newHallway = config.hallway.copyOf()
      newHallway[i] = amphipod
      val newSideRooms = config.sideRooms.toMutableMap()
      newSideRooms[roomType] = SideRoom(roomType, room.hallwayIndex, room.content.dropLast(1))
      val newConfig =
          Configuration(
              newHallway,
              newSideRooms,
              config.currentEnergyUsed + requiredEnergy,
              config.maxRoomSize)
      organizeAmphipods(newConfig)
      hasMovedAmphipod = true
    }
  }

  return hasMovedAmphipod
}

fun tryMoveAmphipodFromWrongRoomToCorrectRoom(config: Configuration): Boolean {
  var hasMovedAmphipod = false

  for ((roomType, room) in config.sideRooms) {
    if (room.containsWrongAmphipod()) {
      val amphipod = room.content.last()
      val destinationRoom = config.sideRooms[amphipod]!!
      val isWayToRoomFree =
          config.hallway.copyOfRange(
                  Math.min(room.hallwayIndex, destinationRoom.hallwayIndex),
                  Math.max(room.hallwayIndex, destinationRoom.hallwayIndex))
              .all { it == null }
      if (roomType != amphipod && destinationRoom.canEnter() && isWayToRoomFree) {
        val spacesToHallway = (config.maxRoomSize - room.content.size) + 1
        val spacesInHallway = (room.hallwayIndex - destinationRoom.hallwayIndex).absoluteValue
        val spacesToFillRoom = (config.maxRoomSize - destinationRoom.content.size)
        val overallSpaces = spacesToHallway + spacesInHallway + spacesToFillRoom
        val requiredEnergy = overallSpaces * amphipod.requiredEnergy

        val newSideRooms = config.sideRooms.toMutableMap()
        newSideRooms[roomType] = SideRoom(roomType, room.hallwayIndex, room.content.dropLast(1))
        newSideRooms[amphipod] =
            SideRoom(
                amphipod,
                destinationRoom.hallwayIndex,
                destinationRoom.content.plusElement(amphipod))
        val newConfig =
            Configuration(
                config.hallway,
                newSideRooms,
                config.currentEnergyUsed + requiredEnergy,
                config.maxRoomSize)
        organizeAmphipods(newConfig)

        hasMovedAmphipod = true
      }
    }
  }

  return hasMovedAmphipod
}

fun tryMoveAmphipodFromHallwayToRoom(config: Configuration): Boolean {
  var hasMovedAmphipod = false

  for (i in config.hallway.indices) {
    val amphipod = config.hallway[i] ?: continue

    val destinationRoom = config.sideRooms[amphipod]!!
    val isWayToRoomFree =
        config.hallway.copyOfRange(
                Math.min(i + 1, destinationRoom.hallwayIndex),
                Math.max(i, destinationRoom.hallwayIndex))
            .all { it == null }
    if (destinationRoom.canEnter() && isWayToRoomFree) {
      val spacesToRoomEntry = (i - destinationRoom.hallwayIndex).absoluteValue
      val spacesToFillRoom = (config.maxRoomSize - destinationRoom.content.size)
      val overallSpaces = spacesToRoomEntry + spacesToFillRoom
      val requiredEnergy = overallSpaces * amphipod.requiredEnergy

      val newHallway = config.hallway.copyOf()
      newHallway[i] = null
      val newSideRooms = config.sideRooms.toMutableMap()
      newSideRooms[amphipod] =
          SideRoom(
              amphipod, destinationRoom.hallwayIndex, destinationRoom.content.plusElement(amphipod))
      val newConfig =
          Configuration(
              newHallway,
              newSideRooms,
              config.currentEnergyUsed + requiredEnergy,
              config.maxRoomSize)
      organizeAmphipods(newConfig)
      hasMovedAmphipod = true
    }
  }
  return hasMovedAmphipod
}
