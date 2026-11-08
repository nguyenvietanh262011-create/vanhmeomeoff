package com.example.addon;

import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import com.example.addon.modules.Miner;

public class AddonTemplate extends MeteorAddon {
    public static final Category VANHMEOMEO = new Category("vanhmeomeo");

    @Override
    public void onInitialize() {
        Modules.get().add(new Miner());
    }

    @Override
    public String getPackage() {
        return "com.example.addon";
    }
}
