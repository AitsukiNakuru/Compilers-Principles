package edu.gxu.grammar;

import edu.gxu.common.CategoryCodeEnum;
import edu.gxu.common.LREnum;
import edu.gxu.lexical.LexicalAnalysis;
import edu.gxu.lexical.Token;


import java.io.IOException;
import java.util.*;

public class GrammarAnalysis {
    ArrayList<AnalyzeStep> analyzeStepList = new ArrayList<>();
    ArrayDeque<Integer> stateStack = new ArrayDeque<>();
    ArrayDeque<String> charStack = new ArrayDeque<>();
    ArrayDeque<String> inputStack = new ArrayDeque<>();
    LexicalAnalysis lexicalAnalysis;
    AnalyzeTable analyzeTable = new AnalyzeTable();
    Integer index = 0;

    public GrammarAnalysis(ArrayList<Token> inputString) throws IOException, ClassNotFoundException {
        for (Token token : inputString) {
            inputStack.addLast(token.value);
        }
        inputStack.addLast(LREnum.Sharp.getString());
        stateStack.addLast(0);
        charStack.addLast(LREnum.Sharp.getString());
    }

    public boolean analysis() {
        analyzeStepList.add(new AnalyzeStep(
                index++, stackToString(stateStack),
                stackToString(charStack),
                stackToString(inputStack),
                " ", " ", " ", " "));
        System.out.println(analyzeStepList.get(analyzeStepList.size() - 1).toString());
        while (true) {
            String input = inputStack.getFirst();
            Integer state = stateStack.getLast();
            String nextAction = analyzeTable.getNextAction(state, input);
            if (nextAction.equals(LREnum.Error.getString())) {
                return false;
            }
            if (nextAction.equals(LREnum.Accept.getString())) {
                return true;
            }
            if (!handleAction(nextAction)) {
                return false;
            }
            System.out.println(analyzeStepList.get(analyzeStepList.size() - 1).toString());
        }

    }

    public boolean handleAction(String action) {

        /*
          移进
          1. 根据状态栈顶和输入符号获取移进状态
          2. 将移进状态加入状态栈
          3. 将输入符号加入符号栈
         */
        if (action.startsWith("Shift")) {
            AnalyzeStep analyzeStep = new AnalyzeStep(
                index++, stackToString(stateStack),
                stackToString(charStack),
                stackToString(inputStack),
                "Shift", " ", action, " ");
            analyzeStepList.add(analyzeStep);
            // 1. 根据状态栈顶和输入符号获取移进状态
            Integer nextState = Integer.parseInt(action.substring(5));
            // 2.将移进状态加入状态栈
            stateStack.addLast(nextState);
            // 3. 将输入符号加入符号栈
            charStack.addLast(Objects.requireNonNull(inputStack.pollFirst()));
        }
        /*
          归约
          1. 根据状态栈顶与输入符号获取归约产生式
          2. 状态栈弹出与产生式右边长度相等数量的状态
          3. 符号栈弹出与产生式右边长度相等数量的符号
          4. 根据状态栈顶与产生式左边的非终结符获取GoTo状态
          5. 将GoTo状态加入状态栈
          6. 将产生式左边的符号加入符号栈
         */
        if (action.startsWith("Reduce")) {
            AnalyzeStep analyzeStep = new AnalyzeStep(
                index++, stackToString(stateStack),
                stackToString(charStack),
                stackToString(inputStack),
                "Reduce", " ", action, " ");
            // 1. 根据状态栈顶与输入符号获取归约产生式
            int productionIndex = Integer.parseInt(action.substring(6));
            Production production = Util.productionList.get(productionIndex);
            analyzeStep.production = String.valueOf(production);
            // 2. 状态栈弹出与产生式右边长度相等数量的状态
            batchPollLast(stateStack, production.right.size());
            // 3. 符号栈弹出与产生式右边长度相等数量的符号
            batchPollLast(charStack, production.right.size());
            // 4. 根据状态栈顶与产生式左边的非终结符获取GoTo状态
            Integer GoTo = analyzeTable.getGoTo(stateStack.getLast(), production.left);
            analyzeStep.Goto = String.valueOf(GoTo);
            // 5. 将GoTo状态加入状态栈
            stateStack.addLast(GoTo);
            if (GoTo == -1) {
                return false;
            }
            // 6. 将产生式左边的符号加入符号栈
            charStack.addLast(production.left);
            analyzeStepList.add(analyzeStep);
        }
        return true;
    }
    public void batchPollLast(ArrayDeque<?> stack, int count) {
        for (int i= 0; i< count; i++) {
            stack.pollLast();
        }
    }
    public String stackToString(ArrayDeque<?> stack) {
        StringBuilder sb = new StringBuilder();
        for (Object o : stack) {
            sb.append(o.toString()).append(" ");
        }
        return sb.toString();
    }

    public String parseToken(Token token) {
        int code = token.categoryCode;
        if (code == CategoryCodeEnum.Identifier.getCode()) {
            return "id";
        }
        return "idk";
    }
}
