package com.example.day22;

public class RebootStep {
    final CubeState cubeState;
    final Cuboid region;

    public RebootStep(CubeState cubeState, Cuboid region) {
        this.cubeState = cubeState;
        this.region = region;
    }

    public static RebootStep parse(String input) {
        var state = input.split("\\s+")[0].equals("on") ? CubeState.ON : CubeState.OFF;
        var ranges =
                input
                        .split("\\s+")
                        [1]
                        .replace("x=", "")
                        .replace("y=", "")
                        .replace("z=", "")
                        .split(",");

        var xRange = ranges[0].split("\\.\\.");
        var yRange = ranges[1].split("\\.\\.");
        var zRange = ranges[2].split("\\.\\.");

        var cuboid =
                new Cuboid(
                        new Coordinate(Integer.parseInt(xRange[0]), Integer.parseInt(yRange[0]), Integer.parseInt(zRange[0])),
                        new Coordinate(Integer.parseInt(xRange[1]), Integer.parseInt(yRange[1]), Integer.parseInt(zRange[1]))
                );
        return new RebootStep(state, cuboid);
    }
}
