package edu.gxu;

import edu.gxu.grammar.AnalyzeTable;
import edu.gxu.grammar.GrammarAnalysis;
import edu.gxu.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import edu.gxu.lexical.Token;
public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //new Gui();
        String[] str;
        str= "id := id".split(" ");// Test-All-4
        str = "( ( a ) )".split(" ");// Test-All-5
        str = "int id = int".split(" ");// Test-All-6
        str = "int identifier ( ) { int36 identifier = const , identifier = const , identifier = const ; for ( int36 identifier = const ; identifier < const ; identifier ++ ) { identifier = identifier ++ ; for ( int36 identifier = const ; identifier < const ; identifier ++ ) { identifier = identifier ++ ; } } }".split(" ");

        ArrayList<Token> tokenList = new ArrayList<>();
        for (int i = 0; i<str.length; i++) {
            tokenList.add(i, new Token(str[i], "type", 1, 1));
        }
        GrammarAnalysis grammarAnalysis = new GrammarAnalysis(tokenList);
        grammarAnalysis.analysis();

    }
}