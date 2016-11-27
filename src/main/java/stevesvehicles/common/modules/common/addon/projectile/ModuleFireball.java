package stevesvehicles.common.modules.common.addon.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleFireball extends ModuleProjectile {
	public ModuleFireball(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public boolean isValidProjectile(ItemStack item) {
		return item.getItem() == Items.FIRE_CHARGE;
	}

	@Override
	public Entity createProjectile(Entity target, ItemStack item) {
		return new EntitySmallFireball(getVehicle().getWorld());
	}
}