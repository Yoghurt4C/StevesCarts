package vswe.stevesvehicles.modules.data.registries;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import vswe.stevesvehicles.modules.data.ModuleData;
import vswe.stevesvehicles.modules.data.ModuleDataHull;
import vswe.stevesvehicles.modules.data.ModuleRegistry;
import vswe.stevesvehicles.modules.data.ModuleSide;
import vswe.stevesvehicles.old.Helpers.Localization;
import vswe.stevesvehicles.old.Helpers.ResourceHelper;
import vswe.stevesvehicles.old.Models.Cart.ModelHull;
import vswe.stevesvehicles.old.Models.Cart.ModelHullTop;
import vswe.stevesvehicles.old.Models.Cart.ModelPigHead;
import vswe.stevesvehicles.old.Models.Cart.ModelPigHelmet;
import vswe.stevesvehicles.old.Models.Cart.ModelPigTail;
import vswe.stevesvehicles.old.Models.Cart.ModelPumpkinHull;
import vswe.stevesvehicles.old.Models.Cart.ModelPumpkinHullTop;
import vswe.stevesvehicles.old.Modules.Hull.ModuleCheatHull;
import vswe.stevesvehicles.old.Modules.Hull.ModuleGalgadorian;
import vswe.stevesvehicles.old.Modules.Hull.ModulePig;
import vswe.stevesvehicles.old.Modules.Hull.ModulePumpkin;
import vswe.stevesvehicles.old.Modules.Hull.ModuleReinforced;
import vswe.stevesvehicles.old.Modules.Hull.ModuleStandard;
import vswe.stevesvehicles.old.Modules.Hull.ModuleWood;
import vswe.stevesvehicles.old.StevesVehicles;
import vswe.stevesvehicles.vehicles.VehicleRegistry;

import static vswe.stevesvehicles.old.Helpers.ComponentTypes.*;

public class ModuleRegistryCartHulls extends ModuleRegistry {
    public ModuleRegistryCartHulls() {
        super("steves_carts_hulls");

        ModuleData wood = new ModuleDataHull("wooden_hull", ModuleWood.class, 50, 1, 0, 15) {
            @Override
            @SideOnly(Side.CLIENT)
            public void loadModels() {
                addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/hullModelWooden.png")));
                addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/hullModelWoodenTop.png")));
            }
        };

        wood.addShapedRecipe(   "planks",        null,          "planks",
                                "planks",       "planks",       "planks",
                                WOODEN_WHEELS,   null,          WOODEN_WHEELS   );

        wood.addVehicles(VehicleRegistry.CART);
        register(wood);



        ModuleData standard = new ModuleDataHull("standard_hull", ModuleStandard.class, 200, 3, 6, 50) {
            @Override
            @SideOnly(Side.CLIENT)
            public void loadModels() {
                addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/hullModelStandard.png")));
                addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/hullModelStandardTop.png")));
            }
        };

        standard.addShapedRecipe(   Items.iron_ingot,   null,               Items.iron_ingot,
                                    Items.iron_ingot,   Items.iron_ingot,   Items.iron_ingot,
                                    IRON_WHEELS,        null,               IRON_WHEELS);

        standard.addVehicles(VehicleRegistry.CART);
        register(standard);



        ModuleData reinforced = new ModuleDataHull("reinforced_hull", ModuleReinforced.class, 500, 5, 12, 150) {
            @Override
            @SideOnly(Side.CLIENT)
            public void loadModels() {
                addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/hullModelLarge.png")));
                addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/hullModelLargeTop.png")));
            }
        };

        reinforced.addShapedRecipe( REINFORCED_METAL,   null,               REINFORCED_METAL,
                                    REINFORCED_METAL,   REINFORCED_METAL,   REINFORCED_METAL,
                                    REINFORCED_WHEELS,  null,               REINFORCED_WHEELS);

        reinforced.addVehicles(VehicleRegistry.CART);
        register(reinforced);



        ModuleData galgadorian = new ModuleDataHull("galgadorian_hull", ModuleGalgadorian.class, 1000, 5, 12, 150) {
            @Override
            @SideOnly(Side.CLIENT)
            public void loadModels() {
                addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/hullModelGalgadorian.png")));
                addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/hullModelGalgadorianTop.png")));
            }
        };

        galgadorian.addShapedRecipe(    GALGADORIAN_METAL,      null,                   GALGADORIAN_METAL,
                GALGADORIAN_METAL,      GALGADORIAN_METAL,      GALGADORIAN_METAL,
                GALGADORIAN_WHEELS,     null,                   GALGADORIAN_WHEELS);

        galgadorian.addVehicles(VehicleRegistry.CART);
        register(galgadorian);



        ModuleData pumpkin = new ModuleDataHull("pumpkin_chariot", ModulePumpkin.class, 40, 1, 0, 15) {
            @Override
            @SideOnly(Side.CLIENT)
            public void loadModels() {
                addModel("Hull", new ModelPumpkinHull(ResourceHelper.getResource("/models/hullModelPumpkin.png"), ResourceHelper.getResource("/models/hullModelWooden.png")));
                addModel("Top", new ModelPumpkinHullTop(ResourceHelper.getResource("/models/hullModelPumpkinTop.png"), ResourceHelper.getResource("/models/hullModelWoodenTop.png")));
            }
        };

        pumpkin.addShapedRecipe(    "planks",       null,           "planks",
                                    "planks",       Blocks.pumpkin, "planks",
                                    WOODEN_WHEELS,  null,           WOODEN_WHEELS);

        if (!StevesVehicles.isHalloween) {
            pumpkin.lock();
        }

        pumpkin.addVehicles(VehicleRegistry.CART);
        register(pumpkin);

        ModuleData pig = new ModuleDataHull("mechanical_pig", ModulePig.class, 150, 2, 4, 50) {
            @Override
            @SideOnly(Side.CLIENT)
            public void loadModels() {
                addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/hullModelPig.png")));
                addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/hullModelPigTop.png")));
                addModel("Head", new ModelPigHead());
                addModel("Tail", new ModelPigTail());
                addModel("Helmet", new ModelPigHelmet(false));
                addModel("Helmet_Overlay", new ModelPigHelmet(true));
            }
        };

        pig.addSides(ModuleSide.FRONT);
        pig.addMessage(Localization.MODULE_INFO.PIG_MESSAGE);

        pig.addShapedRecipe(    Items.porkchop,     null,               Items.porkchop,
                                Items.porkchop,     Items.porkchop,     Items.porkchop,
                                IRON_WHEELS,        null,               IRON_WHEELS);

        pig.addVehicles(VehicleRegistry.CART);
        register(pig);


        ModuleData creative = new ModuleDataHull("creative_hull", ModuleCheatHull.class, 10000, 5, 12, 150) {
            @Override
            @SideOnly(Side.CLIENT)
            public void loadModels() {
                addModel("Hull", new ModelHull(ResourceHelper.getResource("/models/hullModelCreative.png")));
                addModel("Top", new ModelHullTop(ResourceHelper.getResource("/models/hullModelCreativeTop.png")));
            }
        };

        creative.addVehicles(VehicleRegistry.CART);
        register(creative);
    }



}