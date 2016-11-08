package vswe.stevescarts.Modules.Storages.Chests;

import net.minecraft.inventory.IInventory;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Interfaces.GuiMinecart;
import vswe.stevescarts.Modules.Storages.ModuleStorage;
import vswe.stevescarts.Slots.SlotBase;
import vswe.stevescarts.Slots.SlotChest;

public abstract class ModuleChest extends ModuleStorage {
	private float chestAngle;

	public ModuleChest(final MinecartModular cart) {
		super(cart);
	}

	@Override
	public void update() {
		super.update();
		this.handleChest();
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	protected SlotBase getSlot(final int slotId, final int x, final int y) {
		return new SlotChest(this.getCart(), slotId, 8 + x * 18, 16 + y * 18);
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, this.getModuleName(), 8, 6, 4210752);
	}

	@Override
	public int guiWidth() {
		return 15 + this.getInventoryWidth() * 18;
	}

	@Override
	public int guiHeight() {
		return 20 + this.getInventoryHeight() * 18;
	}

	public float getChestAngle() {
		return this.chestAngle;
	}

	protected boolean lidClosed() {
		return this.chestAngle <= 0.0f;
	}

	protected float getLidSpeed() {
		return 0.15707964f;
	}

	protected float chestFullyOpenAngle() {
		return 1.3744469f;
	}

	protected boolean hasVisualChest() {
		return true;
	}

	protected boolean playChestSound() {
		return this.hasVisualChest();
	}

	@Override
	public int numberOfDataWatchers() {
		if (this.hasVisualChest()) {
			return 1;
		}
		return 0;
	}

	@Override
	public void initDw() {
		if (this.hasVisualChest()) {
			this.addDw(0, 0);
		}
	}

	public void openChest() {
		if (this.hasVisualChest()) {
			this.updateDw(0, this.getDw(0) + 1);
		}
	}

	public void closeChest() {
		if (this.hasVisualChest()) {
			this.updateDw(0, this.getDw(0) - 1);
		}
	}

	protected boolean isChestActive() {
		if (!this.hasVisualChest()) {
			return false;
		}
		if (this.isPlaceholder()) {
			return this.getSimInfo().getChestActive();
		}
		return this.getDw(0) > 0;
	}

	protected void handleChest() {
		if (!this.hasVisualChest()) {
			return;
		}
		if (this.isChestActive() && this.lidClosed() && this.playChestSound()) {
//			this.getCart().worldObj.playSoundEffect(this.getCart().posX, this.getCart().posY, this.getCart().posZ, "random.chestopen", 0.5f, this.getCart().worldObj.rand.nextFloat() * 0.1f + 0.9f);
		}
		if (this.isChestActive() && this.chestAngle < this.chestFullyOpenAngle()) {
			this.chestAngle += this.getLidSpeed();
			if (this.chestAngle > this.chestFullyOpenAngle()) {
				this.chestAngle = this.chestFullyOpenAngle();
			}
		} else if (!this.isChestActive() && !this.lidClosed()) {
			final float lastAngle = this.chestAngle;
			this.chestAngle -= this.getLidSpeed();
			if (this.chestAngle < 1.1780972450961724 && lastAngle >= 1.1780972450961724 && this.playChestSound()) {
//				this.getCart().worldObj.playSoundEffect(this.getCart().posX, this.getCart().posY, this.getCart().posZ, "random.chestclosed", 0.5f, this.getCart().worldObj.rand.nextFloat() * 0.1f + 0.9f);
			}
			if (this.chestAngle < 0.0f) {
				this.chestAngle = 0.0f;
			}
		}
	}

	public boolean isCompletelyFilled() {
		for (int i = 0; i < this.getInventorySize(); ++i) {
			if (this.getStack(i) == null) {
				return false;
			}
		}
		return true;
	}

	public boolean isCompletelyEmpty() {
		for (int i = 0; i < this.getInventorySize(); ++i) {
			if (this.getStack(i) != null) {
				return false;
			}
		}
		return true;
	}
}
