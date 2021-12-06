package com.example

import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

fun readFileInt(day: Int): List<Int> {
  val paddedDayNumber = day.toString().padStart(2, '0')
  val uri = object {}.javaClass.getResource("/input$paddedDayNumber.txt")?.toURI()
  if (uri != null) {
    return Files.readAllLines(Path.of(uri)).stream().map(Integer::parseInt).toList()
  } else {
    throw RuntimeException("Failed reading file")
  }
}

fun readFileString(day: Int): List<String> {
  val paddedDayNumber = day.toString().padStart(2, '0')
  val uri = object {}.javaClass.getResource("/input$paddedDayNumber.txt")?.toURI()
  if (uri != null) {
    return Files.readAllLines(Path.of(uri))
  } else {
    throw RuntimeException("Failed reading file")
  }
}
