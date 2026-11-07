package com.mobius.stream.Game.Map.BPS.Particle;

public class ParticlePool {
    public static final int MAX_PARTICLES = 20000;

    public final float[] x = new float[MAX_PARTICLES];
    public final float[] y = new float[MAX_PARTICLES];
    public final float[] vx = new float[MAX_PARTICLES];
    public final float[] vy = new float[MAX_PARTICLES];
    public final float[] gravity = new float[MAX_PARTICLES];
    public final float[] drag = new float[MAX_PARTICLES];

    public final float[] life = new float[MAX_PARTICLES];
    public final float[] maxLife = new float[MAX_PARTICLES];

    public final float[] size = new float[MAX_PARTICLES];
    public final float[] startSize = new float[MAX_PARTICLES];
    public final float[] endSize = new float[MAX_PARTICLES];
    public final float[] rotation = new float[MAX_PARTICLES];
    public final float[] rotSpeed = new float[MAX_PARTICLES];

    public final float[] startR = new float[MAX_PARTICLES];
    public final float[] startG = new float[MAX_PARTICLES];
    public final float[] startB = new float[MAX_PARTICLES];
    public final float[] startA = new float[MAX_PARTICLES];

    public final float[] endR = new float[MAX_PARTICLES];
    public final float[] endG = new float[MAX_PARTICLES];
    public final float[] endB = new float[MAX_PARTICLES];
    public final float[] endA = new float[MAX_PARTICLES];

    public final float[] r = new float[MAX_PARTICLES];
    public final float[] g = new float[MAX_PARTICLES];
    public final float[] b = new float[MAX_PARTICLES];
    public final float[] a = new float[MAX_PARTICLES];

    public final int[] region = new int[MAX_PARTICLES];

    public int count;

    public ParticlePool() {
        count = 0;
    }

    public int obtain() {
        if (count < MAX_PARTICLES) {
            return count++;
        }
        return -1;
    }

    public void free(int i) {
        int last = count - 1;
        if (i != last) {
            copySlot(i, last);
        }
        count--;
    }

    private void copySlot(int to, int from) {
        x[to] = x[from];
        y[to] = y[from];
        vx[to] = vx[from];
        vy[to] = vy[from];
        gravity[to] = gravity[from];
        drag[to] = drag[from];

        life[to] = life[from];
        maxLife[to] = maxLife[from];

        size[to] = size[from];
        startSize[to] = startSize[from];
        endSize[to] = endSize[from];
        rotation[to] = rotation[from];
        rotSpeed[to] = rotSpeed[from];

        startR[to] = startR[from];
        startG[to] = startG[from];
        startB[to] = startB[from];
        startA[to] = startA[from];
        endR[to] = endR[from];
        endG[to] = endG[from];
        endB[to] = endB[from];
        endA[to] = endA[from];

        r[to] = r[from];
        g[to] = g[from];
        b[to] = b[from];
        a[to] = a[from];

        region[to] = region[from];
    }

    public void clear() {
        count = 0;
    }
}
