package vswe.stevescarts.Listeners;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.Carts.MinecartModular;

public class OverlayRenderer {
	public OverlayRenderer() {
		FMLCommonHandler.instance().bus().register(this);
	}

	@SubscribeEvent
	public void onRenderTick(final TickEvent.RenderTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			this.renderOverlay();
		}
	}

	@SideOnly(Side.CLIENT)
	private void renderOverlay() {
		final Minecraft minecraft = Minecraft.getMinecraft();
		final EntityPlayer player = minecraft.thePlayer;
		if (minecraft.currentScreen == null && player.getRidingEntity() != null && player.getRidingEntity() instanceof MinecartModular) {
			((MinecartModular) player.getRidingEntity()).renderOverlay(minecraft);
		}
	}
}
