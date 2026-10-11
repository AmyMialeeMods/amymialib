package dev.amymialee.amymialib.modules.easings;

import java.util.function.Function;

public enum EasingD {
    LINEAR(EasingD::easeLinear),
    IN_SINE(EasingD::easeInSine),
    OUT_SINE(EasingD::easeOutSine),
    IN_OUT_SINE(EasingD::easeInOutSine),
    IN_QUAD(EasingD::easeInQuad),
    OUT_QUAD(EasingD::easeOutQuad),
    IN_OUT_QUAD(EasingD::easeInOutQuad),
    IN_CUBIC(EasingD::easeInCubic),
    OUT_CUBIC(EasingD::easeOutCubic),
    IN_OUT_CUBIC(EasingD::easeInOutCubic),
    IN_QUART(EasingD::easeInQuart),
    OUT_QUART(EasingD::easeOutQuart),
    IN_OUT_QUART(EasingD::easeInOutQuart),
    IN_QUINT(EasingD::easeInQuint),
    OUT_QUINT(EasingD::easeOutQuint),
    IN_OUT_QUINT(EasingD::easeInOutQuint),
    IN_EXPO(EasingD::easeInExpo),
    OUT_EXPO(EasingD::easeOutExpo),
    IN_OUT_EXPO(EasingD::easeInOutExpo),
    IN_CIRC(EasingD::easeInCirc),
    OUT_CIRC(EasingD::easeOutCirc),
    IN_OUT_CIRC(EasingD::easeInOutCirc),
    IN_BACK(EasingD::easeInBack),
    OUT_BACK(EasingD::easeOutBack),
    IN_OUT_BACK(EasingD::easeInOutBack),
    IN_ELASTIC(EasingD::easeInElastic),
    OUT_ELASTIC(EasingD::easeOutElastic),
    IN_OUT_ELASTIC(EasingD::easeInOutElastic),
    IN_BOUNCE(EasingD::easeInBounce),
    OUT_BOUNCE(EasingD::easeOutBounce),
    IN_OUT_BOUNCE(EasingD::easeInOutBounce);
    
    private final Function<Double, Double> func;
    
    EasingD(Function<Double, Double> func) {
        this.func = func;
    }
    
    public double ease(double x) {
        return func.apply(x);
    }

    public double interpolate(double delta, double start, double end) {
        return start + func.apply(delta) * (end - start);
    }

    public static double easeLinear(double x) {
        return x;
    }
    
    public static double easeInSine(double x) {
        return 1 - Math.cos((x * Math.PI) / 2);
    }

    public static double easeOutSine(double x) {
        return Math.sin((x * Math.PI) / 2);
    }

    public static double easeInOutSine(double x) {
        return -(Math.cos(Math.PI * x) - 1) / 2;
    }

    public static double easeInQuad(double x) {
        return x * x;
    }

    public static double easeOutQuad(double x) {
        return 1 - (1 - x) * (1 - x);
    }

    public static double easeInOutQuad(double x) {
        return x < 0.5 ? 2 * x * x : 1 - Math.pow(-2 * x + 2, 2) / 2;
    }

    public static double easeInCubic(double x) {
        return x * x * x;
    }

    public static double easeOutCubic(double x) {
        return 1 - Math.pow(1 - x, 3);
    }

    public static double easeInOutCubic(double x) {
        return x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2;
    }

    public static double easeInQuart(double x) {
        return x * x * x * x;
    }

    public static double easeOutQuart(double x) {
        return 1 - Math.pow(1 - x, 4);
    }

    public static double easeInOutQuart(double x) {
        return x < 0.5 ? 8 * x * x * x * x : 1 - Math.pow(-2 * x + 2, 4) / 2;
    }

    public static double easeInQuint(double x) {
        return x * x * x * x * x;
    }

    public static double easeOutQuint(double x) {
        return 1 - Math.pow(1 - x, 5);
    }

    public static double easeInOutQuint(double x) {
        return x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;
    }

    public static double easeInExpo(double x) {
        return x == 0 ? 0 : Math.pow(2, 10 * x - 10);
    }

    public static double easeOutExpo(double x) {
        return x == 1 ? 1 : 1 - Math.pow(2, -10 * x);
    }

    public static double easeInOutExpo(double x) {
        if (x == 0) return 0;
        if (x == 1) return 1;
        return x < 0.5 ? Math.pow(2, 20 * x - 10) / 2 : (2 - Math.pow(2, -20 * x + 10)) / 2;
    }

    public static double easeInCirc(double x) {
        return 1 - Math.sqrt(1 - Math.pow(x, 2));
    }

    public static double easeOutCirc(double x) {
        return Math.sqrt(1 - Math.pow(x - 1, 2));
    }

    public static double easeInOutCirc(double x) {
        return x < 0.5 ? (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2 : (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2;
    }

    public static double easeInBack(double x) {
        final var c1 = 1.70158;
        return (c1 + 1) * x * x * x - c1 * x * x;
    }

    public static double easeOutBack(double x) {
        final var c1 = 1.70158;
        return 1 + (c1 + 1) * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2);
    }

    public static double easeInOutBack(double x) {
        final var c2 = 2.5949095;
        return x < 0.5 ? (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
                : (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
    }

    public static double easeInElastic(double x) {
        if (x == 0) return 0;
        if (x == 1) return 1;
        return -Math.pow(2, 10 * x - 10) * Math.sin((x * 10 - 10.75) * (2 * Math.PI) / 3);
    }

    public static double easeOutElastic(double x) {
        if (x == 0) return 0;
        if (x == 1) return 1;
        return Math.pow(2, -10 * x) * Math.sin((x * 10 - 0.75) * (2 * Math.PI) / 3) + 1;
    }

    public static double easeInOutElastic(double x) {
        if (x == 0) return 0;
        if (x == 1) return 1;
        var sin = Math.sin((20 * x - 11.125) * (2 * Math.PI) / 4.5);
        return x < 0.5 ? -(Math.pow(2, 20 * x - 10) * sin) / 2 : (Math.pow(2, -20 * x + 10) * sin) / 2 + 1;
    }

    public static double easeInBounce(double x) {
        return 1 - easeOutBounce(1 - x);
    }

    public static double easeOutBounce(double x) {
        final var n1 = 7.5625;
        final var d1 = 2.75;
        if (x < 1 / d1) return n1 * x * x;
        if (x < 2 / d1) return n1 * (x -= 1.5 / d1) * x + 0.75;
        if (x < 2.5 / d1) return n1 * (x -= 2.25 / d1) * x + 0.9375;
        return n1 * (x -= 2.625 / d1) * x + 0.984375;
    }

    public static double easeInOutBounce(double x) {
        return x < 0.5 ? (1 - easeOutBounce(1 - 2 * x)) / 2 : (1 + easeOutBounce(2 * x - 1)) / 2;
    }
}