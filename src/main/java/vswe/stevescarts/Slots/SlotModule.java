package vswe.stevescarts.Slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import vswe.stevescarts.Items.ModItems;

public class SlotModule extends Slot {
	public SlotModule(final IInventory iinventory, final int i, final int j, final int k) {
		super(iinventory, i, j, k);
	}

	public boolean isItemValid(final ItemStack itemstack) {
		return itemstack.getItem() == ModItems.modules;
	}
}