package io.github.phantamanta44.mcmlg;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Airhorn {

    private static final ResourceLocation texAirhorn = new ResourceLocation("mcmlg", "textures/airhorn.png");
    private static final ResourceLocation texOverlay = new ResourceLocation("mcmlg", "textures/overlay.png");

    private final Minecraft mc;

    private long lastHit = -1L;

    public Airhorn() {
        this.mc = Minecraft.getMinecraft();
    }

    @SubscribeEvent
    public void onDamage(LivingHurtEvent event) {
        if (event.entityLiving.equals(mc.thePlayer)) {
            mc.thePlayer.playSound("mcmlg:mcmlg.airhorn", 1F, 1F);
            lastHit = System.currentTimeMillis() + 1000;
        }
    }

    @SubscribeEvent
    public void onFovUpdate(FOVUpdateEvent event) {
        long timer = lastHit - System.currentTimeMillis();
        if (timer > 0)
            event.newfov *= 1.01 - Math.pow(timer - 500, 2) / 25000000D;
    }

    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Pre event) {
        long timer = lastHit - System.currentTimeMillis();
        if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR) {
            if (timer > 0) {
                ScaledResolution sr = new ScaledResolution(mc);
                double w = sr.getScaledWidth(), h = sr.getScaledHeight();
                double x = w / 2D, y = h / 2D;
                Tessellator tess = Tessellator.getInstance();
                WorldRenderer wr = tess.getWorldRenderer();

                GL11.glPushMatrix();
                GL11.glTranslated(x, y, 0D);
                GL11.glRotated(Math.sin((double)timer / 40D) * 140D, 0D, 0D, 1D);
                GL11.glTranslated(-x, -y, 0D);
                mc.getTextureManager().bindTexture(texAirhorn);
                wr.begin(7, DefaultVertexFormats.POSITION_TEX);
                wr.pos(x - 64D, y - 32D, -90D).tex(0D, 1D).endVertex();
                wr.pos(x + 64D, y - 32D, -90D).tex(1D, 1D).endVertex();
                wr.pos(x + 64D, y - 160D, -90D).tex(1D, 0D).endVertex();
                wr.pos(x - 64D, y - 160D, -90D).tex(0D, 0D).endVertex();
                tess.draw();
                GL11.glPopMatrix();

                GL11.glPushMatrix();
                mc.getTextureManager().bindTexture(texOverlay);
                double phase = -Math.pow(timer - 500, 2) / 250000D + 1;
                GL11.glColor4f(1F, 1F, 1F, (float)phase * 0.3F);
                double scale = 1 + phase * 0.1;
                GL11.glTranslated(x, y, 0D);
                GL11.glScaled(scale, scale, 1D);
                GL11.glTranslated(-x, -y, 0D);
                wr.begin(7, DefaultVertexFormats.POSITION_TEX);
                wr.pos(0, h, -90D).tex(0D, 1D).endVertex();
                wr.pos(w, h, -90D).tex(1D, 1D).endVertex();
                wr.pos(w, 0, -90D).tex(1D, 0D).endVertex();
                wr.pos(0, 0, -90D).tex(0D, 0D).endVertex();
                tess.draw();
                GL11.glColor4f(1F, 1F, 1F, 1F);
                GL11.glPopMatrix();
            }
        }
    }

}
