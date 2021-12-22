package com.example.day22;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReactorCore {
    List<Cuboid> turnedOnRegions = Collections.emptyList();

    public void reboot(List<RebootStep> steps) {
        turnedOnRegions = Collections.emptyList();
        for (var step : steps) {
            var newRegion = step.region;
            var newRegions = new ArrayList<Cuboid>(turnedOnRegions.size());

            for (var region : turnedOnRegions) {
                if (region.overlaps(newRegion)) {
                    newRegions.addAll(region.subtract(newRegion));
                } else {
                    newRegions.add(region);
                }
            }
            if (step.cubeState == CubeState.ON) {
                newRegions.add(newRegion);
            }
            turnedOnRegions = newRegions;
        }
    }

    public Long turnedOnCubesCount() {
        var turnedOnCubes = 0L;
        for (var region : turnedOnRegions) {
            turnedOnCubes += region.cubesCount();
        }
        return turnedOnCubes;
    }
}
