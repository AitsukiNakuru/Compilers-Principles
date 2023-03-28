package edu.gxu.lyc;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

public class Util {
    /**
     * 关键字表
     */
    public static String[] keywordList = {
            "auto", "double", "int", "struct", "break",
            "else", "long", "switch", "case", "enum",
            "register", "typedef", "char", "extern", "return",
            "union", "const", "float", "short", "unsigned",
            "continue", "for", "signed", "void", "default",
            "goto", "sizeof", "volatile", "do", "if",
            "while", "static", "String"
    };
    /**
     * 关键字种别码 1xx
     */
    public static Map<String, Integer> keywordCodeMap = new HashMap<>() {
        @Serial
        private static final long serialVersionUID = 1L;

        {
            for (int i = 0; i < keywordList.length; i++) {
                put(keywordList[i], i + 101);
            }
        }
    };

    public static Integer getKeywordCode(StringBuilder matches) {
        return keywordCodeMap.get(matches.toString());
    }

    /**
     * 是否为关键字
     *
     * @param s 需要判断的字符串
     * @return 是否为关键字
     */
    public static boolean isKeyword(String s) {
        return keywordCodeMap.containsKey(s);
    }

    /**
     * 运算符表
     */
    public static String[] operatorList = {
            "=", "+", "-", "*", "/", "%",
            "++", "--",
            "<", "<=", ">", ">=", "==", "!=",
            "&&", "||", "!", "~", "&", "|", "^", ">>", "<<",
            "+=", "-=", "*=", "/=", "%=", "&=", " ^=", "|=", "<<=", ">>="
    };
    /**
     * 运算符种别码 2xx
     */
    public static Map<String, Integer> operatorCodeMap = new HashMap<>() {
        @Serial
        private static final long serialVersionUID = 1L;

        {
            for (int i = 0; i < operatorList.length; i++) {
                put(operatorList[i], i + 201);
            }
        }
    };

    public static int getOperatorCode(String s) {
        return operatorCodeMap.get(s);
    }

    /**
     * 是否为运算符
     *
     * @param s 需要判断的字符串
     * @return 是否为运算符
     */
    public static boolean isOperator(String s) {
        return operatorCodeMap.containsKey(s);
    }

    /**
     * 获取符号名
     *
     * @param s 需要判断的字符串
     * @return 符号名
     */
    public static String getName(String s) {
        return switch (s) {
            case "," -> "Comma";
            case ";" -> "Semicolon";
            case "[" -> "LeftSquareBracket";
            case "]" -> "RightSquareBracket";
            case "{" -> "LeftCurlyBracket";
            case "}" -> "RightCurlyBracket";
            case "(" -> "LeftParentheses";
            case ")" -> "RightParentheses";
            case "=" -> "Assign";
            default -> "Error";
        };


    }

    /**
     * 界符表
     */
    public static String[] delimiterList = {
            ",", ";", "[", "]", "{", "}", "(", ")"
    };
    /**
     * 界符种别码 3xx
     */
    public static Map<String, Integer> delimiterCodeMap = new HashMap<>() {
        @Serial
        private static final long serialVersionUID = 1L;

        {
            for (int i = 0; i < delimiterList.length; i++) {
                put(delimiterList[i], i + 301);
            }
        }
    };

    /**
     * 获取界符种别码
     * @param s 需要判断的字符串
     * @return 界符种别码
     */
    public static int getDelimiterCode(String s) {
        return delimiterCodeMap.get(s);
    }

    /**
     * 是否为界符
     *
     * @param s 需要判断的字符串
     * @return 是否为界符
     */
    public static boolean isDelimiter(String s) {
        return delimiterCodeMap.containsKey(s);
    }

    /**
     * 是否可以跟随 "="
     *
     * @param ch 需要判断的字符
     * @return 是否可以跟随 "="
     */
    public static boolean canFollowEquals(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '=' || ch == '>'
                || ch == '<' || ch == '&' || ch == '|' || ch == '^' || ch == '%' || ch == '!';
    }

    /**
     * 是否可以跟随一个与自己相同的字符
     *
     * @param ch 需要判断的字符
     * @return 是否可以跟随一个与自己相同的字符
     */
    public static boolean canFollowSelf(char ch) {
        return ch == '+' || ch == '-' || ch == '&' || ch == '|' || ch == '>' || ch == '<' || ch == '=';
    }

    /**
     * 是否为结束字符
     *
     * @param ch 需要判断的字符
     * @return 是否为结束字符
     */
    public static boolean isAlpha(char ch) {
        return ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') );
    }
    /**
     * 是否为字母或下划线
     *
     * @param ch 需要判断的字符
     * @return 是否为字母或下划线
     */
    public static boolean isAlphaOrUnderline(char ch) {
        return ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_');
    }

    /**
     * 是否为数字
     *
     * @param ch 需要判断的字符 需要判断的字符
     * @return 是否为数字
     */
    public static boolean isDigit(char ch) {
        return (ch >= '0' && ch <= '9');
    }

    /**
     * 是否为可以在数字阶段输入的符号('.', 'e', 'E', '-', '+')
     * @param ch 需要判断的字符 需要判断的字符
     * @return 是否为可以在数字阶段输入的符号
     */
    public static boolean isDigitChar(char ch) {
        return ch == '.' || ch == 'e' || ch == '-' || ch == 'E' || ch == '+';
    }

    /**
     * 是否为其他字符('\t', '\0', '\n', '\r')
     * @param ch 需要判断的字符
     * @return 是否为其他字符
     */
    public static boolean isOtherChar(char ch) {
        return ch == '\t' || ch == '\0' || ch == '\n' || ch == '\r'||ch==' ';
    }

    /**
     * 获取下一个字符串DFA状态
     * @param state 当前状态
     * @param ch 当前字符
     * @return 下一个状态
     */
    public static int getNextStringState(int state, char ch) {
        switch (state) {
            case 1-> {

            }
            case 2-> {
                if (ch == '\\') {
                    return 4;
                }
                if (ch != '\"') {
                    return 3;
                }
            }
            case 3-> {
                if (ch == '\\') {
                    return 4;
                }
                if (ch != '\"') {
                    return 3;
                }
                return 5;
            }
            case 4-> {
                return 3;
            }
        }
        return 6;
    }

    /**
     * 获取下一个字符DFA状态
     * @param state 当前状态
     * @param ch 当前字符
     * @return 下一个状态
     */
    public static int getNextCharState(int state, char ch) {
        switch (state) {
            case 1-> {
                if (ch == '\'') {
                    return 2;
                }
            }
            case 2-> {
                if (ch == '\\') {
                    return 4;
                }
                if (ch != '\'') {//使用排除法，既不是',也不是\,说明是任意其他字符（包括汉字）。
                    return 3;
                }
            }
            case 3-> {
                if (ch == '\'') {
                    return 5;
                }
            }
            case 4-> {
                if(isAlpha(ch)){
                    return 3;
                }
                else {
                    return 6;
                }
            }
        }
        return 6;
    }

    /**
     * 是否为转义字符
     *
     * @param ch 需要判断的字符
     * @return 是否为转义字符
     */
    public static boolean isEscapeCharacter(char ch) {
        return ch == 'a' || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r'
                || ch == 't' || ch == 'v' || ch == '?' || ch == '0';
    }

    /**
     * 获取下一个数字DFA状态
     * @param state 当前状态
     * @param ch 当前字符
     * @return 下一个状态
     */
    public static int getNextDigitState(int state, char ch) {
        switch (state) {
            case 1-> {
                if (Util.isDigit(ch)) {
                    return 2;
                }
            }
            case 2-> {
                if (Util.isDigit(ch)) {
                    return 2;
                }
                if (ch == 'e' || ch == 'E') {
                    return 5;
                }
                if (ch == '.') {
                    return 3;
                }
            }
            case 3-> {
                if (Util.isDigit(ch)) {
                    return 4;
                }
            }
            case 4-> {
                if (Util.isDigit(ch)) {
                    return 4;
                }
                if (ch == 'e' || ch == 'E') {
                    return 5;
                }
            }
            case 5-> {
                if (ch == '+' || ch == '-') {
                    return 6;
                }
                if (Util.isDigit(ch)) {
                    return 7;
                }
            }
            case 6-> {
                if (Util.isDigit(ch)) {
                    return 7;
                }
            }
            case 7-> {
                if (Util.isDigit(ch)) {
                    return 7;
                }
            }
        }
        return 8;
    }


    /**
     * 获取下一个字符DFA状态
     * @param state 当前状态
     * @param ch 当前字符
     * @return 下一个状态
     */
    public static int getNextCommentState(int state, char ch) {
        switch (state) {
            case 1-> {
                if (ch == '/') {
                    return 2;
                }
            }
            case 2-> {
                if (ch == '*') {
                    return 3;
                }
            }
            case 3-> {
                if (ch == '*') {
                    return 4;
                }
                if (ch!= '*') {
                    return 3;
                }
            }
            case 4-> {
                if (ch == '*') {
                    return 4;
                }
                if (ch == '/') {
                    return 5;
                }
                if (ch != '*') {
                    return 3;
                }
            }
            default-> {
                return 6;
            }
        }
        return 6;
    }
}