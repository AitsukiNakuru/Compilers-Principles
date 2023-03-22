package edu.gxu.lexical;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;


public class Analysis {
    private final String text;  // 读入的测试样例文本
    private final JTable mainTable;  // 行数-Token-种别码-单词类别
    private final JTable errorTable;  // 行数-错误内容-错误信息

    public Analysis(String text, JTable mainTable, JTable errorTable) {
        this.text = text;
        this.mainTable = mainTable;
        this.errorTable = errorTable;
    }

    public static int symbol_pos = 0;  // 记录符号表位置
    public static Map<String, Integer> symbol = new HashMap<>();  // 符号表HashMap

    public static int constant_pos = 0;  // 记录常量位置
    public static Map<String, Integer> constant = new HashMap<>();  // 常量表HashMap

    public void lex() {
        String[] texts = text.split("\n");
        //按行分割
        symbol.clear();
        //清除符号表
        symbol_pos = 0;
        constant.clear();
        //清除常量表
        constant_pos = 0;
        for (int m = 0; m < texts.length; m++) {
            String str = texts[m];
            if (str.equals(""))
                continue;
                //忽略空行
            else {
                char[] strLine = str.toCharArray();
                for (int i = 0; i < strLine.length; i++) {
                    //逐个字符的分析过程
                    char ch = strLine[i];
                    if (ch == ' ')
                        continue;

                    StringBuilder token = new StringBuilder();
                    // 识别关键字和标识符
                    if (Util.isAlpha(ch)) {
                        do {
                            token.append(ch);
                            i++;
                            if (i >= strLine.length)
                                break;
                            ch = strLine[i];
                        } while (ch != '\0' && (Util.isAlpha(ch) || Util.isDigit(ch)));
                        i--;
                        // 识别关键字
                        if (Util.isKeyword(token.toString())) {
                            DefaultTableModel tableModel = (DefaultTableModel) mainTable.getModel();
                            tableModel.addRow(new Object[]{m + 1, token.toString(), token.toString().toUpperCase(), "-"});
                            mainTable.invalidate();
                        }
                        // 识别标识符
                        else {
                            //当前识别到的token没有重复出现
                            if (symbol.isEmpty() || !symbol.containsKey(token.toString())) {
                                symbol.put(token.toString(), symbol_pos);
                                symbol_pos++;
                            }
                            DefaultTableModel tableModel1 = (DefaultTableModel) mainTable.getModel();
                            tableModel1.addRow(new Object[]{m + 1, token.toString(), "IDN", token.toString()});
                            mainTable.invalidate();
                        }

                    }
                    // 识别无符号数
                    else if (Util.isDigit(ch)) {
                        int state = 1;
                        int k;
                        boolean isFloat = false;
                        boolean isSci_not = false;
                        while ((ch != '\0') && (Util.isDigit(ch) || ch == '.' || ch == 'e'
                                || ch == '-' || ch == 'E' || ch == '+')) {
                            if (ch == '.')
                                isFloat = true;
                            if (ch == 'e' || ch == 'E') {
                                isFloat = false;
                                isSci_not = true;
                            }

                            for (k = 0; k <= 6; k++) {
                                char[] tmpStr = Util.digitDFA[state].toCharArray();
                                //依据当前状态索引DFA转换表
                                if (ch != '#' && Util.is_digit_state(ch, tmpStr[k]) == 1) {
                                    token.append(ch);
                                    state = k;
                                    break;
                                }
                            }
                            if (k > 6)
                                break;
                            i++;
                            if (i >= strLine.length)
                                break;
                            ch = strLine[i];
                        }
                        boolean haveMistake = false;
                        // 非终态
                        if (state == 2 || state == 4 || state == 5) {
                            haveMistake = true;
                        }
                        // 无符号数后面紧跟的符号错误
                        else {
                            if ((ch == '.') || (!Util.isOperator(String.valueOf(ch))
                                    && !Util.isDigit(ch) && !Util.isDelimiter(String.valueOf(ch))
                                    && ch != ' '))
                                haveMistake = true;
                        }
                        // 错误处理策略是直接读取到下一个界符
                        if (haveMistake) {
                            while (ch != '\0' && ch != ',' && ch != ';' && ch != ' ') {
                                token.append(ch);
                                i++;
                                if (i >= strLine.length)
                                    break;
                                ch = strLine[i];
                            }
                            DefaultTableModel tableModel2 = (DefaultTableModel) errorTable.getModel();
                            tableModel2.addRow(new Object[]{m + 1, token.toString(), "unsigned int error", "ERROR"});
                            errorTable.invalidate();
                        } else {
                            if (constant.isEmpty() || !constant.containsKey(token.toString())) {
                                constant.put(token.toString(), constant_pos);
                                constant_pos++;
                            }
                            if (isSci_not) {
                                DefaultTableModel tableModel1 = (DefaultTableModel) mainTable.getModel();
                                tableModel1.addRow(new Object[]{m + 1, token.toString(), "SCONST", token.toString()});
                                mainTable.invalidate();
                            } else if (isFloat) {
                                DefaultTableModel tableModel1 = (DefaultTableModel) mainTable.getModel();
                                tableModel1.addRow(new Object[]{m + 1, token.toString(), "FCONST", token.toString()});
                                mainTable.invalidate();
                            } else {
                                DefaultTableModel tableModel1 = (DefaultTableModel) mainTable.getModel();
                                tableModel1.addRow(new Object[]{m + 1, token.toString(), "CONST", token.toString()});
                                mainTable.invalidate();
                            }
                        }
                        i--;
                    }
                    // 识别字符常量
                    else if (ch == '\'') {
                        int state = 0;
                        token.append(ch);
                        while (state != 3) {
                            i++;
                            if (i >= strLine.length)
                                break;
                            ch = strLine[i];
                            boolean flag = false;
                            for (int k = 0; k < 4; k++) {
                                char[] tmpStr = Util.charDFA[state].toCharArray();
                                if (Util.is_char_state(ch, tmpStr[k])) {
                                    token.append(ch);
                                    state = k;
                                    flag = true;
                                    break;
                                }
                            }
                            if (!flag)
                                break;
                        }
                        if (state != 3) {
                            DefaultTableModel tableModel2 = (DefaultTableModel) errorTable.getModel();
                            tableModel2.addRow(new Object[]{m + 1, token.toString(), "String error", "ERROR"});
                            errorTable.invalidate();
                            i--;
                        } else {
                            if (constant.isEmpty() || !constant.containsKey(token.toString())) {
                                constant.put(token.toString(), constant_pos);
                                constant_pos++;
                            }
                            DefaultTableModel tableModel1 = (DefaultTableModel) mainTable.getModel();
                            tableModel1.addRow(new Object[]{m + 1, token.toString(), "CCONST", token.toString()});
                            mainTable.invalidate();
                        }
                    }
                    // 识别字符串常量
                    else if (ch == '"') {
                        boolean haveMistake = false;
                        StringBuilder str1 = new StringBuilder();
                        str1.append(ch);
                        int state = 0;
                        while (state != 3) {
                            i++;
                            if (i >= strLine.length - 1) {
                                haveMistake = true;
                                break;
                            }
                            ch = strLine[i];
                            if (ch == '\0') {
                                haveMistake = true;
                                break;
                            }
                            for (int k = 0; k < 4; k++) {
                                char[] tmpStr = Util.stringDFA[state].toCharArray();
                                if (Util.is_string_state(ch, tmpStr[k])) {
                                    str1.append(ch);
                                    if (k == 2 && state == 1)  // 转义字符  
                                    {
                                        if (Util.isEsSt(ch))
                                            token.append('\\').append(ch);
                                        else
                                            token.append(ch);
                                    } else if (k != 3 && k != 1)
                                        token.append(ch);
                                    state = k;
                                    break;
                                }
                            }
                        }
                        if (haveMistake) {
                            DefaultTableModel tableModel2 = (DefaultTableModel) errorTable.getModel();
                            tableModel2.addRow(new Object[]{m + 1, str1.toString(), "String error"});
                            errorTable.invalidate();
                            i--;
                        } else {
                            if (constant.isEmpty() || !constant.containsKey(token.toString())) {
                                constant.put(token.toString(), constant_pos);
                                constant_pos++;
                            }
                            DefaultTableModel tableModel1 = (DefaultTableModel) mainTable.getModel();
                            tableModel1.addRow(new Object[]{m + 1, str1.toString(), "STRCONST", str1.toString()});
                            mainTable.invalidate();
                        }
                    }
                    //  识别注释//
                    else if (ch == '/') {
                        token.append(ch);
                        i++;
                        if (i >= strLine.length)
                            break;
                        ch = strLine[i];

                        //不是多行注释及单行注释
                        if (ch != '*' && ch != '/') {
                            if (ch == '=')
                                token.append(ch); // /=  
                            else {
                                i--; // 指针回退 
                            }
                            DefaultTableModel tableModel1 = (DefaultTableModel) mainTable.getModel();
                            tableModel1.addRow(new Object[]{m + 1, token.toString(), "OP", token.toString()});
                            mainTable.invalidate();
                        }
                        // 注释可能是‘//’也可能是‘/*’
                        else {
                            boolean haveMistake = false;
                            if (ch == '*') {
                                // ch == '*'
                                token.append(ch);
                                int state = 2;

                                while (state != 4) {
                                    if (i == strLine.length - 1) {
                                        token.append('\n');
                                        m++;
                                        if (m >= texts.length) {
                                            haveMistake = true;
                                            break;
                                        }
                                        str = texts[m];
                                        if (str.equals(""))
                                            continue;
                                        else {
                                            strLine = str.toCharArray();
                                            i = 0;
                                            ch = strLine[i];
                                        }
                                    } else {
                                        i++;
                                        ch = strLine[i];
                                    }

                                    for (int k = 2; k <= 4; k++) {
                                        char[] tmpStr = Util.noteDFA[state].toCharArray();
                                        if (Util.is_note_state(ch, tmpStr[k], state)) {
                                            token.append(ch);
                                            state = k;
                                            break;
                                        }
                                    }
                                }
                            } else {
                                //单行注释读取所有字符
                                int index = str.lastIndexOf("//");

                                String tmpStr = str.substring(index);
                                int tmpInt = tmpStr.length();
                                for (int k = 0; k < tmpInt; k++)
                                    i++;
                                token = new StringBuilder(tmpStr);

                            }
                            if (haveMistake) {
                                DefaultTableModel tableModel2 = (DefaultTableModel) errorTable.getModel();
                                tableModel2.addRow(new Object[]{m + 1, token.toString(), "Note error", "ERROR"});
                                errorTable.invalidate();
                                --i;
                            } else {
                                DefaultTableModel tableModel1 = (DefaultTableModel) mainTable.getModel();
                                tableModel1.addRow(new Object[]{m + 1, token.toString(), "NOTE", "-"});
                                mainTable.invalidate();
                            }
                        }
                    }
                    // 运算符和界符
                    else if (Util.isOperator(String.valueOf(ch)) || Util.isDelimiter(String.valueOf(ch))) {
                        token.append(ch);
                        if (Util.isPlusEqu(ch))  // 后面可以用一个"="
                        {
                            i++;
                            if (i >= strLine.length)
                                break;
                            ch = strLine[i];
                            if (ch == '=')
                                token.append(ch);
                            else {
                                if (Util.isPlusSame(strLine[i - 1]) && ch == strLine[i - 1])  // 后面可以用一个和自己一样的
                                    token.append(ch);
                                else
                                    i--;
                            }
                        }
                        if (token.length() == 1)  //判断是否为界符
                        {
                            String signal = token.toString();
                            DefaultTableModel tableModel1 = (DefaultTableModel) mainTable.getModel();
                            if (Util.isDelimiter(signal)) {
                                tableModel1.addRow(new Object[]{m + 1, token.toString(), Util.getName(token.toString()), "-"});
                            } else {
                                tableModel1.addRow(new Object[]{m + 1, token.toString(), "OP", token.toString()});
                            }
                            mainTable.invalidate();
                        } else {
                            DefaultTableModel tableModel1 = (DefaultTableModel) mainTable.getModel();
                            tableModel1.addRow(new Object[]{m + 1, token.toString(), "OP", token.toString()});
                            mainTable.invalidate();
                        }
                    }
                    //不合法字符
                    else {
                        if (ch != '\t' && ch != '\0' && ch != '\n' && ch != '\r') {
                            DefaultTableModel tableModel2 = (DefaultTableModel) errorTable.getModel();
                            tableModel2.addRow(new Object[]{m + 1, token.toString(), "Unknown char"});
                            errorTable.invalidate();
                            System.out.println(ch);
                        }
                    }
                }
            }
        }
    }
}
