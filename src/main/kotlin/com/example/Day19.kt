package com.example

import kotlin.math.*

val rotationMatrices = generatePossibleRotationMatrices()

fun main() {
  val beaconScanners = parse(readLinesString(19))
  val trenchMap = TrenchMap(beaconScanners)
  println("Part1: ${trenchMap.beacons.size}")
  println("Part2: ${trenchMap.getLargestManhattenDistance()}")
}

class TrenchMap(beaconScanners: List<BeaconScanner>) {
  val beacons = mutableSetOf<Vector>()
  private val relativeScannerPos = mutableMapOf<Int, Vector>()

  fun getLargestManhattenDistance(): Int {
    var largestDistance = 0
    val allScannerPositions = relativeScannerPos.values.toList()
    for (i in allScannerPositions.indices) {
      for (j in allScannerPositions.indices.drop(i)) {
        val scannerPos1 = allScannerPositions[i]
        val scannerPos2 = allScannerPositions[j]
        val dist = scannerPos1.manhattenDistance(scannerPos2)
        if (dist > largestDistance) {
          largestDistance = dist
        }
      }
    }
    return largestDistance
  }

  init {
    val unconnectedBeaconScanners = beaconScanners.drop(1).toMutableList()
    val connectedBeaconScanners = mutableListOf(beaconScanners.first())
    relativeScannerPos[0] = Vector(intArrayOf(0, 0, 0))
    while (unconnectedBeaconScanners.isNotEmpty()) {
      println(unconnectedBeaconScanners.size)
      outer@ for (connectedScanner in connectedBeaconScanners.reversed()) {
        for (unconnectedScanner in unconnectedBeaconScanners) {
          val overlappingResult = connectedScanner.tryOverlap(unconnectedScanner)
          if (overlappingResult != null) {
            relativeScannerPos[unconnectedScanner.id] =
                relativeScannerPos[connectedScanner.id]!! + overlappingResult.relativeScannerPos
            unconnectedBeaconScanners.remove(unconnectedScanner)

            val newConnectedScanner =
                BeaconScanner(
                    unconnectedScanner.id,
                    unconnectedScanner.getScannedBeacons(overlappingResult.rotation).toMutableSet())
            connectedBeaconScanners.add(newConnectedScanner)
            break@outer
          }
        }
      }
    }
    for (scanner in connectedBeaconScanners) {
      for (beacon in scanner.getScannedBeacons(Matrix.identity(3))) {
        beacons.add(beacon + relativeScannerPos[scanner.id]!!)
      }
    }
  }
}

data class OverlappingResult(val rotation: Matrix, val relativeScannerPos: Vector)

class BeaconScanner(val id: Int, private val scannedBeacons: MutableSet<Vector>) {
  private val rotatedBeaconsCache = mutableMapOf<Matrix, Set<Vector>>()

  fun tryOverlap(other: BeaconScanner): OverlappingResult? {
    for (rotation in rotationMatrices) {
      val otherScannedBeacons = other.getScannedBeacons(rotation)
      for (origin in this.scannedBeacons) {
        val thisBeaconsFromOrigin = mutableSetOf<Vector>()
        for (beacon in this.scannedBeacons) {
          thisBeaconsFromOrigin.add(beacon - origin)
        }
        for (otherOrigin in otherScannedBeacons) {
          val otherBeaconsFromOrigin = mutableSetOf<Vector>()
          for (beacon in otherScannedBeacons) {
            otherBeaconsFromOrigin.add(beacon - otherOrigin)
          }

          val overlappingBeacons = thisBeaconsFromOrigin.intersect(otherBeaconsFromOrigin)
          if (overlappingBeacons.size >= 12) {
            return OverlappingResult(rotation, origin - otherOrigin)
          }
        }
      }
    }
    return null
  }

  fun addScannedBeacon(beacon: Vector) = scannedBeacons.add(beacon)

  fun getScannedBeacons(rotation: Matrix): Set<Vector> {
    return if (rotatedBeaconsCache.containsKey(rotation)) {
      rotatedBeaconsCache[rotation]!!
    } else {
      val rotatedBeacons = mutableSetOf<Vector>()
      for (beacon in scannedBeacons) {
        val rotatedBeacon = rotation * beacon
        rotatedBeacons.add(rotatedBeacon)
      }
      rotatedBeaconsCache[rotation] = rotatedBeacons
      rotatedBeaconsCache[rotation]!!
    }
  }

  fun debugPrint() {
    val string = StringBuilder()
    for (beacons in rotatedBeaconsCache.values) {
      string.append("--- scanner $id ---\n")
      for (beacon in beacons) {
        string.append(beacon)
        string.append("\n")
      }
      string.append("\n")
    }
    println(string.toString())
  }

  override fun toString(): String {
    val string = StringBuilder()
    string.append("--- scanner $id ---\n")
    string.append("scanned beacons count: ${scannedBeacons.size}")
    for (beacon in scannedBeacons) {
      string.append(beacon)
      string.append("\n")
    }
    return string.toString()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as BeaconScanner

    if (id != other.id) return false

    return true
  }

  override fun hashCode(): Int {
    return id
  }
}

class Vector(val values: IntArray) {
  val rowCount = values.size

  fun manhattenDistance(other: Vector): Int {
    val delta = this - other
    var sum = 0
    for (value in delta.values) {
      sum += value
    }
    return sum.absoluteValue
  }

  operator fun minus(other: Vector): Vector {
    if (rowCount != other.rowCount) {
      throw IllegalArgumentException("Subtraction not possible: this row count != other row count")
    }
    val result = values.zip(other.values).map { it.first - it.second }
    return Vector(result.toIntArray())
  }

  operator fun plus(other: Vector): Vector {
    if (rowCount != other.rowCount) {
      throw IllegalArgumentException("Addition not possible: this row count != other row count")
    }
    val result = values.zip(other.values).map { it.first + it.second }
    return Vector(result.toIntArray())
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Vector

    if (!values.contentEquals(other.values)) return false

    return true
  }

  override fun hashCode(): Int {
    return values.contentHashCode()
  }

  override fun toString(): String {
    return values.contentToString()
  }
}

class Matrix(val matrix: Array<IntArray>) {
  companion object {
    fun identity(size: Int): Matrix {
      val result = Array(size) { IntArray(size) }
      for (i in 0 until size) {
        result[i][i] = 1
      }
      return Matrix(result)
    }
  }

  val colCount = matrix[0].size
  val rowCount = matrix.size

  operator fun times(vector: Vector): Vector {
    if (vector.rowCount != this.colCount) {
      throw IllegalArgumentException(
          "Matrix multiplication not possible: column count of left argument != row count of right argument")
    }
    val result = IntArray(this.rowCount)
    for (rowIndex in matrix.indices) {
      for (colIndex in matrix[0].indices) {
        result[rowIndex] += matrix[rowIndex][colIndex] * vector.values[colIndex]
      }
    }
    return Vector(result)
  }

  operator fun times(other: Matrix): Matrix {
    if (other.rowCount != this.colCount) {
      throw IllegalArgumentException(
          "Matrix multiplication not possible: column count of left argument != row count of right argument")
    }
    val result = Array(this.rowCount) { IntArray(other.colCount) }
    for (rowIndex in result.indices) {
      for (colIndex in result[0].indices) {
        for (i in matrix[0].indices) {
          result[rowIndex][colIndex] += matrix[rowIndex][i] * other.matrix[i][colIndex]
        }
      }
    }
    return Matrix(result)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Matrix

    if (!matrix.contentDeepEquals(other.matrix)) return false

    return true
  }

  override fun hashCode(): Int {
    return matrix.contentDeepHashCode()
  }

  override fun toString(): String {
    val result = StringBuilder()

    result.append("⎡")
    for (col in matrix.first()) {
      result.append(col.toString().padStart(3, ' '))
    }
    result.append(" ⎤")
    result.append("\n")

    for (line in matrix.drop(1).dropLast(1)) {
      result.append("⎢")
      for (col in line) {
        result.append(col.toString().padStart(3, ' '))
      }
      result.append(" ⎥")
      result.append("\n")
    }

    result.append("⎣")
    for (col in matrix.last()) {
      result.append(col.toString().padStart(3, ' '))
    }
    result.append(" ⎦")
    result.append("\n")

    return result.toString()
  }
}

fun generatePossibleRotationMatrices(): Set<Matrix> {
  val rotationMatrices = mutableSetOf<Matrix>()
  for (x in 0..3) {
    for (y in 0..3) {
      for (z in 0..3) {
        val rotationMatrix = generateRotationMatrix(x * PI / 2, y * PI / 2, z * PI / 2)
        rotationMatrices.add(rotationMatrix)
      }
    }
  }
  return rotationMatrices
}

fun generateRotationMatrix(
    xRot: Double,
    yRot: Double,
    zRot: Double,
): Matrix {
  val result = Array(3) { IntArray(3) }
  result[0][0] = (cos(zRot) * cos(yRot)).roundToInt()
  result[0][1] = (cos(zRot) * sin(yRot) * sin(xRot) - sin(zRot) * cos(xRot)).roundToInt()
  result[0][2] = (cos(zRot) * sin(yRot) * cos(xRot) + sin(zRot) * sin(xRot)).roundToInt()
  result[1][0] = (sin(zRot) * cos(yRot)).roundToInt()
  result[1][1] = (sin(zRot) * sin(yRot) * sin(xRot) + cos(zRot) * cos(xRot)).roundToInt()
  result[1][2] = (sin(zRot) * sin(yRot) * cos(xRot) - cos(zRot) * sin(xRot)).roundToInt()
  result[2][0] = (-sin(yRot)).roundToInt()
  result[2][1] = (cos(yRot) * sin(xRot)).roundToInt()
  result[2][2] = (cos(yRot) * cos(xRot)).roundToInt()
  return Matrix(result)
}

fun parse(lines: List<String>): List<BeaconScanner> {
  val allScanners = mutableListOf<BeaconScanner>()
  var currentScanner: BeaconScanner? = null
  for (line in lines) {
    if (line.isBlank()) {
      allScanners.add(currentScanner!!)
    } else if (line.matches("^--- scanner \\d+ ---".toRegex())) {
      val id = line.replace("--- scanner ", "").replace(" ---", "").toInt()
      currentScanner = BeaconScanner(id, mutableSetOf())
    } else {
      val (x, y, z) = line.split(",").map { it.toInt() }
      currentScanner!!.addScannedBeacon(Vector(intArrayOf(x, y, z)))
    }
  }
  allScanners.add(currentScanner!!)
  return allScanners
}
