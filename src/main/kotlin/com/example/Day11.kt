package com.example

// Part1:
// const val maxSteps = 100
// Part2:
const val maxSteps = 1000

fun main() {
  val energyLevels =
      readLinesString(11).map {
        it.split("").drop(1).dropLast(1).map(Integer::parseInt).toMutableList()
      }
  val maxPosition = Position(energyLevels[0].lastIndex, energyLevels.lastIndex)

  val octopuses = Array(energyLevels.size) { Array<Octopus?>(energyLevels[0].size) { null } }
  for (y in energyLevels.indices) {
    for (x in energyLevels[y].indices) {
      val position = Position(x, y)
      octopuses[position.y][position.x] = Octopus(position, energyLevels[y][x], mutableListOf())
    }
  }

  for (y in energyLevels.indices) {
    for (x in energyLevels[y].indices) {
      val octopus = octopuses[y][x]!!
      val nearbyPositions = octopus.position.getNearbyPositions(maxPosition)
      for (nearbyPosition in nearbyPositions) {
        octopus.nearbyOctopuses.add(octopuses[nearbyPosition.y][nearbyPosition.x]!!)
      }
    }
  }

  var stepAfterAllFlashed = -1
  for (step in 1..maxSteps) {
    for (y in energyLevels.indices) {
      for (x in energyLevels[y].indices) {
        val octopus = octopuses[y][x]!!
        octopus.increaseEnergyLevel(step)
      }
    }

    println()
    println("After step $step:")
    var allFlashed = true
    for (y in energyLevels.indices) {
      for (x in energyLevels[y].indices) {
        val octopus = octopuses[y][x]!!
        octopus.postStep(step)
        print(octopus.energyLevel)
        if (!octopus.hasFlashed(step)) {
          allFlashed = false
        }
      }
      println()
    }

    if (allFlashed) {
      stepAfterAllFlashed = step
      break
    }
  }

  println()
  println("Sum flashes:")
  println(octopuses.flatten().sumOf { it!!.countFlashes })

  println()
  println("Step after all flashed:")
  println(stepAfterAllFlashed)
}

data class Position(val x: Int, val y: Int) {
  fun getNearbyPositions(maxPosition: Position): List<Position> {
    val nearbyPositions = mutableListOf<Position>()
    if (x > 0) {
      val left = Position(x - 1, y)
      nearbyPositions.add(left)
      if (y > 0) {
        val upperLeft = Position(x - 1, y - 1)
        nearbyPositions.add(upperLeft)
      }
      if (y < maxPosition.y) {
        val lowerLeft = Position(x - 1, y + 1)
        nearbyPositions.add(lowerLeft)
      }
    }

    if (x < maxPosition.x) {
      val right = Position(x + 1, y)
      nearbyPositions.add(right)
      if (y > 0) {
        val upperRight = Position(x + 1, y - 1)
        nearbyPositions.add(upperRight)
      }
      if (y < maxPosition.y) {
        val lowerRight = Position(x + 1, y + 1)
        nearbyPositions.add(lowerRight)
      }
    }

    if (y < maxPosition.y) {
      val lower = Position(x, y + 1)
      nearbyPositions.add(lower)
    }

    if (y > 0) {
      val upper = Position(x, y - 1)
      nearbyPositions.add(upper)
    }

    return nearbyPositions
  }
}

data class Octopus(
    val position: Position,
    var energyLevel: Int,
    var nearbyOctopuses: MutableList<Octopus>
) {
  var countFlashes = 0
    private set
  private var lastStepFlashed = -1

  fun increaseEnergyLevel(step: Int) {
    energyLevel++
    checkFlash(step)
  }

  fun hasFlashed(step: Int) = lastStepFlashed == step

  fun postStep(step: Int) {
    if (hasFlashed(step)) {
      energyLevel = 0
    }
  }

  private fun checkFlash(step: Int) {
    if (energyLevel > 9 && lastStepFlashed < step) {
      countFlashes++
      lastStepFlashed = step
      for (nearbyOctopus in nearbyOctopuses) {
        nearbyOctopus.increaseEnergyLevel(step)
      }
    }
  }
}
