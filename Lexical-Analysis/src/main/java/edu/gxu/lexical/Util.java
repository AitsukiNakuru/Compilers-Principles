package edu.gxu.lexical;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

public class Util {

    //�ؼ���
    public static String[] keywords = {"auto", "double", "int", "struct",
            "break", "else", "long", "switch", "case", "enum", "register",
            "typedef", "char", "extern", "return", "union", "const", "float",
            "short", "unsigned", "continue", "for", "signed", "void",
            "default", "goto", "sizeof", "volatile", "do", "if", "while",
            "static", "String"};
    public static Map<String, Integer> keywords_code = new HashMap<>() {
        @Serial
        private static final long serialVersionUID = 1L;

        {
            for (int i = 0; i < keywords.length; i++) {
                put(keywords[i], i + 101);
            }
        }
    };

    public static Boolean isKeyword(String s) {
        return keywords_code.containsKey(s);
    }

    public static String[] operator = {"+", "-", "*", "/", "%", "++", "--",
            "<", "<=", ">", ">=", "==", "!=",
            "&&", "||", "!", "~", "&", "|", "^", ">>", "<<",
            "+=", "-=", "*=", "/=", "%=", "&=", " ^=", "|=", "<<=", ">>="};
    public static Map<String, Integer> operator_code = new HashMap<>() {
        @Serial
        private static final long serialVersionUID = 1L;

        {
            for (int i = 0; i < operator.length; i++) {
                put(operator[i], i + 201);
            }
        }
    };

    public static Boolean isOperator(String s) {
        return operator_code.containsKey(s);
    }

    public static String getName(String s) {
        if (s.equals(","))
            return "COL";
        if (s.equals(";"))
            return "SEMI";
        if (s.equals("["))
            return "MLP";
        if (s.equals("]"))
            return "MRP";
        if (s.equals("{"))
            return "LP";
        if (s.equals("}"))
            return "RP";
        if (s.equals("("))
            return "SLP";
        if (s.equals(")"))
            return "SRP";
        if (s.equals("="))
            return "EQU";
        return "ERROR";

    }

    public static String[] delimiter = {",", ";", "[", "]", "{", "}", "(", ")", "="};
    public static Map<String, Integer> delimiter_code = new HashMap<>() {
        @Serial
        private static final long serialVersionUID = 1L;

        {
            for (int i = 0; i < delimiter.length; i++) {
                put(delimiter[i], i + 301);
            }
        }
    };

    public static Boolean isDelimiter(String s) {
        return delimiter_code.containsKey(s);
    }


    public static Boolean isPlusEqu(char ch)  // ��Щ���ź���ɸ������"="
    {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '=' || ch == '>'
                || ch == '<' || ch == '&' || ch == '|' || ch == '^' || ch == '%' || ch == '!';
    }

    public static Boolean isPlusSame(char ch)  // ��Щ���ź�����ٸ���ͬ�����
    {
        return ch == '+' || ch == '-' || ch == '&' || ch == '|' || ch == '>' || ch == '<';
    }

    public static Boolean isAlpha(char ch) {
        return ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_');
    }

    public static Boolean isDigit(char ch) {
        return (ch >= '0' && ch <= '9');
    }


    // String DFA : a���������ַ���b�����\��"֮����ַ�
    public static String[] stringDFA =
            {
                    "#\\b#",
                    "##a#",
                    "#\\b\"",
                    "####"
            };

    /**
     * �ַ���DFA״̬ƥ�亯��
     *
     * @param ch  ��ǰ�ַ�
     * @param key ״̬���е��ַ�
     * @return ƥ��ɹ�����true�����򷵻�false
     */
    public static Boolean is_string_state(char ch, char key) {
        if (key == 'a')
            return true;
        if (key == '\\')
            return ch == key;
        if (key == '"')
            return ch == key;
        if (key == 'b')
            return ch != '\\' && ch != '"';
        return false;
    }

    // char DFA : a���������ַ���b�����\��'֮����ַ�
    public static String[] charDFA =
            {
                    "#\\b#",
                    "##a#",
                    "###\'",
                    "####"
            };

    /**
     * �ַ�DFA״̬ƥ�亯��
     *
     * @param ch  ��ǰ�ַ�
     * @param key ״̬���е��ַ�
     * @return ƥ��ɹ�����true�����򷵻�false
     */
    public static Boolean is_char_state(char ch, char key) {
        if (key == 'a')
            return true;
        if (key == '\\')
            return ch == key;
        if (key == '\'')
            return ch == key;
        if (key == 'b')
            return ch != '\\' && ch != '\'';
        return false;
    }

    public static Boolean isEsSt(char ch) {
        return ch == 'a' || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r'
                || ch == 't' || ch == 'v' || ch == '?' || ch == '0';
    }

    public static String[] digitDFA = {
            "#d#####",
            "#d.#e##",
            "###d###",
            "###de##",
            "#####-d",
            "######d",
            "######d"
    };
    /**
     * ����DFA״̬ƥ�亯��
     *
     * @param ch   ��ǰ�ַ�
     * @param test ״̬���е��ַ�
     * @return ƥ��ɹ�����true�����򷵻�false
     */
    public static int is_digit_state(char ch, char test) {
        if (test == 'd') {
            if (isDigit(ch))
                return 1;
            else
                return 0;
        } else if (test == '-') {
            if (ch == '-' || ch == '+')
                return 1;
            else
                return 0;
        } else if (test == 'e') {
            if (ch == 'e' || ch == 'E')
                return 1;
            else
                return 0;
        } else {
            if (ch == test)
                return 1;
            else
                return 0;
        }
    }


    public static String[] noteDFA = {
            "#/###",
            "##*##",
            "##c*#",
            "##c*/",
            "#####"
    };
    /**
     * ע��DFA״̬ƥ�亯��
     *
     * @param ch ��ǰ�ַ�
     * @param nD ״̬���е��ַ�
     * @return ƥ��ɹ�����true�����򷵻�false
     */
    public static Boolean is_note_state(char ch, char nD, int s) {
        if (s == 2) {
            if (nD == 'c') {
                return ch != '*';
            }
        }
        if (s == 3) {
            if (nD == 'c') {
                return ch != '*' && ch != '/';
            }
        }
        return ch == nD;
    }
}
