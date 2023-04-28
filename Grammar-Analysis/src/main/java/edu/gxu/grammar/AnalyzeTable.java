package edu.gxu.grammar;

import edu.gxu.common.LREnum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class AnalyzeTable {
    public DFA dfa;
    public int actionLength;
    public int gotoLength;
    public String[] actionTitleList;
    public String[] gotoTitleList;
    public HashMap<Integer, HashMap<String, String>> analyzeMap;
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
        // 这里和张志路的不一样，待验证
        // 还是用我的吧
        this.actionTitleList = new String[Util.terminalSet.size() + 1];
        this.gotoTitleList = new String[Util.nonTerminalSet.size()];
        int i = 0, j = 0;
        for (String terminal : Util.terminalSet) {
            if (!terminal.equals(LREnum.Epsilon.getString())) {
                actionTitleList[i] = terminal;
                i += 1;
            }
        }
        actionTitleList[i] = LREnum.Sharp.getString();

        for (String notTerminal : Util.nonTerminalSet) {
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
        startLRItemSet.addLRItem(new LRItem(Util.getProductionListByLeft(LREnum.StartChar.getString()).get(0), LREnum.Sharp.getString(), 0));
        int originalSize = startLRItemSet.lrItems.size();
        do {
            originalSize = startLRItemSet.lrItems.size();
            // 先将所有处于归约状态的项目过滤 = 过滤所有点在最后的项目
            for (LRItem lrItem : Util.filterLRItemSetByReductionState(startLRItemSet.lrItems, false)) {
                // 若项目[A→α·Bβ, a]∈closure(I)，且B→γ是文法的产生式，β∈V*
                // 求First(βa)
                HashSet<String> firstBeta = new HashSet<>();
                fillFirstBeta(firstBeta, lrItem);
                // 则对任何b∈FIRST(βa)有[B→·γ, b]∈closure(I)；
                fillLRItemSetByFirstBetaAndLRItem(firstBeta, startLRItemSet, lrItem);
            }
        } while (originalSize != startLRItemSet.lrItems.size());
        dfa.lrItemSetList.add(startLRItemSet);
        HashMap<String, HashSet<LRItem>> accessibleMap = Util.getAccessibleMap(startLRItemSet);
        for (String key : accessibleMap.keySet()) {
            addState(startLRItemSet.id, key, accessibleMap.get(key));
        }
    }

    public void addState(int lastState, String path, HashSet<LRItem> originalLRItemSet) throws IOException, ClassNotFoundException {

        LRItemSet lrItemSet = new LRItemSet(0);
        // deepCopy，很重要！
        lrItemSet.addLRItem(originalLRItemSet);
        lrItemSet.lrItems.forEach(LRItem::moveDotToNext);
        int originalSize = lrItemSet.lrItems.size();
        do {
            originalSize = lrItemSet.lrItems.size();
            for (LRItem lrItem : Util.filterLRItemSetByReductionState(lrItemSet.lrItems, false)) {
                HashSet<String> firstBeta = new HashSet<>();
                fillFirstBeta(firstBeta, lrItem);
                ArrayList<Production> productionList = Util.getProductionListByLeft(lrItem.getCharAfterDot());
                fillLRItemSetByFirstBetaAndLRItem(firstBeta, lrItemSet, lrItem);
            }
        } while (originalSize != lrItemSet.lrItems.size());


        lrItemSet.id = dfa.size();
        if (fillGoMap(lrItemSet, lastState, path)) {
            dfa.addLRItemSet(lrItemSet);
        } else {
            return;
        }

        HashMap<String, HashSet<LRItem>> accessibleMap = Util.getAccessibleMap(lrItemSet);
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
        if (Util.isNonTerminal(charAfterDot)) {
            for (Production production : Util.getProductionListByLeft(charAfterDot)) {
                for (String first : firstBeta) {
                    if (!Util.isEmptyChar(first)) {
                        LRItem newLRItem;
                        if (Util.isEmptyChar(production.getRightFirstChar())) {
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
                HashSet<String> tempFirst = Util.getFirstSetByChar(lrItem.getRightByIndex(i));
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
        if (Util.isTerminal(lrItem.getCharAfterDot())) {
            return;
        }
        for (int i = lrItem.dotIndex + 1; i <= lrItem.production.getLastIndex(); i++) {

            HashSet<String> tempFirst = Util.getFirstSetByChar(lrItem.getRightByIndex(i));
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
                if (Util.isNonTerminal(path)) {
                    analyzeMap.get(mainKey).put(path, "GoTo" + goMap.get(mainKey).get(path));
                }
            }
        }

    }
    public void fillAnalyzeMap(LRItemSet lrItemSet) {
        for (LRItem lrItem : lrItemSet.lrItems) {
            // 若[A→α·, a]∈Ii, 且A→α为文法第j个产生式,则：action[i, a]=rj;
            if (lrItem.isReductionState()) {
                analyzeMap.get(lrItemSet.id).put(lrItem.lookahead, "Reduce" + Util.getIndexByProduction(lrItem.production));
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

    public String getNextAction(Integer state, String input) {
        String nextAction = analyzeMap.get(state).get(input);
        if (nextAction == null) {
            return LREnum.Error.getString();
        }
        return nextAction;
    }
    public Integer getGoTo(Integer state, String nonTerminal) {
        String GoTo = analyzeMap.get(state).get(nonTerminal);
        if (GoTo == null) {
            return -1;
        }
        return Integer.parseInt(GoTo.substring(4));
    }

}
