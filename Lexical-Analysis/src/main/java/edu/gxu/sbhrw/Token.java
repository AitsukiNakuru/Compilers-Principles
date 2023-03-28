package edu.gxu.sbhrw;

public class Token {
    String value;
    String type;
    Integer categoryCode;
    Integer lineNumber;

    @Override
    public String toString() {
        return "Token{" +
                "value='" + value + '\'' +
                ", type='" + type + '\'' +
                ", categoryCode=" + categoryCode +
                ", lineNumber=" + lineNumber +
                '}' + '\n';
    }

    public Token(String value, String type, Integer categoryCode, Integer lineNumber) {
        this.value = value;
        this.type = type;
        this.categoryCode = categoryCode;
        this.lineNumber = lineNumber;

    }
}
