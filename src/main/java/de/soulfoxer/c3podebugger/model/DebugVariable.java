package de.soulfoxer.c3podebugger.model;

public record DebugVariable(String name, String value, String type, boolean isGlobal) {}