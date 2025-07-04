package com.logistics.model;

import java.util.Objects;

public class Route {
    private City source;
    private City destination;
    private double distance;

    public Route(City source, City destination, double distance) {
        this.source = source;
        this.destination = destination;
        this.distance = distance;
    }

    public City getSource() {
        return source;
    }

    public City getDestination() {
        return destination;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return source.getName() + " --(" + distance + " km)--> " + destination.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return Double.compare(route.distance, distance) == 0 &&
                ((source.equals(route.source) && destination.equals(route.destination)) ||
                        (source.equals(route.destination) && destination.equals(route.source)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance) ^ Objects.hash(source.getId() + destination.getId());
    }
}