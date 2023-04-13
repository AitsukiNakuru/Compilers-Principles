package edu.gxu.grammar;

public class AnalyzeStep {
    Integer index;
    String stateStack;
    String charStack;
    String inputString;
    String actionType;
    String production;
    String Action;
    String Goto;

    public AnalyzeStep(Integer index, String stateStack, String charStack, String inputString, String actionType, String production, String action, String Goto) {
        this.index = index;
        this.stateStack = stateStack;
        this.charStack = charStack;
        this.inputString = inputString;
        this.actionType = actionType;
        this.production = production;
        this.Action = action;
        this.Goto = Goto;
    }

    @Override
    public String toString() {
        return "AnalyzeStep{" +
                "index=" + index +
                ", stateStack='" + stateStack + '\'' +
                ", charStack='" + charStack + '\'' +
                ", inputString='" + inputString + '\'' +
                ", actionType='" + actionType + '\'' +
                ", production='" + production + '\'' +
                ", Action='" + Action + '\'' +
                ", Goto='" + Goto + '\'' +
                '}';
    }
}
