package com.kvieta.boilerfuels;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.fluids.FluidStack;

import com.kvieta.boilerfuels.config.MainConfig;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.*;
import gregtech.api.GregTechAPI;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;

public class CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        if (!Loader.isModLoaded("Railcraft")) {
            throw new RuntimeException("BoilerFuels: Railcraft is missing!");
        }
        MainConfig.init(event.getSuggestedConfigurationFile());
        BoilerFuels.LOG.info("BoilerFuels version " + Tags.VERSION);
        registerBoilerFuels();
    }

    public void init(FMLInitializationEvent event) {
        GregTechAPI.sAfterGTPostload.add(() -> {
            BoilerFuels.LOG.info("GT postload complete");
            if (MainConfig.regenerateFuelList) {
                MainConfig.clearFuelList();
                detectGTFuels();
            }
        });
    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        BoilerFuels.LOG.info("PostInit phase");

    }

    public void serverStarting(FMLServerStartingEvent event) {}

    private void registerBoilerFuels() {

        /*
         * don't add fuel, bioethanol or creosote here since railcraft adds them
         * boilerFuelMap.put("fuel", 12); // GT Diesel
         * boilerFuelMap.put("bioethanol", 4.8); // GT Ethanol
         * boilerFuelMap.put("creosote", 0.8);
         */

        for (Map.Entry<String, Integer> fuel : MainConfig.getFuels()
            .entrySet()) {
            String fluid = fuel.getKey();
            int rcHeat = fuel.getValue();
            String rcMsg = fluid + "@" + rcHeat;
            BoilerFuels.LOG.info("adding fuel {} to rc boiler with heat value {}", fluid, rcHeat);
            FMLInterModComms.sendMessage("Railcraft", "boiler-fuel-liquid", rcMsg);
        }

    }

    private static final Set<String> BLACKLISTED_FUELS = new HashSet<>(Arrays.asList("fuel", "bioethanol", "creosote"));

    private void detectGTFuels() {
        BoilerFuels.LOG.info("Detecting GT boiler fuels...");
        BoilerFuels.LOG.info(
            "Large boiler recipes: {}",
            RecipeMaps.largeBoilerFakeFuels.getBackend()
                .getAllRecipes()
                .size());
        for (GTRecipe recipe : RecipeMaps.largeBoilerFakeFuels.getBackend()
            .getAllRecipes()) {
            if (recipe.mFluidInputs == null || recipe.mFluidInputs.length == 0) {
                BoilerFuels.LOG.info("detectGTFuels ran but recipe empty");
                continue;
            }

            FluidStack fluid = recipe.mFluidInputs[0];
            String fluidName = fluid.getFluid()
                .getName();

            if (BLACKLISTED_FUELS.contains(fluidName)) {
                continue;
            }

            double steelTime = (double) recipe.mDuration / fluid.amount;
            int rcHeat = (int) Math.ceil(steelTime * (10000.0 / 3)); // 3333.33̅. Based on comparing ethanol heat
                                                                     // value to GT large boiler burn time. Fuel
                                                                     // (Diesel) multiplier is 4000, Creosote is 8000!
            BoilerFuels.LOG.info("updating config: fluid {} with heat value {}", fluidName, rcHeat);
            MainConfig.addFuel(fluidName, rcHeat);
        }
        BoilerFuels.LOG.info("Config changed: {}", MainConfig.config.hasChanged());
        MainConfig.save();
    }
}
