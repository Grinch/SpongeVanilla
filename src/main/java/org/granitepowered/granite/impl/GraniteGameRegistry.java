/*
 * License (MIT)
 *
 * Copyright (c) 2014 Granite Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.granitepowered.granite.impl;

import static org.granitepowered.granite.utils.MinecraftUtils.wrap;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.NotImplementedException;
import org.granitepowered.granite.Granite;
import org.granitepowered.granite.Main;
import org.granitepowered.granite.impl.entity.living.villager.GraniteCareer;
import org.granitepowered.granite.impl.entity.living.villager.GraniteProfession;
import org.granitepowered.granite.impl.item.GraniteEnchantment;
import org.granitepowered.granite.impl.item.inventory.GraniteItemStackBuilder;
import org.granitepowered.granite.impl.potion.GranitePotionBuilder;
import org.granitepowered.granite.impl.util.GraniteRotation;
import org.granitepowered.granite.impl.world.GraniteEnvironment;
import org.granitepowered.granite.mappings.Mappings;
import org.granitepowered.granite.mc.MCBlock;
import org.granitepowered.granite.mc.MCEnchantment;
import org.granitepowered.granite.mc.MCGameRules;
import org.granitepowered.granite.mc.MCItem;
import org.granitepowered.granite.utils.MinecraftUtils;
import org.granitepowered.granite.utils.ReflectionUtils;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.effect.particle.ParticleEffectBuilder;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.hanging.art.Art;
import org.spongepowered.api.entity.living.meta.DyeColor;
import org.spongepowered.api.entity.living.meta.HorseColor;
import org.spongepowered.api.entity.living.meta.HorseStyle;
import org.spongepowered.api.entity.living.meta.HorseVariant;
import org.spongepowered.api.entity.living.meta.OcelotType;
import org.spongepowered.api.entity.living.meta.RabbitType;
import org.spongepowered.api.entity.living.meta.SkeletonType;
import org.spongepowered.api.entity.living.villager.Career;
import org.spongepowered.api.entity.living.villager.Careers;
import org.spongepowered.api.entity.living.villager.Profession;
import org.spongepowered.api.entity.living.villager.Professions;
import org.spongepowered.api.entity.player.gamemode.GameMode;
import org.spongepowered.api.item.Enchantment;
import org.spongepowered.api.item.Enchantments;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackBuilder;
import org.spongepowered.api.item.merchant.TradeOfferBuilder;
import org.spongepowered.api.potion.PotionEffectBuilder;
import org.spongepowered.api.potion.PotionEffectType;
import org.spongepowered.api.util.rotation.Rotation;
import org.spongepowered.api.util.rotation.Rotations;
import org.spongepowered.api.world.Environment;
import org.spongepowered.api.world.Environments;
import org.spongepowered.api.world.biome.BiomeType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GraniteGameRegistry implements GameRegistry {

    Map<String, BiomeType> biomes = Maps.newHashMap();
    Map<String, BlockType> blockTypes = Maps.newHashMap();
    Map<String, Career> careers = Maps.newHashMap();
    Map<String, Enchantment> enchantments = Maps.newHashMap();
    Map<String, Environment> environments = Maps.newHashMap();
    Map<String, ItemType> itemTypes = Maps.newHashMap();
    Map<String, Profession> professions = Maps.newHashMap();
    Map<Profession, List<Career>> professionCareers = Maps.newHashMap();
    Map<Integer, Rotation> rotations = Maps.newHashMap();

    Collection<String> defaultGameRules = new ArrayList<>();

    GraniteItemStackBuilder itemStackBuilder = new GraniteItemStackBuilder();
    GranitePotionBuilder potionBuilder = new GranitePotionBuilder();

    public void register() {
        registerBiomes();
        registerBlocks();
        registerEnchantments();
        registerEnvironments();
        registerGameRules();
        registerItems();
        registerProfessionsAndCareers();
        registerRotations();
    }

    private void registerBiomes() {
        // TODO: Do this later when we see how sponge/mixin goes about doing this :P
        /*Granite.instance.getLogger().info("Registering Biomes");

        try {
            Class biomeGenBaseClass = Mappings.getClass("BiomeGenBase");
            Field biomeList = Mappings.getField(biomeGenBaseClass, "biomeList");
            MCBiomeGenBase[] biomesGenBase = (MCBiomeGenBase[]) biomeList.get(biomeGenBaseClass);

            Field[] fields = BiomeTypes.class.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                String name = "minecraft:" + fields[i].getName().toLowerCase();
                BiomeType biomeType = new GraniteBiomeType(biomesGenBase[i]);
                fields[i].set(null, biomeType);
                biomes.put(name, biomeType);
                if ( Main.debugLog ) Granite.getInstance().getLogger().info("Registered Biome" + name);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }*/
    }

    private void registerBlocks() {
        Granite.instance.getLogger().info("Registering Blocks");

        for (Field field : BlockTypes.class.getDeclaredFields()) {
            ReflectionUtils.forceAccessible(field);

            String name = field.getName().toLowerCase();
            try {
                MCBlock mcBlock = (MCBlock) Mappings.invokeStatic("Blocks", "getRegisteredBlock", name);

                BlockType block = wrap(mcBlock);
                field.set(null, block);
                blockTypes.put(name, block);

                if (Main.debugLog) {
                    Granite.getInstance().getLogger().info("Registered Block minecraft:" + block.getId());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerEnchantments() {
        Granite.instance.getLogger().info("Registering Enchantments");

        for (Field field : Enchantments.class.getDeclaredFields()) {
            ReflectionUtils.forceAccessible(field);

            String name = field.getName().toLowerCase();
            try {
                MCEnchantment mcEnchantment = (MCEnchantment) Mappings.invokeStatic("Enchantment", "getEnchantmentByLocation", name);

                Enchantment enchantment = new GraniteEnchantment(mcEnchantment);
                field.set(null, enchantment);
                enchantments.put("minecraft:" + name, enchantment);

                if (Main.debugLog) {
                    Granite.getInstance().getLogger().info("Registered Enchantment " + enchantment.getId());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }

    private void registerEnvironments() {
        Granite.instance.getLogger().info("Registering Environments");

        for (Field field : Environments.class.getDeclaredFields()) {
            ReflectionUtils.forceAccessible(field);

            String name = field.getName().toLowerCase();
            try {
                Environment environment = new GraniteEnvironment(name);
                field.set(null, environment);
                environments.put("minecraft:" + environment.getName(), environment);
                if (Main.debugLog) {
                    Granite.getInstance().getLogger()
                            .info("Registered Environment " + environment.getName() + " dimId: " + environment.getDimensionId());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerGameRules() {
        Granite.instance.getLogger().info("Registering default GameRules");
        MCGameRules gameRules = MinecraftUtils.instantiate(Mappings.getClass("GameRules"), new Class[]{});
        String[] rules = gameRules.getRules();
        for (String rule : rules) {
            defaultGameRules.add(rule);
            if (Main.debugLog) {
                Granite.getInstance().getLogger().info("Registered default GameRule " + rule);
            }
        }
    }

    private void registerItems() {
        Granite.instance.getLogger().info("Registering Items");

        for (Field field : ItemTypes.class.getDeclaredFields()) {
            ReflectionUtils.forceAccessible(field);

            String name = field.getName().toLowerCase();
            try {
                Object mcItem = Mappings.invokeStatic("Items", "getRegisteredItem", name);

                ItemType item = wrap((MCItem) mcItem);
                field.set(null, item);
                itemTypes.put("minecraft:" + name, item);

                if (Main.debugLog) {
                    Granite.getInstance().getLogger().info("Registered Item minecraft:" + item.getId());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    // TODO: THIS IS BIG, FAT AND UGLY. And need redoing if possible.
    private void registerProfessionsAndCareers() {
        Granite.instance.getLogger().info("Registering Professions");

        for (Field field : Professions.class.getDeclaredFields()) {
            ReflectionUtils.forceAccessible(field);

            String name = field.getName().toLowerCase();
            try {
                Profession profession = new GraniteProfession(name);
                field.set(null, profession);
                professions.put("minecraft:" + name, profession);
                if (Main.debugLog) {
                    Granite.getInstance().getLogger().info("Registered Profession minecraft:" + name);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        Granite.instance.getLogger().info("Registering Careers");

        List<Career> farmers = new ArrayList<>();
        List<Career> librarians = new ArrayList<>();
        List<Career> priests = new ArrayList<>();
        List<Career> blacksmiths = new ArrayList<>();
        List<Career> butchers = new ArrayList<>();

        Profession farmerProfession = professions.get("farmer");
        Profession librarianProfession = professions.get("librarian");
        Profession priestProfession = professions.get("priest");
        Profession blacksmithProfession = professions.get("blacksmith");
        Profession butcherProfession = professions.get("butcher");

        Field[] fields = Careers.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            ReflectionUtils.forceAccessible(fields[i]);

            String name = fields[i].getName().toLowerCase();
            try {
                boolean registered = false;
                if (i < 4) {
                    Career career = new GraniteCareer(farmerProfession, name);
                    fields[i].set(null, career);
                    farmers.add(career);
                    registered = true;
                } else if (i == 4) {
                    Career career = new GraniteCareer(librarianProfession, name);
                    fields[i].set(null, career);
                    librarians.add(career);
                    registered = true;
                } else if (i == 5) {
                    Career career = new GraniteCareer(priestProfession, name);
                    fields[i].set(null, career);
                    priests.add(career);
                    registered = true;
                } else if (i > 5 && i <= 7) {
                    Career career = new GraniteCareer(blacksmithProfession, name);
                    fields[i].set(null, career);
                    blacksmiths.add(career);
                    registered = true;
                } else if (i >= 8 && i <= 10) {
                    Career career = new GraniteCareer(butcherProfession, name);
                    fields[i].set(null, career);
                    butchers.add(career);
                    registered = true;
                }
                if (Main.debugLog && registered) {
                    Granite.getInstance().getLogger().info("Registered Career minecraft:" + name);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        professionCareers.put(farmerProfession, farmers);
        professionCareers.put(librarianProfession, librarians);
        professionCareers.put(priestProfession, priests);
        professionCareers.put(blacksmithProfession, blacksmiths);
        professionCareers.put(butcherProfession, butchers);
    }

    private void registerRotations() {
        Granite.instance.getLogger().info("Registering Rotations");

        int angle = 0;
        Field[] fields = Rotations.class.getDeclaredFields();

        for (Field field : fields) {
            ReflectionUtils.forceAccessible(field);

            try {
                Rotation rotation = new GraniteRotation(angle);
                field.set(null, rotation);
                rotations.put(angle, rotation);
                angle += 45;
                if (Main.debugLog) {
                    Granite.getInstance().getLogger().info("Registered Rotation degrees:" + rotation.getAngle());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Optional<BlockType> getBlock(String id) {
        return Optional.fromNullable(blockTypes.get(id));
    }

    @Override
    public List<BlockType> getBlocks() {
        return (List<BlockType>) blockTypes.values();
    }

    @Override
    public Optional<ItemType> getItem(String id) {
        return Optional.fromNullable(itemTypes.get(id));
    }

    @Override
    public List<ItemType> getItems() {
        return (List<ItemType>) itemTypes.values();
    }

    @Override
    public Optional<BiomeType> getBiome(String id) {
        return Optional.fromNullable(biomes.get(id));
    }

    @Override
    public List<BiomeType> getBiomes() {
        return (List<BiomeType>) biomes.values();
    }

    @Override
    public ItemStackBuilder getItemBuilder() {
        return itemStackBuilder;
    }

    @Override
    public TradeOfferBuilder getTradeOfferBuilder() {
        // TODO: TradeOfferBuilder API
        throw new NotImplementedException("");
    }

    @Override
    public PotionEffectBuilder getPotionEffectBuilder() {
        return potionBuilder;
    }

    @Override
    public Optional<ParticleType> getParticleType(String name) {
        // TODO: Particles API
        throw new NotImplementedException("");
    }

    @Override
    public List<ParticleType> getParticleTypes() {
        // TODO: Particles API
        throw new NotImplementedException("");
    }

    @Override
    public ParticleEffectBuilder getParticleEffectBuilder(ParticleType particle) {
        // TODO: Particles API
        throw new NotImplementedException("");
    }

    @Override
    public Optional<EntityType> getEntity(String id) {
        // TODO: EntityType API
        throw new NotImplementedException("");
    }

    @Override
    public List<EntityType> getEntities() {
        // TODO: EntityType API
        throw new NotImplementedException("");
    }

    @Override
    public Optional<Art> getArt(String id) {
        // TODO: Art API
        throw new NotImplementedException("");
    }

    @Override
    public List<Art> getArts() {
        // TODO: Art API
        throw new NotImplementedException("");
    }

    @Override
    public Optional<DyeColor> getDye(String id) {
        // TODO: Dye API
        throw new NotImplementedException("");
    }

    @Override
    public List<DyeColor> getDyes() {
        // TODO: Dye API
        throw new NotImplementedException("");
    }

    @Override
    public Optional<HorseColor> getHorseColor(String id) {
        // TODO: Horse API
        throw new NotImplementedException("");
    }

    @Override
    public List<HorseColor> getHorseColors() {
        // TODO: Horse API
        throw new NotImplementedException("");
    }

    @Override
    public Optional<HorseStyle> getHorseStyle(String id) {
        // TODO: Horse API
        throw new NotImplementedException("");
    }

    @Override
    public List<HorseStyle> getHorseStyles() {
        // TODO: Horse API
        throw new NotImplementedException("");
    }

    @Override
    public Optional<HorseVariant> getHorseVariant(String id) {
        // TODO: Horse API
        throw new NotImplementedException("");
    }

    @Override
    public List<HorseVariant> getHorseVariants() {
        // TODO: Horse API
        throw new NotImplementedException("");
    }

    @Override
    public Optional<OcelotType> getOcelotType(String id) {
        // TODO: Ocelot API
        throw new NotImplementedException("");
    }

    @Override
    public List<OcelotType> getOcelotTypes() {
        // TODO: Ocelot API
        throw new NotImplementedException("");
    }

    @Override
    public Optional<RabbitType> getRabbitType(String id) {
        // TODO: Rabbit API
        throw new NotImplementedException("");
    }

    @Override
    public List<RabbitType> getRabbitTypes() {
        // TODO: Rabbit API
        throw new NotImplementedException("");
    }

    @Override
    public Optional<SkeletonType> getSkeletonType(String id) {
        // TODO: Spooky Skary Skellingtons API
        throw new NotImplementedException("");
    }

    @Override
    public List<SkeletonType> getSkeletonTypes() {
        // TODO: Spooky Skary Skellingtons API
        throw new NotImplementedException("");
    }

    @Override
    public Optional<Career> getCareer(String id) {
        return Optional.fromNullable(careers.get(id));
    }

    @Override
    public List<Career> getCareers() {
        return (List<Career>) careers.values();
    }

    @Override
    public List<Career> getCareers(Profession profession) {
        return professionCareers.get(profession);
    }

    @Override
    public Optional<Profession> getProfession(String id) {
        return Optional.fromNullable(professions.get(id));
    }

    @Override
    public List<Profession> getProfessions() {
        return (List<Profession>) professions.values();
    }

    @Override
    public List<GameMode> getGameModes() {
        // TODO: GameMode API
        throw new NotImplementedException("");
    }

    @Override
    public List<PotionEffectType> getPotionEffects() {
        // TODO: Potion Effects API
        throw new NotImplementedException("");
    }

    @Override
    public Optional<Enchantment> getEnchantment(String id) {
        return Optional.fromNullable(enchantments.get(id));
    }

    @Override
    public List<Enchantment> getEnchantments() {
        return (List<Enchantment>) enchantments.values();
    }

    @Override
    public Collection<String> getDefaultGameRules() {
        return defaultGameRules;
    }

    @Override
    public Optional<Environment> getEnvironment(String id) {
        return Optional.fromNullable(environments.get(id));
    }

    @Override
    public Optional<Environment> getEnvironment(int dimensionId) {
        for (Environment environment : environments.values()) {
            if (environment.getDimensionId() == dimensionId) {
                return Optional.fromNullable(environment);
            }
        }
        return Optional.absent();
    }

    @Override
    public List<Environment> getEnvironments() {
        return (List<Environment>) environments.values();
    }
}
