package com.javarush.ivannikov.entity;

import com.javarush.ivannikov.model.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Predator extends Organism {
    private static final Logger LOG = LoggerFactory.getLogger(Predator.class);

    protected Predator(String name, int weight, int speed, int maxSpeed, int maxSatiety, int successfulEating, int satiety, boolean status) {
        super(OrganismType.PREDATOR, name, weight, speed, maxSpeed, maxSatiety, successfulEating, satiety, status);
    }

    @Override
    public void eat(Location location) {
        List<Organism> organisms = location.getOrganisms();
        List<Organism> herbivore = new ArrayList<>();
        for (Organism organism : organisms) {
            if (organism.getType() == OrganismType.HERBIVORE && organism.isStatus()) {
                herbivore.add(organism);
            }
        }
        Organism victim;
        int satiety = this.getSatiety();
        int maxSatiety = this.getMaxSatiety();
        if (!herbivore.isEmpty()) {
            victim = herbivore.get(ThreadLocalRandom.current().nextInt(herbivore.size()));
            victim.setStatus(false);
            this.setSatiety(Math.min(satiety + 1, this.getMaxSatiety()));
            LOG.info("Животное {} съело животное {} в локации {}, {}",
                    this.getName(), victim.getName(), this.getRow(), this.getCol());
        } else {
            LOG.info("Животное {} не смогло поесть в локации {}, {}",
                    this.getName(), this.getRow(), this.getCol());
            this.setSatiety(satiety - 2);
        }
        changeSpeed(satiety, maxSatiety);
        changeStatus(satiety);
    }
}
