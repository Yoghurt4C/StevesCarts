package vswe.stevescarts.Modules.Workers.Tools;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IPlantable;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.Localization;
import vswe.stevescarts.Interfaces.GuiMinecart;
import vswe.stevescarts.Modules.ICropModule;
import vswe.stevescarts.Modules.ISuppliesModule;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.Slots.SlotBase;
import vswe.stevescarts.Slots.SlotSeed;

import java.util.ArrayList;

public abstract class ModuleFarmer extends ModuleTool implements ISuppliesModule, ICropModule {
	private ArrayList<ICropModule> plantModules;
	private int farming;
	private float farmAngle;
	private float rigAngle;

	public ModuleFarmer(final MinecartModular cart) {
		super(cart);
		this.rigAngle = -3.926991f;
	}

	protected abstract int getRange();

	public int getExternalRange() {
		return this.getRange();
	}

	@Override
	public void init() {
		super.init();
		this.plantModules = new ArrayList<ICropModule>();
		for (final ModuleBase module : this.getCart().getModules()) {
			if (module instanceof ICropModule) {
				this.plantModules.add((ICropModule) module);
			}
		}
	}

	@Override
	public byte getWorkPriority() {
		return 80;
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, Localization.MODULES.TOOLS.FARMER.translate(), 8, 6, 4210752);
	}

	@Override
	protected int getInventoryWidth() {
		return super.getInventoryWidth() + 3;
	}

	@Override
	protected SlotBase getSlot(final int slotId, int x, final int y) {
		if (x == 0) {
			return super.getSlot(slotId, x, y);
		}
		--x;
		return new SlotSeed(this.getCart(), this, slotId, 8 + x * 18, 28 + y * 18);
	}

	@Override
	public boolean work() {
		BlockPos next = this.getNextblock();
		for (int i = -this.getRange(); i <= this.getRange(); ++i) {
			for (int j = -this.getRange(); j <= this.getRange(); ++j) {
				BlockPos coord = next.add(i, -1, j);
				if (this.farm(coord)) {
					return true;
				}
				if (this.till(coord)) {
					return true;
				}
				if (this.plant(coord)) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean till(BlockPos pos) {
		final Block b = this.getCart().worldObj.getBlockState(pos).getBlock();
		if (this.getCart().worldObj.isAirBlock(pos.up()) && (b == Blocks.GRASS || b == Blocks.DIRT)) {
			if (this.doPreWork()) {
				this.startWorking(10);
				return true;
			}
			this.stopWorking();
			this.getCart().worldObj.setBlockState(pos, Blocks.FARMLAND.getDefaultState());
		}
		return false;
	}

	protected boolean plant(BlockPos pos) {
		int hasSeeds = -1;
		final Block soilblock = this.getCart().worldObj.getBlockState(pos).getBlock();
		if (soilblock != null) {
			for (int i = 0; i < this.getInventorySize(); ++i) {
				if (this.getStack(i) != null && this.isSeedValidHandler(this.getStack(i))) {
					final Block cropblock = this.getCropFromSeedHandler(this.getStack(i));
					if (cropblock != null && cropblock instanceof IPlantable && this.getCart().worldObj.isAirBlock(pos.up()) && soilblock.canSustainPlant(this.getCart().worldObj.getBlockState(pos), this.getCart().worldObj, pos, EnumFacing.UP, (IPlantable) cropblock)) {
						hasSeeds = i;
						break;
					}
				}
			}
			if (hasSeeds != -1) {
				if (this.doPreWork()) {
					this.startWorking(25);
					return true;
				}
				this.stopWorking();
				final Block cropblock2 = this.getCropFromSeedHandler(this.getStack(hasSeeds));
				this.getCart().worldObj.setBlockState(pos.up(), cropblock2.getDefaultState());
				final ItemStack stack = this.getStack(hasSeeds);
				--stack.stackSize;
				if (this.getStack(hasSeeds).stackSize <= 0) {
					this.setStack(hasSeeds, null);
				}
			}
		}
		return false;
	}

	protected boolean farm(BlockPos pos) {
		if (!this.isBroken()) {
			pos = pos.up();
			final Block block = this.getCart().worldObj.getBlockState(pos).getBlock();
			IBlockState blockState = getCart().worldObj.getBlockState(pos.up());
			if (this.isReadyToHarvestHandler(pos)) {
				if (this.doPreWork()) {
					final int efficiency = (this.enchanter != null) ? this.enchanter.getEfficiencyLevel() : 0;
					final int workingtime = (int) (this.getBaseFarmingTime() / Math.pow(1.2999999523162842, efficiency));
					this.setFarming(workingtime * 4);
					this.startWorking(workingtime);
					return true;
				}
				this.stopWorking();
				ArrayList<ItemStack> stuff;
				if (this.shouldSilkTouch(blockState, pos)) {
					stuff = new ArrayList<ItemStack>();
					final ItemStack stack = this.getSilkTouchedItem(blockState);
					if (stack != null) {
						stuff.add(stack);
					}
				} else {
					final int fortune = (this.enchanter != null) ? this.enchanter.getFortuneLevel() : 0;
					stuff = (ArrayList<ItemStack>) block.getDrops(this.getCart().worldObj, pos, blockState, fortune);
				}
				for (final ItemStack iStack : stuff) {
					this.getCart().addItemToChest(iStack);
					if (iStack.stackSize != 0) {
						final EntityItem entityitem = new EntityItem(this.getCart().worldObj, this.getCart().posX, this.getCart().posY, this.getCart().posZ, iStack);
						entityitem.motionX = (pos.getX() - this.getCart().x()) / 10.0f;
						entityitem.motionY = 0.15000000596046448;
						entityitem.motionZ = (pos.getZ() - this.getCart().z()) / 10.0f;
						this.getCart().worldObj.spawnEntityInWorld(entityitem);
					}
				}
				this.getCart().worldObj.setBlockToAir(pos);
				this.damageTool(3);
			}
		}
		return false;
	}

	protected int getBaseFarmingTime() {
		return 25;
	}

	public boolean isSeedValidHandler(final ItemStack seed) {
		for (final ICropModule module : this.plantModules) {
			if (module.isSeedValid(seed)) {
				return true;
			}
		}
		return false;
	}

	protected Block getCropFromSeedHandler(final ItemStack seed) {
		for (final ICropModule module : this.plantModules) {
			if (module.isSeedValid(seed)) {
				return module.getCropFromSeed(seed);
			}
		}
		return null;
	}

	protected boolean isReadyToHarvestHandler(BlockPos pos) {
		for (final ICropModule module : this.plantModules) {
			if (module.isReadyToHarvest(pos)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSeedValid(final ItemStack seed) {
		return seed.getItem() == Items.WHEAT_SEEDS || seed.getItem() == Items.POTATO || seed.getItem() == Items.CARROT;
	}

	@Override
	public Block getCropFromSeed(final ItemStack seed) {
		if (seed.getItem() == Items.CARROT) {
			return Blocks.CARROTS;
		}
		if (seed.getItem() == Items.POTATO) {
			return Blocks.POTATOES;
		}
		if (seed.getItem() == Items.WHEAT_SEEDS) {
			return Blocks.WHEAT;
		}
		return null;
	}

	@Override
	public boolean isReadyToHarvest(BlockPos pos) {
		IBlockState blockState = getCart().worldObj.getBlockState(pos);
		return blockState.getBlock() instanceof BlockCrops &&blockState.getValue(BlockCrops.AGE) == 7;
	}

	public float getFarmAngle() {
		return this.farmAngle;
	}

	public float getRigAngle() {
		return this.rigAngle;
	}

	@Override
	public void initDw() {
		this.addDw(0, 0);
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	private void setFarming(final int val) {
		this.farming = val;
		this.updateDw(0, (byte) ((val > 0) ? 1 : 0));
	}

	protected boolean isFarming() {
		if (this.isPlaceholder()) {
			return this.getSimInfo().getIsFarming();
		}
		return this.getCart().isEngineBurning() && this.getDw(0) != 0;
	}

	@Override
	public void update() {
		super.update();
		if (!this.getCart().worldObj.isRemote) {
			this.setFarming(this.farming - 1);
		} else {
			final float up = -3.926991f;
			final float down = -3.1415927f;
			final boolean flag = this.isFarming();
			if (flag) {
				if (this.rigAngle < down) {
					this.rigAngle += 0.1f;
					if (this.rigAngle > down) {
						this.rigAngle = down;
					}
				} else {
					this.farmAngle = (float) ((this.farmAngle + 0.15f) % 6.283185307179586);
				}
			} else if (this.rigAngle > up) {
				this.rigAngle -= 0.075f;
				if (this.rigAngle < up) {
					this.rigAngle = up;
				}
			}
		}
	}

	@Override
	public boolean haveSupplies() {
		for (int i = 0; i < this.getInventorySize(); ++i) {
			final ItemStack item = this.getStack(i);
			if (item != null && this.isSeedValidHandler(item)) {
				return true;
			}
		}
		return false;
	}
}
