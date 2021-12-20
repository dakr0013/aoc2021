package com.example

fun main() {
  val input = readLinesString(20)
  val imageEnhancement = input.first().toCharArray()
  val image = Image(input.drop(2))
  for (i in 1..2) {
    image.enhance(imageEnhancement)
  }
  println(image.lightPixelCount)
}

class Image(lines: List<String>) {
  private var image: Array<CharArray>
  var lightPixelCount = 0
    private set

  init {
    val blackLine = ".".repeat(lines[0].length + 6).toCharArray()
    val blackLines = arrayOf(blackLine, blackLine, blackLine)
    image = blackLines.deepCopy() + lines.map { "...$it...".toCharArray() } + blackLines.deepCopy()
    for (line in image) {
      for (pixel in line) {
        if (pixel == '#') {
          lightPixelCount++
        }
      }
    }
  }

  private fun expand() {
    val outerChar = image[0][0]
    val outerLine = arrayOf(CharArray(image[0].size + 2) { outerChar })
    image =
        outerLine.deepCopy() +
            image.map { charArrayOf(outerChar) + it + charArrayOf(outerChar) } +
            outerLine.deepCopy()
  }

  fun enhance(enhancement: CharArray) {
    this.expand()
    val newImage = image.deepCopy()
    lightPixelCount = 0
    val xMax = newImage[0].lastIndex
    val yMax = newImage.lastIndex
    for (y in newImage.indices) {
      for (x in newImage[y].indices) {
        val binaryNumber = StringBuilder()
        // top left
        if (y > 0 && x > 0) {
          binaryNumber.append(image[y - 1][x - 1])
        } else {
          binaryNumber.append(image[y][x])
        }
        // top center
        if (y > 0) {
          binaryNumber.append(image[y - 1][x])
        } else {
          binaryNumber.append(image[y][x])
        }
        // top right
        if (y > 0 && x < xMax) {
          binaryNumber.append(image[y - 1][x + 1])
        } else {
          binaryNumber.append(image[y][x])
        }
        // left
        if (x > 0) {
          binaryNumber.append(image[y][x - 1])
        } else {
          binaryNumber.append(image[y][x])
        }
        // center
        binaryNumber.append(image[y][x])
        // right
        if (x < xMax) {
          binaryNumber.append(image[y][x + 1])
        } else {
          binaryNumber.append(image[y][x])
        }
        // bottom left
        if (y < yMax && x > 0) {
          binaryNumber.append(image[y + 1][x - 1])
        } else {
          binaryNumber.append(image[y][x])
        }
        // bottom center
        if (y < yMax) {
          binaryNumber.append(image[y + 1][x])
        } else {
          binaryNumber.append(image[y][x])
        }
        // bottom right
        if (y < yMax && x < xMax) {
          binaryNumber.append(image[y + 1][x + 1])
        } else {
          binaryNumber.append(image[y][x])
        }
        val index = binaryNumber.toString().replace("#", "1").replace(".", "0").toInt(2)
        newImage[y][x] = enhancement[index]
        if (newImage[y][x] == '#') {
          lightPixelCount++
        }
      }
    }
    image = newImage
  }

  fun print() {
    for (line in image) {
      println(line.concatToString())
    }
    println()
  }
}

fun Array<CharArray>.deepCopy(): Array<CharArray> {
  return this.map { it.copyOf() }.toTypedArray()
}

fun CharArray.binaryToInt(): Int {
  return this.concatToString().replace("#", "1").replace(".", "0").toInt(2)
}
