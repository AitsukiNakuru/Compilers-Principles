package edu.gxu.grammar;

import edu.gxu.common.LREnum;

import java.io.*;
import java.util.Objects;


/**
 * LR项目
 *
 * @value production 产生式
 * @value lookahead 向前搜索符
 * @value dotIndex 点的位置
 */
public class LRItem implements Serializable {
    /**
     * LR项目的产生式
     */
    public Production production;
    /**
     * LR项目的向前搜索符
     */
    public String lookahead;
    /**
     * LR项目的点的位置
     */
    public int dotIndex;

    public LRItem(Production production, String lookahead, int dotIndex) {
        this.production = production;
        this.lookahead = lookahead;
        this.dotIndex = dotIndex;
    }

    /**
     * 获取点后面的符号
     * @return 点后面的符号
     */
    public String getCharAfterDot() {
        return production.right.get(dotIndex);
    }

    /**
     * 当前LR项目是否为归约状态，点在最右边的状态
     * @return 当前LR项目是否为归约状态
     */
    public boolean isReductionState() {
        return dotIndex == production.right.size();
    }

    /**
     * 当前LR项目点是否位于产生式最后一个符号的前面， 类似于“A->BBB.C, #”的状态
     * @return 当前LR项目点是否位于产生式最后一个符号的前面
     */
    public boolean isDotBeforeLastChar() {
        return dotIndex == production.getLastIndex();
    }

    /**
     * 当前LR项目的Beta是否为空串
     * @return
     */
    public boolean isBetaEmpty() {
        return isDotBeforeLastChar();
    }
    public String getRightByIndex(Integer index) {
        return production.right.get(index);
    }

    public void moveDotToNext() {
        if (!isReductionState()) {
            dotIndex += 1;
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(production.left).append(LREnum.Arrow.getString());
        int length = production.right.size();
        for (int i = 0; i < length; i++) {
            if (length == 1 && production.right.get(0).equals(LREnum.Epsilon.getString())) {
                result.append(" ").append(LREnum.Dot.getString());
                break;
            } else {
                result.append(" ");
                if (i == dotIndex) {
                    result.append(LREnum.Dot.getString());
                }
                result.append(production.right.get(i));
            }
        }
        if (dotIndex == length && !production.right.get(0).equals(LREnum.Epsilon.getString())) {
            result.append(LREnum.Dot.getString());
        }
        result.append(" ,");
        result.append(lookahead);
        return result.toString();
    }

    public boolean equalTo(LRItem lrItem) {
        return production.equals(lrItem.production) && lookahead.hashCode() == lrItem.lookahead.hashCode() && dotIndex == lrItem.dotIndex;
    }
    public boolean isAcceptItem() {
        if (production.left.equals(LREnum.StartChar.getString())) {
            if (production.right.size() == 1) {
                if (isReductionState()) {
                    return true;
                }
            }
        }
        return false;
    }

    public LRItem deepCopy() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        return (LRItem) ois.readObject();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LRItem lrItem = (LRItem) o;

        return equalTo(lrItem);
    }

    @Override
    public int hashCode() {
        int result = production != null ? production.hashCode() : 0;
        result = 31 * result + (lookahead != null ? lookahead.hashCode() : 0);
        result = 31 * result + dotIndex;
        return result;
    }
}
