package com.paneedah.weaponlib.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ModelAABB {

    public static AxisAlignedBB compute(ModelBase model) {
        if (model.boxList == null || model.boxList.isEmpty()) {
            return new AxisAlignedBB(0, 0, 0, 1, 1, 1);
        }

        Set<ModelRenderer> allChildren = new HashSet<>();
        for (ModelRenderer renderer : model.boxList) {
            if (renderer.childModels != null) {
                allChildren.addAll(renderer.childModels);
            }
        }

        List<ModelRenderer> roots = new ArrayList<>();
        for (ModelRenderer renderer : model.boxList) {
            if (!allChildren.contains(renderer)) {
                roots.add(renderer);
            }
        }

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;
        boolean found = false;

        for (ModelRenderer root : roots) {
            AxisAlignedBB aabb = computeRenderer(root, null);
            if (aabb != null) {
                minX = Math.min(minX, aabb.minX);
                minY = Math.min(minY, aabb.minY);
                minZ = Math.min(minZ, aabb.minZ);
                maxX = Math.max(maxX, aabb.maxX);
                maxY = Math.max(maxY, aabb.maxY);
                maxZ = Math.max(maxZ, aabb.maxZ);
                found = true;
            }
        }

        if (!found) {
            return new AxisAlignedBB(0, 0, 0, 1, 1, 1);
        }

        return new AxisAlignedBB(minX / 16.0, minY / 16.0, minZ / 16.0, maxX / 16.0, maxY / 16.0, maxZ / 16.0);
    }

    private static AxisAlignedBB computeRenderer(ModelRenderer renderer, Transform parent) {
        Transform current = new Transform(renderer, parent);
        
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;
        boolean found = false;

        if (renderer.cubeList != null) {
            for (ModelBox box : renderer.cubeList) {
                double[] xs = {box.posX1, box.posX2};
                double[] ys = {box.posY1, box.posY2};
                double[] zs = {box.posZ1, box.posZ2};

                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 2; j++) {
                        for (int k = 0; k < 2; k++) {
                            Vec3d v = current.apply(new Vec3d(xs[i], ys[j], zs[k]));
                            minX = Math.min(minX, v.x);
                            minY = Math.min(minY, v.y);
                            minZ = Math.min(minZ, v.z);
                            maxX = Math.max(maxX, v.x);
                            maxY = Math.max(maxY, v.y);
                            maxZ = Math.max(maxZ, v.z);
                            found = true;
                        }
                    }
                }
            }
        }

        if (renderer.childModels != null) {
            for (ModelRenderer child : renderer.childModels) {
                AxisAlignedBB childAABB = computeRenderer(child, current);
                if (childAABB != null) {
                    minX = Math.min(minX, childAABB.minX);
                    minY = Math.min(minY, childAABB.minY);
                    minZ = Math.min(minZ, childAABB.minZ);
                    maxX = Math.max(maxX, childAABB.maxX);
                    maxY = Math.max(maxY, childAABB.maxY);
                    maxZ = Math.max(maxZ, childAABB.maxZ);
                    found = true;
                }
            }
        }

        if (!found) {
            return null;
        }

        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static class Transform {
        final double tx, ty, tz;
        final double rx, ry, rz;
        final Transform parent;

        Transform(ModelRenderer renderer, Transform parent) {
            this.tx = renderer.rotationPointX;
            this.ty = renderer.rotationPointY;
            this.tz = renderer.rotationPointZ;
            this.rx = renderer.rotateAngleX;
            this.ry = renderer.rotateAngleY;
            this.rz = renderer.rotateAngleZ;
            this.parent = parent;
        }

        Vec3d apply(Vec3d v) {
            double x = v.x;
            double y = v.y;
            double z = v.z;

            if (rx != 0.0) {
                double cos = Math.cos(rx);
                double sin = Math.sin(rx);
                double ny = y * cos - z * sin;
                double nz = y * sin + z * cos;
                y = ny;
                z = nz;
            }
            if (ry != 0.0) {
                double cos = Math.cos(ry);
                double sin = Math.sin(ry);
                double nx = x * cos + z * sin;
                double nz = -x * sin + z * cos;
                x = nx;
                z = nz;
            }
            if (rz != 0.0) {
                double cos = Math.cos(rz);
                double sin = Math.sin(rz);
                double nx = x * cos - y * sin;
                double ny = x * sin + y * cos;
                x = nx;
                y = ny;
            }

            x += tx;
            y += ty;
            z += tz;

            Vec3d result = new Vec3d(x, y, z);
            if (parent != null) {
                return parent.apply(result);
            }
            return result;
        }
    }

    private ModelAABB() {}
}
