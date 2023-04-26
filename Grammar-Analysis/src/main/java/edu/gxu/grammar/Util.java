package edu.gxu.grammar;

import edu.gxu.common.LREnum;
import edu.gxu.util.FileUtil;

import java.io.IOException;
import java.util.*;


public class Util {
    static public HashSet<String> nonTerminalSet = new HashSet<>();
    static public HashSet<String> terminalSet = new HashSet<>();
    static ArrayList<Production> productionList = new ArrayList<>();
    static public HashMap<String, HashSet<String>> firstMap = new HashMap<>();
    static public HashMap<String, HashSet<String>> followMap = new HashMap<>();

    static {
        try {
            //init("Grammar.txt", "NonTerminal.txt.txt", "Terminal.txt");
            String test = "Test-Follow-2/";
            init(test + "Grammar.txt", test + "NonTerminal.txt", test + "Terminal.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化Util类
     *
     * @param grammarFilePath
     * @param nonTerminalFilePath
     * @param terminalFilePath
     * @throws IOException
     */
    static public void init(String grammarFilePath, String nonTerminalFilePath, String terminalFilePath) throws IOException {
        productionList = readGrammarFile(grammarFilePath);
        nonTerminalSet = readNonTerminal(nonTerminalFilePath);
        terminalSet = readTerminal(terminalFilePath);
        initFirstMap();
        System.out.println(firstMapToString());
        initFollowMap();
        System.out.println(followMapToString());
        System.out.println(productionListToString());
    }

    static public StringBuilder firstMapToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----FirstMap-----").append("\n");
        for (String key : firstMap.keySet()) {
            sb.append("First( ").append(key).append(" )");
            sb.append(" = { ");
            for (String str : firstMap.get(key)) {
                sb.append(str).append(", ");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(" }").append("\n");
        }
        sb.append("\n");
        return sb;
    }

    static public StringBuilder followMapToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----FollowMap-----").append("\n");
        for (String key : followMap.keySet()) {
            sb.append("Follow( ").append(key).append(" )");
            sb.append(" = { ");
            for (String str : followMap.get(key)) {
                sb.append(str).append(", ");
            }
            if (!followMap.get(key).isEmpty()) {
                sb.deleteCharAt(sb.length() - 1);
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append(" }").append("\n");
        }
        sb.append("\n");
        return sb;
    }

    static public StringBuilder productionListToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----ProductionList-----").append("\n");
        for (Production production : productionList) {
            sb.append(production.toString());
            sb.append("\n");
        }
        return sb;
    }

    /**
     * 读取文法文件
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    static public ArrayList<Production> readGrammarFile(String filePath) throws IOException {
        String[] grammarSegmentByLine = FileUtil.readTxtFile(filePath);
        ArrayList<Production> productionList = new ArrayList<>();
        for (String line : grammarSegmentByLine) {
            if (!line.equals("")) {
                productionList.addAll(Production.parse(line));
            }
        }
        return productionList;
    }

    /**
     * 读取非终结符文件
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    static public HashSet<String> readNonTerminal(String filePath) throws IOException {
        String[] nonTerminalSegmentByLine = FileUtil.readTxtFile(filePath);
        HashSet<String> nonTerminalSet = new HashSet<>();
        for (String line : nonTerminalSegmentByLine) {
            if (!line.equals("")) {
                nonTerminalSet.add(line.trim());
            }
        }
        return nonTerminalSet;
    }

    /**
     * 读取终结符文件
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    static public HashSet<String> readTerminal(String filePath) throws IOException {
        String[] terminalSegmentByLine = FileUtil.readTxtFile(filePath);
        HashSet<String> terminalSet = new HashSet<>();
        for (String line : terminalSegmentByLine) {
            if (!line.equals("")) {
                terminalSet.add(line.trim());
            }
        }
        return terminalSet;
    }

    /**
     * 初始化FirstMap
     */
    static public void initFirstMap() {
        for (String terminal : terminalSet) {
            firstMap.put(terminal, new HashSet<>());
            firstMap.get(terminal).add(terminal);
        }
        for (String nonTerminal : nonTerminalSet) {
            firstMap.put(nonTerminal, new HashSet<>());
            firstMap.get(nonTerminal).addAll(findFirst(nonTerminal));
        }
        for (String key : followMap.keySet()) {
            followMap.get(key).remove(LREnum.Epsilon.getString());
        }
    }

    static public HashSet<String> findFirst(String nonTerminal) {
        HashSet<String> result = new HashSet<>();
        int originalSize = 0;
        do {
            originalSize = result.size();
            ArrayList<Production> productionListByLeft = filterProductionListByLeft(productionList, nonTerminal);
            // (1) 若X ∈ Vt，则FIRST(X) = {X}。
            for (Production production : filterProductionListByRightStartWithTerminal(productionListByLeft)) {
                result.add(production.right.get(0));
            }
            // (2) 若X ∈ VN，X → ε，则 ε ∈ FIRST(X) (直接推导)
            for (Production production : filterProductionListByRightStartWithEmpty(productionListByLeft)) {
                result.add(LREnum.Epsilon.getString());
            }
            // 非终结符，递归
            for (Production production : filterProductionListByRightStartWithNonTerminal(productionListByLeft, nonTerminal)) {
                // (3) 若X ∈ Vn，且有产生式X → a……， a ∈ Vt，则a ∈ FIRST(X) (非终结符，将首个终结符加入First集)
                HashSet<String> resultFromNonTerminal = findFirst(production.right.get(0));
                result.addAll(resultFromNonTerminal);
                // (4) 若X→Y1,Y2,……,Yn ∈ Vn，而有产生式X → Y1,Y2,……,Yn。
                //     当Y1,Y2,……,Y(i-1) 直接推出ε时，
                //     则FIRST(Y1) - ε, FIRST(Y2) - ε, …… , FIRST(Y(i-1) - ε)都包含在FIRST(X)中
                if (resultFromNonTerminal.contains(LREnum.Epsilon.getString())) {
                    for (int i = 1; i < production.right.size(); i++) {
                        HashSet<String> resultFromNonTerminalTrimEmpty = findFirst(production.right.get(i));
                        result.addAll(resultFromNonTerminalTrimEmpty);
                        if (!resultFromNonTerminalTrimEmpty.contains(LREnum.Epsilon.getString())) {
                            break;
                        }
                    }
                }
            }
        } while (result.size() != originalSize);
        return result;
    }

    /**
     * 过滤产生式列表，过滤条件为产生式左边为指定非终结符
     *
     * @param productionList 需要过滤的产生式列表
     * @param nonTerminal    非终结符
     * @return 过滤后的产生式列表
     */
    static public ArrayList<Production> filterProductionListByLeft(ArrayList<Production> productionList, String nonTerminal) {
        ArrayList<Production> result = new ArrayList<>();
        for (Production production : productionList) {
            if (production.left.equals(nonTerminal)) {
                result.add(production);
            }
        }
        return result;
    }

    /**
     * 过滤产生式列表，过滤条件为产生式右边以终结符开始
     *
     * @param productionList 需要过滤的产生式列表
     * @return 过滤后的产生式列表
     */
    static public ArrayList<Production> filterProductionListByRightStartWithTerminal(ArrayList<Production> productionList) {
        ArrayList<Production> result = new ArrayList<>();
        for (Production production : productionList) {
            if (terminalSet.contains(production.right.get(0))) {
                result.add(production);
            }
        }
        return result;
    }

    /**
     * 过滤产生式列表，过滤条件为产生式右边以空串开始
     *
     * @param productionList 需要过滤的产生式列表
     * @return 过滤后的产生式列表
     */
    static public ArrayList<Production> filterProductionListByRightStartWithEmpty(ArrayList<Production> productionList) {
        ArrayList<Production> result = new ArrayList<>();
        for (Production production : productionList) {
            if (production.right.get(0).equals(LREnum.Epsilon.getString())) {
                result.add(production);
            }
        }
        return result;
    }

    /**
     * 过滤产生式列表，过滤条件为产生式右边以指定非终结符开始
     *
     * @param productionList 需要过滤的产生式列表
     * @param nonTerminal    指定的非终结符
     * @return 过滤后的产生式列表
     */
    static public ArrayList<Production> filterProductionListByRightStartWithNonTerminal(ArrayList<Production> productionList, String nonTerminal) {
        ArrayList<Production> result = new ArrayList<>();
        for (Production production : productionList) {
            if (nonTerminalSet.contains(production.right.get(0))) {
                // 去除类似于E->E*E这样的左递归
                if (!nonTerminal.equals(production.right.get(0))) {
                    result.add(production);
                }
            }
        }
        return result;
    }

    /**
     * 过滤产生式列表，过滤条件为产生式右边包含指定的非终结符
     *
     * @param productionList 需要过滤的产生式列表
     * @param nonTerminal    指定的非终结符
     * @return 过滤后的产生式
     */
    static public ArrayList<Production> filterProductionListByRightContainsNonTerminal(ArrayList<Production> productionList, String nonTerminal) {
        ArrayList<Production> result = new ArrayList<>();
        for (Production production : productionList) {
            if (production.right.contains(nonTerminal)) {
                result.add(production);
            }
        }
        return result;
    }


    static public void updateFollowMapByProductionAndIndexList(Production production, ArrayList<Integer> indexList, String nonTerminal) {
        for (Integer index : indexList) {
            if (production.isLastChar(index)) {
                followMap.get(nonTerminal).add(LREnum.Sharp.getString());
            } else {
                HashSet<String> addSet = new HashSet<>();
                for (String str : firstMap.get(production.right.get(index + 1))) {
                    if (!str.equals(LREnum.Epsilon.getString())) {
                        addSet.add(str);
                    }
                }
                followMap.get(nonTerminal).addAll(addSet);
            }
        }
    }

    /**
     * 初始化FollowMap
     */

    static public void initFollowMap() {
        for (String nonTerminal : nonTerminalSet) {
            followMap.put(nonTerminal, new HashSet<>());
        }
        for (String terminal : terminalSet) {
            followMap.put(terminal, new HashSet<>());
        }
        followMap.get(LREnum.StartChar.getString()).add(LREnum.Sharp.getString());
        for (String nonTerminal : nonTerminalSet) {
            HashSet<String> original = new HashSet<>();
            original.add(nonTerminal);
            followMap.get(nonTerminal).addAll(findFollow(nonTerminal, original));
        }
        for (String key : followMap.keySet()) {
            followMap.get(key).remove(LREnum.Epsilon.getString());
        }
    }

    static public HashSet<String> findFollow(String nonTerminal, HashSet<String> original) {
        //System.out.println(nonTerminal + " " + original);
        for (Production production : filterProductionListByRightContainsNonTerminal(productionList, nonTerminal)) {
            for (Integer index : production.getIndexInRightByNonTerminal(nonTerminal)) {
                if (production.isLastChar(index)) {
                    if (!original.contains(production.left)) {
                        original.add(production.left);
                        followMap.get(nonTerminal).addAll(findFollow(production.left, original));
                    }
                    continue;
                }
                if (!production.isLastChar(index)) {
                    for (int i = index + 1; i <= production.getLastIndex(); i++) {
                        String nextChar = production.right.get(i);
                        if (getFirstSetByChar(nextChar).contains(LREnum.Epsilon.getString())) {
                            if (!original.contains(production.left)) {
                                original.add(production.left);
                                followMap.get(nonTerminal).addAll(findFollow(production.left, original));
                            }
                            followMap.get(nonTerminal).addAll(firstMap.get(nextChar));
                        } else {
                            followMap.get(nonTerminal).addAll(firstMap.get(nextChar));
                            break;
                        }
                    }
                }
            }
        }
        return followMap.get(nonTerminal);
    }

    /**
     * 获取左边为指定非终结符的所有产生式
     *
     * @param nonTerminal 指定的非终结符
     * @return 左边为指定非终结符的所有产生式
     */
    static public ArrayList<Production> getProductionListByLeft(String nonTerminal) {
        return filterProductionListByLeft(productionList, nonTerminal);
    }

    /**
     * 过滤LR项目族的产生式列表，过滤条件为LR项目是否为归约状态
     *
     * @param lrItemSet LR项目族的产生式列表
     * @param flag      是否为归约状态
     * @return 过滤后的LR项目列表
     */
    static public HashSet<LRItem> filterLRItemSetByReductionState(HashSet<LRItem> lrItemSet, boolean flag) {
        HashSet<LRItem> result = new HashSet<>();
        for (LRItem lrItem : lrItemSet) {
            if (flag && lrItem.isReductionState()) {
                result.add(lrItem);
            }
            if (!flag && !lrItem.isReductionState()) {
                result.add(lrItem);
            }
        }
        return result;
    }

    /**
     * 获取指定符号的First集
     *
     * @param str 指定的符号
     * @return 指定符号的First集
     */
    static public HashSet<String> getFirstSetByChar(String str) {
        HashSet<String> result = new HashSet<>();
        if (str.equals(LREnum.Sharp.getString())) {
            result.add(LREnum.Sharp.getString());
        } else {
            result.addAll(firstMap.get(str));
        }
        return result;
    }

    /**
     * 是否为非终结符
     *
     * @param str 检测符号
     * @return 是否为非终结符
     */
    static public boolean isNonTerminal(String str) {
        return nonTerminalSet.contains(str);
    }

    /**
     * 是否为空串
     *
     * @param str 检测符号
     * @return 是否为空串
     */
    static public boolean isEmptyChar(String str) {
        return str.equals(LREnum.Epsilon.getString());
    }

    static public HashMap<String, HashSet<LRItem>> getAccessibleMap(LRItemSet lrItemSet) {
        HashMap<String, HashSet<LRItem>> result = new HashMap<>();
        for (String path : lrItemSet.getAllCharAfterDot()) {
            if (!result.containsKey(path)) {
                result.put(path, new HashSet<>());
                result.get(path).addAll(lrItemSet.getLRItemSet(path));
            }
        }
        return result;
    }
    static public String getIndexByProduction(Production source) {
        // indexOf不可以
        //Integer temp = productionList.indexOf(production);
        for (int i = 0; i< productionList.size(); i++) {
            if (productionList.get(i).equals(source)) {
                return String.valueOf(i);
            }
        }
        return String.valueOf(-1);
    }

}
