package io.github.phantamanta44.mcmlg;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Hitmarker {

    private static final ResourceLocation texHit = new ResourceLocation("mcmlg", "textures/hitmarker.png");
    private static final ResourceLocation[] texMeme = new ResourceLocation[] {
            new ResourceLocation("mcmlg", "textures/meme/meme1.png"),
            new ResourceLocation("mcmlg", "textures/meme/meme2.png"),
            new ResourceLocation("mcmlg", "textures/meme/meme3.png"),
            new ResourceLocation("mcmlg", "textures/meme/meme4.png"),
            new ResourceLocation("mcmlg", "textures/meme/meme5.png")
    };
    private static final Random rand = new Random();

    private final Minecraft mc;
    private final List<HM> markers;
    private final List<Meme> memes;
    private int hits = 0;
    private long lastHit = -1L;
    private long spam = -1L;

    public Hitmarker() {
        this.mc = Minecraft.getMinecraft();
        this.markers = new ArrayList<>();
        this.memes = new ArrayList<>();
    }

    @SubscribeEvent
    public void onHit(LivingAttackEvent event) {
        if (event.source.getEntity() == mc.thePlayer) {
            long sinceLastHit = System.currentTimeMillis() - lastHit;
            if (sinceLastHit > 3000L)
                hits = 1;
            else
                hits++;
            lastHit = System.currentTimeMillis();
            if (hits == 3) {
                mc.thePlayer.playSound("mcmlg:mcmlg.triple", 1F, 1F);
            } else if (hits > 5) {
                mc.thePlayer.playSound("mcmlg:mcmlg.combo", 1F, 1F);
                memes.add(new Meme(texMeme[rand.nextInt(texMeme.length)]));
                memes.add(new Meme(texMeme[rand.nextInt(texMeme.length)]));
                if (rand.nextInt(7) == 1)
                    spam = System.currentTimeMillis() + 600L;
            }
            mc.thePlayer.playSound("mcmlg:mcmlg.hit", 0.7F, 1F);
            markers.add(new HM(true));
        }
    }
    
    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (spam - System.currentTimeMillis() > 0) {
            mc.thePlayer.playSound("mcmlg:mcmlg.hit", 1F, 1F);
            markers.add(new HM(false));
        }
    }

    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            ScaledResolution sr = new ScaledResolution(mc);
            double w = sr.getScaledWidth(), h = sr.getScaledHeight();
            double x = w / 2D, y = h / 2D;
            Tessellator tess = Tessellator.getInstance();
            WorldRenderer wr = tess.getWorldRenderer();
            markers.removeIf(hm -> hm.render(w, h, x, y, tess, wr, mc));
            memes.removeIf(m -> m.render(w, h, tess, wr, mc));
        }
    }

    private static class HM {

        public boolean c;
        public long ct;
        public double px = -1, py = -1;
        
        public HM(boolean center) {
            this.c = center;
            this.ct = System.currentTimeMillis();
        }
        
        public boolean render(double w, double h, double x, double y, Tessellator tess, WorldRenderer wr, Minecraft mc) {
            if (px == -1 || py == -1) {
                if (c) {
                    this.px = x;
                    this.py = y;
                } else {
                    this.px = rand.nextDouble() * w;
                    this.py = rand.nextDouble() * h;
                }
            }
            GL11.glPushMatrix();
            mc.getTextureManager().bindTexture(texHit);
            wr.begin(7, DefaultVertexFormats.POSITION_TEX);
            wr.pos(px - 16D, py + 16D, -90D).tex(0D, 1D).endVertex();
            wr.pos(px + 16D, py + 16D, -90D).tex(1D, 1D).endVertex();
            wr.pos(px + 16D, py - 16D, -90D).tex(1D, 0D).endVertex();
            wr.pos(px - 16D, py - 16D, -90D).tex(0D, 0D).endVertex();
            tess.draw();
            GL11.glPopMatrix();
            return System.currentTimeMillis() - ct > 250L;
        }

    }

    private static class Meme {

        public ResourceLocation tex;
        public long ct;
        public double px = -1, py = -1;

        public Meme(ResourceLocation tex) {
            this.tex = tex;
            this.ct = System.currentTimeMillis();
        }
        
        public boolean render(double w, double h, Tessellator tess, WorldRenderer wr, Minecraft mc) {
            if (px == -1 || py == -1) {
                this.px = rand.nextDouble() * w;
                this.py = rand.nextDouble() * h;
            }
            GL11.glPushMatrix();
            GL11.glTranslated(px, py, 0D);
            GL11.glRotated(30D * (rand.nextDouble() - 0.5D), 0D, 0D, 1D);
            GL11.glTranslated(-px, -py, 0D);
            mc.getTextureManager().bindTexture(tex);
            wr.begin(7, DefaultVertexFormats.POSITION_TEX);
            wr.pos(px - 64D, py + 64D, -90D).tex(0D, 1D).endVertex();
            wr.pos(px + 64D, py + 64D, -90D).tex(1D, 1D).endVertex();
            wr.pos(px + 64D, py - 64D, -90D).tex(1D, 0D).endVertex();
            wr.pos(px - 64D, py - 64D, -90D).tex(0D, 0D).endVertex();
            tess.draw();
            GL11.glPopMatrix();
            return System.currentTimeMillis() - ct > 1000L;
        }

    }

}
