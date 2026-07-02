package com.paneedah.mwc.asm;

import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

public class ClassInfoProvider {

    private static final Map<String, ClassInfo> classInfoMap = new HashMap<>();

    static {

        classInfoMap.put("net/minecraft/client/renderer/EntityRenderer",
                new ClassInfo("net/minecraft/client/renderer/EntityRenderer", "buq")
                        .addMethodInfo("hurtCameraEffect", "(F)V", "d")
                        .addMethodInfo("setupCameraTransform", "(FI)V", "a")
                        .addMethodInfo("setupViewBobbing", "applyBobbing", "(F)V", "e")
                        .addMethodInfo2("updateCameraAndRender", "(FJ)V", "a", "(FJ)V")
        );

        classInfoMap.put("net/minecraft/client/model/ModelBiped",
                new ClassInfo("net/minecraft/client/model/ModelBiped", "bpx")
                        .addMethodInfo2("render", "(Lnet/minecraft/entity/Entity;FFFFFF)V", "a", "(Lvg;FFFFFF)V")
                        .addMethodInfo2("postRenderArm", "(FLnet/minecraft/util/EnumHandSide;)V", "a", "(FLvo;)V")
        );

        classInfoMap.put("net/minecraft/client/model/ModelPlayer",
                new ClassInfo("net/minecraft/client/model/ModelPlayer", "bqj")
                        .addMethodInfo2("render", "(Lnet/minecraft/entity/Entity;FFFFFF)V", "a", "(Lvg;FFFFFF)V")
        );

        classInfoMap.put("net/minecraft/client/renderer/entity/RenderLivingBase",
                new ClassInfo("net/minecraft/client/renderer/entity/RenderLivingBase", "caa")
                        .addMethodInfo2("renderModel", "(Lnet/minecraft/entity/EntityLivingBase;FFFFFF)V", "a", "(Lvp;FFFFFF)V")
                        .addMethodInfo2("getMainModel", "()Lnet/minecraft/client/model/ModelBase;", "b", "()Lbqf;")
        );

        classInfoMap.put("net/minecraft/client/renderer/entity/layers/LayerArmorBase",
                new ClassInfo("net/minecraft/client/renderer/entity/layers/LayerArmorBase", "cbp")
                        .addMethodInfo2("renderArmorLayer", "(Lnet/minecraft/entity/EntityLivingBase;FFFFFFFLnet/minecraft/inventory/EntityEquipmentSlot;)V", "a", "(Lvp;FFFFFFFLvl;)V")
        );

        classInfoMap.put("net/minecraft/client/renderer/entity/layers/LayerHeldItem",
                new ClassInfo("net/minecraft/client/renderer/entity/layers/LayerHeldItem", "ccc")
                        .addMethodInfo2("renderHeldItem", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Lnet/minecraft/util/EnumHandSide;)V", "a", "(Lvp;Laip;Lbwc$b;Lvo;)V")
                        .addMethodInfo2("translateToHand", "(Lnet/minecraft/util/EnumHandSide;)V", "a", "(Lvo;)V")
                        .addFieldInfo("livingEntityRenderer", "Lnet/minecraft/client/renderer/entity/RenderLivingBase;", "field_177206_a", "Lcaa;")
        );

        classInfoMap.put("net/minecraft/entity/player/EntityPlayer",
                new ClassInfo("net/minecraft/entity/player/EntityPlayer", "aed")
                        .addFieldInfo("inventory", "Lnet/minecraft/entity/player/InventoryPlayer;", "field_71071_by", "Laec;")
        );

        classInfoMap.put("net/minecraft/client/entity/EntityPlayerSP",

                new ClassInfo("net/minecraft/client/entity/EntityPlayerSP", "bud")
                        //new ClassInfo("net/minecraft/client/entity/EntityPlayerSP", "bnn")
                        .addMethodInfo2("isSneaking", "()Z", "aM", "()Z")
                        .addMethodInfo2("updateEntityActionState", "()V", "cr", "()V")
                        .addMethodInfo2("turn", "(FF)V", "c", "(FF)V")
        );
        //

        classInfoMap.put("net/minecraft/entity/player/EntityPlayerMP",
                new ClassInfo("net/minecraft/entity/player/EntityPlayerMP", "oq")
                        .addMethodInfo2("sendSlotContents", "(Lnet/minecraft/inventory/Container;ILnet/minecraft/item/ItemStack;)V", "a", "(Lafr;ILaip;)V")
        );

        classInfoMap.put("net/minecraft/advancements/critereon/InventoryChangeTrigger",
                new ClassInfo("net/minecraft/advancements/critereon/InventoryChangeTrigger", "al")
                        .addMethodInfo2("trigger", "(Lnet/minecraft/entity/player/EntityPlayerMP;Lnet/minecraft/entity/player/InventoryPlayer;)V", "a", "(Loq;Laec;)V")
        );

        classInfoMap.put("net/minecraft/entity/EntityLivingBase",
                new ClassInfo("net/minecraft/entity/EntityLivingBase", "vp")
                        .addMethodInfo2("attackEntityFrom", "(Lnet/minecraft/util/DamageSource;F)Z", "a", "(Lur;F)Z")
                        .addMethodInfo2("knockBack", "(Lnet/minecraft/entity/Entity;FDD)V", "a", "(Lvg;FDD)V")
        );

        classInfoMap.put("net/minecraft/client/model/ModelRenderer",
                new ClassInfo("net/minecraft/client/model/ModelRenderer", "brs")
                        .addMethodInfo2("render", "(F)V", "a", "(F)V")
                        .addFieldInfo("cubeList", "Lnet/minecraft/client/renderer/entity/RenderLivingBase;", "field_78804_l", "Lcaa;")
        );


        classInfoMap.put("paulscode.sound.libraries.SourceLWJGLOpenAL",
                new ClassInfo("paulscode.sound.libraries.SourceLWJGLOpenAL", "paulscode.sound.libraries.SourceLWJGLOpenAL")
                        .addMethodInfo("play", "(Lpaulscode/sound/Channel;)V", "play", "play")
        );


    }

    private static final ClassInfoProvider instance = new ClassInfoProvider();

    public static ClassInfoProvider getInstance() {
        return instance;
    }

    public ClassInfo getClassInfo(String mcpClassName) {
        if (!mcpClassName.equals("paulscode.sound.libraries.SourceLWJGLOpenAL")) {
            return classInfoMap.get(mcpClassName.replace('.', '/'));
        } else {
            return classInfoMap.get(mcpClassName);
        }

    }

    public void sanityCheck(boolean isObfuscated) {
        if (!isObfuscated) {
            return;
        }

        try {
            ClassInfo info = classInfoMap.get("net/minecraft/entity/EntityLivingBase");
            String notchClassName = info.getNotchClassName();

            byte[] bytes = Launch.classLoader.getClassBytes(notchClassName);
            if (bytes == null) {
                throw new RuntimeException("Modern Warfare Cubed ASM sanity check failed! Could not find class '" + notchClassName + "' (EntityLivingBase). Obfuscation mappings mismatch.");
            }

            final boolean[] methodFound = {false};
            ClassReader cr = new ClassReader(bytes);
            cr.accept(new ClassVisitor(Opcodes.ASM4) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    if ("a".equals(name) && "(Lvg;FDD)V".equals(desc)) {
                        methodFound[0] = true;
                    }
                    return null;
                }
            }, 0);

            if (!methodFound[0]) {
                throw new RuntimeException("Modern Warfare Cubed ASM sanity check failed! Class '" + notchClassName + "' (EntityLivingBase) does not have the expected method 'a(Lvg;FDD)V' (knockBack). Your environment uses different obfuscation mappings than MWC expects.");
            }
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("Modern Warfare Cubed ASM sanity check failed!", e);
        }
    }
}
