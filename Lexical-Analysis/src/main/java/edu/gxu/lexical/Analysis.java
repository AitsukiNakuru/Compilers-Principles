package edu.gxu.lexical;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;


public class Analysis {
    private final String text;  // ����Ĳ��������ı�
    private final JTable mainTable;  // ����-Token-�ֱ���-�������
    private final JTable errorTable;  // ����-��������-������Ϣ

    public Analysis(String text, JTable mainTable, JTable errorTable) {
        this.text = text;
        this.mainTable = mainTable;
        this.errorTable = errorTable;
    }

    public static int symbol_pos = 0;  // ��¼���ű�λ��
    public static Map<String, Integer> symbol = new HashMap<>();  // ���ű�HashMap

    public static int constant_pos = 0;  // ��¼����λ��
    public static Map<String, Integer> constant = new HashMap<>();  // ������HashMap

    public void lex() {
        String[] texts = text.split("\n");
        //���зָ�
        symbol.clear();
        //������ű�
        symbol_pos = 0;
        constant.clear();
        //���������
        constant_pos = 0;
        for (int m = 0; m < texts.length; m++) {
            String str = texts[m];
            if (str.equals(""))
                continue;
                //���Կ���
            else {
                char[] strLine = str.toCharArray();
                for (int i = 0; i < strLine.length; i++) {
                    //����ַ��ķ�������
                    char ch = strLine[i];
                    if (ch == ' ')
                        continue;

                    StringBuilder token = new StringBuilder();
                    // ʶ��ؼ��ֺͱ�ʶ��
                    if (Util.isAlpha(ch)) {
                        do {
                            token.append(ch);
                            i++;
                            if (i >= strLine.length)
                                break;
                            ch = strLine[i];
                        } while (ch != '\0' && (Util.isAlpha(ch) || Util.isDigit(ch)));
                        i--;
                        // ʶ��ؼ���
                        if (Util.isKeyword(token.toString())) {
                            DefaultTableModel tableModel = (DefaultTableModel) mainTable.getModel();
                            tableModel.addRow(new Object[]{m + 1, token.toString(), token.toString().toUpperCase(), "-"});
                            mainTable.invalidate();
                        }
                        // ʶ���ʶ��
                        else {
                            //��ǰʶ�𵽵�tokenû���ظ�����
                            if (symbol.isEmpty() || !symbol.containsKey(token.toString())) {
                                symbol.put(token.toString(), symbol_pos);
                                symbol_pos++;
                            }
                            DefaultTableModel tableModel1 = (DefaultTableModel) mainTable.getModel();
                            tableModel1.addRow(new Object[]{m + 1, token.toString(), "IDN", token.toString()});
                            mainTable.invalidate();
                        }

                    }
                    // ʶ���޷�����
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
                                //���ݵ�ǰ״̬����DFAת����
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
                        // ����̬
                        if (state == 2 || state == 4 || state == 5) {
                            haveMistake = true;
                        }
                        // �޷�������������ķ��Ŵ���
                        else {
                            if ((ch == '.') || (!Util.isOperator(String.valueOf(ch))
                                    && !Util.isDigit(ch) && !Util.isDelimiter(String.valueOf(ch))
                                    && ch != ' '))
                                haveMistake = true;
                        }
                        // �����������ֱ�Ӷ�ȡ����һ�����
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
                    // ʶ���ַ�����
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
                    // ʶ���ַ�������
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
                                    if (k == 2 && state == 1)  // ת���ַ�  
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
                    //  ʶ��ע��//
                    else if (ch == '/') {
                        token.append(ch);
                        i++;
                        if (i >= strLine.length)
                            break;
                        ch = strLine[i];

                        //���Ƕ���ע�ͼ�����ע��
                        if (ch != '*' && ch != '/') {
                            if (ch == '=')
                                token.append(ch); // /=  
                            else {
                                i--; // ָ����� 
                            }
                            DefaultTableModel tableModel1 = (DefaultTableModel) mainTable.getModel();
                            tableModel1.addRow(new Object[]{m + 1, token.toString(), "OP", token.toString()});
                            mainTable.invalidate();
                        }
                        // ע�Ϳ����ǡ�//��Ҳ�����ǡ�/*��
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
                                //����ע�Ͷ�ȡ�����ַ�
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
                    // ������ͽ��
                    else if (Util.isOperator(String.valueOf(ch)) || Util.isDelimiter(String.valueOf(ch))) {
                        token.append(ch);
                        if (Util.isPlusEqu(ch))  // ���������һ��"="
                        {
                            i++;
                            if (i >= strLine.length)
                                break;
                            ch = strLine[i];
                            if (ch == '=')
                                token.append(ch);
                            else {
                                if (Util.isPlusSame(strLine[i - 1]) && ch == strLine[i - 1])  // ���������һ�����Լ�һ����
                                    token.append(ch);
                                else
                                    i--;
                            }
                        }
                        if (token.length() == 1)  //�ж��Ƿ�Ϊ���
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
                    //���Ϸ��ַ�
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
