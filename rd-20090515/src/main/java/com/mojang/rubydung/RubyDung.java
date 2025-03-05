package com.mojang.rubydung;

import com.mojang.rubydung.character.Cube;
import com.mojang.rubydung.character.Zombie;
import com.mojang.rubydung.level.*;

import java.awt.Component;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import javax.swing.JOptionPane;

import com.mojang.rubydung.level.tile.Tile;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class RubyDung implements Runnable {
    private static boolean fullScreen;
    private int width;
    private int height;
    private FloatBuffer fogColor0 = BufferUtils.createFloatBuffer(4);
    private FloatBuffer fogColor1 = BufferUtils.createFloatBuffer(4);
    private Timer timer = new Timer(20.0F);
    private Level level;
    private LevelRenderer levelRenderer;
    private Player player;
    private int paintTexture = 1;
    private ArrayList<Zombie> zombies = new ArrayList();
    private IntBuffer viewportBuffer = BufferUtils.createIntBuffer(16);
    private IntBuffer selectBuffer = BufferUtils.createIntBuffer(2000);
    private HitResult hitResult = null;
    FloatBuffer lb = BufferUtils.createFloatBuffer(16);

    public RubyDung(int width, int height, boolean fullScreen) {
        this.width = width;
        this.height = height;
        this.fullScreen = fullScreen;
    }

    public void init() throws LWJGLException, IOException {
        int col0 = 16710650;
        int col1 = 920330;
        float fr = 0.5F;
        float fg = 0.8F;
        float fb = 1.0F;
        this.fogColor0.put(new float[]{(float)(col0 >> 16 & 255) / 255.0F, (float)(col0 >> 8 & 255) / 255.0F, (float)(col0 & 255) / 255.0F, 1.0F});
        this.fogColor0.flip();
        this.fogColor1.put(new float[]{(float)(col1 >> 16 & 255) / 255.0F, (float)(col1 >> 8 & 255) / 255.0F, (float)(col1 & 255) / 255.0F, 1.0F});
        this.fogColor1.flip();
        if(fullScreen) {
            Display.setFullscreen(true);
            this.width = Display.getWidth();
            this.height = Display.getHeight();
        }
        Display.setDisplayMode(new DisplayMode(this.width, this.height));
        Display.create();
        Keyboard.create();
        Mouse.create();
        this.width = Display.getDisplayMode().getWidth();
        this.height = Display.getDisplayMode().getHeight();
        GL11.glEnable(3553);
        GL11.glShadeModel(7425);
        GL11.glClearColor(fr, fg, fb, 0.0F);
        GL11.glClearDepth((double)1.0F);
        GL11.glEnable(2929);
        GL11.glDepthFunc(515);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(5888);
        this.level = new Level(256, 256, 64);
        this.levelRenderer = new LevelRenderer(this.level);
        this.player = new Player(this.level);
        Mouse.setGrabbed(true);

        for(int i = 0; i < 10; ++i) {
            Zombie zombie = new Zombie(this.level, 128.0F, 0.0F, 128.0F);
            zombie.resetPos();
            this.zombies.add(zombie);
        }
    }

    public void destroy() {
        this.level.save();
        Mouse.destroy();
        Keyboard.destroy();
        Display.destroy();
    }

    public void run() {
        try {
            this.init();
        } catch (Exception e) {
            JOptionPane.showMessageDialog((Component)null, e.toString(), "Failed to start RubyDung", 0);
            System.exit(0);
        }

        long lastTime = System.currentTimeMillis();
        int frames = 0;

        try {
            while(!Keyboard.isKeyDown(1) && !Display.isCloseRequested()) {
                this.timer.advanceTime();

                for(int i = 0; i < this.timer.ticks; ++i) {
                    this.tick();
                }

                this.render(this.timer.a);
                ++frames;

                while(System.currentTimeMillis() >= lastTime + 1000L) {
                    System.out.println(frames + " fps, " + Chunk.updates);
                    Chunk.updates = 0;
                    lastTime += 1000L;
                    frames = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.destroy();
        }

    }

    public void tick() {
        while(Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (Keyboard.getEventKey() == 2) {
                    this.paintTexture = 1;
                }

                if (Keyboard.getEventKey() == 3) {
                    this.paintTexture = 3;
                }

                if (Keyboard.getEventKey() == 4) {
                    this.paintTexture = 4;
                }

                if (Keyboard.getEventKey() == 5) {
                    this.paintTexture = 5;
                }

                if (Keyboard.getEventKey() == 28 && Keyboard.getEventKeyState()) {
                    this.level.save();
                }

                if (Keyboard.getEventKey() == 34 && Keyboard.getEventKeyState()) {
                    this.zombies.add(new Zombie(this.level, this.player.x, this.player.y, this.player.z));
                }
            }
        }

        for(int i = 0; i < this.zombies.size(); ++i) {
            ((Zombie)this.zombies.get(i)).tick();
            if(((Zombie)this.zombies.get(i)).removed) {
                this.zombies.remove(i--);
            }
        }

        this.level.tick();
        this.player.tick();
    }

    private void moveCameraToPlayer(float a) {
        GL11.glTranslatef(0.0F, 0.0F, -0.3F);
        GL11.glRotatef(this.player.xRot, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(this.player.yRot, 0.0F, 1.0F, 0.0F);
        float x = this.player.xo + (this.player.x - this.player.xo) * a;
        float y = this.player.yo + (this.player.y - this.player.yo) * a;
        float z = this.player.zo + (this.player.z - this.player.zo) * a;
        GL11.glTranslatef(-x, -y, -z);
    }

    private void setupCamera(float a) {
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GLU.gluPerspective(70.0F, (float)this.width / (float)this.height, 0.05F, 1000.0F);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        this.moveCameraToPlayer(a);
    }

    private void setupOrthoCamera() {
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho((double)0.0F, (double)this.width, (double)this.height, (double)0.0F, (double)100.0F, (double)300.0F);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -200.0F);
    }

    private void setupPickCamera(float a, int x, int y) {
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        this.viewportBuffer.clear();
        GL11.glGetInteger(2978, this.viewportBuffer);
        this.viewportBuffer.flip();
        this.viewportBuffer.limit(16);
        GLU.gluPickMatrix((float)x, (float)y, 5.0F, 5.0F, this.viewportBuffer);
        GLU.gluPerspective(70.0F, (float)this.width / (float)this.height, 0.05F, 1000.0F);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        this.moveCameraToPlayer(a);
    }

    private void pick(float a) {
        this.selectBuffer.clear();
        GL11.glSelectBuffer(this.selectBuffer);
        GL11.glRenderMode(7170);
        this.setupPickCamera(a, this.width / 2, this.height / 2);
        this.levelRenderer.pick(this.player);
        int hits = GL11.glRenderMode(7168);
        this.selectBuffer.flip();
        this.selectBuffer.limit(this.selectBuffer.capacity());
        long closest = 0L;
        int[] names = new int[10];
        int hitNameCount = 0;

        for(int i = 0; i < hits; ++i) {
            int nameCount = this.selectBuffer.get();
            long minZ = (long)this.selectBuffer.get();
            this.selectBuffer.get();
            if (minZ >= closest && i != 0) {
                for(int j = 0; j < nameCount; ++j) {
                    this.selectBuffer.get();
                }
            } else {
                closest = minZ;
                hitNameCount = nameCount;

                for(int j = 0; j < nameCount; ++j) {
                    names[j] = this.selectBuffer.get();
                }
            }
        }

        if (hitNameCount > 0) {
            this.hitResult = new HitResult(names[0], names[1], names[2], names[3], names[4]);
        } else {
            this.hitResult = null;
        }
    }

    public void render(float a) {
        float xo = (float)Mouse.getDX();
        float yo = (float)Mouse.getDY();
        this.player.turn(xo, yo);
        this.pick(a);

        while(Mouse.next()) {
            if (Mouse.getEventButton() == 1 && Mouse.getEventButtonState() && this.hitResult != null) {
                this.level.setTile(this.hitResult.x, this.hitResult.y, this.hitResult.z, 0);
            }

            if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.hitResult != null) {
                int x = this.hitResult.x;
                int y = this.hitResult.y;
                int z = this.hitResult.z;
                if (this.hitResult.f == 0) {
                    --y;
                }

                if (this.hitResult.f == 1) {
                    ++y;
                }

                if (this.hitResult.f == 2) {
                    --z;
                }

                if (this.hitResult.f == 3) {
                    ++z;
                }

                if (this.hitResult.f == 4) {
                    --x;
                }

                if (this.hitResult.f == 5) {
                    ++x;
                }

                this.level.setTile(x, y, z, this.paintTexture);
            }
        }

        GL11.glClear(16640);
        this.setupCamera(a);
        GL11.glEnable(2884);
        this.levelRenderer.updateDirtyChunks(this.player);
        this.setupFog(0);
        GL11.glEnable(2912);
        this.levelRenderer.render(this.player, 0);

        for(int i = 0; i < this.zombies.size(); ++i) {
            if(this.zombies.get(i).isLit()) {
                ((Zombie)this.zombies.get(i)).render(a);
            }
        }

        this.setupFog(1);
        this.levelRenderer.render(this.player, 1);

        for(int i = 0; i < this.zombies.size(); ++i) {
            if(!this.zombies.get(i).isLit()) {
                ((Zombie)this.zombies.get(i)).render(a);
            }
        }

        if (this.hitResult != null) {
            this.levelRenderer.renderHit(this.hitResult);
        }

        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glDisable(2912);
        this.drawGui(a);
        Display.update();
    }

    private void drawGui(float a) {
        GL11.glClear(256);
        this.setupOrthoCamera();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(this.width - 48), 48.0F, 0.0F);
        Tesselator t = Tesselator.instance;
        GL11.glScalef(48.0F, 48.0F, 48.0F);
        GL11.glRotatef(30.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(1.5F, -0.5F, -0.5F);
        int id = Textures.loadTexture("/terrain.png", 9728);
        GL11.glBindTexture(3553, id);
        GL11.glEnable(3553);
        t.init();
        Tile.tiles[this.paintTexture].render(t, this.level, 0, -2, 0, 0);
        t.flush();
        GL11.glDisable(3553);
        GL11.glPopMatrix();
        int wc = this.width / 2;
        int hc = this.height / 2;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        t.init();
        t.vertex((float)(wc + 1), (float)(hc - 8), 0.0F);
        t.vertex((float)(wc - 0), (float)(hc - 8), 0.0F);
        t.vertex((float)(wc - 0), (float)(hc + 9), 0.0F);
        t.vertex((float)(wc + 1), (float)(hc + 9), 0.0F);
        t.vertex((float)(wc + 9), (float)(hc - 0), 0.0F);
        t.vertex((float)(wc - 8), (float)(hc - 0), 0.0F);
        t.vertex((float)(wc - 8), (float)(hc + 1), 0.0F);
        t.vertex((float)(wc + 9), (float)(hc + 1), 0.0F);
        t.flush();
    }

    private void setupFog(int i) {
        if (i == 0) {
            GL11.glFogi(2917, 2048);
            GL11.glFogf(2914, 0.001F);
            GL11.glFog(2918, this.fogColor0);
            GL11.glDisable(2896);
        } else if (i == 1) {
            GL11.glFogi(2917, 2048);
            GL11.glFogf(2914, 0.06F);
            GL11.glFog(2918, this.fogColor1);
            GL11.glEnable(2896);
            GL11.glEnable(2903);
            float br = 0.6F;
            GL11.glLightModel(2899, this.getBuffer(br, br, br, 1.0F));
        }
    }
    private FloatBuffer getBuffer(float a, float b, float c, float d) {
        this.lb.clear();
        this.lb.put(a).put(b).put(c).put(d);
        this.lb.flip();
        return this.lb;
    }

    public static void checkError() {
        int e = GL11.glGetError();
        if (e != 0) {
            throw new IllegalStateException(GLU.gluErrorString(e));
        }
    }

    public static void main(String[] args) throws LWJGLException {
        (new Thread(new RubyDung(1024, 768, false))).start();
    }
}
