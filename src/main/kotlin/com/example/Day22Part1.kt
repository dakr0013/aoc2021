package com.example

fun main() {
  val input = readLinesString(22)
  val reactorCore = mutableMapOf<Coordinate, CubeState>()
  val rebootSteps = input.map { RebootStep.parse(it) }

  for (rebootStep in rebootSteps) {
    for (cube in rebootStep.cuboid) {
      reactorCore[cube] = rebootStep.cubeState
    }
  }

  var onCubesCount = 0
  for (cube in reactorCore.values) {
    if (cube == CubeState.ON) {
      onCubesCount++
    }
  }
  println(onCubesCount)
}

class RebootStep(val cubeState: CubeState, val cuboid: Cuboid) {
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

class Cuboid(val from: Coordinate, val to: Coordinate) : Iterable<Coordinate> {
  override fun iterator(): Iterator<Coordinate> = CuboidIterator(from, to)

  internal class CuboidIterator(private val from: Coordinate, to: Coordinate) :
      Iterator<Coordinate> {
    private val maxX = to.x - from.x
    private val maxY = to.y - from.y
    private val maxZ = to.z - from.z
    private var currentX = 0
    private var currentY = 0
    private var currentZ = 0

    override fun hasNext() = currentX <= maxX && currentY <= maxY && currentZ <= maxZ

    override fun next(): Coordinate {
      val next = Coordinate(from.x + currentX, from.y + currentY, from.z + currentZ)
      currentZ++
      if (currentZ > maxZ) {
        currentZ = 0
        currentY++
        if (currentY > maxY) {
          currentY = 0
          currentX++
        }
      }
      return next
    }
  }
}

data class Coordinate(val x: Int, val y: Int, val z: Int)

enum class CubeState {
  ON,
  OFF
}
