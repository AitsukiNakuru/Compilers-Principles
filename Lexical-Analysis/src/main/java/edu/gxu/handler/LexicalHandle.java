package edu.gxu.handler;

import edu.gxu.sbhrw.Text;
import edu.gxu.sbhrw.Token;

public interface LexicalHandle {
    void initHandle(Text text);
    void fillMatches();
    boolean TestFirstChar();
    boolean canGetToken();
    Token getToken();
}
