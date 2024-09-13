package com.kvieta.boilerfuels;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.common.event.*;

public class CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        BoilerFuels.LOG.info("BoilerFuels version " + Tags.VERSION);
        registerBoilerFuels();
    }

    public void init(FMLInitializationEvent event) {}

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {

    }

    public void serverStarting(FMLServerStartingEvent event) {}

    private void registerBoilerFuels() {
        Map<String, Double> boilerFuelMap = new HashMap<>();

        /*
         * don't add fuel, bioethanol or creosote here since railcraft adds them
         * boilerFuelMap.put("fuel", 12); // GT Diesel
         * boilerFuelMap.put("bioethanol", 4.8); // GT Ethanol
         * boilerFuelMap.put("creosote", 0.8);
         */
        boilerFuelMap.put("fishoil", 0.2); // this one goes in combustion generator.
        boilerFuelMap.put("seedoil", 0.2); // but this one doesn't. semifluid only!.
        boilerFuelMap.put("methanol", 2.1);
        boilerFuelMap.put("liquid_light_fuel", 7.6);
        boilerFuelMap.put("liquid_heavy_fuel", 24.0);
        boilerFuelMap.put("short.mead", 0.1); // you get it from bees, apparently.
        boilerFuelMap.put("biomass", 0.8);
        boilerFuelMap.put("glycerol", 16.4); // yes, glycerol burns twice as long as biodiesel. also longer than
                                             // gasoline v0v
        boilerFuelMap.put("biodiesel", 8.0);
        boilerFuelMap.put("nitrofuel", 25.0); // Cetane-boosted Diesel
        boilerFuelMap.put("jet fuel a", 56.2); // please make jet fuel great
        boilerFuelMap.put("jet fuel no.3", 45.6); // please I beg of you
        boilerFuelMap.put("octane", 2.0);
        boilerFuelMap.put("gasoline", 14.4);
        boilerFuelMap.put("ethanol gasoline", 27.5);
        boilerFuelMap.put("highoctanegasoline", 62.5);
        boilerFuelMap.put("oil", 0.4);
        boilerFuelMap.put("liquid_light_oil", 2.0);
        boilerFuelMap.put("liquid_medium_oil", 3.0); // GT Raw Oil
        boilerFuelMap.put("liquid_heavy_oil", 4.0);
        boilerFuelMap.put("butanol", 10.0);
        boilerFuelMap.put("ether", 13.4);
        boilerFuelMap.put("naphthenicacid", 8.0);

        for (Map.Entry<String, Double> entry : boilerFuelMap.entrySet()) {
            String fluid = entry.getKey();
            double steelTime = entry.getValue();
            int rcHeat = (int) Math.ceil(steelTime * (10000.0 / 3)); // 3333.33̅. Based on comparing ethanol heat
                                                                     // value to GT large boiler burn time. Fuel
                                                                     // (Diesel) multiplier is 4000, Creosote is 8000!
            String rcMsg = fluid + "@" + rcHeat;
            BoilerFuels.LOG.info("adding fuel " + fluid + " to rc boiler with heat value " + rcHeat);
            FMLInterModComms.sendMessage("Railcraft", "boiler-fuel-liquid", rcMsg);
        }

    }
}
