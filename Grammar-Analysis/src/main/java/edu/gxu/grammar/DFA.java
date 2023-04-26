package edu.gxu.grammar;

import java.util.ArrayList;

public class DFA {
    public ArrayList<LRItemSet> lrItemSetList = new ArrayList<>();
    public LRItemSet getLRItemSetByIndex(int index) {
        return lrItemSetList.get(index);
    }
    public int size() {
        return lrItemSetList.size();
    }
    public int getDfaStateCount() {
        return lrItemSetList.size();
    }
    public void addLRItemSet(LRItemSet lrItemSet) {
        lrItemSetList.add(lrItemSet);
    }

    /**
     * 当前DFA中是否已经包含某个状态，若包含则返回该状态的id，否则返回-1
     * @param source 待检测的状态
     * @return 若包含则返回该状态的id，否则返回-1
     */
    public int containsLRItemSet(LRItemSet source) {
        for (LRItemSet lrItemSet : lrItemSetList) {
            if (lrItemSet.equalTo(source)) {
                return lrItemSet.id;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----DFA-----").append("\n");
        for (LRItemSet lrItemSet : lrItemSetList) {
            sb.append("Id: ").append(lrItemSet.id).append("  ").append("Size: ").append(lrItemSet.lrItems.size()).append("\n");
            sb.append(lrItemSet).append("\n");
        }
        return sb.toString();
    }
}
