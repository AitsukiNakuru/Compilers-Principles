package edu.gxu.my;

import edu.gxu.my.Analysis;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.io.File;
import java.io.InputStream;
import java.io.Serial;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Gui extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;

    public Gui() {
        getContentPane().setForeground(Color.WHITE);
        setTitle("词法分析器");    //设置显示窗口标题
        setSize(1600, 900);    //设置窗口显示尺寸
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //置窗口是否可以关闭
        getContentPane().setLayout(null);//设置为绝对定位

        JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setBounds(0, 0, 650, 800);
        scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(scrollPane1);
        JTextArea textArea = new JTextArea();
        scrollPane1.setViewportView(textArea);
        JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setToolTipText("");
        scrollPane2.setBackground(SystemColor.menu);
        scrollPane2.setBounds(850, 0, 650, 800);
        scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(scrollPane2);
        String[] resultTableTitleList = new String[]{"Line Number", "Token", "Type", "Code"};
        JTable resultTable = new JTable(new DefaultTableModel(new Object[][]{}, resultTableTitleList));
        DefaultTableModel resultTableModel = (DefaultTableModel) resultTable.getModel();
        resultTable.setForeground(Color.BLACK);
        resultTable.setFillsViewportHeight(true);
        resultTable.setBackground(new Color(255, 255, 255));
        scrollPane2.setViewportView(resultTable);
        JButton analysisButton = new JButton("analysis");
        analysisButton.addActionListener(e -> {
            Analysis analysis = new Analysis(textArea.getText());
            List<Token> tokenList = analysis.analyze();
            for (Token token : tokenList) {
                resultTableModel.addRow(new Object[]{token.lineNumber, token.value, token.type, token.categoryCode});
            }
            if (resultTable.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "请输入正确文本", "Warning", JOptionPane.PLAIN_MESSAGE);
            }
        });

        analysisButton.setBounds(650, 0, 200, 50);
        getContentPane().add(analysisButton);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Gui();
    }
}
