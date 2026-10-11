package dev.amymialee.amymialib.modules.easings;

import java.util.function.Function;

public enum EasingF {
    LINEAR(EasingF::easeLinear),
    IN_SINE(EasingF::easeInSine),
    OUT_SINE(EasingF::easeOutSine),
    IN_OUT_SINE(EasingF::easeInOutSine),
    IN_QUAD(EasingF::easeInQuad),
    OUT_QUAD(EasingF::easeOutQuad),
    IN_OUT_QUAD(EasingF::easeInOutQuad),
    IN_CUBIC(EasingF::easeInCubic),
    OUT_CUBIC(EasingF::easeOutCubic),
    IN_OUT_CUBIC(EasingF::easeInOutCubic),
    IN_QUART(EasingF::easeInQuart),
    OUT_QUART(EasingF::easeOutQuart),
    IN_OUT_QUART(EasingF::easeInOutQuart),
    IN_QUINT(EasingF::easeInQuint),
    OUT_QUINT(EasingF::easeOutQuint),
    IN_OUT_QUINT(EasingF::easeInOutQuint),
    IN_EXPO(EasingF::easeInExpo),
    OUT_EXPO(EasingF::easeOutExpo),
    IN_OUT_EXPO(EasingF::easeInOutExpo),
    IN_CIRC(EasingF::easeInCirc),
    OUT_CIRC(EasingF::easeOutCirc),
    IN_OUT_CIRC(EasingF::easeInOutCirc),
    IN_BACK(EasingF::easeInBack),
    OUT_BACK(EasingF::easeOutBack),
    IN_OUT_BACK(EasingF::easeInOutBack),
    IN_ELASTIC(EasingF::easeInElastic),
    OUT_ELASTIC(EasingF::easeOutElastic),
    IN_OUT_ELASTIC(EasingF::easeInOutElastic),
    IN_BOUNCE(EasingF::easeInBounce),
    OUT_BOUNCE(EasingF::easeOutBounce),
    IN_OUT_BOUNCE(EasingF::easeInOutBounce);

    private final Function<Float, Float> func;

    EasingF(Function<Float, Float> func) {
        this.func = func;
    }

    public float ease(float x) {
        return func.apply(x);
    }

    public float interpolate(float delta, float start, float end) {
        return start + func.apply(delta) * (end - start);
    }

    public static float easeLinear(float x) {
        return x;
    }
    
    public static float easeInSine(float x) {
        return (float) (1 - Math.cos((x * Math.PI) / 2));
    }

    public static float easeOutSine(float x) {
        return (float) Math.sin((x * Math.PI) / 2);
    }

    public static float easeInOutSine(float x) {
        return (float) (-(Math.cos(Math.PI * x) - 1) / 2);
    }

    public static float easeInQuad(float x) {
        return x * x;
    }

    public static float easeOutQuad(float x) {
        return 1 - (1 - x) * (1 - x);
    }

    public static float easeInOutQuad(float x) {
        return x < 0.5f ? 2 * x * x : (float) (1 - Math.pow(-2 * x + 2, 2) / 2);
    }

    public static float easeInCubic(float x) {
        return x * x * x;
    }

    public static float easeOutCubic(float x) {
        return (float) (1 - Math.pow(1 - x, 3));
    }

    public static float easeInOutCubic(float x) {
        return x < 0.5f ? 4 * x * x * x : (float) (1 - Math.pow(-2 * x + 2, 3) / 2);
    }

    public static float easeInQuart(float x) {
        return x * x * x * x;
    }

    public static float easeOutQuart(float x) {
        return (float) (1 - Math.pow(1 - x, 4));
    }

    public static float easeInOutQuart(float x) {
        return x < 0.5f ? 8 * x * x * x * x : (float) (1 - Math.pow(-2 * x + 2, 4) / 2);
    }

    public static float easeInQuint(float x) {
        return x * x * x * x * x;
    }

    public static float easeOutQuint(float x) {
        return (float) (1 - Math.pow(1 - x, 5));
    }

    public static float easeInOutQuint(float x) {
        return x < 0.5f ? 16 * x * x * x * x * x : (float) (1 - Math.pow(-2 * x + 2, 5) / 2);
    }

    public static float easeInExpo(float x) {
        return x == 0 ? 0 : (float) Math.pow(2, 10 * x - 10);
    }

    public static float easeOutExpo(float x) {
        return x == 1 ? 1 : (float) (1 - Math.pow(2, -10 * x));
    }

    public static float easeInOutExpo(float x) {
        if (x == 0) return 0;
        if (x == 1) return 1;
        return (float) (x < 0.5f ? Math.pow(2, 20 * x - 10) / 2 : (2 - Math.pow(2, -20 * x + 10)) / 2);
    }

    public static float easeInCirc(float x) {
        return (float) (1 - Math.sqrt(1 - Math.pow(x, 2)));
    }

    public static float easeOutCirc(float x) {
        return (float) Math.sqrt(1 - Math.pow(x - 1, 2));
    }

    public static float easeInOutCirc(float x) {
        return (float) (x < 0.5f ? (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2 : (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2);
    }

    public static float easeInBack(float x) {
        final var c1 = 1.70158f;
        return (c1 + 1) * x * x * x - c1 * x * x;
    }

    public static float easeOutBack(float x) {
        final var c1 = 1.70158f;
        return (float) (1 + (c1 + 1) * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2));
    }

    public static float easeInOutBack(float x) {
        final var c2 = 2.5949095f;
        return (float) (x < 0.5f ? (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2 
                        : (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2);
    }

    public static float easeInElastic(float x) {
        if (x == 0) return 0;
        if (x == 1) return 1;
        return (float) (-Math.pow(2, 10 * x - 10) * Math.sin((x * 10 - 10.75) * (2 * Math.PI) / 3));
    }

    public static float easeOutElastic(float x) {
        if (x == 0) return 0;
        if (x == 1) return 1;
        return (float) (Math.pow(2, -10 * x) * Math.sin((x * 10 - 0.75) * (2 * Math.PI) / 3) + 1);
    }

    public static float easeInOutElastic(float x) {
        if (x == 0) return 0;
        if (x == 1) return 1;
        var sin = Math.sin((20 * x - 11.125) * (2 * Math.PI) / 4.5);
        return (float) (x < 0.5 ? -(Math.pow(2, 20 * x - 10) * sin) / 2 : (Math.pow(2, -20 * x + 10) * sin) / 2 + 1);
    }

    public static float easeInBounce(float x) {
        return 1 - easeOutBounce(1 - x);
    }

    public static float easeOutBounce(float x) {
        final var n1 = 7.5625f;
        final var d1 = 2.75f;
        if (x < 1 / d1) return n1 * x * x;
        if (x < 2 / d1) return n1 * (x -= 1.5f / d1) * x + 0.75f;
        if (x < 2.5f / d1) return n1 * (x -= 2.25f / d1) * x + 0.9375f;
        return n1 * (x -= 2.625f / d1) * x + 0.984375f;
    }

    public static float easeInOutBounce(float x) {
        return x < 0.5f ? (1 - easeOutBounce(1 - 2 * x)) / 2 : (1 + easeOutBounce(2 * x - 1)) / 2;
    }
}