package edu.gxu.handler;


import edu.gxu.common.TokenType;
import edu.gxu.sbhrw.Util;
import edu.gxu.sbhrw.Text;
import edu.gxu.sbhrw.Token;

public class KeywordHandle implements LexicalHandle {
    Text text;
    StringBuilder matches;

    @Override
    public void initHandle(Text text) {
        this.text = new Text(text);
        matches = new StringBuilder();
    }

    @Override
    public void fillMatches() {
        char ch;
        while (text.canGetNextChar()) {
            ch = text.getNextChar();
            if (Util.isEndChar(ch)) {
                text.backIndex();
                break;
            }
            if (Util.isAlphaOrUnderline(ch) || Util.isDigit(ch)) {
                text.backIndex();
                break;
            }
            matches.append(ch);
        }
    }

    @Override
    public boolean TestFirstChar() {
        if (text.canGetNextChar()) {
            char ch = text.getNextChar();
            return Util.isAlphaOrUnderline(ch);
        }
        return false;
    }

    @Override
    public boolean canGetToken() {
        return Util.isKeyword(matches.toString());
    }

    @Override
    public Token getToken() {
        return new Token(matches.toString(), TokenType.Keyword.getType(), Util.getKeywordCode(matches), text.getLineNumber());
    }
}
