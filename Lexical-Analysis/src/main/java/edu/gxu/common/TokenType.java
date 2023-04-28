package edu.gxu.common;

public enum TokenType {
    Keyword("Keyword"),
    Operator("Operator"),
    Identifier("IdentifierHandle"),
    NumberConst("NumberConst"),
    Int36Const("Int36Const"),
    StringConst("StringConst"),
    CharConst("CharConst"),
    Comment("Comment"),
    Delimiter("Delimiter"),
    Error("Error"),
    ;
    private final String type;
    TokenType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
}

