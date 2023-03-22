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

            if (Util.isAlphaOrUnderline(ch)) {
                matches.append(ch);
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
                if (Util.isKeyword(matches.toString())) {
                    addToken(matches, TokenType.Keyword.getType(), Util.getKeywordCode(matches));
                } else {
                    addToken(matches, TokenType.Identifier.getType(), 0);
                }
                continue;
            }
            if (Util.isDigit(ch)) {
                int state = 1;
                int k;
                int constCode = 0;
                while ((!Util.isEndChar(ch)) && (Util.isDigit(ch) || Util.isDigitChar(ch))) {
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
                boolean isError = state == 3 || state == 5 || state == 6 || state == 8;
                if (isError) {
                    break;
                }
                addToken(matches, TokenType.NumberConst.getType(), constCode);
                continue;
            }
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
            if (ch == '\"') {
                int state = 2;
                matches.append(ch);
                boolean isError = false;
                while (state != 5 && canGetNextChar()) {
                    ch = getNextChar();
                    if (Util.isEndChar(ch)) {
                        isError = true;
                        break;
                    }
                    int nextState = Util.getNextStringState(state, ch);
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
            if (ch == '/') {
                matches.append(ch);
                ch = getNextChar();
                int state = 3;
                switch (ch) {
                    case '*' -> {
                        matches.append(ch);
                        while (state != 5) {
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
                    case '/' -> {
                        matches.append(ch);
                        while (canGetNextChar()) {
                            ch = getNextChar();
                            matches.append(ch);
                        }
                        addToken(matches, TokenType.Comment.getType(), CategoryCodeEnum.Comment.getCode());
                        continue;
                    }
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
            if (Util.isOperator(String.valueOf(ch))) {
                matches.append(ch);
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
            if (Util.isDelimiter(String.valueOf(ch))) {
                matches.append(ch);
                addToken(matches, TokenType.Delimiter.getType(), Util.getDelimiterCode(matches.toString()));
                continue;
            }
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
