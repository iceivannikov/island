package com.javarush.ivannikov.entity;

import com.javarush.ivannikov.model.Island;
import com.javarush.ivannikov.model.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

public abstract class Organism {
private static final Logger LOG = LoggerFactory.getLogger(Organism.class);

    /**
     * Определяет тип животного
     * HERBIVORE - травоядное
     * PREDATOR - хищник
     */
    private final OrganismType type;

    /**
     * Название животного
     */
    private final String name;

    /**
     * Вес животного (в кг)
     * Используется для расчёта потребления пищи или силы
     * Рекомендуемый диапазон: положительное число
     */
    private int weight;

    /**
     * Скорость передвижения животного
     * Диапазон: от 0 до 5
     * Показывает, на сколько клеток может переместиться за цикл
     * При максимальной сытости (например, при переедании) скорость может снижаться
     */
    private int speed;

    /**
     * Максимальная скорость животного
     * Диапазон: от 0 до 10
     * Показывает, на сколько клеток может переместиться за цикл
     */
    private final int maxSpeed;

    /**
     * Максимальная сытость животного
     * Диапазон: от 1 до 10
     * Характеризует, на сколько животное может быть сытым
     */
    private final int maxSatiety;

    /**
     * Шанс успешного поедания (для хищников и травоядных)
     * Диапазон: от 0 до 5
     * Определяет вероятность успеха поедания пищи
     */
    private final int successfulEating;

    /**
     * Текущий уровень сытости животного
     * Диапазон: от 0 (голоден) до maxSatiety
     * При достижении 0 животное умирает
     * При успешной еде увеличивается на successfulEating, но не больше maxSatiety
     */
    private int satiety;

    /**
     * Состояние животного: true — живое, false — мёртвое
     * Используется для фильтрации животных в симуляции
     */
    private boolean status;
    protected int row;
    protected int col;

    protected Organism(OrganismType type, String name, int weight,
                       int speed, int maxSpeed, int maxSatiety,
                       int successfulEating, int satiety, boolean status) {
        if (type == null) {
            throw new IllegalArgumentException("Тип животного не может быть пустым");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя животного не может быть пустым");
        }
        if (weight <= 0) {
            throw new IllegalArgumentException("Вес животного должен быть положительным");
        }
        if (speed < 0 || speed > 5) {
            throw new IllegalArgumentException("Скорость животного должна быть в диапазоне 0–5");
        }
        if (maxSatiety < 1 || maxSatiety > 10) {
            throw new IllegalArgumentException("Максимальная сытость должна быть в диапазоне 1–10");
        }
        if (successfulEating < 0 || successfulEating > 5) {
            throw new IllegalArgumentException("Шанс успешного поедания должен быть в диапазоне 0–5");
        }
        if (satiety < 0 || satiety > maxSatiety) {
            throw new IllegalArgumentException("Сытость должна быть в диапазоне от 0 до maxSatiety");
        }
        this.type = type;
        this.name = name;
        this.weight = weight;
        this.speed = speed;
        this.maxSpeed = maxSpeed;
        this.maxSatiety = maxSatiety;
        this.successfulEating = successfulEating;
        this.satiety = satiety;
        this.status = status;
    }

    /**
     * Перемещает животное на новую локацию в пределах скорости.
     * Вызывается один раз за цикл симуляции.
     * Реализация выбирает направление случайно или по логике поиска пищи.
     */
    public void move(Island island, Location location) {
        int row = location.getRow();
        int col = location.getCol();
        int deltaRow = ThreadLocalRandom.current().nextInt(-getSpeed(), getSpeed() + 1);
        int deltaCol = ThreadLocalRandom.current().nextInt(-getSpeed(), getSpeed() + 1);
        int newRow = row + deltaRow;
        int newCol = col + deltaCol;
        if (newRow < 0 || newRow >= island.getRows() || newCol < 0 || newCol >= island.getCols()) {
            return;
        }
        Location newLocation = island.getLocation(newRow, newCol);
        Location firstLocation, secondLocation;
       if (newRow < row || (newRow == row && newCol < col)) {
           firstLocation = newLocation;
           secondLocation = location;
       }  else {
           firstLocation = location;
           secondLocation = newLocation;
       }
       firstLocation.withLock(() -> {
           secondLocation.withLock(() -> {
               location.deleteOrganism(this);
               newLocation.addOrganism(this, newRow, newCol);
               LOG.info("Животное {} перемещается из координат {}, {} на новые координаты {}, {}",
                       this.getName(), row, col, newRow, newCol);
           });
       });
    }

    /**
     * Определяет порядок поедания пищи
     * Как для травоядных, так и для хищников
     *
     * @param location клетка острова в которой происходит действие
     */
    public abstract void eat(Location location);

    /**
     * Основной метод который запускает жизненный цикл животного
     *
     * @param island   остров, состоящий из двумерного массива Locations
     * @param location клетка острова в которой происходит действие
     */
    public void liveOneCycle(Island island, Location location) {
        move(island, location);
        eat(location);
    }

    protected void changeStatus(int satiety) {
        if (satiety <= 0) {
            this.setStatus(false);
        }
    }

    protected void changeSpeed(int satiety, int maxSatiety) {
        int speed = this.getSpeed();
        if (satiety < maxSatiety / 2 || satiety > maxSatiety * 0.8) {
            this.setSpeed(Math.max(speed - 1, 0));
        } else {
            this.setSpeed(getMaxSpeed());
        }
    }

    public abstract Organism createOffspring();

    public OrganismType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public int getMaxSatiety() {
        return maxSatiety;
    }

    public int getSuccessfulEating() {
        return successfulEating;
    }

    public int getSatiety() {
        return satiety;
    }

    public void setSatiety(int satiety) {
        this.satiety = satiety;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setCoordinates(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
