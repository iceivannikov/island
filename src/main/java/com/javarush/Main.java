package com.javarush;

import com.javarush.ivannikov.model.Island;

public class Main {
    public static void main(String[] args) {
        Island island = new Island(2, 2);
        island.populate();
        while (island.countAliveOrganisms() > 0) {
            island.simulateStep();
        }
        island.shutdownExecutor();
        System.out.println("Симуляция завершена — все животные вымерли.");
    }
}