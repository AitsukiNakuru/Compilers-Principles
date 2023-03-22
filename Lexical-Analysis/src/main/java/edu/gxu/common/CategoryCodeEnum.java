package edu.gxu.common;

public enum CategoryCodeEnum {
    IntegerConst(1),
    FloatConst(2),
    ScientificConst(3),
    StringConst(4),
    CharConst(5),

    Comment(6),
    ;



    private final int code;
    CategoryCodeEnum(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }
}
