package vswe.stevescarts.modules.engines;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.fluids.FluidRegistry;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiMinecart;
import vswe.stevescarts.helpers.Localization;

public abstract class ModuleThermalBase extends ModuleEngine {
	private short coolantLevel;
	private static final int RELOAD_LIQUID_SIZE = 1;
	private DataParameter<Integer> PRIORITY;

	public ModuleThermalBase(final EntityMinecartModular cart) {
		super(cart);
	}

	@Override
	protected DataParameter<Integer> getPriorityDw() {
		return PRIORITY;
	}

	@Override
	public void initDw() {
		PRIORITY = createDw(DataSerializers.VARINT);
		super.initDw();
	}

	private int getCoolantLevel() {
		return coolantLevel;
	}

	private void setCoolantLevel(final int val) {
		coolantLevel = (short) val;
	}

	@Override
	protected void initPriorityButton() {
		priorityButton = new int[] { 72, 17, 16, 16 };
	}

	protected abstract int getEfficiency();

	protected abstract int getCoolantEfficiency();

	private boolean requiresCoolant() {
		return getCoolantEfficiency() > 0;
	}

	@Override
	public int guiHeight() {
		return 40;
	}

	@Override
	public boolean hasFuel(final int consumption) {
		return super.hasFuel(consumption) && (!requiresCoolant() || getCoolantLevel() >= consumption);
	}

	@Override
	public void consumeFuel(final int consumption) {
		super.consumeFuel(consumption);
		setCoolantLevel(getCoolantLevel() - consumption);
	}

	@Override
	protected void loadFuel() {
		final int consumption = getCart().getConsumption(true) * 2;
		while (getFuelLevel() <= consumption) {
			final int amount = getCart().drain(FluidRegistry.LAVA, 1, false);
			if (amount <= 0) {
				break;
			}
			getCart().drain(FluidRegistry.LAVA, amount, true);
			setFuelLevel(getFuelLevel() + amount * getEfficiency());
		}
		while (requiresCoolant() && getCoolantLevel() <= consumption) {
			final int amount = getCart().drain(FluidRegistry.WATER, 1, false);
			if (amount <= 0) {
				break;
			}
			getCart().drain(FluidRegistry.WATER, amount, true);
			setCoolantLevel(getCoolantLevel() + amount * getCoolantEfficiency());
		}
	}

	@Override
	public int getTotalFuel() {
		final int totalfuel = getFuelLevel() + getCart().drain(FluidRegistry.LAVA, Integer.MAX_VALUE, false) * getEfficiency();
		if (requiresCoolant()) {
			final int totalcoolant = getCoolantLevel() + getCart().drain(FluidRegistry.WATER, Integer.MAX_VALUE, false) * getCoolantEfficiency();
			return Math.min(totalcoolant, totalfuel);
		}
		return totalfuel;
	}

	@Override
	public float[] getGuiBarColor() {
		return new float[] { 1.0f, 0.0f, 0.0f };
	}

	@Override
	public void smoke() {
	}

	@Override
	public void drawForeground(final GuiMinecart gui) {
		drawString(gui, Localization.MODULES.ENGINES.THERMAL.translate(), 8, 6, 4210752);
		int consumption = getCart().getConsumption();
		if (consumption == 0) {
			consumption = 1;
		}
		String str;
		if (getFuelLevel() >= consumption && (!requiresCoolant() || getCoolantLevel() >= consumption)) {
			str = Localization.MODULES.ENGINES.POWERED.translate();
		} else if (getFuelLevel() >= consumption) {
			str = Localization.MODULES.ENGINES.NO_WATER.translate();
		} else {
			str = Localization.MODULES.ENGINES.NO_LAVA.translate();
		}
		drawString(gui, str, 8, 22, 4210752);
	}

	@Override
	public boolean hasSlots() {
		return false;
	}

	@Override
	public int numberOfGuiData() {
		return 2;
	}

	@Override
	protected void checkGuiData(final Object[] info) {
		updateGuiData(info, 0, (short) getFuelLevel());
		if (requiresCoolant()) {
			updateGuiData(info, 1, (short) getCoolantLevel());
		}
	}

	@Override
	public void receiveGuiData(final int id, final short data) {
		if (id == 0) {
			setFuelLevel(data);
		} else if (id == 1) {
			setCoolantLevel(data);
		}
	}

	@Override
	protected void Save(final NBTTagCompound tagCompound, final int id) {
		super.Save(tagCompound, id);
		tagCompound.setShort(generateNBTName("Fuel", id), (short) getFuelLevel());
		if (requiresCoolant()) {
			tagCompound.setShort(generateNBTName("Coolant", id), (short) getCoolantLevel());
		}
	}

	@Override
	protected void Load(final NBTTagCompound tagCompound, final int id) {
		super.Load(tagCompound, id);
		setFuelLevel(tagCompound.getShort(generateNBTName("Fuel", id)));
		if (requiresCoolant()) {
			setCoolantLevel(tagCompound.getShort(generateNBTName("Coolant", id)));
		}
	}
}
