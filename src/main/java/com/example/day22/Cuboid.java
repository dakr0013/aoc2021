package com.example.day22;

import java.util.ArrayList;
import java.util.List;

public class Cuboid {
    final Coordinate from;
    final Coordinate to;

    public Cuboid(Coordinate from, Coordinate to) {
        this.from = from;
        this.to = to;
    }

    public List<Cuboid> subtract(Cuboid other) {
        if (!overlaps(other)) {
            return new ArrayList<>();
        } else {
            var diff = new ArrayList<Cuboid>(8);

            // positive x -> front
            // positive y -> right
            // positive z -> up
            // --------------------------
            // back
            if (this.from.x < other.from.x) {
                var backRegion =
                        new Cuboid(new Coordinate(from.x, from.y, from.z), new Coordinate(other.from.x - 1, to.y, to.z));
                diff.add(backRegion);
            }
            // front
            if (this.to.x > other.to.x) {
                var frontRegion =
                        new Cuboid(new Coordinate(other.to.x + 1, from.y, from.z), new Coordinate(to.x, to.y, to.z));
                diff.add(frontRegion);
            }
            // top
            if (this.to.z > other.to.z) {
                var topRegion =
                        new Cuboid(
                                new Coordinate(Math.max(other.from.x, from.x), from.y, other.to.z + 1),
                                new Coordinate(Math.min(other.to.x, to.x), to.y, to.z));
                diff.add(topRegion);
            }
            // bottom
            if (this.from.z < other.from.z) {
                var bottomRegion =
                        new Cuboid(
                                new Coordinate(Math.max(other.from.x, from.x), from.y, from.z),
                                new Coordinate(Math.min(other.to.x, to.x), to.y, other.from.z - 1));
                diff.add(bottomRegion);
            }
            // right
            if (this.to.y > other.to.y) {
                var rightRegion =
                        new Cuboid(
                                new Coordinate(Math.max(other.from.x, from.x), other.to.y + 1, Math.max(other.from.z, from.z)),
                                new Coordinate(Math.min(other.to.x, to.x), to.y, Math.min(other.to.z, to.z)));
                diff.add(rightRegion);
            }
            // left
            if (this.from.y < other.from.y) {
                var leftRegion =
                        new Cuboid(
                                new Coordinate(Math.max(other.from.x, from.x), from.y, Math.max(other.from.z, from.z)),
                                new Coordinate(Math.min(other.to.x, to.x), other.from.y - 1, Math.min(other.to.z, to.z)));
                diff.add(leftRegion);
            }

            return diff;
        }
    }

    public Long cubesCount() {
        var xSize = to.x - from.x + 1L;
        var ySize = to.y - from.y + 1L;
        var zSize = to.z - from.z + 1L;
        return xSize * ySize * zSize;
    }

    public boolean overlaps(Cuboid other) {
        var xOverlaps = Math.max(other.from.x, from.x) <= Math.min(other.to.x, to.x);
        var yOverlaps = Math.max(other.from.y, from.y) <= Math.min(other.to.y, to.y);
        var zOverlaps = Math.max(other.from.z, from.z) <= Math.min(other.to.z, to.z);
        return xOverlaps && yOverlaps && zOverlaps;
    }
}
