package com.kvieta.boilerfuels.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.common.config.Configuration;

public class MainConfig {

    public static Configuration config;
    private static final String CATEGORY_GENERAL = "general";
    private static final String CATEGORY_FUEL = "fuel";

    public static boolean regenerateFuelList;

    public static void init(File file) {
        config = new Configuration(file);
        config.load();

        regenerateFuelList = config.getBoolean(
            "regenerateFuelList",
            CATEGORY_GENERAL,
            true,
            "Regenerate fuel list on game start (overwrite any changes)");
    }

    public static void save() {
        if (config.hasChanged()) {
            config.save();
        }
    }

    public static void addFuel(String fluidName, int heat) {
        // if (!config.hasKey(CATEGORY_FUEL, fluidName)) {
        // config.get(CATEGORY_FUEL, fluidName, heat, "Railcraft boiler heat value").set(heat);
        // }
        config.get(CATEGORY_FUEL, fluidName, heat)
            .set(heat);
    }

    public static void clearFuelList() {
        config.removeCategory(config.getCategory(CATEGORY_FUEL));
    }

    public static Map<String, Integer> getFuels() {
        Map<String, Integer> result = new HashMap<>();

        for (String key : config.getCategory(CATEGORY_FUEL)
            .keySet()) {
            int value = config.get(CATEGORY_FUEL, key, 0)
                .getInt();
            if (value > 0) {
                result.put(key, value);
            }
        }
        return result;
    }
}
