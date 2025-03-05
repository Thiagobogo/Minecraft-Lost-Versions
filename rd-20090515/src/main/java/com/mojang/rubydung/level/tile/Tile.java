package com.mojang.rubydung.level.tile;

import com.mojang.rubydung.level.Level;
import com.mojang.rubydung.level.Tesselator;
import com.mojang.rubydung.phys.AABB;

import java.util.Random;

public class Tile {

    public static Tile[] tiles = new Tile[256];
    public static Tile rock = new Tile(1, 1);
    public static Tile grass = new GrassTile(2);
    public static Tile dirt = new DirtTile(3, 2);
    public static Tile stoneBrick = new Tile(4, 16);
    public static Tile wood = new Tile(5, 4);
    public int tex;
    public final int id;

    protected Tile(int id) {
        tiles[id] = this;
        this.id = id;
    }

    protected Tile(int id, int tex) {
        this(id);
        this.tex = tex;
    }

    public void render(Tesselator t, Level level, int layer, int x, int y, int z) {
        float c1 = 1.0F;
        float c2 = 0.8F;
        float c3 = 0.6F;
        if (this.shouldRenderFace(level, x, y - 1, z, layer)) {
            t.color(c1, c1, c1);
            this.renderFace(t, x, y, z, 0);
        }

        if (this.shouldRenderFace(level, x, y + 1, z, layer)) {
            t.color(c1, c1, c1);
            this.renderFace(t, x, y, z, 1);
        }

        if (this.shouldRenderFace(level, x, y, z - 1, layer)) {
            t.color(c2, c2, c2);
            this.renderFace(t, x, y, z, 2);
        }

        if (this.shouldRenderFace(level, x, y, z + 1, layer)) {
            t.color(c2, c2, c2);
            this.renderFace(t, x, y, z, 3);
        }

        if (this.shouldRenderFace(level, x - 1, y, z, layer)) {
            t.color(c3, c3, c3);
            this.renderFace(t, x, y, z, 4);
        }

        if (this.shouldRenderFace(level, x + 1, y, z, layer)) {
            t.color(c3, c3, c3);
            this.renderFace(t, x, y, z, 5);
        }
    }

    private boolean shouldRenderFace(Level level, int x, int y, int z, int layer) {
        return !level.isSolidTile(x, y, z) && level.isLit(x, y, z) ^ layer == 1;
    }

    protected int getTexture(int face) {
        return this.tex;
    }

    public void renderFace(Tesselator t, int x, int y, int z, int face) {
        int tex = this.getTexture(face);
        float u0 = (float)(tex % 16) / 16.0F;
        float u1 = u0 + 0.0624375F;
        float v0 = (float)(tex / 16) / 16.0F;
        float v1 = v0 + 0.0624375F;
        float x0 = (float)x + 0.0F;
        float x1 = (float)x + 1.0F;
        float y0 = (float)y + 0.0F;
        float y1 = (float)y + 1.0F;
        float z0 = (float)z + 0.0F;
        float z1 = (float)z + 1.0F;
        if (face == 0) {
            t.vertexUV(x0, y0, z1, u0, v1);
            t.vertexUV(x0, y0, z0, u0, v0);
            t.vertexUV(x1, y0, z0, u1, v0);
            t.vertexUV(x1, y0, z1, u1, v1);
        }

        if (face == 1) {
            t.vertexUV(x1, y1, z1, u1, v1);
            t.vertexUV(x1, y1, z0, u1, v0);
            t.vertexUV(x0, y1, z0, u0, v0);
            t.vertexUV(x0, y1, z1, u0, v1);
        }

        if (face == 2) {
            t.vertexUV(x0, y1, z0, u1, v0);
            t.vertexUV(x1, y1, z0, u0, v0);
            t.vertexUV(x1, y0, z0, u0, v1);
            t.vertexUV(x0, y0, z0, u1, v1);
        }

        if (face == 3) {
            t.vertexUV(x0, y1, z1, u0, v0);
            t.vertexUV(x0, y0, z1, u0, v1);
            t.vertexUV(x1, y0, z1, u1, v1);
            t.vertexUV(x1, y1, z1, u1, v0);
        }

        if (face == 4) {
            t.vertexUV(x0, y1, z1, u1, v0);
            t.vertexUV(x0, y1, z0, u0, v0);
            t.vertexUV(x0, y0, z0, u0, v1);
            t.vertexUV(x0, y0, z1, u1, v1);
        }

        if (face == 5) {
            t.vertexUV(x1, y0, z1, u0, v1);
            t.vertexUV(x1, y0, z0, u1, v1);
            t.vertexUV(x1, y1, z0, u1, v0);
            t.vertexUV(x1, y1, z1, u0, v0);
        }
    }

    public void renderFaceNoTexture(Tesselator t, int x, int y, int z, int face) {
        float x0 = (float)x + 0.0F;
        float x1 = (float)x + 1.0F;
        float y0 = (float)y + 0.0F;
        float y1 = (float)y + 1.0F;
        float z0 = (float)z + 0.0F;
        float z1 = (float)z + 1.0F;
        if (face == 0) {
            t.vertex(x0, y0, z1);
            t.vertex(x0, y0, z0);
            t.vertex(x1, y0, z0);
            t.vertex(x1, y0, z1);
        }

        if (face == 1) {
            t.vertex(x1, y1, z1);
            t.vertex(x1, y1, z0);
            t.vertex(x0, y1, z0);
            t.vertex(x0, y1, z1);
        }

        if (face == 2) {
            t.vertex(x0, y1, z0);
            t.vertex(x1, y1, z0);
            t.vertex(x1, y0, z0);
            t.vertex(x0, y0, z0);
        }

        if (face == 3) {
            t.vertex(x0, y1, z1);
            t.vertex(x0, y0, z1);
            t.vertex(x1, y0, z1);
            t.vertex(x1, y1, z1);
        }

        if (face == 4) {
            t.vertex(x0, y1, z1);
            t.vertex(x0, y1, z0);
            t.vertex(x0, y0, z0);
            t.vertex(x0, y0, z1);
        }

        if (face == 5) {
            t.vertex(x1, y0, z1);
            t.vertex(x1, y0, z0);
            t.vertex(x1, y1, z0);
            t.vertex(x1, y1, z1);
        }
    }

    public AABB getTileAABB(int x, int y, int z) {
        return new AABB((float)x, (float)y, (float)z, (float)(x + 1), (float)(y + 1), (float)(z + 1));
    }

    public AABB getAABB(int x, int y, int z) {
        return new AABB((float)x, (float)y, (float)z, (float)(x + 1), (float)(y + 1), (float)(z + 1));
    }

    public boolean blocksLight() {
        return true;
    }

    public boolean isSolid() {
        return true;
    }

    public void tick(Level level, int x, int y, int z, Random random) {
    }
}
