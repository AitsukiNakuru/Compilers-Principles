package edu.gxu.grammar;

import edu.gxu.common.LREnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * 产生式 A->BCD
 *
 * @value left 产生式左边
 * @value right 产生式右边，将每个符号分开存储
 */
public class Production implements Serializable {
    /**
     * 产生式左边
     */
    public String left;
    /**
     * 产生式右边，将每个符号分开存储
     */
    public ArrayList<String> right = new ArrayList<>();


    /**
     * 构造一个产生式 A->B C D
     *
     * @param left  产生式的左边 A
     * @param right 产生式的右边 B C D
     */
    public Production(String left, String right) {
        this.left = left.trim();
        String[] rightSegmentBySpace = right.split(" ");
        for (String str : rightSegmentBySpace) {
            if (!str.trim().equals("")) {
                this.right.add(str.trim());
            }
        }
    }

    /**
     * 构造一个产生式 A->BCD
     *
     * @param str A->BCD
     */
    public static ArrayList<Production> parse(String str) {
        String[] div = str.split(LREnum.Arrow.getString());
        ArrayList<Production> result = new ArrayList<>();
        for (String right : div[1].split("\\|")) {
            result.add(new Production(div[0], right));
        }
        return result;
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(left).append(" ").append(LREnum.Arrow.getString()).append(" ");
        for (String str : right) {
            result.append(str);
        }
        result.append("\n");
        return result.toString().trim();
    }


    public boolean equals(Production obj) {
        return this.toString().equals(obj.toString());
    }

    /**
     * 返回产生式右边最后一个字符的下标 right.size() - 1
     *
     * @return 返回产生式右边最后一个字符的下标
     */
    public Integer getLastIndex() {
        return this.right.size() - 1;
    }

    public boolean isLastChar(Integer index) {
        return Objects.equals(index, getLastIndex());
    }

    public String getRightFirstChar() {
        return right.get(0);
    }


    /**
     * 获取指定非终结符在产生式右边的下标列表
     *
     * @param nonTerminal 非终结符
     * @return 下标列表
     */
    public ArrayList<Integer> getIndexInRightByNonTerminal(String nonTerminal) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < right.size(); i++) {
            if (right.get(i).equals(nonTerminal)) {
                result.add(i);
            }
        }
        return result;
    }
}
