package edu.gxu.yanshou;

import edu.gxu.common.CategoryCodeEnum;
import edu.gxu.common.TokenType;

import javax.swing.*;
import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    boolean isInt36Area;
    boolean isInt36AreaDefined;
    public Map<String, Integer> int36Map = new HashMap<>();
    int int36code = 0;

    public void addInt36Map(String str) {
        if (int36Map.containsKey(str)) {//有过就不加
            return;
        } else {
            int36Map.put(str, int36code);
            int36code++;
        }
    }

    public boolean isInt36Identifier(String str) {
        if (int36Map.containsKey(str)) {
            return true;
        } else
            return false;
    }

    public void init() {
        isInt36Area = false;
        isInt36AreaDefined = false;
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
     *
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
     *
     * @return 是否可以获取下一行字符
     */
    public boolean canGetNextLine() {
        return lineNumber + 1 < segmentTextList.length;
    }

    public boolean isEndChar(char ch) {
        return index == charList.length - 1;
    }

    /**
     * 获取下一行字符，并重置index为-1，index = -1, lineNumber += 1
     */
    //int36 a=0;a=a+10;
    //z=z+10
    public void getNextLine() {
        index = -1;
        lineNumber += 1;
        charList = segmentTextList[lineNumber].toCharArray();
    }

    /**
     * 将matches加入tokenList中
     *
     * @param matches      已经匹配的序列
     * @param type         token类型
     * @param categoryCode 种别码
     */
    public void addToken(StringBuilder matches, String type, Integer categoryCode) {
        tokenList.add(new Token(matches.toString(), type, categoryCode, lineNumber + 1));
    }

    public void handleLine() {
        while (canGetNextChar()) {
            char ch = getNextChar();
            StringBuilder matches = new StringBuilder();
            // 小写字母或者下划线开头，关键字或者标识符
            if (Util.isLowerAlphaOrUnderline(ch)) {
                matches.append(ch);
                // 一直读取直到不是小写字母或者下划线或者数字为止
                while (canGetNextChar()) {
                    ch = getNextChar();
//                    if (isEndChar(ch)) {
//                        backIndex();
//                        break;
//                    }
                    if (!Util.isLowerAlphaOrUnderline(ch) && !Util.isDigit(ch)) {
                        backIndex();
                        break;
                    }
                    matches.append(ch);
                }
                // 判断是关键字还是标识符
                if (Util.isKeyword(matches.toString())) {
                    addToken(matches, TokenType.Keyword.getType(), Util.getKeywordCode(matches));
                    if (matches.toString().equals("int36")) {
                        System.out.println("int36:" + matches);
                        isInt36Area = true;
                        isInt36AreaDefined = true;
                    }

                } else {
                    if (isInt36AreaDefined) {
                        addInt36Map(matches.toString());
                    }
                    if (isInt36Identifier(matches.toString())) {
                        isInt36Area = true;
                    }
                    addToken(matches, TokenType.Identifier.getType(), 0);
                }
                continue;
            }
            // 大写字母开头的int36常量
            if (Util.isBigAlpha(ch)) {
                matches.append(ch);
                // 一直读取直到不是小写字母或者数字为止
                while (canGetNextChar()) {
                    ch = getNextChar();
                    if (!Util.isBigAlpha(ch) && !Util.isDigit(ch)) {
                        backIndex();
                        break;
                    }
                    matches.append(ch);
                }
                addToken(matches, TokenType.Int36Const.getType(), 0);
                continue;
            }
            // 数字开头，常量
            if (Util.isDigit(ch)) {
                int state = 1;
                int constCode = 0;
                String type = TokenType.NumberConst.getType();
                // 一直读取直到与数字无关的字符
//                while ((!isEndChar(ch)) && (Util.isDigit(ch) || Util.isDigitChar(ch))) {
                while ((Util.isDigit(ch) || Util.isBigAlpha(ch))) {
                    // 如果不是整数则改变数字类型
                    if (ch == '.') {
                        constCode = CategoryCodeEnum.FloatConst.getCode();
                    }
                    if (Util.isBigAlpha(ch) || isInt36Area) {//含有大写字母，是int36类型。
                        constCode = CategoryCodeEnum.Int36Const.getCode();
                        type = TokenType.Int36Const.getType();
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
//                    if (Util.isBigAlpha(ch)) {//含有大写字母，是int36类型。
//                        constCode = CategoryCodeEnum.Int36Const.getCode();
//                        type =TokenType.Int36Const.getType();
//
//                    }
                }
                if (isInt36Area) {
                    type = TokenType.Int36Const.getType();
                }
                // 判断是否为非终态
                boolean isError = state == 3 || state == 5 || state == 6 || state == 8;
                if (isError) {
                    break;
                }
                backIndex();
                addToken(matches, type, constCode);
                continue;
            }
            // '开头，字符
            if (ch == '\'') {
                int state = 2;
                matches.append(ch);
                while (state != 5 && state != 6) {
                    if (canGetNextChar()) {
                        ch = getNextChar();
                        matches.append(ch);
                        state = Util.getNextCharState(state, ch);
                    } else
                        break;
                }
                if (state == 5) {
                    addToken(matches, TokenType.CharConst.getType(), CategoryCodeEnum.CharConst.getCode());
                } else {
                    addToken(matches, TokenType.Error.getType(), CategoryCodeEnum.Error.getCode());
                }
                continue;
            }
            // "开头，字符串
            if (ch == '\"') {
                int state = 2;
                matches.append(ch);
                boolean isError = false;
                // 一直读取到下一个 '\"'为止
                while (state != 5 && canGetNextChar()) {
                    ch = getNextChar();
//                    if (isEndChar(ch)) {
//                        isError = true;
//                        break;
//                    }
                    int nextState = Util.getNextStringState(state, ch);
                    // 可能'\\'需要特判，不太清楚。
                    // 经过验证，发现不需要特判
                    if (state == 4) {
                        //matches.append('\\');
                    }
                    if (nextState == 6) {
                        isError = true;
                        backIndex();
                        break;
                    }
                    matches.append(ch);
                    state = nextState;
                }
                if (state == 5) {
                    addToken(matches, TokenType.StringConst.getType(), CategoryCodeEnum.StringConst.getCode());
                } else {
                    addToken(matches, TokenType.Error.getType(), CategoryCodeEnum.Error.getCode());
                }
                continue;
            }
            // /开头，注释或者操作符/=
            if (ch == '/') {
                matches.append(ch);
                if (!canGetNextChar()) {
                    addToken(matches, TokenType.Operator.getType(), Util.getOperatorCode(matches.toString()));
                } else {
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
                                        state = 3;
                                        getNextLine();
                                        continue;
                                    }
                                    addToken(matches, TokenType.Error.getType(), CategoryCodeEnum.Error.getCode());
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
                }

                continue;
            }
            // 操作符开头，操作符长度最多为3，只需要判断当前字符和下两个字符
            if (Util.isOperator(String.valueOf(ch))) {
                String optTest3 = "", optTest2 = "";
                if ((index + 2) < charList.length)
                    optTest3 = "" + ch + charList[index + 1] + charList[index + 2];
                if ((index + 1) < charList.length)
                    optTest2 = "" + charList[index] + charList[index + 1];
                if (index + 2 < charList.length && Util.isOperator(String.valueOf(optTest3))) {//还有三个字符，看这三个是否能构成操作符。
                    index += 2;
                    matches.append(optTest3);
                } else if (index + 1 < charList.length && Util.isOperator(String.valueOf(optTest2))) {//还有三个字符，看这三个是否能构成操作符。
                    index += 1;
                    matches.append(optTest2);
                } else {
                    matches.append(ch);
                }
                addToken(matches, TokenType.Operator.getType(), Util.getOperatorCode(matches.toString()));
                continue;
            }
            /*if (Util.isOperator(String.valueOf(ch))) {
                matches.append(ch);
                // 先判断是否可以接self，再判断一个字符是否和self相同
                if (Util.canFollowSelf(ch)&&ch!='=') {//只有ch!="="才能进去，ch=='='将由下一个if处理。
                    //// 否则===会进canFollowSelf和canFollowEquals的if语句里面。可能会被判为操作符，。
                    char self = ch;
                    if (canGetNextChar()) {
                        ch = getNextChar();
                        if (ch == self) {
                            matches.append(ch);
                        } else {
                            backIndex();
                        }
                    }
                }
                // 先判断是否可以接等号，在判断下一个字符是否为等号
                if (Util.canFollowEquals(ch)) {
                    if (canGetNextChar()) {
                        ch = getNextChar();
                        if (ch == '=') {
                            matches.append(ch);
                        } else {
                            backIndex();
                        }
                    }
                }
                addToken(matches, TokenType.Operator.getType(), Util.getOperatorCode(matches.toString()));
                continue;
            }*/
            // 判断是否为界符，界符长度为1，可以直接判断
            if (Util.isDelimiter(String.valueOf(ch))) {
                matches.append(ch);
                addToken(matches, TokenType.Delimiter.getType(), Util.getDelimiterCode(matches.toString()));
                if (matches.toString().equals(";")) {
                    System.out.println("1" + matches);
                    isInt36Area = false;
                    isInt36AreaDefined = false;
                }
                continue;
            }
            // 剩下的是非法情况
            else {
                if (!Util.isOtherChar(ch)) {
                    matches.append(ch);
                    addToken(matches, TokenType.Error.getType(), CategoryCodeEnum.Error.getCode());
                    continue;
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
        //System.out.println(tokenList.toString());
        return tokenList;
    }
}
