package edu.gxu.my;

import java.util.*;

import edu.gxu.common.CategoryCodeEnum;
import edu.gxu.common.TokenType;

public class Analysis {
    String originText;  // 读入的测试样例文本
    ArrayList<Token> tokenList = new ArrayList<>();


    public Analysis(String originText) {
        this.originText = originText;
    }

    String[] segmentTextList;
    /**
     * 当前行号
     */
    int lineNumber;
    /**
     * 当前行的字符表
     */
    char[] charList;
    /**
     * 下标，用于读取字符表charList
     */
    int index;

    /**
     * 读取字符前的初始化，按换行分割原始文本
     */
    public void init() {
        lineNumber = -1;
        index = -1;
        tokenList.clear();
        segmentTextList = originText.split("\n");
    }

    /**
     * 是否可以获取当前行的下一个字符
     *
     * @return 是否可以获取当前行的下一个字符
     */
    public boolean canGetNextChar() {
        return index + 1 < charList.length;
    }

    /**
     * 获取当前行的下一个字符
     * @return 下一个字符
     */
    public char getNextChar() {
        index += 1;
        return charList[index];
    }

    /**
     * 回退index，index -= 1;
     */
    public void backIndex() {
        index -= 1;
    }

    /**
     * 是否可以获取下一行字符
     * @return 是否可以获取下一行字符
     */
    public boolean canGetNextLine() {
        return lineNumber+1 < segmentTextList.length;
    }

    /**
     * 获取下一行字符，并重置index为-1，index = -1, lineNumber += 1
     */
    public void getNextLine() {
        index = -1;
        lineNumber += 1;
        charList = segmentTextList[lineNumber].toCharArray();
    }

    /**
     * 将matches加入tokenList中
     * @param matches 已经匹配的序列
     * @param type token类型
     * @param categoryCode 种别码
     */
    public void addToken(StringBuilder matches, String type, Integer categoryCode) {
        tokenList.add(new Token(matches.toString(), type, categoryCode, lineNumber));
    }

    public void handleLine() {
        while (canGetNextChar()) {
            char ch = getNextChar();
            StringBuilder matches = new StringBuilder();
            // 字母或者下划线开头，关键字或者标识符
            if (Util.isAlphaOrUnderline(ch)) {
                matches.append(ch);
                // 一直读取直到不是字母或者下划线或者数字为止
                while (canGetNextChar()) {
                    ch = getNextChar();
                    if (Util.isEndChar(ch)) {
                        backIndex();
                        break;
                    }
                    if (!Util.isAlphaOrUnderline(ch) && !Util.isDigit(ch)) {
                        backIndex();
                        break;
                    }
                    matches.append(ch);
                }
                // 判断是关键字还是界符
                if (Util.isKeyword(matches.toString())) {
                    addToken(matches, TokenType.Keyword.getType(), Util.getKeywordCode(matches));
                } else {
                    addToken(matches, TokenType.Identifier.getType(), 0);
                }
                continue;
            }
            // 数字开头，数字常量
            if (Util.isDigit(ch)) {
                int state = 1;
                int k;
                int constCode = 0;
                // 一直读取直到与数字无关的字符
                while ((!Util.isEndChar(ch)) && (Util.isDigit(ch) || Util.isDigitChar(ch))) {
                    // 如果不是整数则改变数字类型
                    if (ch == '.'){
                        constCode = CategoryCodeEnum.FloatConst.getCode();
                    }
                    if (ch == 'e' || ch == 'E') {
                        constCode = CategoryCodeEnum.ScientificConst.getCode();
                    }

                    int nextState = Util.getNextDigitState(state, ch);
                    state = nextState;
                    matches.append(ch);
                    if (nextState == 8) {
                        backIndex();
                        break;
                    }
                    if (canGetNextChar()) {
                        ch = getNextChar();
                    } else {
                        break;
                    }
                }
                // 判断是否为终态
                boolean isError = state == 3 || state == 5 || state == 6 || state == 8;
                if (isError) {
                    break;
                }
                addToken(matches, TokenType.NumberConst.getType(), constCode);
                continue;
            }
            // '\''开头，字符
            if (ch == '\'') {
                int state = 2;
                while (state != 5 && state != 6) {
                    if (canGetNextChar()) {
                        ch = getNextChar();
                        matches.append(ch);
                    }
                    state = Util.getNextCharState(state, ch);
                }
                if (state == 5) {
                    addToken(matches, TokenType.CharConst.getType(), CategoryCodeEnum.CharConst.getCode());
                }
            }
            // '\"'开头，字符串
            if (ch == '\"') {
                int state = 2;
                matches.append(ch);
                boolean isError = false;
                // 一直读取到下一个 '\"'为止
                while (state != 5 && canGetNextChar()) {
                    ch = getNextChar();
                    if (Util.isEndChar(ch)) {
                        isError = true;
                        break;
                    }
                    int nextState = Util.getNextStringState(state, ch);
                    // 可能'\\'需要特判，不太清楚
                    if (state == 4) {
                        matches.append('\\');
                    }
                    if (nextState == 6) {
                        isError = true;
                        backIndex();
                        break;
                    }
                    matches.append(ch);
                    state = nextState;
                }
                if (!isError) {
                    addToken(matches, TokenType.StringConst.getType(), CategoryCodeEnum.StringConst.getCode());
                }
                continue;
            }
            // '/'开头，注释或者操作符/=
            if (ch == '/') {
                matches.append(ch);
                ch = getNextChar();
                int state = 3;
                switch (ch) {
                    // 多行注释
                    case '*' -> {
                        matches.append(ch);
                        while (state != 5) {
                            // 如果当前行已经结束，添加回车，读取下一行
                            if (!canGetNextChar()) {
                                matches.append('\n');
                                if (canGetNextLine()) {
                                    getNextLine();
                                    continue;
                                }
                                break;
                            }
                            ch = getNextChar();
                            matches.append(ch);
                            state = Util.getNextCommentState(state, ch);
                        }
                    }
                    // 单行注释
                    case '/' -> {
                        matches.append(ch);
                        // 直接读完当前行
                        while (canGetNextChar()) {
                            ch = getNextChar();
                            matches.append(ch);
                        }
                        addToken(matches, TokenType.Comment.getType(), CategoryCodeEnum.Comment.getCode());
                        continue;
                    }
                    // 操作符/=
                    default -> {
                        if (ch == '=') {
                            matches.append(ch);
                        } else {
                            backIndex();
                        }
                        addToken(matches, TokenType.Operator.getType(), Util.getOperatorCode(matches.toString()));
                        continue;
                    }
                }
                if (state != 5) {
                    break;
                }
                addToken(matches, TokenType.Comment.getType(), CategoryCodeEnum.Comment.getCode());
                continue;
            }
            // 操作符开头，操作符长度最多为3，只需要判断当前字符和下两个字符
            if (Util.isOperator(String.valueOf(ch))) {
                matches.append(ch);
                // 先判断是否可以接self，再判断一个字符是否和self相同
                if (Util.canFollowSelf(ch)) {
                    char self = ch;
                    if (canGetNextChar()) {
                        ch = getNextChar();
                    }
                    if (ch == self) {
                        matches.append(ch);
                    } else {
                        backIndex();
                    }
                }
                // 先判断是否可以接等号，在判断下一个字符是否为等号
                if (Util.canFollowEquals(ch)) {
                    if (canGetNextLine()) {
                        ch = getNextChar();
                    }
                    if (ch == '=') {
                        matches.append(ch);
                    } else {
                        backIndex();
                    }
                }
                addToken(matches, TokenType.Operator.getType(), Util.getOperatorCode(matches.toString()));
                continue;
            }
            // 判断是否为界符，界符长度为1，可以直接判断
            if (Util.isDelimiter(String.valueOf(ch))) {
                matches.append(ch);
                addToken(matches, TokenType.Delimiter.getType(), Util.getDelimiterCode(matches.toString()));
                continue;
            }
            // 剩下的是非法情况，不想写
            else {
                if (!Util.isOtherChar(ch)) {

                }
            }
        }
    }

    public List<Token> analyze() {
        init();
        while (canGetNextLine()) {
            getNextLine();
            handleLine();
        }
        System.out.println(tokenList.toString());
        return tokenList;
    }
}
