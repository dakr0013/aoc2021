package com.example

fun main() {
  val caveConnections = readLinesString(12)
  val caves = mutableMapOf<String, Cave2>()

  for (connection in caveConnections) {
    val (start, end) = connection.split("-")
    val startCave = caves.getOrPut(start) { Cave2(start, mutableSetOf()) }
    val endCave = caves.getOrPut(end) { Cave2(end, mutableSetOf()) }
    startCave.connectedCaves.add(endCave)
    endCave.connectedCaves.add(startCave)
  }

  val result = caves["start"]!!.countPathsTo("end")
  println(result)
}

class Cave2(val name: String, val connectedCaves: MutableSet<Cave2>) {
  private val isBig = name.uppercase() == name

  private fun canVisit(currPath: String) =
      if (isBig) {
        true
      } else if (name.matches("^(start|end)$".toRegex())) {
        !currPath.contains(name)
      } else {
        val currentVisitedCaves = currPath.split("-")
        val numVisitedSameCave =
            currentVisitedCaves.groupBy { it }.filterKeys { it.lowercase() == it }.values
        !currPath.contains(name) || numVisitedSameCave.all { it.size < 2 }
      }

  fun countPathsTo(target: String): Int {
    return countPathsToRec(target, "start")
  }

  private fun countPathsToRec(target: String, currPath: String): Int {
    return if (name == target) {
      println("$currPath valid")
      1
    } else {
      connectedCaves.sumOf { cave ->
        if (cave.canVisit(currPath)) {
          cave.countPathsToRec(target, "$currPath-${cave.name}")
        } else {
          println("$currPath-${cave.name} invalid")
          0
        }
      }
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Cave2

    if (name != other.name) return false

    return true
  }

  override fun hashCode() = name.hashCode()
}
