package edu.gxu.sbhrw;

public class Text {
    String originText;
    String[] segmentTextList;
    char[] charList;
    int lineNumber;
    int charIndex;

    public Text(Text text) {
        this.originText = text.originText;
        this.segmentTextList = text.segmentTextList;
        this.charList = text.charList;
        this.lineNumber = text.lineNumber;
        this.charIndex = text.charIndex;
    }

    /**
     * 读取字符前的初始化，按换行分割原始文本
     */
    public void initText() {
        lineNumber = -1;
        charIndex = -1;
        segmentTextList = originText.split("\n");
    }
    /**
     * 是否可以获取当前行的下一个字符
     *
     * @return 是否可以获取当前行的下一个字符
     */
    public boolean canGetNextChar() {
        return charIndex + 1 < charList.length;
    }

    /**
     * 获取当前行的下一个字符
     * @return 下一个字符
     */
    public char getNextChar() {
        charIndex += 1;
        return charList[charIndex];
    }

    /**
     * 回退charIndex，charIndex -= 1;
     */
    public void backIndex() {
        charIndex -= 1;
    }

    /**
     * 是否可以获取下一行字符
     * @return 是否可以获取下一行字符
     */
    public boolean canGetNextLine() {
        return lineNumber+1 < segmentTextList.length;
    }

    /**
     * 获取下一行字符，并重置index为-1，index = -1, lineNumber += 1
     */
    public void getNextLine() {
        charIndex = -1;
        lineNumber += 1;
        charList = segmentTextList[lineNumber].toCharArray();
    }

    public String getOriginText() {
        return originText;
    }

    public void setOriginText(String originText) {
        this.originText = originText;
    }

    public String[] getSegmentTextList() {
        return segmentTextList;
    }

    public void setSegmentTextList(String[] segmentTextList) {
        this.segmentTextList = segmentTextList;
    }

    public char[] getCharList() {
        return charList;
    }

    public void setCharList(char[] charList) {
        this.charList = charList;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getCharIndex() {
        return charIndex;
    }

    public void setCharIndex(int charIndex) {
        this.charIndex = charIndex;
    }
}
