package net.mrqx.potato.brain;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(BigPotatoBrainMod.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@SuppressWarnings("DataFlowIssue")
public class BigPotatoBrainMod {
    public static final String MODID = "big_potato_brain";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Block> BIG_BRAIN = BLOCKS.register("big_brain", BlockBigBrain::new);
    public static final RegistryObject<BlockEntityType<?>> BIG_BRAIN_BLOCK_ENTITY = BLOCK_ENTITIES.register("big_brain", () -> BlockEntityType.Builder.of(BlockEntityBigBrain::new, BIG_BRAIN.get()).build(null));
    public static final RegistryObject<Item> POTATO_OF_KNOWLEDGE = ITEMS.register("potato_of_knowledge", ItemKnowledgePotato::new);
    public static final RegistryObject<Item> BIG_BRAIN_ITEM = ITEMS.register("big_brain", () -> new BlockItem(BIG_BRAIN.get(), new Item.Properties().rarity(Rarity.COMMON)));

    public BigPotatoBrainMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
    }

    @SubscribeEvent
    public static void onBuildCreativeModeTabContentsEvent(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS)) {
            event.accept(BIG_BRAIN_ITEM.get());
        }
    }
}
