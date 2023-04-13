package edu.gxu;

import edu.gxu.grammar.AnalyzeTable;
import edu.gxu.grammar.GrammarAnalysis;
import edu.gxu.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String[] str = FileUtil.readTxtFile("Test-First-1/Input.txt");
        ArrayList<String> input = new ArrayList<>(Arrays.asList(str[0].split("")));
        GrammarAnalysis grammarAnalysis = new GrammarAnalysis(input);
        grammarAnalysis.analysis();
    }
}