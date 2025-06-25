package com.javarush.ivannikov.model;

import com.javarush.ivannikov.entity.Grass;
import com.javarush.ivannikov.entity.Organism;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Location {
    private final ReentrantLock lock = new ReentrantLock();
    private final List<Organism> organisms = new ArrayList<>();
    private Grass grass;
    private final int row;
    private final int col;

    public Location(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void withLock(Runnable action) {
        lock.lock();
        try {
            action.run();
        } finally {
            lock.unlock();
        }
    }

    public void addOrganism(Organism organism) {
        this.withLock(() -> organisms.add(organism));
    }

    public void addOrganism(Organism organism, int row, int col) {
        this.withLock(() -> {
            organism.setCoordinates(row, col);
            organisms.add(organism);
        });
    }

    public void deleteOrganism() {
        this.withLock(() -> organisms.removeIf(organism -> !organism.isStatus()));
    }

    public void deleteOrganism(Organism organism) {
        this.withLock(() -> organisms.remove(organism));
    }

    public List<Organism> getOrganisms() {
        return new ArrayList<>(organisms);
    }

    public Grass getGrass() {
        return grass;
    }

    public void setGrass(Grass grass) {
        this.grass = grass;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
