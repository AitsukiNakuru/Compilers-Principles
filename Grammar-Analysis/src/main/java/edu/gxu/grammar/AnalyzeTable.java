package edu.gxu.grammar;

import edu.gxu.common.LREnum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AnalyzeTable {
    public static DFA dfa;
    public int actionLength;
    public int gotoLength;
    public String[] actionTitleList;
    public String[] gotoTitleList;
    /**
     * HashMap<状态编号， HashMap<输入字符， 操作>>
     */
    public HashMap<Integer, HashMap<String, String>> analyzeMap;
    /**
     * HashMap<状态编号， HashMap<输入字符, 下一个状态>>
     */
    public HashMap<Integer, HashMap<String, Integer>> goMap;

    public AnalyzeTable() throws IOException, ClassNotFoundException {
        initTitleList();
        intiDFA();
        System.out.println(dfa);
        System.out.println(goMapToString());
        initAnalyzeMap();
    }



    /**
     * 初始化表头
     */
    public void initTitleList() {
        this.actionTitleList = new String[GrammarUtil.terminalSet.size() + 1];
        this.gotoTitleList = new String[GrammarUtil.nonTerminalSet.size()];
        int i = 0, j = 0;
        for (String terminal : GrammarUtil.terminalSet) {
            if (!terminal.equals(LREnum.Epsilon.getString())) {
                actionTitleList[i] = terminal;
                i += 1;
            }
        }
        actionTitleList[i] = LREnum.Sharp.getString();

        for (String notTerminal : GrammarUtil.nonTerminalSet) {
            gotoTitleList[j] = notTerminal;
            j += 1;
        }
    }

    public void intiDFA() throws IOException, ClassNotFoundException {
        this.actionLength = actionTitleList.length;
        this.gotoLength = gotoTitleList.length;
        this.goMap = new HashMap<>();
        createDFA();
    }

    public void createDFA() throws IOException, ClassNotFoundException {
        this.dfa = new DFA();
        LRItemSet startLRItemSet = new LRItemSet(0);
        startLRItemSet.addLRItem(new LRItem(GrammarUtil.getProductionListByLeft(LREnum.StartChar.getString()).get(0), LREnum.Sharp.getString(), 0));
        // 求闭包
        int originalSize = startLRItemSet.lrItems.size();
        do {
            originalSize = startLRItemSet.lrItems.size();
            // 先将所有处于归约状态的项目过滤 = 过滤所有点在最后的项目
            for (LRItem lrItem : GrammarUtil.filterLRItemSetByReductionState(startLRItemSet.lrItems, false)) {
                // 若项目[A→α·Bβ, a]∈closure(I)，且B→γ是文法的产生式，β∈V*
                // 求First(βa)
                HashSet<String> firstBeta = new HashSet<>();
                fillFirstBeta(firstBeta, lrItem);
                // 则对任何b∈FIRST(βa)有[B→·γ, b]∈closure(I)；
                fillLRItemSetByFirstBetaAndLRItem(firstBeta, startLRItemSet, lrItem);
            }
            // 再对所有可以产生空串的项目处理？
        } while (originalSize != startLRItemSet.lrItems.size());
        dfa.lrItemSetList.add(startLRItemSet);
        HashMap<String, HashSet<LRItem>> accessibleMap = GrammarUtil.getAccessibleMap(startLRItemSet);
        for (String key : accessibleMap.keySet()) {
            addState(startLRItemSet.id, key, accessibleMap.get(key));
        }
    }

    /**
     * 添加状态
     * @param lastState 上一个状态
     * @param path 通过的路径
     * @param originalLRItemSet 上一个状态的LR项目族的其中一个LR项目，通过该项目触发此函数
     */
    public void addState(int lastState, String path, HashSet<LRItem> originalLRItemSet) throws IOException, ClassNotFoundException {

        LRItemSet lrItemSet = new LRItemSet(0);
        // deepCopy，很重要！
        lrItemSet.addLRItem(originalLRItemSet);
        // 将上一个LR项目的点向后移动
        lrItemSet.lrItems.forEach(LRItem::moveDotToNext);
        int originalSize = lrItemSet.lrItems.size();
        // 求闭包，直到不再增大
        do {
            originalSize = lrItemSet.lrItems.size();
            // 查找出所有非归约状态的LR项目
            for (LRItem lrItem : GrammarUtil.filterLRItemSetByReductionState(lrItemSet.lrItems, false)) {
                // 根据当前LR项目获取firstBeta
                HashSet<String> firstBeta = new HashSet<>();
                fillFirstBeta(firstBeta, lrItem);
                // 根据当前LR项目的产生式获取可以直接推出的产生式？？？写在下一个函数里面了
                ArrayList<Production> productionList = GrammarUtil.getProductionListByLeft(lrItem.getCharAfterDot());
                // 根据firstBet和LR项目填充LR项目族
                fillLRItemSetByFirstBetaAndLRItem(firstBeta, lrItemSet, lrItem);
            }
        } while (originalSize != lrItemSet.lrItems.size());

        // 如果DFA中已经包含了此状态，则不添加次状态且不再继续向后查找
        lrItemSet.id = dfa.size();
        if (fillGoMap(lrItemSet, lastState, path)) {
            dfa.addLRItemSet(lrItemSet);
        } else {
            return;
        }
        // 获取当前状态可以到达的状态，将这些状态添加到DFA中
        HashMap<String, HashSet<LRItem>> accessibleMap = GrammarUtil.getAccessibleMap(lrItemSet);
        for (String key : accessibleMap.keySet()) {
            addState(lrItemSet.id, key, accessibleMap.get(key));
        }

    }

    /**
     * 填充GoMap，返回当前DFA是否包含待添加的LR项目族
     * @param lrItemSet 待添加LR项目族
     * @param lastState 上一个状态
     * @param path 路径
     * @return 当前DFA是否包含待添加的LR项目族
     */
    public boolean fillGoMap(LRItemSet lrItemSet, Integer lastState, String path) {
        if (!goMap.containsKey(lastState)) {
            goMap.put(lastState, new HashMap<>());
        }
        // DFA中包含当前状态
        if (dfa.containsLRItemSet(lrItemSet) != -1) {
            goMap.get(lastState).put(path, dfa.containsLRItemSet(lrItemSet));
            return false;
        }
        //DFA中不包含当前状态
        goMap.get(lastState).put(path, lrItemSet.id);
        return true;
    }
    /**
     * 填充LR项目族通过First集和当前的LR项目，ppt91页步骤二
     *
     * @param firstBeta First集
     * @param lrItemSet LR项目族
     * @param lrItem    LR项目
     */
    public void fillLRItemSetByFirstBetaAndLRItem(HashSet<String> firstBeta, LRItemSet lrItemSet, LRItem lrItem) {
        // 获取点后面的那个符号
        String charAfterDot = lrItem.getCharAfterDot();
        if (GrammarUtil.isNonTerminal(charAfterDot)) {
            for (Production production : GrammarUtil.getProductionListByLeft(charAfterDot)) {
                for (String first : firstBeta) {
                    if (!GrammarUtil.isEmptyChar(first)) {
                        LRItem newLRItem;
                        if (GrammarUtil.isEmptyChar(production.getRightFirstChar())) {
                            newLRItem = new LRItem(production, first, 1);
                        } else {
                            newLRItem = new LRItem(production, first, 0);
                        }
                        lrItemSet.addLRItem(newLRItem);
                    }
                }
            }
        }

    }
    /**
     * 填充fistBeta
     *
     * @param firstBeta firstBeta
     * @param lrItem    LR项目
     */
    public void fillFirstBeta(HashSet<String> firstBeta, LRItem lrItem) {
        // 求firstBeta有点问题
        /*if (lrItem.isDotBeforeLastChar()) {
            firstBeta.add(lrItem.lookahead);
        } else {
            boolean flag = true;
            for (int i = lrItem.dotIndex + 1; i <= lrItem.production.getLastIndex(); i++) {
                HashSet<String> tempFirst = GrammarUtil.getFirstSetByChar(lrItem.getRightByIndex(i));
                firstBeta.addAll(tempFirst);
                if (!tempFirst.contains(LREnum.Epsilon.getString())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                firstBeta.add(lrItem.lookahead);
            }
        }*/
        // 点后面是终结符直接不管
        if (GrammarUtil.isTerminal(lrItem.getCharAfterDot())) {
            return;
        }
        for (int i = lrItem.dotIndex + 1; i <= lrItem.production.getLastIndex(); i++) {

            HashSet<String> tempFirst = GrammarUtil.getFirstSetByChar(lrItem.getRightByIndex(i));
            firstBeta.addAll(tempFirst);
            // 如果tempFirst不包含空，则不继续向后查找
            if (!tempFirst.contains(LREnum.Epsilon.getString())) {
                break;
            }
        }
        if (firstBeta.isEmpty()) {
            firstBeta.add(lrItem.lookahead);
        }
    }

    /**
     * 初始化分析表
     */
    public void initAnalyzeMap() {
        analyzeMap = new HashMap<>();
        // ①对每一个项目集Ii，在分析表中记入状态i的分析动作表：
        for (LRItemSet lrItemSet : dfa.lrItemSetList) {
            analyzeMap.put(lrItemSet.id, new HashMap<>());
            fillAnalyzeMap(lrItemSet);
        }
        // ② 若go(Ii, A)=Ij, 且A∈VN, 则goto[i, A]=j;
        for (Integer mainKey : goMap.keySet()) {
            for (String path : goMap.get(mainKey).keySet()) {
                if (GrammarUtil.isNonTerminal(path)) {
                    analyzeMap.get(mainKey).put(path, "GoTo" + goMap.get(mainKey).get(path));
                }
            }
        }

    }

    /**
     * 根据一个LR项目族填充分析表
     * @param lrItemSet LR项目族
     */
    public void fillAnalyzeMap(LRItemSet lrItemSet) {
        for (LRItem lrItem : lrItemSet.lrItems) {
            // 若[A→α·, a]∈Ii, 且A→α为文法第j个产生式,则：action[i, a]=rj;
            if (lrItem.isReductionState()) {
                analyzeMap.get(lrItemSet.id).put(lrItem.lookahead, "Reduce" + GrammarUtil.getIndexByProduction(lrItem.production));
            }
            // 若[A→α·aβ, b]∈Ii, 而go(Ii,a)=Ij, 则: action[i, a] =Sj;
            else {
                String charAfterDot = lrItem.getCharAfterDot();
                Integer to = goMap.get(lrItemSet.id).get(charAfterDot);
                if (to != null) {
                    analyzeMap.get(lrItemSet.id).put(charAfterDot, "Shift" + to);
                }
            }
        }
        // 若[S’→S·, #]∈Ii, 则：action[i, #]=acc.
        if (lrItemSet.containsAcceptItem()) {
            analyzeMap.get(lrItemSet.id).put(LREnum.Sharp.getString(), LREnum.Accept.getString());
        }
    }

    public String goMapToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----GoMap-----").append("\n");
        for (Integer mainKey : goMap.keySet()) {
            sb.append("From: ").append(mainKey).append("\n");
            for (String pathKey : goMap.get(mainKey).keySet()) {
                sb.append("Path: ").append(pathKey);
                sb.append("  To: ").append(goMap.get(mainKey).get(pathKey));
                sb.append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * 获取下一个动作
     * @param state 当前状态
     * @param input 输入字符
     * @return 下一个动作
     */
    public String getNextAction(Integer state, String input) {
        // 先从分析表中查询
        String nextAction = analyzeMap.get(state).get(input);
        if (nextAction == null) {
            // 无法从分析表中查找到下一个动作，再查找是否有空串的归约状态
            nextAction = getNextEpsilonReductionAction(state, input);
            if (nextAction.equals(LREnum.Error.getString())) {
                return LREnum.Error.getString();
            }
        }
        return nextAction;
    }
    /**
     * 获取当前状态的空串归约状态
     * @param state 当前状态
     * @param input 下一个输入的字符
     * @return 空串归约操作
     */
    static public String getNextEpsilonReductionAction(Integer state, String input) {
        LRItemSet lrItemSet = dfa.lrItemSetList.get(state);
        HashSet<LRItem> epsilonReduction = GrammarUtil.filterLRItemSetByProductEpsilon(lrItemSet.lrItems, true);
        if (epsilonReduction.size() != 1) {
            return LREnum.Error.getString();
        }
        if (!epsilonReduction.iterator().next().lookahead.equals(input)) {
            return LREnum.Error.getString();
        }
        return "Reduce" + GrammarUtil.getIndexByProduction(epsilonReduction.iterator().next().production);
    }
    public Integer getGoTo(Integer state, String nonTerminal) {
        String GoTo = analyzeMap.get(state).get(nonTerminal);
        if (GoTo == null) {
            return -1;
        }
        return Integer.parseInt(GoTo.substring(4));
    }

}
