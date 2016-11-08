package vswe.stevescarts.Helpers;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import vswe.stevescarts.Modules.Addons.ModuleCrafter;

public class CraftingDummy extends InventoryCrafting {
	private int inventoryWidth;
	private ModuleCrafter module;

	public CraftingDummy(final ModuleCrafter module) {
		super(null, 3, 3);
		this.inventoryWidth = 3;
		this.module = module;
	}

	public int getSizeInventory() {
		return 9;
	}

	public ItemStack getStackInSlot(final int par1) {
		return (par1 >= this.getSizeInventory()) ? null : this.module.getStack(par1);
	}

	public ItemStack getStackInRowAndColumn(final int par1, final int par2) {
		if (par1 >= 0 && par1 < this.inventoryWidth) {
			final int k = par1 + par2 * this.inventoryWidth;
			return this.getStackInSlot(k);
		}
		return null;
	}

	public ItemStack getStackInSlotOnClosing(final int par1) {
		return null;
	}

	public ItemStack decrStackSize(final int par1, final int par2) {
		return null;
	}

	public void setInventorySlotContents(final int par1, final ItemStack par2ItemStack) {
	}

	public void update() {
		this.module.setStack(9, this.getResult());
	}

	public ItemStack getResult() {
		return CraftingManager.getInstance().findMatchingRecipe(this, this.module.getCart().worldObj);
	}

	public IRecipe getRecipe() {
		for (int i = 0; i < CraftingManager.getInstance().getRecipeList().size(); ++i) {
			final IRecipe irecipe = CraftingManager.getInstance().getRecipeList().get(i);
			if (irecipe.matches(this, this.module.getCart().worldObj)) {
				return irecipe;
			}
		}
		return null;
	}
}
