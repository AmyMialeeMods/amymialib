#ifndef AMY_MIA_NOISE_GLSL
#define AMY_MIA_NOISE_GLSL
#include <amymialib:hash.glsl>
//----------------------------------------------------------------------------------------//

/*
    1D, 2D & 3D Value Noise
    https://www.shadertoy.com/view/4dS3Wd
    BSD License
*/
float valueNoise(float x) {
    float i = floor(x);
    float f = fract(x); f = f * f * (3.0 - 2.0 * f);
    return mix(hash11(i), hash11(i + 1.0), f);
}

float valueNoise(vec2 x) {
    vec2 i = floor(x);
    float a = hash12(i);
    float b = hash12(i + vec2(1.0, 0.0));
    float c = hash12(i + vec2(0.0, 1.0));
    float d = hash12(i + vec2(1.0, 1.0));
    vec2 f = fract(x); f = f * f * (3.0 - 2.0 * f);
    return mix(a, b, f.x) + (c - a) * f.y * (1.0 - f.x) + (d - b) * f.x * f.y;
}

float valueNoise(vec3 x) {
    const vec3 step = vec3(110, 241, 171);
    float n = dot(floor(x), step);
    vec3 f = fract(x); f = f * f * (3. - 2. * f);
    return mix(mix(
            mix(hash11(n + dot(step, vec3(0, 0, 0))), hash11(n + dot(step, vec3(1, 0, 0))), f.x),
            mix(hash11(n + dot(step, vec3(0, 1, 0))), hash11(n + dot(step, vec3(1, 1, 0))), f.x), f.y), mix(
            mix(hash11(n + dot(step, vec3(0, 0, 1))), hash11(n + dot(step, vec3(1, 0, 1))), f.x),
            mix(hash11(n + dot(step, vec3(0, 1, 1))), hash11(n + dot(step, vec3(1, 1, 1))), f.x), f.y), f.z);
}

float fractBrownianMotion(float x) {
    float v = 0.0;
    float a = 0.5;
    float shift = float(100);
    for (int i = 0; i < 4; ++i) {
        v += a * valueNoise(x);
        x = x * 2.0 + shift;
        a *= 0.5;
    }
    return v;
}

float fractBrownianMotion(vec2 x) {
    float v = 0.0;
    float a = 0.5;
    vec2 shift = vec2(100);
    mat2 rot = mat2(cos(0.5), sin(0.5), -sin(0.5), cos(0.50));
    for (int i = 0; i < 4; ++i) {
        v += a * valueNoise(x);
        x = rot * x * 2.0 + shift;
        a *= 0.5;
    }
    return v;
}

float fractBrownianMotion(vec3 x) {
    float v = 0.0;
    float a = 0.5;
    vec3 shift = vec3(100);
    for (int i = 0; i < 4; ++i) {
        v += a * valueNoise(x);
        x = x * 2.0 + shift;
        a *= 0.5;
    }
    return v;
}

//----------------------------------------------------------------------------------------//
#endif