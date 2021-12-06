package com.example

fun main() {
    val commands = readFileString(2)
    var horizontalPosition = 0
    var verticalPosition = 0
    var aim = 0
    for(command in commands) {
        val direction = command.split(" ")[0]
        val amount = Integer.parseInt(command.split(" ")[1])

        when(direction) {
            "forward" -> {
                horizontalPosition += amount
                verticalPosition += amount * aim
            }
            "up" -> aim -= amount
            "down" -> aim += amount
        }
    }

    val result = verticalPosition * horizontalPosition
    println(result)
}
