package com.paneedah.weaponlib.render.wavefront;

import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.paneedah.mwc.proxies.ClientProxy.MC;
import static com.paneedah.mwc.ProjectConstants.ID;

public class WavefrontLoader {

    private static final String HEADER_OBJECT = "o";
    private static final String HEADER_VERTEX = "v";
    private static final String HEADER_TEX_COORD = "vt";
    private static final String HEADER_LIGHT_COORD = "vn";
    private static final String HEADER_FACE = "f";
    private static final String OBJ_MODEL_LOCATION = ID + ":models/obj/";

	/*
	public static WavefrontModel loadSubModel(String modelName, String objectName) {
		ResourceLocation resLoc = new ResourceLocation(OBJ_MODEL_LOCATION + modelName + ".obj");
	}*/

    public static WavefrontModel load(String modelName) {
        return load(new ResourceLocation(OBJ_MODEL_LOCATION + modelName + ".obj"));
    }

    public static WavefrontModel loadSubModel(String model, String subModel) {
        return loadSubModel(model, subModel, false);
    }

    public static WavefrontModel loadSubModel(String model, String subModel, boolean vaoMode) {
        boolean startRead = false;
        ArrayList<String> lines = new ArrayList<>();
        ResourceLocation loc = new ResourceLocation(OBJ_MODEL_LOCATION + model + ".obj");

        try (IResource resource = MC.getResourceManager().getResource(loc);
             InputStream is = resource.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            while (br.ready()) {
                String line = br.readLine();

                if (line.startsWith("o")) {
                    if (line.split(" ")[1].equals(subModel)) {
                        startRead = true;
                    } else {
                        break;
                        //	startRead = false;
                    }
                    startRead = true;
                }
                if (startRead) {
                    lines.add(line);
                }

                if (!startRead && (line.startsWith("v") || line.startsWith("vn") || line.startsWith("vt"))) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return process(lines, vaoMode);
    }

    public static WavefrontModel loadWeaponModel(String model, List<String> objectNames, boolean vaoMode) {
        boolean startRead = false;
        ArrayList<String> lines = new ArrayList<>();
        ResourceLocation loc = new ResourceLocation(OBJ_MODEL_LOCATION + model + ".obj");

        try (IResource resource = MC.getResourceManager().getResource(loc);
             InputStream is = resource.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            while (br.ready()) {
                String line = br.readLine();

                if (line.startsWith("o")) {
                    String objectName = line.split(" ")[1];
                    startRead = objectNames.contains(objectName);
                }
                if (startRead) {
                    lines.add(line);
                }

                if (!startRead && (line.startsWith("v") || line.startsWith("vn") || line.startsWith("vt"))) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return process(lines, vaoMode);
    }


    public static WavefrontModel load(ResourceLocation loc) {
        ArrayList<String> lines = new ArrayList<>();

        try (IResource resource = MC.getResourceManager().getResource(loc);
             InputStream is = resource.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            while (br.ready()) {
                lines.add(br.readLine());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return process(lines, false);
    }

    private static WavefrontModel process(ArrayList<String> unprocessed, boolean vaoMode) {
        WavefrontModel model = new WavefrontModel();
        ArrayList<String[]> preprocesFace = new ArrayList<>();

        for (String rawLine : unprocessed) {
            // We are not interested in blank lines.
            if (rawLine.isEmpty()) {
                continue;
            }

            String[] line = rawLine.split(" ");

            // pass if it's not useful stuff
            if (!line[0].equals(HEADER_OBJECT) && !line[0].equals(HEADER_VERTEX) && !line[0].equals(HEADER_LIGHT_COORD) && !line[0].equals(HEADER_TEX_COORD) && !line[0].equals(HEADER_FACE)) {
                continue;
            }

            switch (line[0]) {
                case HEADER_VERTEX:
                    float[] vertex = new float[3];

                    for (int i = 1; i < line.length; ++i) {
                        vertex[i - 1] = Float.parseFloat(line[i]) * 0.2f;
                    }

                    model.vertex.add(vertex);
                    break;
                case HEADER_TEX_COORD:
                    float[] texCoord = new float[2];
                    for (int i = 1; i < line.length; ++i) {
                        texCoord[i - 1] = Float.parseFloat(line[i]);
                    }

                    model.texcoord.add(texCoord);
                    break;
                case HEADER_FACE:
                    preprocesFace.add(line);
                    break;
                case HEADER_LIGHT_COORD:
                    float[] normal = new float[3];
                    for (int i = 1; i < line.length; ++i) {
                        normal[i - 1] = Float.parseFloat(line[i]);
                    }

                    model.normals.add(normal);
                    break;
            }
        }

        // System.out.println("Count: " + model.vertex.size());

        for (String[] ar : preprocesFace) {
            model.buildIndexBuffer(ar);
            // model.buildFace(ar);
        }

        if (!vaoMode) {
            model.build();
        } else {
            model.buildVAO();
        }

        return model;
    }
}
