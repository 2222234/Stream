package com.mobius.stream.Stage.Element;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;

public class CometActor extends Actor implements Disposable {

    private static final float CYCLE_DURATION = 2.5f;
    private static final float FADE_IN_END = 0.25f;
    private static final float FADE_OUT_START = 0.78f;
    private static final float FADE_OUT_LIMIT = 0.86f;
    private static final String VERT =
        """
            attribute vec4 a_position;
            attribute vec4 a_color;
            attribute vec2 a_texCoord0;
            uniform mat4 u_projTrans;
            varying vec2 v_uv;
            varying vec4 v_color;
            void main() {
                v_uv = a_texCoord0;
                v_color = a_color;
                gl_Position = u_projTrans * a_position;
            }
            """;
    private static final String FRAG =
        """
            #ifdef GL_ES
            precision highp float;
            #endif

            varying vec2 v_uv;
            varying vec4 v_color;

            uniform vec2  u_resolution;
            uniform float u_time;
            uniform float u_alpha;
            uniform float u_apply_envelope;

            #define PI  3.14159265359
            #define TAU 6.28318530718

            float comet(float param, float pLight, float tailK, float headK) {
                float sd = pLight - param;
                sd = sd - floor(sd + 0.5);
                return exp(-max(sd, 0.0) * tailK) * exp(min(sd, 0.0) * headK);
            }

            float sdRoundArc(vec2 p, float R, float param, float pHead, float bandLen, float r) {
                float dRadial = abs(length(p) - R);
                float sd = param - pHead;
                sd = sd - floor(sd + 0.5);
                float dPara = max(max(-bandLen - sd, sd), 0.0);
                float dArc  = dPara * 4.0 * PI * r;
                return sqrt(dRadial * dRadial + dArc * dArc);
            }

            void main() {
                vec2 fragCoord = v_uv * u_resolution;
                vec2 uv = (2.0 * fragCoord - u_resolution.xy) / u_resolution.y;

                float r = 0.2;
                vec2 c1 = vec2(-r, 0.0);
                vec2 c2 = vec2( r, 0.0);
                vec2 p1 = uv - c1;
                vec2 p2 = uv - c2;

                float d1 = length(p1) - r;
                float d2 = length(p2) - r;

                float aa = 2.0 / u_resolution.y;
                float w  = 0.2;

                float ring1 = smoothstep(w + aa, w - aa, abs(d1));
                float ring2 = smoothstep(w + aa, w - aa, abs(d2));

                float th1    = mod(atan(p1.y, p1.x), TAU);
                float param1 = th1 / (2.0 * TAU);

                float a2     = atan(p2.y, p2.x);
                float th2    = mod(PI - a2, TAU) + PI;
                float param2 = 0.5 + (th2 - PI) / (2.0 * TAU);

                float cycleT = fract(u_time * 0.4);
                float pLight = cycleT;

                float openAnim  = smoothstep(0.00, 0.15, cycleT);
                float closeAnim = smoothstep(1.00, 0.85, cycleT);
                float lenEnv    = smoothstep(0.00, 0.25, cycleT) * smoothstep(1.00, 0.78, cycleT);

                float envelope = mix(1.0, openAnim * closeAnim, u_apply_envelope);
                float BAND_LEN = mix(0.18, mix(0.02, 0.18, lenEnv), u_apply_envelope);

                float tailK = 8.0;
                float headK = 200.0;

                float spot1 = comet(param1, pLight, tailK, headK);
                float spot2 = comet(param2, pLight, tailK, headK);

                float STROKE_W = 0.012;
                float rInner = r - w;
                float rOuter = r + w;

                bool  hasInner = (rInner > STROKE_W);
                float s1i = hasInner ? sdRoundArc(p1, rInner, param1, pLight, BAND_LEN, r) : 1e9;
                float s2i = hasInner ? sdRoundArc(p2, rInner, param2, pLight, BAND_LEN, r) : 1e9;
                float s1o = sdRoundArc(p1, rOuter, param1, pLight, BAND_LEN, r);
                float s2o = sdRoundArc(p2, rOuter, param2, pLight, BAND_LEN, r);

                float stroke1 = smoothstep(STROKE_W + aa, STROKE_W - aa, min(s1i, s1o));
                float stroke2 = smoothstep(STROKE_W + aa, STROKE_W - aa, min(s2i, s2o));

                float band1 = smoothstep(0.02, 0.40, spot1);
                float band2 = smoothstep(0.02, 0.40, spot2);

                float outline = max(stroke1 * band1, stroke2 * band2);

                // ★ 背景圆完全透明 —— 不再累加静态圆环灰度
                float body = max(spot1 * ring1, spot2 * ring2) * 0.5;

                float gray = body    * envelope;
                gray = max(gray, outline * envelope);

                float glow = exp(-abs(min(d1, d2)) * 35.0) * 0.06;
                gray = max(gray, glow * (spot1 + spot2) * envelope);

                gray *= u_alpha;

                gl_FragColor = vec4(vec3(gray), 0.0);
            }
            """;
    private final ShaderProgram program;
    private final TextureRegion pixel;
    private State state = State.IDLE;
    private float time = 0f;
    private float stateTime = 0f;
    private float globalAlpha = 0f;
    private Runnable onStartComplete;
    private Runnable onEndComplete;

    public CometActor() {
        ShaderProgram.pedantic = false;
        program = new ShaderProgram(VERT, FRAG);
        if (!program.isCompiled()) {
            throw new IllegalStateException("CometActor shader 编译失败:\n" + program.getLog());
        }

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        Texture tex = new Texture(pm);
        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pm.dispose();
        pixel = new TextureRegion(tex);

        setSize(60f, 45f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        delta = Math.min(delta, 1f / 20f);

        switch (state) {
            case IDLE:
                globalAlpha = 0f;
                break;

            case FADING_IN:
                time += delta;
                stateTime += delta;
                if (getCycleT() >= FADE_IN_END) {
                    switchTo(State.LOOPING);
                    if (onStartComplete != null) onStartComplete.run();
                }
                break;

            case LOOPING:
                time += delta;
                globalAlpha = 1f;
                break;

            case PAUSED:
                break;

            case PENDING_END: {
                time += delta;
                globalAlpha = 1f;
                float cycleT = getCycleT();
                if (cycleT >= FADE_OUT_START && cycleT < FADE_OUT_LIMIT) {
                    switchTo(State.FADING_OUT);
                }
                break;
            }

            case FADING_OUT: {
                time += delta;
                stateTime += delta;
                float totalDur = (1f - FADE_OUT_START) * CYCLE_DURATION;
                float t = Math.min(stateTime / totalDur, 1f);
                globalAlpha = 1f - t;

                if (t >= 1f) {
                    time = 0f;
                    globalAlpha = 0f;
                    switchTo(State.IDLE);
                    if (onEndComplete != null) onEndComplete.run();
                }
                break;
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (state == State.IDLE && globalAlpha <= 0.0001f) return;

        float x = getX();
        float y = getY();
        float w = getWidth();
        float h = getHeight();

        ShaderProgram prevShader = batch.getShader();
        int prevSrc = GL20.GL_SRC_ALPHA;
        int prevDst = GL20.GL_ONE_MINUS_SRC_ALPHA;
        if (batch instanceof SpriteBatch sb) {
            prevSrc = sb.getBlendSrcFunc();
            prevDst = sb.getBlendDstFunc();
        }

        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);
        batch.setShader(program);

        program.setUniformf("u_resolution", w, h);
        program.setUniformf("u_time", time);
        program.setUniformf("u_alpha", globalAlpha * parentAlpha);
        program.setUniformf("u_apply_envelope", getApplyEnvelope());

        Color c = getColor();
        batch.setColor(c.r, c.g, c.b, c.a);
        batch.draw(pixel, x, y, w, h);

        batch.setShader(prevShader);
        batch.setBlendFunction(prevSrc, prevDst);
    }

    public void start() {
        time = 0f;
        stateTime = 0f;
        globalAlpha = 1f;
        switchTo(State.FADING_IN);
    }

    public void togglePause() {
        if (state == State.LOOPING) switchTo(State.PAUSED);
        else if (state == State.PAUSED) switchTo(State.LOOPING);
    }

    public void pause() {
        if (state == State.LOOPING) switchTo(State.PAUSED);
    }

    public void resume() {
        if (state == State.PAUSED) switchTo(State.LOOPING);
    }

    public void end() {
        if (state == State.IDLE ||
            state == State.FADING_IN ||
            state == State.PENDING_END ||
            state == State.FADING_OUT) return;
        switchTo(State.PENDING_END);
    }

    public void stop() {
        time = 0f;
        stateTime = 0f;
        globalAlpha = 0f;
        switchTo(State.IDLE);
    }

    private float getCycleT() {
        float t = time / CYCLE_DURATION;
        return t - (float) Math.floor(t);
    }

    private float getApplyEnvelope() {
        return (state == State.FADING_IN || state == State.FADING_OUT) ? 1f : 0f;
    }

    private void switchTo(State newState) {
        if (state == newState) return;
        state = newState;
        stateTime = 0f;
    }

    public State getState() {
        return state;
    }

    public float getTime() {
        return time;
    }

    public boolean isPlaying() {
        return state == State.LOOPING;
    }

    public void setOnStartComplete(Runnable r) {
        this.onStartComplete = r;
    }

    public void setOnEndComplete(Runnable r) {
        this.onEndComplete = r;
    }

    @Override
    public void dispose() {
        program.dispose();
        pixel.getTexture().dispose();
    }

    public enum State {
        IDLE, FADING_IN, LOOPING, PAUSED, PENDING_END, FADING_OUT
    }
}
