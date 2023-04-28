package edu.gxu.common;

import edu.gxu.lexical.Token;

public enum TokenTypeEnum {
    Keyword("Keyword"),
    Operator("Operator"),
    Identifier("Identifier"),
    NumberConst("NumberConst"),
    Int36Const("Int36Const"),
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

    public static TokenTypeEnum getTypeByToken(Token token){
        for(TokenTypeEnum tokenTypeEnum : values()){
            if (tokenTypeEnum.getType().equals(token.type)) {
                //获取指定的枚举
                return tokenTypeEnum;
            }
        }
        return null;
    }
}

