package vswe.stevescarts.Modules.Addons;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Carts.MinecartModular;
import vswe.stevescarts.Helpers.LabelInformation;
import vswe.stevescarts.Helpers.Localization;
import vswe.stevescarts.Helpers.ResourceHelper;
import vswe.stevescarts.Interfaces.GuiMinecart;
import vswe.stevescarts.Modules.Engines.ModuleEngine;
import vswe.stevescarts.Modules.ModuleBase;
import vswe.stevescarts.Modules.Workers.Tools.ModuleTool;
import vswe.stevescarts.Slots.SlotBase;
import vswe.stevescarts.Slots.SlotChest;

import java.util.ArrayList;

public class ModuleLabel extends ModuleAddon {
	private ArrayList<LabelInformation> labels;
	private int delay;
	private ArrayList<SlotBase> storageSlots;
	private ModuleTool tool;

	public ModuleLabel(final MinecartModular cart) {
		super(cart);
		this.delay = 0;
		(this.labels = new ArrayList<LabelInformation>()).add(new LabelInformation(Localization.MODULES.ADDONS.NAME) {
			@Override
			public String getLabel() {
				return ModuleLabel.this.getCart().getCartName();
			}
		});
		this.labels.add(new LabelInformation(Localization.MODULES.ADDONS.DISTANCE) {
			@Override
			public String getLabel() {
				return Localization.MODULES.ADDONS.DISTANCE_LONG.translate(String.valueOf((int) ModuleLabel.this.getCart().getDistanceToEntity(getClientPlayer())));
			}
		});
		this.labels.add(new LabelInformation(Localization.MODULES.ADDONS.POSITION) {
			@Override
			public String getLabel() {
				return Localization.MODULES.ADDONS.POSITION_LONG.translate(String.valueOf(ModuleLabel.this.getCart().x()), String.valueOf(ModuleLabel.this.getCart().y()), String.valueOf(ModuleLabel.this.getCart().z()));
			}
		});
		this.labels.add(new LabelInformation(Localization.MODULES.ADDONS.FUEL) {
			@Override
			public String getLabel() {
				int seconds = getIntDw(1);
				if (seconds == -1) {
					return Localization.MODULES.ADDONS.FUEL_NO_CONSUMPTION.translate();
				}
				int minutes = seconds / 60;
				seconds -= minutes * 60;
				final int hours = minutes / 60;
				minutes -= hours * 60;
				return String.format(Localization.MODULES.ADDONS.FUEL_LONG.translate() + ": %02d:%02d:%02d", hours, minutes, seconds);
			}
		});
		this.labels.add(new LabelInformation(Localization.MODULES.ADDONS.STORAGE) {
			@Override
			public String getLabel() {
				int used = getDw(2);
				if (used < 0) {
					used += 256;
				}
				return (ModuleLabel.this.storageSlots == null) ? "" : (Localization.MODULES.ADDONS.STORAGE.translate() + ": " + used + "/" + ModuleLabel.this.storageSlots.size() + (
					(ModuleLabel.this.storageSlots.size() == 0) ? "" : ("[" + (int) (100.0f * used / ModuleLabel.this.storageSlots.size()) + "%]")));
			}
		});
	}

	@Override
	public void preInit() {
		if (this.getCart().getModules() != null) {
			for (final ModuleBase moduleBase : this.getCart().getModules()) {
				if (moduleBase instanceof ModuleTool) {
					this.tool = (ModuleTool) moduleBase;
					this.labels.add(new LabelInformation(Localization.MODULES.ADDONS.DURABILITY) {
						@Override
						public String getLabel() {
							if (!ModuleLabel.this.tool.useDurability()) {
								return Localization.MODULES.ADDONS.UNBREAKABLE.translate();
							}
							final int data = getIntDw(3);
							if (data == 0) {
								return Localization.MODULES.ADDONS.BROKEN.translate();
							}
							if (data > 0) {
								return Localization.MODULES.ADDONS.DURABILITY.translate() + ": " + data + " / " + ModuleLabel.this.tool.getMaxDurability() + " [" + 100 * data / ModuleLabel.this.tool.getMaxDurability() + "%]";
							}
							if (data == -1) {
								return "";
							}
							if (data == -2) {
								return Localization.MODULES.ADDONS.NOT_BROKEN.translate();
							}
							return Localization.MODULES.ADDONS.REPAIR.translate() + " [" + -(data + 3) + "%]";
						}
					});
					break;
				}
			}
		}
	}

	@Override
	public void init() {
		this.storageSlots = new ArrayList<SlotBase>();
		for (final ModuleBase module : this.getCart().getModules()) {
			if (module.getSlots() != null) {
				for (final SlotBase slot : module.getSlots()) {
					if (slot instanceof SlotChest) {
						this.storageSlots.add(slot);
					}
				}
			}
		}
	}

	private boolean hasTool() {
		return this.tool != null;
	}

	private boolean hasToolWithDurability() {
		return this.hasTool() && this.tool.useDurability();
	}

	@Override
	public void addToLabel(final ArrayList<String> label) {
		for (int i = 0; i < this.labels.size(); ++i) {
			if (this.isActive(i)) {
				label.add(this.labels.get(i).getLabel());
			}
		}
	}

	private int[] getBoxArea(final int i) {
		return new int[] { 10, 17 + i * 12, 8, 8 };
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawBackground(final GuiMinecart gui, final int x, final int y) {
		ResourceHelper.bindResource("/gui/label.png");
		for (int i = 0; i < this.labels.size(); ++i) {
			final int[] rect = this.getBoxArea(i);
			this.drawImage(gui, rect, this.isActive(i) ? 8 : 0, 0);
			this.drawImage(gui, rect, this.inRect(x, y, rect) ? 8 : 0, 8);
		}
	}

	private boolean isActive(final int i) {
		return !this.isPlaceholder() && (this.getDw(0) & 1 << i) != 0x0;
	}

	private void toggleActive(final int i) {
		this.updateDw(0, this.getDw(0) ^ 1 << i);
	}

	@Override
	public int numberOfDataWatchers() {
		int count = 3;
		if (this.hasToolWithDurability()) {
			++count;
		}
		return count;
	}

	@Override
	public void initDw() {
		this.addDw(0, 0);
		this.addIntDw(1, 0);
		this.addDw(2, 0);
		if (this.hasToolWithDurability()) {
			this.addIntDw(3, -1);
		}
	}

	@Override
	public void update() {
		if (!this.isPlaceholder() && !this.getCart().worldObj.isRemote) {
			if (this.delay <= 0) {
				if (this.isActive(3)) {
					int data = 0;
					for (final ModuleEngine engine : this.getCart().getEngines()) {
						if (engine.getPriority() != 3) {
							data += engine.getTotalFuel();
						}
					}
					if (data != 0) {
						final int consumption = this.getCart().getConsumption();
						if (consumption == 0) {
							data = -1;
						} else {
							data /= consumption * 20;
						}
					}
					this.updateIntDw(1, data);
				}
				if (this.isActive(4)) {
					int data = 0;
					for (final SlotBase slot : this.storageSlots) {
						if (slot.getHasStack()) {
							++data;
						}
					}
					this.updateDw(2, (byte) data);
				}
				if (this.hasToolWithDurability()) {
					if (this.isActive(5)) {
						if (this.tool.isRepairing()) {
							if (this.tool.isActuallyRepairing()) {
								this.updateIntDw(3, -3 - this.tool.getRepairPercentage());
							} else {
								this.updateIntDw(3, -2);
							}
						} else {
							this.updateIntDw(3, this.tool.getCurrentDurability());
						}
					} else if (this.getIntDw(3) != -1) {
						this.updateIntDw(3, -1);
					}
				}
				this.delay = 20;
			} else if (this.delay > 0) {
				--this.delay;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void mouseClicked(final GuiMinecart gui, final int x, final int y, final int button) {
		for (int i = 0; i < this.labels.size(); ++i) {
			final int[] rect = this.getBoxArea(i);
			if (this.inRect(x, y, rect)) {
				this.sendPacket(0, (byte) i);
				break;
			}
		}
	}

	@Override
	protected int numberOfPackets() {
		return 1;
	}

	@Override
	protected void receivePacket(final int id, final byte[] data, final EntityPlayer player) {
		if (id == 0) {
			this.toggleActive(data[0]);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawForeground(final GuiMinecart gui) {
		this.drawString(gui, Localization.MODULES.ADDONS.LABELS.translate(), 8, 6, 4210752);
		for (int i = 0; i < this.labels.size(); ++i) {
			final int[] rect = this.getBoxArea(i);
			this.drawString(gui, this.labels.get(i).getName(), rect[0] + 12, rect[1] + 1, 4210752);
		}
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public boolean hasSlots() {
		return false;
	}

	@Override
	public int guiWidth() {
		return 92;
	}

	@Override
	public int guiHeight() {
		return 77;
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		this.updateDw(0, tagCompound.getByte(this.generateNBTName("Active", id)));
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		tagCompound.setByte(this.generateNBTName("Active", id), this.getDw(0));
	}
}
