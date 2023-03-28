package edu.gxu.handler;

import edu.gxu.sbhrw.Text;
import edu.gxu.sbhrw.Token;

public class CharHandle implements LexicalHandle{
    @Override
    public void initHandle(Text text) {

    }

    @Override
    public void fillMatches() {

    }

    @Override
    public boolean TestFirstChar() {
        return false;
    }

    @Override
    public boolean canGetToken() {
        return false;
    }

    @Override
    public Token getToken() {
        return null;
    }
}
