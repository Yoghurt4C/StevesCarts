package vswe.stevescarts.Upgrades;

import net.minecraft.nbt.NBTTagCompound;
import vswe.stevescarts.TileEntities.TileEntityUpgrade;

public abstract class RechargerBase extends BaseEffect {
	@Override
	public void update(final TileEntityUpgrade upgrade) {
		if (!upgrade.getWorld().isRemote && this.canGenerate(upgrade)) {
			final NBTTagCompound comp = upgrade.getCompound();
			if (comp.getShort("GenerateCooldown") >= 1200 / this.getAmount(upgrade)) {
				comp.setShort("GenerateCooldown", (short) 0);
				upgrade.getMaster().increaseFuel(1);
			} else {
				comp.setShort("GenerateCooldown", (short) (comp.getShort("GenerateCooldown") + 1));
			}
		}
	}

	protected abstract boolean canGenerate(final TileEntityUpgrade p0);

	protected abstract int getAmount(final TileEntityUpgrade p0);

	@Override
	public void init(final TileEntityUpgrade upgrade) {
		upgrade.getCompound().setShort("GenerateCooldown", (short) 0);
	}
}
