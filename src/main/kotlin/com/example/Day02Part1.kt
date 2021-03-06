package com.example

fun main() {
    val commands = readLinesString(2)
    var horizontalPosition = 0
    var verticalPosition = 0
    for(command in commands) {
        val direction = command.split(" ")[0]
        val amount = Integer.parseInt(command.split(" ")[1])

        when(direction) {
            "forward" -> horizontalPosition += amount
            "up" -> verticalPosition -= amount
            "down" -> verticalPosition += amount
        }
    }

    val result = verticalPosition * horizontalPosition
    println(result)
}
