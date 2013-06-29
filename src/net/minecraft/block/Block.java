package net.minecraft.block;

import static net.minecraftforge.common.ForgeDirection.DOWN;
import static net.minecraftforge.common.ForgeDirection.UP;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemColored;
import net.minecraft.item.ItemLeaves;
import net.minecraft.item.ItemLilyPad;
import net.minecraft.item.ItemMultiTextureTile;
import net.minecraft.item.ItemPiston;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemSnow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.RotationHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Block
{
    protected static int[] blockFireSpreadSpeed = new int[4096];
    protected static int[] blockFlammability = new int[4096];
    /**
     * used as foreach item, if item.tab = current tab, display it on the screen
     */
    private CreativeTabs displayOnCreativeTab;
    public static final StepSound soundPowderFootstep = new StepSound("stone", 1.0F, 1.0F);
    public static final StepSound soundWoodFootstep = new StepSound("wood", 1.0F, 1.0F);
    public static final StepSound soundGravelFootstep = new StepSound("gravel", 1.0F, 1.0F);
    public static final StepSound soundGrassFootstep = new StepSound("grass", 1.0F, 1.0F);
    public static final StepSound soundStoneFootstep = new StepSound("stone", 1.0F, 1.0F);
    public static final StepSound soundMetalFootstep = new StepSound("stone", 1.0F, 1.5F);
    public static final StepSound soundGlassFootstep = new StepSoundStone("stone", 1.0F, 1.0F);
    public static final StepSound soundClothFootstep = new StepSound("cloth", 1.0F, 1.0F);
    public static final StepSound soundSandFootstep = new StepSound("sand", 1.0F, 1.0F);
    public static final StepSound soundSnowFootstep = new StepSound("snow", 1.0F, 1.0F);
    public static final StepSound soundLadderFootstep = new StepSoundSand("ladder", 1.0F, 1.0F);
    public static final StepSound soundAnvilFootstep = new StepSoundAnvil("anvil", 0.3F, 1.0F);

    /** List of ly/ff (BlockType) containing the already registered blocks. */
    public static final Block[] blocksList = new Block[4096];

    /**
     * An array of 4096 booleans corresponding to the result of the isOpaqueCube() method for each block ID
     */
    public static final boolean[] opaqueCubeLookup = new boolean[4096];

    /** How much light is subtracted for going through this block */
    public static final int[] lightOpacity = new int[4096];

    /** Array of booleans that tells if a block can grass */
    public static final boolean[] canBlockGrass = new boolean[4096];

    /** Amount of light emitted */
    public static final int[] lightValue = new int[4096];

    /**
     * Flag if block ID should use the brightest neighbor light value as its own
     */
    public static boolean[] useNeighborBrightness = new boolean[4096];
    public static final Block stone = (new BlockStone(1)).setHardness(1.5F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("stone");
    public static final BlockGrass grass = (BlockGrass)(new BlockGrass(2)).setHardness(0.6F).setStepSound(soundGrassFootstep).setUnlocalizedName("grass");
    public static final Block dirt = (new BlockDirt(3)).setHardness(0.5F).setStepSound(soundGravelFootstep).setUnlocalizedName("dirt");
    public static final Block cobblestone = (new Block(4, Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("stonebrick").setCreativeTab(CreativeTabs.tabBlock);
    public static final Block planks = (new BlockWood(5)).setHardness(2.0F).setResistance(5.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("wood");
    public static final Block sapling = (new BlockSapling(6)).setHardness(0.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("sapling");
    public static final Block bedrock = (new Block(7, Material.rock)).setBlockUnbreakable().setResistance(6000000.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("bedrock").disableStats().setCreativeTab(CreativeTabs.tabBlock);
    public static final BlockFluid waterMoving = (BlockFluid)(new BlockFlowing(8, Material.water)).setHardness(100.0F).setLightOpacity(3).setUnlocalizedName("water").disableStats();
    public static final Block waterStill = (new BlockStationary(9, Material.water)).setHardness(100.0F).setLightOpacity(3).setUnlocalizedName("water").disableStats();
    public static final BlockFluid lavaMoving = (BlockFluid)(new BlockFlowing(10, Material.lava)).setHardness(0.0F).setLightValue(1.0F).setUnlocalizedName("lava").disableStats();

    /** Stationary lava source block */
    public static final Block lavaStill = (new BlockStationary(11, Material.lava)).setHardness(100.0F).setLightValue(1.0F).setUnlocalizedName("lava").disableStats();
    public static final Block sand = (new BlockSand(12)).setHardness(0.5F).setStepSound(soundSandFootstep).setUnlocalizedName("sand");
    public static final Block gravel = (new BlockGravel(13)).setHardness(0.6F).setStepSound(soundGravelFootstep).setUnlocalizedName("gravel");
    public static final Block oreGold = (new BlockOre(14)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreGold");
    public static final Block oreIron = (new BlockOre(15)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreIron");
    public static final Block oreCoal = (new BlockOre(16)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreCoal");
    public static final Block wood = (new BlockLog(17)).setHardness(2.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("log");
    public static final BlockLeaves leaves = (BlockLeaves)(new BlockLeaves(18)).setHardness(0.2F).setLightOpacity(1).setStepSound(soundGrassFootstep).setUnlocalizedName("leaves");
    public static final Block sponge = (new BlockSponge(19)).setHardness(0.6F).setStepSound(soundGrassFootstep).setUnlocalizedName("sponge");
    public static final Block glass = (new BlockGlass(20, Material.glass, false)).setHardness(0.3F).setStepSound(soundGlassFootstep).setUnlocalizedName("glass");
    public static final Block oreLapis = (new BlockOre(21)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreLapis");
    public static final Block blockLapis = (new Block(22, Material.rock)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("blockLapis").setCreativeTab(CreativeTabs.tabBlock);
    public static final Block dispenser = (new BlockDispenser(23)).setHardness(3.5F).setStepSound(soundStoneFootstep).setUnlocalizedName("dispenser");
    public static final Block sandStone = (new BlockSandStone(24)).setStepSound(soundStoneFootstep).setHardness(0.8F).setUnlocalizedName("sandStone");
    public static final Block music = (new BlockNote(25)).setHardness(0.8F).setUnlocalizedName("musicBlock");
    public static final Block bed = (new BlockBed(26)).setHardness(0.2F).setUnlocalizedName("bed").disableStats();
    public static final Block railPowered = (new BlockRailPowered(27)).setHardness(0.7F).setStepSound(soundMetalFootstep).setUnlocalizedName("goldenRail");
    public static final Block railDetector = (new BlockDetectorRail(28)).setHardness(0.7F).setStepSound(soundMetalFootstep).setUnlocalizedName("detectorRail");
    public static final BlockPistonBase pistonStickyBase = (BlockPistonBase)(new BlockPistonBase(29, true)).setUnlocalizedName("pistonStickyBase");
    public static final Block web = (new BlockWeb(30)).setLightOpacity(1).setHardness(4.0F).setUnlocalizedName("web");
    public static final BlockTallGrass tallGrass = (BlockTallGrass)(new BlockTallGrass(31)).setHardness(0.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("tallgrass");
    public static final BlockDeadBush deadBush = (BlockDeadBush)(new BlockDeadBush(32)).setHardness(0.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("deadbush");
    public static final BlockPistonBase pistonBase = (BlockPistonBase)(new BlockPistonBase(33, false)).setUnlocalizedName("pistonBase");
    public static final BlockPistonExtension pistonExtension = new BlockPistonExtension(34);
    public static final Block cloth = (new BlockCloth()).setHardness(0.8F).setStepSound(soundClothFootstep).setUnlocalizedName("cloth");
    public static final BlockPistonMoving pistonMoving = new BlockPistonMoving(36);
    public static final BlockFlower plantYellow = (BlockFlower)(new BlockFlower(37)).setHardness(0.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("flower");
    public static final BlockFlower plantRed = (BlockFlower)(new BlockFlower(38)).setHardness(0.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("rose");
    public static final BlockFlower mushroomBrown = (BlockFlower)(new BlockMushroom(39, "mushroom_brown")).setHardness(0.0F).setStepSound(soundGrassFootstep).setLightValue(0.125F).setUnlocalizedName("mushroom");
    public static final BlockFlower mushroomRed = (BlockFlower)(new BlockMushroom(40, "mushroom_red")).setHardness(0.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("mushroom");
    public static final Block blockGold = (new BlockOreStorage(41)).setHardness(3.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("blockGold");
    public static final Block blockIron = (new BlockOreStorage(42)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("blockIron");

    /** stoneDoubleSlab */
    public static final BlockHalfSlab stoneDoubleSlab = (BlockHalfSlab)(new BlockStep(43, true)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("stoneSlab");

    /** stoneSingleSlab */
    public static final BlockHalfSlab stoneSingleSlab = (BlockHalfSlab)(new BlockStep(44, false)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("stoneSlab");
    public static final Block brick = (new Block(45, Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("brick").setCreativeTab(CreativeTabs.tabBlock);
    public static final Block tnt = (new BlockTNT(46)).setHardness(0.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("tnt");
    public static final Block bookShelf = (new BlockBookshelf(47)).setHardness(1.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("bookshelf");
    public static final Block cobblestoneMossy = (new Block(48, Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("stoneMoss").setCreativeTab(CreativeTabs.tabBlock);
    public static final Block obsidian = (new BlockObsidian(49)).setHardness(50.0F).setResistance(2000.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("obsidian");
    public static final Block torchWood = (new BlockTorch(50)).setHardness(0.0F).setLightValue(0.9375F).setStepSound(soundWoodFootstep).setUnlocalizedName("torch");
    public static final BlockFire fire = (BlockFire)(new BlockFire(51)).setHardness(0.0F).setLightValue(1.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("fire").disableStats();
    public static final Block mobSpawner = (new BlockMobSpawner(52)).setHardness(5.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("mobSpawner").disableStats();
    public static final Block stairsWoodOak = (new BlockStairs(53, planks, 0)).setUnlocalizedName("stairsWood");
    public static final BlockChest chest = (BlockChest)(new BlockChest(54, 0)).setHardness(2.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("chest");
    public static final BlockRedstoneWire redstoneWire = (BlockRedstoneWire)(new BlockRedstoneWire(55)).setHardness(0.0F).setStepSound(soundPowderFootstep).setUnlocalizedName("redstoneDust").disableStats();
    public static final Block oreDiamond = (new BlockOre(56)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreDiamond");
    public static final Block blockDiamond = (new BlockOreStorage(57)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("blockDiamond");
    public static final Block workbench = (new BlockWorkbench(58)).setHardness(2.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("workbench");
    public static final Block crops = (new BlockCrops(59)).setUnlocalizedName("crops");
    public static final Block tilledField = (new BlockFarmland(60)).setHardness(0.6F).setStepSound(soundGravelFootstep).setUnlocalizedName("farmland");
    public static final Block furnaceIdle = (new BlockFurnace(61, false)).setHardness(3.5F).setStepSound(soundStoneFootstep).setUnlocalizedName("furnace").setCreativeTab(CreativeTabs.tabDecorations);
    public static final Block furnaceBurning = (new BlockFurnace(62, true)).setHardness(3.5F).setStepSound(soundStoneFootstep).setLightValue(0.875F).setUnlocalizedName("furnace");
    public static final Block signPost = (new BlockSign(63, TileEntitySign.class, true)).setHardness(1.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("sign").disableStats();
    public static final Block doorWood = (new BlockDoor(64, Material.wood)).setHardness(3.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("doorWood").disableStats();
    public static final Block ladder = (new BlockLadder(65)).setHardness(0.4F).setStepSound(soundLadderFootstep).setUnlocalizedName("ladder");
    public static final Block rail = (new BlockRail(66)).setHardness(0.7F).setStepSound(soundMetalFootstep).setUnlocalizedName("rail");
    public static final Block stairsCobblestone = (new BlockStairs(67, cobblestone, 0)).setUnlocalizedName("stairsStone");
    public static final Block signWall = (new BlockSign(68, TileEntitySign.class, false)).setHardness(1.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("sign").disableStats();
    public static final Block lever = (new BlockLever(69)).setHardness(0.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("lever");
    public static final Block pressurePlateStone = (new BlockPressurePlate(70, "stone", Material.rock, EnumMobType.mobs)).setHardness(0.5F).setStepSound(soundStoneFootstep).setUnlocalizedName("pressurePlate");
    public static final Block doorIron = (new BlockDoor(71, Material.iron)).setHardness(5.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("doorIron").disableStats();
    public static final Block pressurePlatePlanks = (new BlockPressurePlate(72, "wood", Material.wood, EnumMobType.everything)).setHardness(0.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("pressurePlate");
    public static final Block oreRedstone = (new BlockRedstoneOre(73, false)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreRedstone").setCreativeTab(CreativeTabs.tabBlock);
    public static final Block oreRedstoneGlowing = (new BlockRedstoneOre(74, true)).setLightValue(0.625F).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreRedstone");
    public static final Block torchRedstoneIdle = (new BlockRedstoneTorch(75, false)).setHardness(0.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("notGate");
    public static final Block torchRedstoneActive = (new BlockRedstoneTorch(76, true)).setHardness(0.0F).setLightValue(0.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("notGate").setCreativeTab(CreativeTabs.tabRedstone);
    public static final Block stoneButton = (new BlockButtonStone(77)).setHardness(0.5F).setStepSound(soundStoneFootstep).setUnlocalizedName("button");
    public static final Block snow = (new BlockSnow(78)).setHardness(0.1F).setStepSound(soundSnowFootstep).setUnlocalizedName("snow").setLightOpacity(0);
    public static final Block ice = (new BlockIce(79)).setHardness(0.5F).setLightOpacity(3).setStepSound(soundGlassFootstep).setUnlocalizedName("ice");
    public static final Block blockSnow = (new BlockSnowBlock(80)).setHardness(0.2F).setStepSound(soundSnowFootstep).setUnlocalizedName("snow");
    public static final Block cactus = (new BlockCactus(81)).setHardness(0.4F).setStepSound(soundClothFootstep).setUnlocalizedName("cactus");
    public static final Block blockClay = (new BlockClay(82)).setHardness(0.6F).setStepSound(soundGravelFootstep).setUnlocalizedName("clay");
    public static final Block reed = (new BlockReed(83)).setHardness(0.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("reeds").disableStats();
    public static final Block jukebox = (new BlockJukeBox(84)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("jukebox");
    public static final Block fence = (new BlockFence(85, "wood", Material.wood)).setHardness(2.0F).setResistance(5.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("fence");
    public static final Block pumpkin = (new BlockPumpkin(86, false)).setHardness(1.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("pumpkin");
    public static final Block netherrack = (new BlockNetherrack(87)).setHardness(0.4F).setStepSound(soundStoneFootstep).setUnlocalizedName("hellrock");
    public static final Block slowSand = (new BlockSoulSand(88)).setHardness(0.5F).setStepSound(soundSandFootstep).setUnlocalizedName("hellsand");
    public static final Block glowStone = (new BlockGlowStone(89, Material.glass)).setHardness(0.3F).setStepSound(soundGlassFootstep).setLightValue(1.0F).setUnlocalizedName("lightgem");

    /** The purple teleport blocks inside the obsidian circle */
    public static final BlockPortal portal = (BlockPortal)(new BlockPortal(90)).setHardness(-1.0F).setStepSound(soundGlassFootstep).setLightValue(0.75F).setUnlocalizedName("portal");
    public static final Block pumpkinLantern = (new BlockPumpkin(91, true)).setHardness(1.0F).setStepSound(soundWoodFootstep).setLightValue(1.0F).setUnlocalizedName("litpumpkin");
    public static final Block cake = (new BlockCake(92)).setHardness(0.5F).setStepSound(soundClothFootstep).setUnlocalizedName("cake").disableStats();
    public static final BlockRedstoneRepeater redstoneRepeaterIdle = (BlockRedstoneRepeater)(new BlockRedstoneRepeater(93, false)).setHardness(0.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("diode").disableStats();
    public static final BlockRedstoneRepeater redstoneRepeaterActive = (BlockRedstoneRepeater)(new BlockRedstoneRepeater(94, true)).setHardness(0.0F).setLightValue(0.625F).setStepSound(soundWoodFootstep).setUnlocalizedName("diode").disableStats();

    /**
     * April fools secret locked chest, only spawns on new chunks on 1st April.
     */
    public static final Block lockedChest = (new BlockLockedChest(95)).setHardness(0.0F).setLightValue(1.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("lockedchest").setTickRandomly(true);
    public static final Block trapdoor = (new BlockTrapDoor(96, Material.wood)).setHardness(3.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("trapdoor").disableStats();
    public static final Block silverfish = (new BlockSilverfish(97)).setHardness(0.75F).setUnlocalizedName("monsterStoneEgg");
    public static final Block stoneBrick = (new BlockStoneBrick(98)).setHardness(1.5F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("stonebricksmooth");
    public static final Block mushroomCapBrown = (new BlockMushroomCap(99, Material.wood, 0)).setHardness(0.2F).setStepSound(soundWoodFootstep).setUnlocalizedName("mushroom");
    public static final Block mushroomCapRed = (new BlockMushroomCap(100, Material.wood, 1)).setHardness(0.2F).setStepSound(soundWoodFootstep).setUnlocalizedName("mushroom");
    public static final Block fenceIron = (new BlockPane(101, "fenceIron", "fenceIron", Material.iron, true)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("fenceIron");
    public static final Block thinGlass = (new BlockPane(102, "glass", "thinglass_top", Material.glass, false)).setHardness(0.3F).setStepSound(soundGlassFootstep).setUnlocalizedName("thinGlass");
    public static final Block melon = (new BlockMelon(103)).setHardness(1.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("melon");
    public static final Block pumpkinStem = (new BlockStem(104, pumpkin)).setHardness(0.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("pumpkinStem");
    public static final Block melonStem = (new BlockStem(105, melon)).setHardness(0.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("pumpkinStem");
    public static final Block vine = (new BlockVine(106)).setHardness(0.2F).setStepSound(soundGrassFootstep).setUnlocalizedName("vine");
    public static final Block fenceGate = (new BlockFenceGate(107)).setHardness(2.0F).setResistance(5.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("fenceGate");
    public static final Block stairsBrick = (new BlockStairs(108, brick, 0)).setUnlocalizedName("stairsBrick");
    public static final Block stairsStoneBrick = (new BlockStairs(109, stoneBrick, 0)).setUnlocalizedName("stairsStoneBrickSmooth");
    public static final BlockMycelium mycelium = (BlockMycelium)(new BlockMycelium(110)).setHardness(0.6F).setStepSound(soundGrassFootstep).setUnlocalizedName("mycel");
    public static final Block waterlily = (new BlockLilyPad(111)).setHardness(0.0F).setStepSound(soundGrassFootstep).setUnlocalizedName("waterlily");
    public static final Block netherBrick = (new Block(112, Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("netherBrick").setCreativeTab(CreativeTabs.tabBlock);
    public static final Block netherFence = (new BlockFence(113, "netherBrick", Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("netherFence");
    public static final Block stairsNetherBrick = (new BlockStairs(114, netherBrick, 0)).setUnlocalizedName("stairsNetherBrick");
    public static final Block netherStalk = (new BlockNetherStalk(115)).setUnlocalizedName("netherStalk");
    public static final Block enchantmentTable = (new BlockEnchantmentTable(116)).setHardness(5.0F).setResistance(2000.0F).setUnlocalizedName("enchantmentTable");
    public static final Block brewingStand = (new BlockBrewingStand(117)).setHardness(0.5F).setLightValue(0.125F).setUnlocalizedName("brewingStand");
    public static final BlockCauldron cauldron = (BlockCauldron)(new BlockCauldron(118)).setHardness(2.0F).setUnlocalizedName("cauldron");
    public static final Block endPortal = (new BlockEndPortal(119, Material.portal)).setHardness(-1.0F).setResistance(6000000.0F);
    public static final Block endPortalFrame = (new BlockEndPortalFrame(120)).setStepSound(soundGlassFootstep).setLightValue(0.125F).setHardness(-1.0F).setUnlocalizedName("endPortalFrame").setResistance(6000000.0F).setCreativeTab(CreativeTabs.tabDecorations);

    /** The rock found in The End. */
    public static final Block whiteStone = (new Block(121, Material.rock)).setHardness(3.0F).setResistance(15.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("whiteStone").setCreativeTab(CreativeTabs.tabBlock);
    public static final Block dragonEgg = (new BlockDragonEgg(122)).setHardness(3.0F).setResistance(15.0F).setStepSound(soundStoneFootstep).setLightValue(0.125F).setUnlocalizedName("dragonEgg");
    public static final Block redstoneLampIdle = (new BlockRedstoneLight(123, false)).setHardness(0.3F).setStepSound(soundGlassFootstep).setUnlocalizedName("redstoneLight").setCreativeTab(CreativeTabs.tabRedstone);
    public static final Block redstoneLampActive = (new BlockRedstoneLight(124, true)).setHardness(0.3F).setStepSound(soundGlassFootstep).setUnlocalizedName("redstoneLight");
    public static final BlockHalfSlab woodDoubleSlab = (BlockHalfSlab)(new BlockWoodSlab(125, true)).setHardness(2.0F).setResistance(5.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("woodSlab");
    public static final BlockHalfSlab woodSingleSlab = (BlockHalfSlab)(new BlockWoodSlab(126, false)).setHardness(2.0F).setResistance(5.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("woodSlab");
    public static final Block cocoaPlant = (new BlockCocoa(127)).setHardness(0.2F).setResistance(5.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("cocoa");
    public static final Block stairsSandStone = (new BlockStairs(128, sandStone, 0)).setUnlocalizedName("stairsSandStone");
    public static final Block oreEmerald = (new BlockOre(129)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("oreEmerald");
    public static final Block enderChest = (new BlockEnderChest(130)).setHardness(22.5F).setResistance(1000.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("enderChest").setLightValue(0.5F);
    public static final BlockTripWireSource tripWireSource = (BlockTripWireSource)(new BlockTripWireSource(131)).setUnlocalizedName("tripWireSource");
    public static final Block tripWire = (new BlockTripWire(132)).setUnlocalizedName("tripWire");
    public static final Block blockEmerald = (new BlockOreStorage(133)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("blockEmerald");
    public static final Block stairsWoodSpruce = (new BlockStairs(134, planks, 1)).setUnlocalizedName("stairsWoodSpruce");
    public static final Block stairsWoodBirch = (new BlockStairs(135, planks, 2)).setUnlocalizedName("stairsWoodBirch");
    public static final Block stairsWoodJungle = (new BlockStairs(136, planks, 3)).setUnlocalizedName("stairsWoodJungle");
    public static final Block commandBlock = (new BlockCommandBlock(137)).setUnlocalizedName("commandBlock");
    public static final BlockBeacon beacon = (BlockBeacon)(new BlockBeacon(138)).setUnlocalizedName("beacon").setLightValue(1.0F);
    public static final Block cobblestoneWall = (new BlockWall(139, cobblestone)).setUnlocalizedName("cobbleWall");
    public static final Block flowerPot = (new BlockFlowerPot(140)).setHardness(0.0F).setStepSound(soundPowderFootstep).setUnlocalizedName("flowerPot");
    public static final Block carrot = (new BlockCarrot(141)).setUnlocalizedName("carrots");
    public static final Block potato = (new BlockPotato(142)).setUnlocalizedName("potatoes");
    public static final Block woodenButton = (new BlockButtonWood(143)).setHardness(0.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("button");
    public static final Block skull = (new BlockSkull(144)).setHardness(1.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("skull");
    public static final Block anvil = (new BlockAnvil(145)).setHardness(5.0F).setStepSound(soundAnvilFootstep).setResistance(2000.0F).setUnlocalizedName("anvil");
    public static final Block chestTrapped = (new BlockChest(146, 1)).setHardness(2.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("chestTrap");
    public static final Block pressurePlateGold = (new BlockPressurePlateWeighted(147, "blockGold", Material.iron, 64)).setHardness(0.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("weightedPlate_light");
    public static final Block pressurePlateIron = (new BlockPressurePlateWeighted(148, "blockIron", Material.iron, 640)).setHardness(0.5F).setStepSound(soundWoodFootstep).setUnlocalizedName("weightedPlate_heavy");
    public static final BlockComparator redstoneComparatorIdle = (BlockComparator)(new BlockComparator(149, false)).setHardness(0.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("comparator").disableStats();
    public static final BlockComparator redstoneComparatorActive = (BlockComparator)(new BlockComparator(150, true)).setHardness(0.0F).setLightValue(0.625F).setStepSound(soundWoodFootstep).setUnlocalizedName("comparator").disableStats();
    public static final BlockDaylightDetector daylightSensor = (BlockDaylightDetector)(new BlockDaylightDetector(151)).setHardness(0.2F).setStepSound(soundWoodFootstep).setUnlocalizedName("daylightDetector");
    public static final Block blockRedstone = (new BlockPoweredOre(152)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setUnlocalizedName("blockRedstone");
    public static final Block oreNetherQuartz = (new BlockOre(153)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setUnlocalizedName("netherquartz");
    public static final BlockHopper hopperBlock = (BlockHopper)(new BlockHopper(154)).setHardness(3.0F).setResistance(8.0F).setStepSound(soundWoodFootstep).setUnlocalizedName("hopper");
    public static final Block blockNetherQuartz = (new BlockQuartz(155)).setStepSound(soundStoneFootstep).setHardness(0.8F).setUnlocalizedName("quartzBlock");
    public static final Block stairsNetherQuartz = (new BlockStairs(156, blockNetherQuartz, 0)).setUnlocalizedName("stairsQuartz");
    public static final Block railActivator = (new BlockRailPowered(157)).setHardness(0.7F).setStepSound(soundMetalFootstep).setUnlocalizedName("activatorRail");
    public static final Block dropper = (new BlockDropper(158)).setHardness(3.5F).setStepSound(soundStoneFootstep).setUnlocalizedName("dropper");

    /** ID of the block. */
    public final int blockID;

    /** Indicates how many hits it takes to break a block. */
    public float blockHardness;

    /** Indicates the blocks resistance to explosions. */
    public float blockResistance;

    /**
     * set to true when Block's constructor is called through the chain of super()'s. Note: Never used
     */
    protected boolean blockConstructorCalled = true;

    /**
     * If this field is true, the block is counted for statistics (mined or placed)
     */
    protected boolean enableStats = true;

    /**
     * Flags whether or not this block is of a type that needs random ticking. Ref-counted by ExtendedBlockStorage in
     * order to broadly cull a chunk from the random chunk update list for efficiency's sake.
     */
    protected boolean needsRandomTick;

    /** true if the Block contains a Tile Entity */
    protected boolean isBlockContainer;

    /** minimum X for the block bounds (local coordinates) */
    protected double minX;

    /** minimum Y for the block bounds (local coordinates) */
    protected double minY;

    /** minimum Z for the block bounds (local coordinates) */
    protected double minZ;

    /** maximum X for the block bounds (local coordinates) */
    protected double maxX;

    /** maximum Y for the block bounds (local coordinates) */
    protected double maxY;

    /** maximum Z for the block bounds (local coordinates) */
    protected double maxZ;

    /** Sound of stepping on the block */
    public StepSound stepSound;
    public float blockParticleGravity;

    /** Block material definition. */
    public final Material blockMaterial;

    /**
     * Determines how much velocity is maintained while moving on top of this block
     */
    public float slipperiness;

    /** The unlocalized name of this block. */
    private String unlocalizedName;
    @SideOnly(Side.CLIENT)
    protected Icon blockIcon;
    
    public boolean isForgeBlock; // MCPC+  

    public Block(int par1, Material par2Material)
    {
        this.stepSound = soundPowderFootstep;
        this.blockParticleGravity = 1.0F;
        this.slipperiness = 0.6F;

        if (blocksList[par1] != null)
        {
            throw new IllegalArgumentException("Slot " + par1 + " is already occupied by " + blocksList[par1] + " when adding " + this);
        }
        else
        {
            this.blockMaterial = par2Material;
            blocksList[par1] = this;
            this.blockID = par1;
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            opaqueCubeLookup[par1] = this.isOpaqueCube();
            lightOpacity[par1] = this.isOpaqueCube() ? 255 : 0;
            canBlockGrass[par1] = !par2Material.getCanBlockGrass();
        }
        org.bukkit.Material.addMaterial(this.blockID); // MCPC+ - many mods do not register blocks through GameRegistry so to be safe we need to add materials here
        this.isForgeBlock = (this.getClass().getName().length() > 3 && !this.getClass().getName().startsWith("net.minecraft.block")) ? true : false; // MCPC+
    }

    /**
     * This method is called on a block after all other blocks gets already created. You can use it to reference and
     * configure something on the block that needs the others ones.
     */
    protected void initializeBlock() {}

    /**
     * Sets the footstep sound for the block. Returns the object for convenience in constructing.
     */
    public Block setStepSound(StepSound par1StepSound)
    {
        this.stepSound = par1StepSound;
        return this;
    }

    /**
     * Sets how much light is blocked going through this block. Returns the object for convenience in constructing.
     */
    public Block setLightOpacity(int par1)
    {
        lightOpacity[this.blockID] = par1;
        return this;
    }

    /**
     * Sets the amount of light emitted by a block from 0.0f to 1.0f (converts internally to 0-15). Returns the object
     * for convenience in constructing.
     */
    public Block setLightValue(float par1)
    {
        lightValue[this.blockID] = (int)(15.0F * par1);
        return this;
    }

    /**
     * Sets the the blocks resistance to explosions. Returns the object for convenience in constructing.
     */
    public Block setResistance(float par1)
    {
        this.blockResistance = par1 * 3.0F;
        return this;
    }

    public static boolean isNormalCube(int par0)
    {
        Block block = blocksList[par0];
        return block == null ? false : block.blockMaterial.isOpaque() && block.renderAsNormalBlock() && !block.canProvidePower();
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return true;
    }

    public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return !this.blockMaterial.blocksMovement();
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 0;
    }

    /**
     * Sets how many hits it takes to break a block.
     */
    public Block setHardness(float par1)
    {
        this.blockHardness = par1;

        if (this.blockResistance < par1 * 5.0F)
        {
            this.blockResistance = par1 * 5.0F;
        }

        return this;
    }

    /**
     * This method will make the hardness of the block equals to -1, and the block is indestructible.
     */
    public Block setBlockUnbreakable()
    {
        this.setHardness(-1.0F);
        return this;
    }

    /**
     * Returns the block hardness at a location. Args: world, x, y, z
     */
    public float getBlockHardness(World par1World, int par2, int par3, int par4)
    {
        return this.blockHardness;
    }

    /**
     * Sets whether this block type will receive random update ticks
     */
    public Block setTickRandomly(boolean par1)
    {
        this.needsRandomTick = par1;
        return this;
    }

    /**
     * Returns whether or not this block is of a type that needs random ticking. Called for ref-counting purposes by
     * ExtendedBlockStorage in order to broadly cull a chunk from the random chunk update list for efficiency's sake.
     */
    public boolean getTickRandomly()
    {
        return this.needsRandomTick;
    }

    @Deprecated //Forge: New Metadata sensitive version.
    public boolean hasTileEntity()
    {
        return hasTileEntity(0);
    }

    /**
     * Sets the bounds of the block.  minX, minY, minZ, maxX, maxY, maxZ
     */
    public final void setBlockBounds(float par1, float par2, float par3, float par4, float par5, float par6)
    {
        this.minX = (double)par1;
        this.minY = (double)par2;
        this.minZ = (double)par3;
        this.maxX = (double)par4;
        this.maxY = (double)par5;
        this.maxZ = (double)par6;
    }

    @SideOnly(Side.CLIENT)

    /**
     * How bright to render this block based on the light its receiving. Args: iBlockAccess, x, y, z
     */
    public float getBlockBrightness(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return par1IBlockAccess.getBrightness(par2, par3, par4, getLightValue(par1IBlockAccess, par2, par3, par4));
    }

    @SideOnly(Side.CLIENT)

    /**
     * Goes straight to getLightBrightnessForSkyBlocks for Blocks, does some fancy computing for Fluids
     */
    public int getMixedBrightnessForBlock(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return par1IBlockAccess.getLightBrightnessForSkyBlocks(par2, par3, par4, getLightValue(par1IBlockAccess, par2, par3, par4));
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return par5 == 0 && this.minY > 0.0D ? true : (par5 == 1 && this.maxY < 1.0D ? true : (par5 == 2 && this.minZ > 0.0D ? true : (par5 == 3 && this.maxZ < 1.0D ? true : (par5 == 4 && this.minX > 0.0D ? true : (par5 == 5 && this.maxX < 1.0D ? true : !par1IBlockAccess.isBlockOpaqueCube(par2, par3, par4))))));
    }

    /**
     * Returns Returns true if the given side of this block type should be rendered (if it's solid or not), if the
     * adjacent block is at the given coordinates. Args: blockAccess, x, y, z, side
     */
    public boolean isBlockSolid(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return par1IBlockAccess.getBlockMaterial(par2, par3, par4).isSolid();
    }

    @SideOnly(Side.CLIENT)

    /**
     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
     */
    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return this.getIcon(par5, par1IBlockAccess.getBlockMetadata(par2, par3, par4));
    }

    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(int par1, int par2)
    {
        return this.blockIcon;
    }

    /**
     * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
     * mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
     */
    public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity)
    {
        AxisAlignedBB axisalignedbb1 = this.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);

        if (axisalignedbb1 != null && par5AxisAlignedBB.intersectsWith(axisalignedbb1))
        {
            par6List.add(axisalignedbb1);
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the block texture based on the side being looked at.  Args: side
     */
    public final Icon getBlockTextureFromSide(int par1)
    {
        return this.getIcon(par1, 0);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return AxisAlignedBB.getAABBPool().getAABB((double)par2 + this.minX, (double)par3 + this.minY, (double)par4 + this.minZ, (double)par2 + this.maxX, (double)par3 + this.maxY, (double)par4 + this.maxZ);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return AxisAlignedBB.getAABBPool().getAABB((double)par2 + this.minX, (double)par3 + this.minY, (double)par4 + this.minZ, (double)par2 + this.maxX, (double)par3 + this.maxY, (double)par4 + this.maxZ);
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return true;
    }

    /**
     * Returns whether this block is collideable based on the arguments passed in Args: blockMetaData, unknownFlag
     */
    public boolean canCollideCheck(int par1, boolean par2)
    {
        return this.isCollidable();
    }

    /**
     * Returns if this block is collidable (only used by Fire). Args: x, y, z
     */
    public boolean isCollidable()
    {
        return true;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {}

    @SideOnly(Side.CLIENT)

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {}

    /**
     * Called right before the block is destroyed by a player.  Args: world, x, y, z, metaData
     */
    public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5) {}

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {}

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World par1World)
    {
        return 10;
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4) {}

    /**
     * ejects contained items into the world, and notifies neighbours of an update, as appropriate
     */
    public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        if (hasTileEntity(par6) && !(this instanceof BlockContainer))
        {
            par1World.removeBlockTileEntity(par2, par3, par4);
        }
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random par1Random)
    {
        return 1;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return this.blockID;
    }

    /**
     * Gets the hardness of block at the given coordinates in the given world, relative to the ability of the given
     * EntityPlayer.
     */
    public float getPlayerRelativeBlockHardness(EntityPlayer par1EntityPlayer, World par2World, int par3, int par4, int par5)
    {
        float f = this.getBlockHardness(par2World, par3, par4, par5);
        return ForgeHooks.blockStrength(this, par1EntityPlayer, par2World, par3, par4, par5);
    }

    /**
     * Drops the specified block items
     */
    public final void dropBlockAsItem(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        this.dropBlockAsItemWithChance(par1World, par2, par3, par4, par5, 1.0F, par6);
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropBlockAsItemWithChance(World par1World, int par2, int par3, int par4, int par5, float par6, int par7)
    {
        if (!par1World.isRemote)
        {
            ArrayList<ItemStack> items = getBlockDropped(par1World, par2, par3, par4, par5, par7);

            for (ItemStack item : items)
            {
                if (par1World.rand.nextFloat() <= par6)
                {
                    this.dropBlockAsItem_do(par1World, par2, par3, par4, item);
                }
            }
        }
    }

    /**
     * Spawns EntityItem in the world for the given ItemStack if the world is not remote.
     */
    protected void dropBlockAsItem_do(World par1World, int par2, int par3, int par4, ItemStack par5ItemStack)
    {
        if (!par1World.isRemote && par1World.getGameRules().getGameRuleBooleanValue("doTileDrops"))
        {
            float f = 0.7F;
            double d0 = (double)(par1World.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d1 = (double)(par1World.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d2 = (double)(par1World.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(par1World, (double)par2 + d0, (double)par3 + d1, (double)par4 + d2, par5ItemStack);
            entityitem.delayBeforeCanPickup = 10;
            par1World.spawnEntityInWorld(entityitem);
        }
    }

    /**
     * called by spawner, ore, redstoneOre blocks
     */
    protected void dropXpOnBlockBreak(World par1World, int par2, int par3, int par4, int par5)
    {
        if (!par1World.isRemote)
        {
            while (par5 > 0)
            {
                int i1 = EntityXPOrb.getXPSplit(par5);
                par5 -= i1;
                par1World.spawnEntityInWorld(new EntityXPOrb(par1World, (double)par2 + 0.5D, (double)par3 + 0.5D, (double)par4 + 0.5D, i1));
            }
        }
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int damageDropped(int par1)
    {
        return 0;
    }

    /**
     * Returns how much this block can resist explosions from the passed in entity.
     */
    public float getExplosionResistance(Entity par1Entity)
    {
        return this.blockResistance / 5.0F;
    }

    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit. Args: world,
     * x, y, z, startVec, endVec
     */
    public MovingObjectPosition collisionRayTrace(World par1World, int par2, int par3, int par4, Vec3 par5Vec3, Vec3 par6Vec3)
    {
        this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        par5Vec3 = par5Vec3.addVector((double)(-par2), (double)(-par3), (double)(-par4));
        par6Vec3 = par6Vec3.addVector((double)(-par2), (double)(-par3), (double)(-par4));
        Vec3 vec32 = par5Vec3.getIntermediateWithXValue(par6Vec3, this.minX);
        Vec3 vec33 = par5Vec3.getIntermediateWithXValue(par6Vec3, this.maxX);
        Vec3 vec34 = par5Vec3.getIntermediateWithYValue(par6Vec3, this.minY);
        Vec3 vec35 = par5Vec3.getIntermediateWithYValue(par6Vec3, this.maxY);
        Vec3 vec36 = par5Vec3.getIntermediateWithZValue(par6Vec3, this.minZ);
        Vec3 vec37 = par5Vec3.getIntermediateWithZValue(par6Vec3, this.maxZ);

        if (!this.isVecInsideYZBounds(vec32))
        {
            vec32 = null;
        }

        if (!this.isVecInsideYZBounds(vec33))
        {
            vec33 = null;
        }

        if (!this.isVecInsideXZBounds(vec34))
        {
            vec34 = null;
        }

        if (!this.isVecInsideXZBounds(vec35))
        {
            vec35 = null;
        }

        if (!this.isVecInsideXYBounds(vec36))
        {
            vec36 = null;
        }

        if (!this.isVecInsideXYBounds(vec37))
        {
            vec37 = null;
        }

        Vec3 vec38 = null;

        if (vec32 != null && (vec38 == null || par5Vec3.squareDistanceTo(vec32) < par5Vec3.squareDistanceTo(vec38)))
        {
            vec38 = vec32;
        }

        if (vec33 != null && (vec38 == null || par5Vec3.squareDistanceTo(vec33) < par5Vec3.squareDistanceTo(vec38)))
        {
            vec38 = vec33;
        }

        if (vec34 != null && (vec38 == null || par5Vec3.squareDistanceTo(vec34) < par5Vec3.squareDistanceTo(vec38)))
        {
            vec38 = vec34;
        }

        if (vec35 != null && (vec38 == null || par5Vec3.squareDistanceTo(vec35) < par5Vec3.squareDistanceTo(vec38)))
        {
            vec38 = vec35;
        }

        if (vec36 != null && (vec38 == null || par5Vec3.squareDistanceTo(vec36) < par5Vec3.squareDistanceTo(vec38)))
        {
            vec38 = vec36;
        }

        if (vec37 != null && (vec38 == null || par5Vec3.squareDistanceTo(vec37) < par5Vec3.squareDistanceTo(vec38)))
        {
            vec38 = vec37;
        }

        if (vec38 == null)
        {
            return null;
        }
        else
        {
            byte b0 = -1;

            if (vec38 == vec32)
            {
                b0 = 4;
            }

            if (vec38 == vec33)
            {
                b0 = 5;
            }

            if (vec38 == vec34)
            {
                b0 = 0;
            }

            if (vec38 == vec35)
            {
                b0 = 1;
            }

            if (vec38 == vec36)
            {
                b0 = 2;
            }

            if (vec38 == vec37)
            {
                b0 = 3;
            }

            return new MovingObjectPosition(par2, par3, par4, b0, vec38.addVector((double)par2, (double)par3, (double)par4));
        }
    }

    /**
     * Checks if a vector is within the Y and Z bounds of the block.
     */
    private boolean isVecInsideYZBounds(Vec3 par1Vec3)
    {
        return par1Vec3 == null ? false : par1Vec3.yCoord >= this.minY && par1Vec3.yCoord <= this.maxY && par1Vec3.zCoord >= this.minZ && par1Vec3.zCoord <= this.maxZ;
    }

    /**
     * Checks if a vector is within the X and Z bounds of the block.
     */
    private boolean isVecInsideXZBounds(Vec3 par1Vec3)
    {
        return par1Vec3 == null ? false : par1Vec3.xCoord >= this.minX && par1Vec3.xCoord <= this.maxX && par1Vec3.zCoord >= this.minZ && par1Vec3.zCoord <= this.maxZ;
    }

    /**
     * Checks if a vector is within the X and Y bounds of the block.
     */
    private boolean isVecInsideXYBounds(Vec3 par1Vec3)
    {
        return par1Vec3 == null ? false : par1Vec3.xCoord >= this.minX && par1Vec3.xCoord <= this.maxX && par1Vec3.yCoord >= this.minY && par1Vec3.yCoord <= this.maxY;
    }

    /**
     * Called upon the block being destroyed by an explosion
     */
    public void onBlockDestroyedByExplosion(World par1World, int par2, int par3, int par4, Explosion par5Explosion) {}

    public boolean canPlaceBlockOnSide(World par1World, int par2, int par3, int par4, int par5, ItemStack par6ItemStack)
    {
        return this.canPlaceBlockOnSide(par1World, par2, par3, par4, par5);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
     */
    public int getRenderBlockPass()
    {
        return 0;
    }

    /**
     * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
     */
    public boolean canPlaceBlockOnSide(World par1World, int par2, int par3, int par4, int par5)
    {
        return this.canPlaceBlockAt(par1World, par2, par3, par4);
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        int l = par1World.getBlockId(par2, par3, par4);
        Block block = Block.blocksList[l];
        return block == null || block.isBlockReplaceable(par1World, par2, par3, par4);
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        return false;
    }

    /**
     * Called whenever an entity is walking on top of this block. Args: world, x, y, z, entity
     */
    public void onEntityWalking(World par1World, int par2, int par3, int par4, Entity par5Entity) {}

    /**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
     */
    public int onBlockPlaced(World par1World, int par2, int par3, int par4, int par5, float par6, float par7, float par8, int par9)
    {
        return par9;
    }

    /**
     * Called when the block is clicked by a player. Args: x, y, z, entityPlayer
     */
    public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {}

    /**
     * Can add to the passed in vector for a movement vector to be applied to the entity. Args: x, y, z, entity, vec3d
     */
    public void velocityToAddToEntity(World par1World, int par2, int par3, int par4, Entity par5Entity, Vec3 par6Vec3) {}

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {}

    /**
     * returns the block bounderies minX value
     */
    public final double getBlockBoundsMinX()
    {
        return this.minX;
    }

    /**
     * returns the block bounderies maxX value
     */
    public final double getBlockBoundsMaxX()
    {
        return this.maxX;
    }

    /**
     * returns the block bounderies minY value
     */
    public final double getBlockBoundsMinY()
    {
        return this.minY;
    }

    /**
     * returns the block bounderies maxY value
     */
    public final double getBlockBoundsMaxY()
    {
        return this.maxY;
    }

    /**
     * returns the block bounderies minZ value
     */
    public final double getBlockBoundsMinZ()
    {
        return this.minZ;
    }

    /**
     * returns the block bounderies maxZ value
     */
    public final double getBlockBoundsMaxZ()
    {
        return this.maxZ;
    }

    @SideOnly(Side.CLIENT)
    public int getBlockColor()
    {
        return 16777215;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the color this block should be rendered. Used by leaves.
     */
    public int getRenderColor(int par1)
    {
        return 16777215;
    }

    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return 0;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return 16777215;
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return false;
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
    public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {}

    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return 0;
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender() {}

    /**
     * Called when the player destroys a block with an item that can harvest it. (i, j, k) are the coordinates of the
     * block and l is the block's subtype/damage.
     */
    public void harvestBlock(World par1World, EntityPlayer par2EntityPlayer, int par3, int par4, int par5, int par6)
    {
        par2EntityPlayer.addStat(StatList.mineBlockStatArray[this.blockID], 1);
        par2EntityPlayer.addExhaustion(0.025F);

        if (this.canSilkHarvest(par1World, par2EntityPlayer, par3, par4, par5, par6) && EnchantmentHelper.getSilkTouchModifier(par2EntityPlayer))
        {
            ItemStack itemstack = this.createStackedBlock(par6);

            if (itemstack != null)
            {
                this.dropBlockAsItem_do(par1World, par3, par4, par5, itemstack);
            }
        }
        else
        {
            int i1 = EnchantmentHelper.getFortuneModifier(par2EntityPlayer);
            this.dropBlockAsItem(par1World, par3, par4, par5, par6, i1);
        }
    }

    private int silk_check_meta = -1; //Dirty hack to stop us from needing to special case the silk check hook.
    /**
     * Return true if a player with Silk Touch can harvest this block directly, and not its normal drops.
     */
    protected boolean canSilkHarvest()
    {
        return this.renderAsNormalBlock() && !this.hasTileEntity(silk_check_meta);
    }

    /**
     * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
     */
    protected ItemStack createStackedBlock(int par1)
    {
        int j = 0;

        if (this.blockID >= 0 && this.blockID < Item.itemsList.length && Item.itemsList[this.blockID].getHasSubtypes())
        {
            j = par1;
        }

        return new ItemStack(this.blockID, 1, j);
    }

    /**
     * Returns the usual quantity dropped by the block plus a bonus of 1 to 'i' (inclusive).
     */
    public int quantityDroppedWithBonus(int par1, Random par2Random)
    {
        return this.quantityDropped(par2Random);
    }

    /**
     * Can this block stay at this position.  Similar to canPlaceBlockAt except gets checked often with plants.
     */
    public boolean canBlockStay(World par1World, int par2, int par3, int par4)
    {
        return true;
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving, ItemStack par6ItemStack) {}

    /**
     * Called after a block is placed
     */
    public void onPostBlockPlaced(World par1World, int par2, int par3, int par4, int par5) {}

    public Block setUnlocalizedName(String par1Str)
    {
        this.unlocalizedName = par1Str;
        return this;
    }

    /**
     * Gets the localized name of this block. Used for the statistics page.
     */
    public String getLocalizedName()
    {
        return StatCollector.translateToLocal(this.getUnlocalizedName() + ".name");
    }

    /**
     * Returns the unlocalized name of this block.
     */
    public String getUnlocalizedName()
    {
        return "tile." + this.unlocalizedName;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the unlocalized name without the tile. prefix. Caution: client-only.
     */
    public String getUnlocalizedName2()
    {
        return this.unlocalizedName;
    }

    /**
     * Called when the block receives a BlockEvent - see World.addBlockEvent. By default, passes it on to the tile
     * entity at this location. Args: world, x, y, z, blockID, EventID, event parameter
     */
    public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        return false;
    }

    /**
     * Return the state of blocks statistics flags - if the block is counted for mined and placed.
     */
    public boolean getEnableStats()
    {
        return this.enableStats;
    }

    /**
     * Disable statistics for the block, the block will no count for mined or placed.
     */
    protected Block disableStats()
    {
        this.enableStats = false;
        return this;
    }

    /**
     * Returns the mobility information of the block, 0 = free, 1 = can't push but can move over, 2 = total immobility
     * and stop pistons
     */
    public int getMobilityFlag()
    {
        return this.blockMaterial.getMaterialMobility();
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the default ambient occlusion value based on block opacity
     */
    public float getAmbientOcclusionLightValue(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return par1IBlockAccess.isBlockNormalCube(par2, par3, par4) ? 0.2F : 1.0F;
    }

    /**
     * Block's chance to react to an entity falling on it.
     */
    public void onFallenUpon(World par1World, int par2, int par3, int par4, Entity par5Entity, float par6) {}

    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(World par1World, int par2, int par3, int par4)
    {
        return this.blockID;
    }

    /**
     * Get the block's damage value (for use with pick block).
     */
    public int getDamageValue(World par1World, int par2, int par3, int par4)
    {
        return this.damageDropped(par1World.getBlockMetadata(par2, par3, par4));
    }

    @SideOnly(Side.CLIENT)

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(par1, 1, 0));
    }

    /**
     * Sets the CreativeTab to display this block on.
     */
    public Block setCreativeTab(CreativeTabs par1CreativeTabs)
    {
        this.displayOnCreativeTab = par1CreativeTabs;
        return this;
    }

    /**
     * Called when the block is attempted to be harvested
     */
    public void onBlockHarvested(World par1World, int par2, int par3, int par4, int par5, EntityPlayer par6EntityPlayer) {}

    @SideOnly(Side.CLIENT)

    /**
     * Returns the CreativeTab to display the given block on.
     */
    public CreativeTabs getCreativeTabToDisplayOn()
    {
        return this.displayOnCreativeTab;
    }

    /**
     * Called when this block is set (with meta data).
     */
    public void onSetBlockIDWithMetaData(World par1World, int par2, int par3, int par4, int par5) {}

    /**
     * currently only used by BlockCauldron to incrament meta-data during rain
     */
    public void fillWithRain(World par1World, int par2, int par3, int par4) {}

    @SideOnly(Side.CLIENT)
    public boolean func_82505_u_()
    {
        return false;
    }

    public boolean func_82506_l()
    {
        return true;
    }

    /**
     * Return whether this block can drop from an explosion.
     */
    public boolean canDropFromExplosion(Explosion par1Explosion)
    {
        return true;
    }

    /**
     * Returns true if the given block ID is equivalent to this one. Example: redstoneTorchOn matches itself and
     * redstoneTorchOff, and vice versa. Most blocks only match themselves.
     */
    public boolean isAssociatedBlockID(int par1)
    {
        return this.blockID == par1;
    }

    /**
     * Static version of isAssociatedBlockID.
     */
    public static boolean isAssociatedBlockID(int par0, int par1)
    {
        return par0 == par1 ? true : (par0 != 0 && par1 != 0 && blocksList[par0] != null && blocksList[par1] != null ? blocksList[par0].isAssociatedBlockID(par1) : false);
    }

    /**
     * If this returns true, then comparators facing away from this block will use the value from
     * getComparatorInputOverride instead of the actual redstone signal strength.
     */
    public boolean hasComparatorInputOverride()
    {
        return false;
    }

    /**
     * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
     * strength when this block inputs to a comparator.
     */
    public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5)
    {
        return 0;
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon(this.unlocalizedName);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Gets the icon name of the ItemBlock corresponding to this block. Used by hoppers.
     */
    public String getItemIconName()
    {
        return null;
    }

    static
    {
        Item.itemsList[cloth.blockID] = (new ItemCloth(cloth.blockID - 256)).setUnlocalizedName("cloth");
        Item.itemsList[wood.blockID] = (new ItemMultiTextureTile(wood.blockID - 256, wood, BlockLog.woodType)).setUnlocalizedName("log");
        Item.itemsList[planks.blockID] = (new ItemMultiTextureTile(planks.blockID - 256, planks, BlockWood.woodType)).setUnlocalizedName("wood");
        Item.itemsList[silverfish.blockID] = (new ItemMultiTextureTile(silverfish.blockID - 256, silverfish, BlockSilverfish.silverfishStoneTypes)).setUnlocalizedName("monsterStoneEgg");
        Item.itemsList[stoneBrick.blockID] = (new ItemMultiTextureTile(stoneBrick.blockID - 256, stoneBrick, BlockStoneBrick.STONE_BRICK_TYPES)).setUnlocalizedName("stonebricksmooth");
        Item.itemsList[sandStone.blockID] = (new ItemMultiTextureTile(sandStone.blockID - 256, sandStone, BlockSandStone.SAND_STONE_TYPES)).setUnlocalizedName("sandStone");
        Item.itemsList[blockNetherQuartz.blockID] = (new ItemMultiTextureTile(blockNetherQuartz.blockID - 256, blockNetherQuartz, BlockQuartz.quartzBlockTypes)).setUnlocalizedName("quartzBlock");
        Item.itemsList[stoneSingleSlab.blockID] = (new ItemSlab(stoneSingleSlab.blockID - 256, stoneSingleSlab, stoneDoubleSlab, false)).setUnlocalizedName("stoneSlab");
        Item.itemsList[stoneDoubleSlab.blockID] = (new ItemSlab(stoneDoubleSlab.blockID - 256, stoneSingleSlab, stoneDoubleSlab, true)).setUnlocalizedName("stoneSlab");
        Item.itemsList[woodSingleSlab.blockID] = (new ItemSlab(woodSingleSlab.blockID - 256, woodSingleSlab, woodDoubleSlab, false)).setUnlocalizedName("woodSlab");
        Item.itemsList[woodDoubleSlab.blockID] = (new ItemSlab(woodDoubleSlab.blockID - 256, woodSingleSlab, woodDoubleSlab, true)).setUnlocalizedName("woodSlab");
        Item.itemsList[sapling.blockID] = (new ItemMultiTextureTile(sapling.blockID - 256, sapling, BlockSapling.WOOD_TYPES)).setUnlocalizedName("sapling");
        Item.itemsList[leaves.blockID] = (new ItemLeaves(leaves.blockID - 256)).setUnlocalizedName("leaves");
        Item.itemsList[vine.blockID] = new ItemColored(vine.blockID - 256, false);
        Item.itemsList[tallGrass.blockID] = (new ItemColored(tallGrass.blockID - 256, true)).setBlockNames(new String[] {"shrub", "grass", "fern"});
        Item.itemsList[snow.blockID] = new ItemSnow(snow.blockID - 256, snow);
        Item.itemsList[waterlily.blockID] = new ItemLilyPad(waterlily.blockID - 256);
        Item.itemsList[pistonBase.blockID] = new ItemPiston(pistonBase.blockID - 256);
        Item.itemsList[pistonStickyBase.blockID] = new ItemPiston(pistonStickyBase.blockID - 256);
        Item.itemsList[cobblestoneWall.blockID] = (new ItemMultiTextureTile(cobblestoneWall.blockID - 256, cobblestoneWall, BlockWall.types)).setUnlocalizedName("cobbleWall");
        Item.itemsList[anvil.blockID] = (new ItemAnvilBlock(anvil)).setUnlocalizedName("anvil");

        for (int i = 0; i < 256; ++i)
        {
            if (blocksList[i] != null)
            {
                if (Item.itemsList[i] == null)
                {
                    Item.itemsList[i] = new ItemBlock(i - 256);
                    blocksList[i].initializeBlock();
                }

                boolean flag = false;

                if (i > 0 && blocksList[i].getRenderType() == 10)
                {
                    flag = true;
                }

                if (i > 0 && blocksList[i] instanceof BlockHalfSlab)
                {
                    flag = true;
                }

                if (i == tilledField.blockID)
                {
                    flag = true;
                }

                if (canBlockGrass[i])
                {
                    flag = true;
                }

                if (lightOpacity[i] == 0)
                {
                    flag = true;
                }

                useNeighborBrightness[i] = flag;
            }
        }

        canBlockGrass[0] = true;
        StatList.initBreakableStats();
    }

    /* =================================================== FORGE START =====================================*/
    /**
     * Get a light value for the block at the specified coordinates, normal ranges are between 0 and 15
     *
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @return The light value
     */
    public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
        Block block = blocksList[world.getBlockId(x, y, z)];
        if (block != null && block != this)
        {
            return block.getLightValue(world, x, y, z);
        }
        return lightValue[blockID];
    }

    /**
     * Checks if a player or entity can use this block to 'climb' like a ladder.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @param entity The entity trying to use the ladder, CAN be null.
     * @return True if the block should act like a ladder
    */
    public boolean isLadder(World world, int x, int y, int z, EntityLiving entity)
    {
    	return isLadder(world, x, y, z);
    }
    
    @Deprecated //See EntityLiving sensitive version above
    public boolean isLadder(World world, int x, int y, int z)
    {
    	return false;
    }

    /**
     * Return true if the block is a normal, solid cube.  This
     * determines indirect power state, entity ejection from blocks, and a few
     * others.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @return True if the block is a full cube
     */
    public boolean isBlockNormalCube(World world, int x, int y, int z)
    {
        return blockMaterial.isOpaque() && renderAsNormalBlock() && !canProvidePower();
    }

    /**
     * Checks if the block is a solid face on the given side, used by placement logic.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @param side The side to check
     * @return True if the block is solid on the specified side.
     */
    public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (this instanceof BlockHalfSlab)
        {
            return (((meta & 8) == 8 && (side == UP)) || isOpaqueCube());
        }
        else if (this instanceof BlockFarmland)
        {
            return (side != DOWN && side != UP);
        }
        else if (this instanceof BlockStairs)
        {
            boolean flipped = ((meta & 4) != 0);
            return ((meta & 3) + side.ordinal() == 5) || (side == UP && flipped);
        }
        else if (this instanceof BlockHopper && side == UP)
        {
            return true;
        }
        else if (this instanceof BlockPoweredOre)
        {
            return true;
        }
        return isBlockNormalCube(world, x, y, z);
    }

    /**
     * Determines if a new block can be replace the space occupied by this one,
     * Used in the player's placement code to make the block act like water, and lava.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @return True if the block is replaceable by another block
     */
    public boolean isBlockReplaceable(World world, int x, int y, int z)
    {
        return blockMaterial.isReplaceable();
    }

    /**
     * Determines if this block should set fire and deal fire damage
     * to entities coming into contact with it.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @return True if the block should deal damage
     */
    public boolean isBlockBurning(World world, int x, int y, int z)
    {
        return false;
    }

    /**
     * Determines this block should be treated as an air block
     * by the rest of the code. This method is primarily
     * useful for creating pure logic-blocks that will be invisible
     * to the player and otherwise interact as air would.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @return True if the block considered air
     */
    public boolean isAirBlock(World world, int x, int y, int z)
    {
        return false;
    }

    /**
     * Determines if the player can harvest this block, obtaining it's drops when the block is destroyed.
     *
     * @param player The player damaging the block, may be null
     * @param meta The block's current metadata
     * @return True to spawn the drops
     */
    public boolean canHarvestBlock(EntityPlayer player, int meta)
    {
        return ForgeHooks.canHarvestBlock(this, player, meta);
    }

    /**
     * Called when a player removes a block.  This is responsible for
     * actually destroying the block, and the block is intact at time of call.
     * This is called regardless of whether the player can harvest the block or
     * not.
     *
     * Return true if the block is actually destroyed.
     *
     * Note: When used in multiplayer, this is called on both client and
     * server sides!
     *
     * @param world The current world
     * @param player The player damaging the block, may be null
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @return True if the block is actually destroyed.
     */
    public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z)
    {
        return world.setBlockToAir(x, y, z);
    }

    /**
     * Called when a new CreativeContainer is opened, populate the list
     * with all of the items for this block you want a player in creative mode
     * to have access to.
     *
     * @param itemList The list of items to display on the creative inventory.
     */
    public void addCreativeItems(ArrayList itemList)
    {
    }

    /**
     * Chance that fire will spread and consume this block.
     * 300 being a 100% chance, 0, being a 0% chance.
     *
     * @param world The current world
     * @param x The blocks X position
     * @param y The blocks Y position
     * @param z The blocks Z position
     * @param metadata The blocks current metadata
     * @param face The face that the fire is coming from
     * @return A number ranging from 0 to 300 relating used to determine if the block will be consumed by fire
     */
    public int getFlammability(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face)
    {
        return blockFlammability[blockID];
    }

    /**
     * Called when fire is updating, checks if a block face can catch fire.
     *
     *
     * @param world The current world
     * @param x The blocks X position
     * @param y The blocks Y position
     * @param z The blocks Z position
     * @param metadata The blocks current metadata
     * @param face The face that the fire is coming from
     * @return True if the face can be on fire, false otherwise.
     */
    public boolean isFlammable(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face)
    {
        return getFlammability(world, x, y, z, metadata, face) > 0;
    }

    /**
     * Called when fire is updating on a neighbor block.
     * The higher the number returned, the faster fire will spread around this block.
     *
     * @param world The current world
     * @param x The blocks X position
     * @param y The blocks Y position
     * @param z The blocks Z position
     * @param metadata The blocks current metadata
     * @param face The face that the fire is coming from
     * @return A number that is used to determine the speed of fire growth around the block
     */
    public int getFireSpreadSpeed(World world, int x, int y, int z, int metadata, ForgeDirection face)
    {
        return blockFireSpreadSpeed[blockID];
    }

    /**
     * Currently only called by fire when it is on top of this block.
     * Returning true will prevent the fire from naturally dying during updating.
     * Also prevents firing from dying from rain.
     *
     * @param world The current world
     * @param x The blocks X position
     * @param y The blocks Y position
     * @param z The blocks Z position
     * @param metadata The blocks current metadata
     * @param side The face that the fire is coming from
     * @return True if this block sustains fire, meaning it will never go out.
     */
    public boolean isFireSource(World world, int x, int y, int z, int metadata, ForgeDirection side)
    {
        if (blockID == Block.netherrack.blockID && side == UP)
        {
            return true;
        }
        if ((world.provider instanceof WorldProviderEnd) && blockID == Block.bedrock.blockID && side == UP)
        {
            return true;
        }
        return false;
    }

    /**
     * Called by BlockFire to setup the burn values of vanilla blocks.
     * @param id The block id
     * @param encouragement How much the block encourages fire to spread
     * @param flammability how easy a block is to catch fire
     */
    public static void setBurnProperties(int id, int encouragement, int flammability)
    {
        blockFireSpreadSpeed[id] = encouragement;
        blockFlammability[id] = flammability;
    }

    private boolean isTileProvider = this instanceof ITileEntityProvider;
    /**
     * Called throughout the code as a replacement for block instanceof BlockContainer
     * Moving this to the Block base class allows for mods that wish to extend vinella
     * blocks, and also want to have a tile entity on that block, may.
     *
     * Return true from this function to specify this block has a tile entity.
     *
     * @param metadata Metadata of the current block
     * @return True if block has a tile entity, false otherwise
     */
    public boolean hasTileEntity(int metadata)
    {
        return isTileProvider;
    }

    /**
     * Called throughout the code as a replacement for ITileEntityProvider.createNewTileEntity
     * Return the same thing you would from that function.
     * This will fall back to ITileEntityProvider.createNewTileEntity(World) if this block is a ITileEntityProvider
     *
     * @param metadata The Metadata of the current block
     * @return A instance of a class extending TileEntity
     */
    public TileEntity createTileEntity(World world, int metadata)
    {
        if (isTileProvider)
        {
            return ((ITileEntityProvider)this).createNewTileEntity(world);
        }
        return null;
    }

    /**
     * Metadata and fortune sensitive version, this replaces the old (int meta, Random rand)
     * version in 1.1.
     *
     * @param meta Blocks Metadata
     * @param fortune Current item fortune level
     * @param random Random number generator
     * @return The number of items to drop
     */
    public int quantityDropped(int meta, int fortune, Random random)
    {
        return quantityDroppedWithBonus(fortune, random);
    }

    /**
     * This returns a complete list of items dropped from this block.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param metadata Current metadata
     * @param fortune Breakers fortune level
     * @return A ArrayList containing all items this block drops
     */
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune)
    {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

        int count = quantityDropped(metadata, fortune, world.rand);
        for(int i = 0; i < count; i++)
        {
            int id = idDropped(metadata, world.rand, fortune);
            if (id > 0)
            {
                ret.add(new ItemStack(id, 1, damageDropped(metadata)));
            }
        }
        return ret;
    }

    /**
     * Return true from this function if the player with silk touch can harvest this block directly, and not it's normal drops.
     *
     * @param world The world
     * @param player The player doing the harvesting
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param metadata The metadata
     * @return True if the block can be directly harvested using silk touch
     */
    public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata)
    {
        silk_check_meta = metadata;
        boolean ret = this.canSilkHarvest();
        silk_check_meta = 0;
        return ret;
    }

    /**
     * Determines if a specified mob type can spawn on this block, returning false will
     * prevent any mob from spawning on the block.
     *
     * @param type The Mob Category Type
     * @param world The current world
     * @param x The X Position
     * @param y The Y Position
     * @param z The Z Position
     * @return True to allow a mob of the specified category to spawn, false to prevent it.
     */
    public boolean canCreatureSpawn(EnumCreatureType type, World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (this instanceof BlockStep)
        {
            return (((meta & 8) == 8) || isOpaqueCube());
        }
        else if (this instanceof BlockStairs)
        {
            return ((meta & 4) != 0);
        }
        return isBlockSolidOnSide(world, x, y, z, UP);
    }

    /**
     * Determines if this block is classified as a Bed, Allowing
     * players to sleep in it, though the block has to specifically
     * perform the sleeping functionality in it's activated event.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param player The player or camera entity, null in some cases.
     * @return True to treat this as a bed
     */
    public boolean isBed(World world, int x, int y, int z, EntityLiving player)
    {
        return blockID == Block.bed.blockID;
    }

    /**
     * Returns the position that the player is moved to upon
     * waking up, or respawning at the bed.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param player The player or camera entity, null in some cases.
     * @return The spawn position
     */
    public ChunkCoordinates getBedSpawnPosition(World world, int x, int y, int z, EntityPlayer player)
    {
        return BlockBed.getNearestEmptyChunkCoordinates(world, x, y, z, 0);
    }

    /**
     * Called when a user either starts or stops sleeping in the bed.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param player The player or camera entity, null in some cases.
     * @param occupied True if we are occupying the bed, or false if they are stopping use of the bed
     */
    public void setBedOccupied(World world, int x, int y, int z, EntityPlayer player, boolean occupied)
    {
        BlockBed.setBedOccupied(world,  x, y, z, occupied);
    }

    /**
     * Returns the direction of the block. Same values that
     * are returned by BlockDirectional
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @return Bed direction
     */
    public int getBedDirection(IBlockAccess world, int x, int y, int z)
    {
        return BlockBed.getDirection(world.getBlockMetadata(x,  y, z));
    }

    /**
     * Determines if the current block is the foot half of the bed.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @return True if the current block is the foot side of a bed.
     */
    public boolean isBedFoot(IBlockAccess world, int x, int y, int z)
    {
        return BlockBed.isBlockHeadOfBed(world.getBlockMetadata(x,  y, z));
    }

    /**
     * Called when a leaf should start its decay process.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     */
    public void beginLeavesDecay(World world, int x, int y, int z){}

    /**
     * Determines if this block can prevent leaves connected to it from decaying.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @return true if the presence this block can prevent leaves from decaying.
     */
    public boolean canSustainLeaves(World world, int x, int y, int z)
    {
        return false;
    }

    /**
     * Determines if this block is considered a leaf block, used to apply the leaf decay and generation system.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @return true if this block is considered leaves.
     */
    public boolean isLeaves(World world, int x, int y, int z)
    {
        return false;
    }

    /**
     * Used during tree growth to determine if newly generated leaves can replace this block.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @return true if this block can be replaced by growing leaves.
     */
    public boolean canBeReplacedByLeaves(World world, int x, int y, int z)
    {
        return !Block.opaqueCubeLookup[this.blockID];
    }

    /**
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @return  true if the block is wood (logs)
     */
    public boolean isWood(World world, int x, int y, int z)
    {
         return false;
    }

    /**
     * Determines if the current block is replaceable by Ore veins during world generation.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param target The generic target block the gen is looking for, Standards define stone
     *      for overworld generation, and neatherack for the nether.
     * @return True to allow this block to be replaced by a ore
     */
    public boolean isGenMineableReplaceable(World world, int x, int y, int z, int target)
    {
        return blockID == target;
    }

    /**
     * Location sensitive version of getExplosionRestance
     *
     * @param par1Entity The entity that caused the explosion
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param explosionX Explosion source X Position
     * @param explosionY Explosion source X Position
     * @param explosionZ Explosion source X Position
     * @return The amount of the explosion absorbed.
     */
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ)
    {
        return getExplosionResistance(par1Entity);
    }

    /**
     * Called when the block is destroyed by an explosion.
     * Useful for allowing the block to take into account tile entities,
     * metadata, etc. when exploded, before it is removed.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param Explosion The explosion instance affecting the block
     */
    public void onBlockExploded(World world, int x, int y, int z, Explosion explosion)
    {
        world.setBlockToAir(x, y, z);
        onBlockDestroyedByExplosion(world, x, y, z, explosion);
    }

    /**
     * Determine if this block can make a redstone connection on the side provided,
     * Useful to control which sides are inputs and outputs for redstone wires.
     *
     * Side:
     *  -1: UP
     *   0: NORTH
     *   1: EAST
     *   2: SOUTH
     *   3: WEST
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param side The side that is trying to make the connection
     * @return True to make the connection
     */
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
    {
        return Block.blocksList[blockID].canProvidePower() && side != -1;
    }

    /**
     * Determines if a torch can be placed on the top surface of this block.
     * Useful for creating your own block that torches can be on, such as fences.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @return True to allow the torch to be placed
     */
    public boolean canPlaceTorchOnTop(World world, int x, int y, int z)
    {
        if (world.doesBlockHaveSolidTopSurface(x, y, z))
        {
            return true;
        }
        else
        {
            int id = world.getBlockId(x, y, z);
            return id == Block.fence.blockID || id == Block.netherFence.blockID || id == Block.glass.blockID || id == Block.cobblestoneWall.blockID;
        }
    }


    /**
     * Determines if this block should render in this pass.
     *
     * @param pass The pass in question
     * @return True to render
     */
    public boolean canRenderInPass(int pass)
    {
        return pass == getRenderBlockPass();
    }

    /**
     * Called when a user uses the creative pick block button on this block
     *
     * @param target The full target the player is looking at
     * @return A ItemStack to add to the player's inventory, Null if nothing should be added.
     */
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        int id = idPicked(world, x, y, z);

        if (id == 0)
        {
            return null;
        }

        Item item = Item.itemsList[id];
        if (item == null)
        {
            return null;
        }

        return new ItemStack(id, 1, getDamageValue(world, x, y, z));
    }

    /**
     * Used by getTopSolidOrLiquidBlock while placing biome decorations, villages, etc
     * Also used to determine if the player can spawn on this block.
     *
     * @return False to disallow spawning
     */
    public boolean isBlockFoliage(World world, int x, int y, int z)
    {
        return false;
    }

    /**
     * Spawn a digging particle effect in the world, this is a wrapper
     * around EffectRenderer.addBlockHitEffects to allow the block more
     * control over the particles. Useful when you have entirely different
     * texture sheets for different sides/locations in the world.
     *
     * @param world The current world
     * @param target The target the player is looking at {x/y/z/side/sub}
     * @param effectRenderer A reference to the current effect renderer.
     * @return True to prevent vanilla digging particles form spawning.
     */
    @SideOnly(Side.CLIENT)
    public boolean addBlockHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer)
    {
        return false;
    }

    /**
     * Spawn particles for when the block is destroyed. Due to the nature
     * of how this is invoked, the x/y/z locations are not always guaranteed
     * to host your block. So be sure to do proper sanity checks before assuming
     * that the location is this block.
     *
     * @param world The current world
     * @param x X position to spawn the particle
     * @param y Y position to spawn the particle
     * @param z Z position to spawn the particle
     * @param meta The metadata for the block before it was destroyed.
     * @param effectRenderer A reference to the current effect renderer.
     * @return True to prevent vanilla break particles from spawning.
     */
    @SideOnly(Side.CLIENT)
    public boolean addBlockDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer)
    {
        return false;
    }

    /**
     * Determines if this block can support the passed in plant, allowing it to be planted and grow.
     * Some examples:
     *   Reeds check if its a reed, or if its sand/dirt/grass and adjacent to water
     *   Cacti checks if its a cacti, or if its sand
     *   Nether types check for soul sand
     *   Crops check for tilled soil
     *   Caves check if it's a colid surface
     *   Plains check if its grass or dirt
     *   Water check if its still water
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z position
     * @param direction The direction relative to the given position the plant wants to be, typically its UP
     * @param plant The plant that wants to check
     * @return True to allow the plant to be planted/stay.
     */
    public boolean canSustainPlant(World world, int x, int y, int z, ForgeDirection direction, IPlantable plant)
    {
        int plantID = plant.getPlantID(world, x, y + 1, z);
        EnumPlantType plantType = plant.getPlantType(world, x, y + 1, z);

        if (plantID == cactus.blockID && blockID == cactus.blockID)
        {
            return true;
        }

        if (plantID == reed.blockID && blockID == reed.blockID)
        {
            return true;
        }

        if (plant instanceof BlockFlower && ((BlockFlower)plant).canThisPlantGrowOnThisBlockID(blockID))
        {
            return true;
        }

        switch (plantType)
        {
            case Desert: return blockID == sand.blockID;
            case Nether: return blockID == slowSand.blockID;
            case Crop:   return blockID == tilledField.blockID;
            case Cave:   return isBlockSolidOnSide(world, x, y, z, UP);
            case Plains: return blockID == grass.blockID || blockID == dirt.blockID;
            case Water:  return world.getBlockMaterial(x, y, z) == Material.water && world.getBlockMetadata(x, y, z) == 0;
            case Beach:
                boolean isBeach = (blockID == Block.grass.blockID || blockID == Block.dirt.blockID || blockID == Block.sand.blockID);
                boolean hasWater = (world.getBlockMaterial(x - 1, y, z    ) == Material.water ||
                                    world.getBlockMaterial(x + 1, y, z    ) == Material.water ||
                                    world.getBlockMaterial(x,     y, z - 1) == Material.water ||
                                    world.getBlockMaterial(x,     y, z + 1) == Material.water);
                return isBeach && hasWater;
        }

        return false;
    }

    /**
     * Called when a plant grows on this block, only implemented for saplings using the WorldGen*Trees classes right now.
     * Modder may implement this for custom plants.
     * This does not use ForgeDirection, because large/huge trees can be located in non-representable direction,
     * so the source location is specified.
     * Currently this just changes the block to dirt if it was grass.
     *
     * Note: This happens DURING the generation, the generation may not be complete when this is called.
     *
     * @param world Current world
     * @param x Soil X
     * @param y Soil Y
     * @param z Soil Z
     * @param sourceX Plant growth location X
     * @param sourceY Plant growth location Y
     * @param sourceZ Plant growth location Z
     */
    public void onPlantGrow(World world, int x, int y, int z, int sourceX, int sourceY, int sourceZ)
    {
        if (blockID == grass.blockID)
        {
            world.setBlock(x, y, z, dirt.blockID, 0, 2);
        }
    }

    /**
     * Checks if this soil is fertile, typically this means that growth rates
     * of plants on this soil will be slightly sped up.
     * Only vanilla case is tilledField when it is within range of water.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z position
     * @return True if the soil should be considered fertile.
     */
    public boolean isFertile(World world, int x, int y, int z)
    {
        if (blockID == tilledField.blockID)
        {
            return world.getBlockMetadata(x, y, z) > 0;
        }

        return false;
    }

    /**
     * Location aware and overrideable version of the lightOpacity array,
     * return the number to subtract from the light value when it passes through this block.
     *
     * This is not guaranteed to have the tile entity in place before this is called, so it is
     * Recommended that you have your tile entity call relight after being placed if you
     * rely on it for light info.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z position
     * @return The amount of light to block, 0 for air, 255 for fully opaque.
     */
    public int getLightOpacity(World world, int x, int y, int z)
    {
        return lightOpacity[blockID];
    }

    /**
     * Determines if this block is destroyed when a ender dragon tries to fly through it.
     * The block will be set to 0, nothing will drop.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z position
     * @return True to allow the ender dragon to destroy this block
     */
    public boolean canDragonDestroy(World world, int x, int y, int z)
    {
        return blockID != obsidian.blockID && blockID != whiteStone.blockID && blockID != bedrock.blockID;
    }

    /**
     * Determines if this block can be used as the base of a beacon.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z position
     * @param beaconX Beacons X Position
     * @param beaconY Beacons Y Position
     * @param beaconZ Beacons Z Position
     * @return True, to support the beacon, and make it active with this block.
     */
    public boolean isBeaconBase(World worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ)
    {
        return (blockID == blockEmerald.blockID || blockID == blockGold.blockID || blockID == blockDiamond.blockID || blockID == blockIron.blockID);
    }

    /**
     * Rotate the block. For vanilla blocks this rotates around the axis passed in (generally, it should be the "face" that was hit).
     * Note: for mod blocks, this is up to the block and modder to decide. It is not mandated that it be a rotation around the
     * face, but could be a rotation to orient *to* that face, or a visiting of possible rotations.
     * The method should return true if the rotation was successful though.
     *
     * @param worldObj The world
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param axis The axis to rotate around
     * @return True if the rotation was successful, False if the rotation failed, or is not possible
     */
    public boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis)
    {
        return RotationHelper.rotateVanillaBlock(this, worldObj, x, y, z, axis);
    }

    /**
     * Get the rotations that can apply to the block at the specified coordinates. Null means no rotations are possible.
     * Note, this is up to the block to decide. It may not be accurate or representative.
     * @param worldObj The world
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @return An array of valid axes to rotate around, or null for none or unknown
     */
    public ForgeDirection[] getValidRotations(World worldObj, int x, int y, int z)
    {
        return RotationHelper.getValidVanillaBlockRotations(this);
    }

    /**
     * Determines the amount of enchanting power this block can provide to an enchanting table.
     * @param world The World
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @return The amount of enchanting power this block produces.
     */
    public float getEnchantPowerBonus(World world, int x, int y, int z)
    {
        return getEnchantPower(world, x, y, z);
    }

    @Deprecated //Changed return to float, see above.
    public int getEnchantPower(World world, int x, int y, int z)
    {
        return blockID == bookShelf.blockID ? 1 : 0;
    }
    /**
     * Common way to recolour a block with an external tool
     * @param world The world
     * @param x X
     * @param y Y
     * @param z Z
     * @param side The side hit with the colouring tool
     * @param colour The colour to change to
     * @return If the recolouring was successful
     */
    public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour)
    {
        if (blockID == cloth.blockID)
        {
            int meta = world.getBlockMetadata(x, y, z);
            if (meta != colour)
            {
                world.setBlockMetadataWithNotify(x, y, z, colour, 3);
                return true;
            }
        }
        return false;
    }
}
