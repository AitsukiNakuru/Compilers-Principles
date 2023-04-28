package edu.gxu;

import edu.gxu.grammar.AnalyzeStep;
import edu.gxu.grammar.GrammarAnalysis;
import edu.gxu.lexical.LexicalAnalysis;
import edu.gxu.lexical.Token;
import mdlaf.MaterialLookAndFeel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Gui extends JFrame {
    public Gui() {
        /*try {
            UIManager.setLookAndFeel(new MaterialLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }*/
        getContentPane().setForeground(Color.WHITE);
        setTitle("语法分析器");    //设置显示窗口标题
        setSize(1600, 900);    //设置窗口显示尺寸
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //置窗口是否可以关闭
        getContentPane().setLayout(null);//设置为绝对定位

        JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setBounds(0, 0, 400, 800);
        scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(scrollPane1);
        JTextArea textArea = new JTextArea();
        scrollPane1.setViewportView(textArea);


        JButton analysisButton = new JButton("Analyze");
        analysisButton.setBounds(100, 800, 100, 50);

        getContentPane().add(analysisButton);


        JScrollPane firstScrollPane = new JScrollPane();
        firstScrollPane.setBounds(400, 0, 400, 450);
        firstScrollPane.setBackground(SystemColor.menu);
        String[] firstTitleList = new String[]{"Char", "First"};
        JTable firstTable = new JTable(new DefaultTableModel(new Object[][]{}, firstTitleList));
        firstTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        firstScrollPane.setViewportView(firstTable);
        firstScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(firstScrollPane);

        JScrollPane followScrollPane = new JScrollPane();
        followScrollPane.setBounds(400, 450, 400, 450);
        followScrollPane.setBackground(SystemColor.menu);
        String[] followTitleList = new String[]{"Char", "Follow"};
        JTable followTable = new JTable(new DefaultTableModel(new Object[][]{}, followTitleList));
        followTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        followScrollPane.setViewportView(followTable);
        followScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(followScrollPane);

        JScrollPane analyzeScrollPane = new JScrollPane();
        analyzeScrollPane.setBounds(800, 0, 800, 450);
        analyzeScrollPane.setBackground(SystemColor.menu);
        String[] analyzeTitleList = new String[]{"IDK", "IDK"};
        JTable analyzeTable = new JTable(new DefaultTableModel(new Object[][]{}, analyzeTitleList));
        analyzeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        analyzeScrollPane.setViewportView(analyzeTable);
        analyzeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(analyzeScrollPane);


        JScrollPane resultScrollPane = new JScrollPane();
        resultScrollPane.setBounds(800, 450, 800, 450);
        resultScrollPane.setBackground(SystemColor.menu);
        String[] resultTitleList = new String[]{
                "Step", "State Stack", "Char Stack", "Input Stack", "Action Type",
                "Production", "Action", "GoTo"
        };
        JTable resultTable = new JTable(new DefaultTableModel(new Object[][]{}, resultTitleList));
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        resultScrollPane.setViewportView(resultTable);
        resultScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(resultScrollPane);
        analysisButton.addActionListener(e -> {
            System.out.println(textArea.getText());
            LexicalAnalysis lexicalAnalysis = new LexicalAnalysis(textArea.getText());
            ArrayList<Token> tokenList = lexicalAnalysis.analyze();
            System.out.println(tokenList.toString());
            //DefaultTableModel firstTableModel = (DefaultTableModel) firstTable.getModel();
            //firstTableModel.setRowCount(0);
            try {
                GrammarAnalysis grammarAnalysis = new GrammarAnalysis(tokenList);
                grammarAnalysis.analysis();
                ArrayList<AnalyzeStep> analyzeStepList = grammarAnalysis.getAnalyzeStepList();
                

            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }

        });
        setVisible(true);
    }
}
