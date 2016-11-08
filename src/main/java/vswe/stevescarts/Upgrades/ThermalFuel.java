package vswe.stevescarts.Upgrades;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import vswe.stevescarts.Helpers.Localization;
import vswe.stevescarts.TileEntities.TileEntityUpgrade;

public class ThermalFuel extends TankEffect {
	public static final int LAVA_EFFICIENCY = 3;

	@Override
	public int getTankSize() {
		return 12000;
	}

	@Override
	public String getName() {
		return Localization.UPGRADES.THERMAL.translate();
	}

	@Override
	public void update(final TileEntityUpgrade upgrade) {
		super.update(upgrade);
		if (!upgrade.getWorld().isRemote && upgrade.getMaster() != null && upgrade.getFluid() != null && upgrade.getFluid().getFluid().equals(FluidRegistry.LAVA)) {
			final int fuelspace = upgrade.getMaster().getMaxFuelLevel() - upgrade.getMaster().getFuelLevel();
			final int unitspace = Math.min(fuelspace / 3, 200);
			if (unitspace > 100) {
				final FluidStack drain = upgrade.drain(unitspace, false);
				if (drain != null && drain.amount > 0) {
					upgrade.getMaster().setFuelLevel(upgrade.getMaster().getFuelLevel() + drain.amount * 3);
					upgrade.drain(unitspace, true);
				}
			}
		}
	}
}
