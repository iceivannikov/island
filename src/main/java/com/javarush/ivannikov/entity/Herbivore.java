package com.javarush.ivannikov.entity;

import com.javarush.ivannikov.model.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Herbivore extends Organism {
    private static final Logger LOG = LoggerFactory.getLogger(Herbivore.class);

    protected Herbivore(String name, int weight, int speed, int maxSpeed, int maxSatiety, int successfulEating, int satiety, boolean status) {
        super(OrganismType.HERBIVORE, name, weight, speed, maxSpeed, maxSatiety, successfulEating, satiety, status);
    }

    @Override
    public void eat(Location location) {
        int satiety = this.getSatiety();
        Grass grass = location.getGrass();
        int chance = ThreadLocalRandom.current().nextInt(0, this.getSuccessfulEating() + 1);
        if (!Objects.isNull(grass) && chance % 2 == 0) {
            this.setSatiety(Math.min(satiety + 1, this.getMaxSatiety()));
            LOG.info("Животное {} поело траву в локации {}, {}",
                    this.getName(), this.getRow(), this.getCol());
        } else {
            this.setSatiety(satiety - 2);
            LOG.info("Животное {} не поело траву в локации {}, {}",
                    this.getName(), this.getRow(), this.getCol());
        }
        int maxSatiety = this.getMaxSatiety();
        changeSpeed(satiety, maxSatiety);
        changeStatus(satiety);
    }
}
