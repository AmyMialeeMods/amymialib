package dev.amymialee.amymialib.modules.scribbler.scribbles;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface EarlyScribble extends AutoScribbler {
    void before();
}