package com.example

fun main() {
  val caveConnections = readLinesString(12)
  val caves = mutableMapOf<String, Cave>()

  for (connection in caveConnections) {
    val (start, end) = connection.split("-")
    val startCave = caves.getOrPut(start) { Cave(start, mutableSetOf()) }
    val endCave = caves.getOrPut(end) { Cave(end, mutableSetOf()) }
    startCave.connectedCaves.add(endCave)
    endCave.connectedCaves.add(startCave)
  }

  val result = caves["start"]!!.countPathsTo("end")
  println(result)
}

class Cave(val name: String, val connectedCaves: MutableSet<Cave>) {
  private val isBig = name.uppercase() == name

  private fun canVisit(currPath: String) =
      if (isBig) {
        true
      } else {
        !currPath.contains(name)
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

    other as Cave

    if (name != other.name) return false

    return true
  }

  override fun hashCode() = name.hashCode()
}
