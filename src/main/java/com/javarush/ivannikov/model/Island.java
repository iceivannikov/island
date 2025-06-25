package com.javarush.ivannikov.model;

import com.javarush.ivannikov.entity.Grass;
import com.javarush.ivannikov.entity.Organism;
import com.javarush.ivannikov.factory.AnimalFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Island {
    private static final Logger LOG = LoggerFactory.getLogger(Island.class);
    private final int numThreads = Runtime.getRuntime().availableProcessors() + 2;
    private final ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
    private final Location[][] locations;
    private final int rows;
    private final int cols;

    public Island(int rows, int cols) {
        this.locations = new Location[rows][cols];
        this.rows = rows;
        this.cols = cols;
        LOG.info("Начинаем создание острова размером {}х{}", rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                locations[i][j] = new Location(i, j);
            }
        }
        LOG.info("Создание острова завершено");
    }

    public void populate() {
        LOG.info("Начинаем заселение острова животными и растениями");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Location location = locations[i][j];
                boolean hasGrass = ThreadLocalRandom.current().nextBoolean();
                if (hasGrass) {
                    location.setGrass(new Grass(true));
                } else {
                    location.setGrass(null);
                }
                int herbivore = ThreadLocalRandom.current().nextInt(0, 3);
                for (int k = 0; k < herbivore; k++) {
                    location.addOrganism(AnimalFactory.randomHerbivore());
                }
                int predator = ThreadLocalRandom.current().nextInt(0, 2);
                for (int k = 0; k < predator; k++) {
                    location.addOrganism(AnimalFactory.randomPredator());
                }
                LOG.info("В локации по координатам {}, {} заселены: Трава={}, Травоядных={}, Хищников={}",
                        i, j, hasGrass ? "да" : "нет", herbivore, predator);
            }
        }
        LOG.info("Заселение острова завершено");
    }

    public void simulateStep() {
        List<Future<?>> futures = new ArrayList<>();
        for (int t = 0; t < numThreads; t++) {
            int startRow = t * rows / numThreads;
            int endRow = (t + 1) * rows / numThreads;
            Future<?> future = executorService.submit(() -> {
                for (int i = startRow; i < endRow; i++) {
                    for (int j = 0; j < cols; j++) {
                        Location location = locations[i][j];
                        processLocation(location);
                    }
                }
            });
            futures.add(future);
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public int countAliveOrganisms() {
        Map<String, Integer> map = new ConcurrentHashMap<>();
        int count = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                for (Organism o : locations[i][j].getOrganisms()) {
                    if (o.isStatus()) {
                        map.put(o.getName(), map.getOrDefault(o.getName(), 0) + 1);
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private void processLocation(Location location) {
        List<Organism> snapshot = new ArrayList<>(location.getOrganisms());
        for (Organism o : snapshot) {
            o.liveOneCycle(this, location);
        }
        reproduceSpeciesInLocation(location);
    }

    public void shutdownExecutor() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS)) {
                    LOG.warn("Пул потоков не завершился корректно.");
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void reproduceSpeciesInLocation(Location location) {
        Map<String, List<Organism>> dataForReproduction = new HashMap<>();
        for (Organism organism : new ArrayList<>(location.getOrganisms())) {
            dataForReproduction
                    .computeIfAbsent(organism.getName(), k -> new ArrayList<>())
                    .add(organism);
        }
        for (List<Organism> group : new ArrayList<>(dataForReproduction.values())) {
            int pairs = group.size() / 2;
            for (int i = 0; i < pairs; i++) {
                Organism baby = group.getFirst().createOffspring();
                location.addOrganism(baby);
                LOG.info("Родилось новое животное {} в локации {}, {}",
                        baby.getName(), baby.getRow(), baby.getCol());
            }
        }
    }

    public Location getLocation(int row, int col) {
        return locations[row][col];
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}
