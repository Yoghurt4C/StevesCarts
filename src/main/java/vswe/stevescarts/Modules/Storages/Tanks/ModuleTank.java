package vswe.stevescarts.Modules.Storages.Tanks;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.*;
import vswe.stevescarts.Interfaces.GuiBase;
import vswe.stevescarts.Interfaces.GuiMinecart;
import vswe.stevescarts.Modules.Storages.ModuleStorage;
import vswe.stevescarts.Slots.SlotBase;
import vswe.stevescarts.Slots.SlotLiquidInput;
import vswe.stevescarts.Slots.SlotLiquidOutput;

public abstract class ModuleTank extends ModuleStorage implements IFluidTank, ITankHolder {
	protected Tank tank;
	private int tick;
	protected int[] tankBounds;

	public ModuleTank(final MinecartModular cart) {
		super(cart);
		this.tankBounds = new int[] { 35, 20, 36, 51 };
		this.tank = new Tank(this, this.getTankSize(), 0);
	}

	protected abstract int getTankSize();

	public boolean hasGui() {
		return true;
	}

	protected SlotBase getSlot(final int slotId, final int x, final int y) {
		if (y == 0) {
			return new SlotLiquidInput(this.getCart(), this.tank, -1, slotId, 8 + x * 18, 24 + y * 24);
		}
		return new SlotLiquidOutput(this.getCart(), slotId, 8 + x * 18, 24 + y * 24);
	}

	@SideOnly(Side.CLIENT)
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, this.getModuleName(), 8, 6, 4210752);
	}

	public int getInventoryWidth() {
		return 1;
	}

	public int getInventoryHeight() {
		return 2;
	}

	public int guiWidth() {
		return 100;
	}

	public int guiHeight() {
		return 80;
	}

	public boolean hasVisualTank() {
		return true;
	}

	public void update() {
		super.update();
		if (this.tick-- <= 0) {
			this.tick = 5;
			if (!this.getCart().worldObj.isRemote) {
				this.tank.containerTransfer();
			} else if (!this.isPlaceholder()) {
				if (this.getShortDw(0) == -1) {
					this.tank.setFluid(null);
				} else {
					this.tank.setFluid(new FluidStack((int) this.getShortDw(0), this.getIntDw(1)));
				}
			}
		}
	}

	public ItemStack getInputContainer(final int tankid) {
		return this.getStack(0);
	}

	public void clearInputContainer(final int tankid) {
		this.setStack(0, null);
	}

	public void addToOutputContainer(final int tankid, final ItemStack item) {
		this.addStack(1, item);
	}

	public void onFluidUpdated(final int tankid) {
		if (this.getCart().worldObj.isRemote) {
			return;
		}
		this.updateDw();
	}

//	@SideOnly(Side.CLIENT)
//	public void drawImage(final int tankid, final GuiBase gui, final IIcon icon, final int targetX, final int targetY, final int srcX, final int srcY, final int sizeX, final int sizeY) {
//		this.drawImage((GuiMinecart) gui, icon, targetX, targetY, srcX, srcY, sizeX, sizeY);
//	}

	@SideOnly(Side.CLIENT)
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		this.tank.drawFluid(gui, this.tankBounds[0], this.tankBounds[1]);
		ResourceHelper.bindResource("/gui/tank.png");
		this.drawImage(gui, this.tankBounds, 0, 0);
	}

	@SideOnly(Side.CLIENT)
	public void drawMouseOver(final GuiMinecart gui, final int x, final int y) {
		this.drawStringOnMouseOver(gui, this.getTankInfo(), x, y, this.tankBounds);
	}

	protected String getTankInfo() {
		String str = this.tank.getMouseOver();
		if (this.tank.isLocked()) {
			str = str + "\n\n" + ColorHelper.GREEN + Localization.MODULES.TANKS.LOCKED.translate() + "\n" + Localization.MODULES.TANKS.UNLOCK.translate();
		} else if (this.tank.getFluid() != null) {
			str = str + "\n\n" + Localization.MODULES.TANKS.LOCK.translate();
		}
		return str;
	}

	public FluidStack getFluid() {
		return (this.tank.getFluid() == null) ? null : this.tank.getFluid().copy();
	}

	public int getCapacity() {
		return this.getTankSize();
	}

	public int fill(final FluidStack resource, final boolean doFill) {
		return this.tank.fill(resource, doFill, this.getCart().worldObj.isRemote);
	}

	public FluidStack drain(final int maxDrain, final boolean doDrain) {
		return this.tank.drain(maxDrain, doDrain, this.getCart().worldObj.isRemote);
	}

	protected void Save(final NBTTagCompound tagCompound, final int id) {
		if (this.tank.getFluid() != null) {
			final NBTTagCompound compound = new NBTTagCompound();
			this.tank.getFluid().writeToNBT(compound);
			tagCompound.setTag(this.generateNBTName("Fluid", id), compound);
		}
		tagCompound.setBoolean(this.generateNBTName("Locked", id), this.tank.isLocked());
	}

	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.tank.setFluid(FluidStack.loadFluidStackFromNBT(tagCompound.getCompoundTag(this.generateNBTName("Fluid", id))));
		this.tank.setLocked(tagCompound.getBoolean(this.generateNBTName("Locked", id)));
		this.updateDw();
	}

	public int numberOfDataWatchers() {
		return 2;
	}

	protected void updateDw() {
		this.updateShortDw(0, (this.tank.getFluid() == null) ? -1 : this.tank.getFluid().fluidID);
		this.updateIntDw(1, (this.tank.getFluid() == null) ? -1 : this.tank.getFluid().amount);
	}

	public void initDw() {
		this.addShortDw(0, (this.tank.getFluid() == null) ? -1 : this.tank.getFluid().fluidID);
		this.addIntDw(1, (this.tank.getFluid() == null) ? -1 : this.tank.getFluid().amount);
	}

	public float getFluidRenderHeight() {
		if (this.tank.getFluid() == null) {
			return 0.0f;
		}
		return this.tank.getFluid().amount / this.getTankSize();
	}

	public boolean isCompletelyFilled() {
		return this.getFluid() != null && this.getFluid().amount >= this.getTankSize();
	}

	public boolean isCompletelyEmpty() {
		return this.getFluid() == null || this.getFluid().amount == 0;
	}

	public int getFluidAmount() {
		return (this.getFluid() == null) ? 0 : this.getFluid().amount;
	}

	public FluidTankInfo getInfo() {
		return new FluidTankInfo(this.getFluid(), this.getCapacity());
	}

	protected int numberOfPackets() {
		return 1;
	}

	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0 && (this.getFluid() != null || this.tank.isLocked())) {
			this.tank.setLocked(!this.tank.isLocked());
			if (!this.tank.isLocked() && this.tank.getFluid() != null && this.tank.getFluid().amount <= 0) {
				this.tank.setFluid(null);
				this.updateDw();
			}
		}
	}

	public int numberOfGuiData() {
		return 1;
	}

	protected void checkGuiData(final Object[] info) {
		this.updateGuiData(info, 0, (short) (this.tank.isLocked() ? 1 : 0));
	}

	public void receiveGuiData(final int id, final short data) {
		if (id == 0) {
			this.tank.setLocked(data != 0);
		}
	}

	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		if (this.inRect(x, y, this.tankBounds)) {
			byte data = (byte) button;
			if (GuiScreen.isShiftKeyDown()) {
				data |= 0x2;
			}
			this.sendPacket(0, data);
		}
	}

	@Override
	public void drawImage(int p0, GuiBase p1, int p3, int p4, int p5, int p6, int p7, int p8) {
		//TODO help me
	}
}