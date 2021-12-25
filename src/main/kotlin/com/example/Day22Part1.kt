package com.example

import kotlin.math.max
import kotlin.math.min

fun main() {
  val input = readLinesString(22)
  val rebootSteps = input.map { RebootStep.parse(it) }
  val reactorCore = ReactorCore()
  reactorCore.reboot(rebootSteps)
  println(reactorCore.turnedOnCubesCount(true))
}

class ReactorCore(var turnedOnRegions: List<Cuboid> = emptyList()) {
  private val initializationProcedureRegion =
      Cuboid(Coordinate(-50, -50, -50), Coordinate(50, 50, 50))

  fun reboot(steps: List<RebootStep>) {
    turnedOnRegions = emptyList()
    for (step in steps) {
      val newRegion = step.region
      val newRegions = mutableListOf<Cuboid>()

      for (region in turnedOnRegions) {
        if (region.overlaps(newRegion)) {
          newRegions.addAll(region.subtract(newRegion))
        } else {
          newRegions.add(region)
        }
      }
      if (step.cubeState == CubeState.ON) {
        newRegions.add(newRegion)
      }
      turnedOnRegions = newRegions
    }
  }

  fun turnedOnCubesCount(onlyConsiderInitProcedureRegion: Boolean = false): Long {
    var turnedOnCubes = 0L
    for (region in turnedOnRegions) {
      turnedOnCubes +=
          if (onlyConsiderInitProcedureRegion) {
            region.cubesCount(initializationProcedureRegion)
          } else {
            region.cubesCount()
          }
    }
    return turnedOnCubes
  }
}

data class Cuboid(val from: Coordinate, val to: Coordinate) {
  fun subtract(other: Cuboid): List<Cuboid> {
    if (!overlaps(other)) {
      return listOf(this)
    } else {
      val difference = mutableListOf<Cuboid>()

      // positive x -> front
      // positive y -> right
      // positive z -> up
      // --------------------------
      // back
      if (this.from.x < other.from.x) {
        val backRegion =
            Cuboid(Coordinate(from.x, from.y, from.z), Coordinate(other.from.x - 1, to.y, to.z))
        difference.add(backRegion)
      }
      // front
      if (this.to.x > other.to.x) {
        val frontRegion =
            Cuboid(Coordinate(other.to.x + 1, from.y, from.z), Coordinate(to.x, to.y, to.z))
        difference.add(frontRegion)
      }
      // top
      if (this.to.z > other.to.z) {
        val topRegion =
            Cuboid(
                Coordinate(max(other.from.x, from.x), from.y, other.to.z + 1),
                Coordinate(min(other.to.x, to.x), to.y, to.z))
        difference.add(topRegion)
      }
      // bottom
      if (this.from.z < other.from.z) {
        val bottomRegion =
            Cuboid(
                Coordinate(max(other.from.x, from.x), from.y, from.z),
                Coordinate(min(other.to.x, to.x), to.y, other.from.z - 1))
        difference.add(bottomRegion)
      }
      // right
      if (this.to.y > other.to.y) {
        val rightRegion =
            Cuboid(
                Coordinate(max(other.from.x, from.x), other.to.y + 1, max(other.from.z, from.z)),
                Coordinate(min(other.to.x, to.x), to.y, min(other.to.z, to.z)))
        difference.add(rightRegion)
      }
      // left
      if (this.from.y < other.from.y) {
        val leftRegion =
            Cuboid(
                Coordinate(max(other.from.x, from.x), from.y, max(other.from.z, from.z)),
                Coordinate(min(other.to.x, to.x), other.from.y - 1, min(other.to.z, to.z)))
        difference.add(leftRegion)
      }

      return difference
    }
  }

  fun cubesCount(withIn: Cuboid = maxSized): Long {
    if (withIn == maxSized) {
      val xSize = to.x - from.x + 1L
      val ySize = to.y - from.y + 1L
      val zSize = to.z - from.z + 1L
      return xSize * ySize * zSize
    }
    if (!this.overlaps(withIn)) {
      return 0L
    }
    val overallCubeCount = this.cubesCount()
    val cubeCountOutside = this.subtract(withIn).sumOf { it.cubesCount() }
    return overallCubeCount - cubeCountOutside
  }

  fun overlaps(other: Cuboid): Boolean {
    val xOverlaps = max(other.from.x, from.x) <= min(other.to.x, to.x)
    val yOverlaps = max(other.from.y, from.y) <= min(other.to.y, to.y)
    val zOverlaps = max(other.from.z, from.z) <= min(other.to.z, to.z)
    return xOverlaps && yOverlaps && zOverlaps
  }

  companion object {
    val maxSized =
        Cuboid(
            Coordinate(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE),
            Coordinate(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE))
  }
}

data class Coordinate(val x: Int, val y: Int, val z: Int)

enum class CubeState {
  ON,
  OFF
}

data class RebootStep(val cubeState: CubeState, val region: Cuboid) {
  companion object {
    fun parse(input: String): RebootStep {
      val state =
          if (input.split(" ").first() == "on") {
            CubeState.ON
          } else {
            CubeState.OFF
          }
      val (xRange, yRange, zRange) =
          input
              .split(" ")
              .last()
              .replace("x=", "")
              .replace("y=", "")
              .replace("z=", "")
              .split(",")
              .map {
                val (first, last) = it.split("..")
                first.toInt()..last.toInt()
              }
      val cuboid =
          Cuboid(
              Coordinate(xRange.first, yRange.first, zRange.first),
              Coordinate(xRange.last, yRange.last, zRange.last),
          )
      return RebootStep(state, cuboid)
    }
  }
}
