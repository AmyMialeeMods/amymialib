#ifndef AMY_MIA_UTIL_GLSL
#define AMY_MIA_UTIL_GLSL
//----------------------------------------------------------------------------------------//

vec3 desaturate(vec3 colour, float reduceByPercent) {
    vec3 luminanceFinder = vec3(0.2126, 0.7152, 0.0722);
    float luminance = dot(colour, luminanceFinder);
    return mix(colour, vec3(luminance), reduceByPercent);
}

vec3 contrast(vec3 colour, float contrast) {
    return clamp((colour - contrast) * contrast + contrast, 0.0, 1.0);
}

//----------------------------------------------------------------------------------------//
#endif