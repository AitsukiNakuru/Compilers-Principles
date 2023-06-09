package edu.gxu.common;

public enum LREnum {
    Dot("·"),
    Epsilon("ε"),
    Arrow("->"),
    Sharp("#"),
    Error("--"),
    Accept("ACCEPT"),
    StartChar("S'"),
    ;
    private final String str;
    LREnum(String str) {
        this.str = str;
    }
    public String getString() {
        return str;
    }
}
