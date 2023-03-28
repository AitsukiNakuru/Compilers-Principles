package edu.gxu.handler;

import edu.gxu.common.CategoryCodeEnum;
import edu.gxu.common.TokenType;
import edu.gxu.my.Util;
import edu.gxu.sbhrw.Text;
import edu.gxu.sbhrw.Token;

public class DigitHandle implements LexicalHandle{
    Text text;
    StringBuilder matches;
    int state = 1;
    int constCode = 0;

    @Override
    public void initHandle(Text text) {
        this.text = new Text(text);
        matches = new StringBuilder();
    }

    @Override
    public void fillMatches() {
        char ch = text.getNextChar();
        // 一直读取直到与数字无关的字符
        while ((Util.isDigit(ch) || Util.isDigitChar(ch))) {
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
                text.backIndex();
                break;
            }
            if (text.canGetNextChar()) {
                ch = text.getNextChar();
            } else {
                break;
            }
        }
    }

    @Override
    public boolean TestFirstChar() {
        if (text.canGetNextChar()) {
            char ch = text.getNextChar();
            return Util.isDigit(ch);
        }
        return false;
    }

    @Override
    public boolean canGetToken() {
        // 判断是否为终态
        return state == 3 || state == 5 || state == 6 || state == 8;
    }

    @Override
    public Token getToken() {
        return new Token(matches.toString(), TokenType.NumberConst.getType(), CategoryCodeEnum.IntegerConst.getCode(), text.getLineNumber());
    }
}
