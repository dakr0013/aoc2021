package com.example.day22;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class Day22 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(new File("src/main/resources/input22.txt").toPath());
        var rebootSteps = input.stream().map(RebootStep::parse).collect(Collectors.toList());
        var reactorCore = new ReactorCore();
        reactorCore.reboot(rebootSteps);
        System.out.println(reactorCore.turnedOnCubesCount());
    }
}
