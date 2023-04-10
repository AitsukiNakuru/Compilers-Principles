package edu.gxu.common;

public enum TokenTypeEnum {
    Keyword("Keyword"),
    Operator("Operator"),
    Identifier("IdentifierHandle"),
    NumberConst("NumberConst"),
    StringConst("StringConst"),
    CharConst("CharConst"),
    Comment("Comment"),
    Delimiter("Delimiter"),
    Error("Error"),
    ;
    private final String type;
    TokenTypeEnum(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
}

