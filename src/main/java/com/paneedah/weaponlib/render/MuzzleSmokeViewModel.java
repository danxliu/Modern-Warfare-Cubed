package com.paneedah.weaponlib.render;

import static com.paneedah.mwc.ProjectConstants.ID;
import static com.paneedah.mwc.proxies.ClientProxy.MC;

import com.paneedah.weaponlib.ClientEventHandler;
import com.paneedah.weaponlib.Weapon;
import com.paneedah.weaponlib.animation.gui.AnimationGUI;
import com.paneedah.weaponlib.animation.gui.AnimationModeProcessor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

/**
 * First-person view-model muzzle smoke. Renders inside the held-item
 * GL pass
 */
public final class MuzzleSmokeViewModel {

    public static final MuzzleSmokeViewModel INSTANCE =
        new MuzzleSmokeViewModel();

    private static final ResourceLocation SMOKE_TEXTURE = new ResourceLocation(
        ID + ":textures/smokes/smoke4.png"
    );

    private static final long MAX_AGE_MS = 500L;
    private static final int MAX_PUFFS = 24;
    private static final float DEFAULT_BASE_SIZE = 2.5f;

    private static final class Puff {

        final long spawnTime = System.currentTimeMillis();
        final float baseSize;
        final float spin;
        final float driftForward;
        final float driftX;
        final float driftY;
        final int colorSeed;

        Puff(float baseSize) {
            this.baseSize = baseSize;
            this.spin = (float) (Math.random() * 2.0 * Math.PI);
            this.driftForward = 0.4f + (float) Math.random() * 0.4f;
            this.driftX = ((float) Math.random() - 0.5f) * 0.3f;
            this.driftY = ((float) Math.random() - 0.5f) * 0.3f;
            this.colorSeed = (int) (Math.random() * 10000);
        }
    }

    private final List<Puff> active = new ArrayList<>();

    private MuzzleSmokeViewModel() {}

    public void spawn() {
        spawn(DEFAULT_BASE_SIZE);
    }

    public void spawn(float baseSize) {
        if (active.size() >= MAX_PUFFS) {
            active.remove(0);
        }
        active.add(new Puff(baseSize));
    }

    public void renderAll(Weapon weapon, ItemStack weaponItemStack) {
        if (active.isEmpty()) {
            return;
        }

        final long now = System.currentTimeMillis();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.disableCull();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.blendFunc(
            GL11.GL_SRC_ALPHA,
            GL11.GL_ONE_MINUS_SRC_ALPHA
        );
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);

        MC.getTextureManager().bindTexture(SMOKE_TEXTURE);
        GlStateManager.glTexParameteri(
            GL11.GL_TEXTURE_2D,
            GL11.GL_TEXTURE_MIN_FILTER,
            GL11.GL_LINEAR
        );
        GlStateManager.glTexParameteri(
            GL11.GL_TEXTURE_2D,
            GL11.GL_TEXTURE_MAG_FILTER,
            GL11.GL_LINEAR
        );

        Vec3d muzzle = weapon.getMuzzlePosition();
        if (
            AnimationModeProcessor.getInstance().getFPSMode() &&
            AnimationGUI.getInstance().forceFlash.isState()
        ) {
            muzzle = ClientEventHandler.debugmuzzlePosition;
        }
        GlStateManager.translate(muzzle.x, muzzle.y, muzzle.z);

        final Iterator<Puff> it = active.iterator();
        while (it.hasNext()) {
            final Puff puff = it.next();
            final float t = (now - puff.spawnTime) / (float) MAX_AGE_MS;
            if (t >= 1.0F) {
                it.remove();
                continue;
            }

            final float alpha = 1.0F - t;

            final int index = Math.min((int) (t * 16.0F), 15);
            final float cell = 1.0F / 4.0F;
            final float u0 = (index % 4) * cell;
            final float v0 = (index / 4) * cell;
            final float u1 = u0 + cell;
            final float v1 = v0 + cell;

            final Random tint = new Random(puff.colorSeed);
            final float grey = tint.nextFloat() * 0.5F + 0.4F;
            final float size = puff.baseSize * (0.6F + 0.6F * t);

            GlStateManager.color(grey, grey, grey, alpha);
            GlStateManager.pushMatrix();
            GlStateManager.translate(
                puff.driftX * t,
                puff.driftY * t,
                -puff.driftForward * t
            );
            GlStateManager.rotate(puff.spin, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            drawFlatQuad(size, u0, v0, u1, v1);
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableCull();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }

    private static void drawFlatQuad(
        float size,
        float u0,
        float v0,
        float u1,
        float v1
    ) {
        final Tessellator t = Tessellator.getInstance();
        final BufferBuilder bb = t.getBuffer();
        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bb.pos(-size, 0.0, -size).tex(u0, v0).endVertex();
        bb.pos(size, 0.0, -size).tex(u0, v1).endVertex();
        bb.pos(size, 0.0, size).tex(u1, v1).endVertex();
        bb.pos(-size, 0.0, size).tex(u1, v0).endVertex();
        t.draw();
    }
}
