package dev.amymialee.amymialib.util.datagentasks;

public interface AMNameProvider {
    default String getName() {
        return "AMNameProvider/Default";
    }
}