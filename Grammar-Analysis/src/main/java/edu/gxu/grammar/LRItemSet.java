package edu.gxu.grammar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class LRItemSet {
    /**
     * 项目族编号，DFA状态编号
     */
    public int id;
    /**
     * LR项目列表
     */
    public HashSet<LRItem> lrItems = new HashSet<>();

    public LRItemSet(int id) {
        this.id = id;
    }

    /**
     * 是否包含某个LR项目
     *
     * @param lrItem LR项目
     * @return 是否包含某个LR项目
     */
    public boolean containsLRItem(LRItem lrItem) {
        for (LRItem item : lrItems) {
            if (lrItem.equalTo(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将一个LR项目添加进当前LR项目族中
     *
     * @param lrItem 添加的LR项目
     * @return 是否添加成功
     */
    public boolean addLRItem(LRItem lrItem) {
        if (containsLRItem(lrItem)) {
            return false;
        } else {
            lrItems.add(lrItem);
            return true;
        }
    }

    /**
     * 获取项目族中所有LR项目中点后面的符号
     *
     * @return 项目族中所有LR项目中点后面的符号
     */
    public ArrayList<String> getCharAfterDotList() {
        ArrayList<String> result = new ArrayList<>();
        for (LRItem lrItem : lrItems) {
            if (lrItem.production.right.size() == lrItem.dotIndex) {
                continue;
            }
            String str = lrItem.production.right.get(lrItem.dotIndex);
            if (!result.contains(str)) {
                result.add(str);
            }
        }
        return result;
    }

    /**
     * 不知道这个是干什么用的
     * 返回一个项目集中，s与项目集"."后面的符号相匹配的，所在的产生式状态列表
     *
     * @param str
     * @return
     * @throws
     */
    public HashSet<LRItem> getLRItemSet(String str) {
        HashSet<LRItem> result = new HashSet<>();
        for (LRItem lrItem : lrItems) {
            if (!lrItem.isReductionState()) {
                if (lrItem.getCharAfterDot().equals(str)) {
                    result.add(lrItem);
                }
            }
        }
        return result;
    }

    /**
     * 获取LR项目族中所有点后面的符号
     *
     * @return LR项目族中所有点后面的符号
     */
    public HashSet<String> getAllCharAfterDot() {
        HashSet<String> result = new HashSet<>();
        for (LRItem lrItem : lrItems) {
            if (!lrItem.isReductionState()) {
                result.add(lrItem.getCharAfterDot());
            }
        }
        return result;
    }

    public void addLRItem(HashSet<LRItem> addSet) throws IOException, ClassNotFoundException {
        for (LRItem lrItem : addSet) {
            lrItems.add(lrItem.deepCopy());
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (LRItem lrItem : lrItems) {
            result.append(lrItem);
            result.append("\n");
        }
        return result.toString();
    }

    /**
     * 比较两个LR项目族是否相等，仅比较LR项目族中的每个LR项目是否相同，与LR项目族的id无关
     * @param lrItemSet 待比较的LR项目族
     * @return 两个LR项目族是否相等
     */
    public boolean equalTo(LRItemSet lrItemSet) {
        if (this.lrItems.size() != lrItemSet.lrItems.size()) {
            return false;
        }
        for (LRItem targetLRItem : lrItemSet.lrItems) {
            if (!this.containsLRItem(targetLRItem)) {
                return false;
            }
        }
        return true;
    }
    public boolean isSameState(LRItemSet lrItemSet) {
        return lrItems.equals(lrItemSet.lrItems);
    }

    public boolean containsAcceptItem() {
        for (LRItem lrItem : lrItems) {
            if (lrItem.isAcceptItem()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LRItemSet lrItemSet = (LRItemSet) o;

        return equalTo(lrItemSet);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (lrItems != null ? lrItems.hashCode() : 0);
        return result;
    }
}
