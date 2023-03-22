package edu.gxu.common;

public enum TokenType {
    Keyword("Keyword"),
    Operator("Operator"),
    Identifier("Identifier"),
    NumberConst("NumberConst"),
    StringConst("StringConst"),
    CharConst("CharConst"),
    Comment("Comment"),
    Delimiter("Delimiter"),
    ;
    private final String type;
    TokenType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
}

