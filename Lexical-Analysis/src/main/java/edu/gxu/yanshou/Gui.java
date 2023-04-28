package edu.gxu.yanshou;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.Serial;
import java.util.List;

public class Gui extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;

    public Gui() {
        Container container = getContentPane();
        container.setForeground(Color.WHITE);
        setTitle("词法分析器");    //设置显示窗口标题
        setSize(1200, 700);    //设置窗口显示尺寸
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //置窗口是否可以关闭
        container.setLayout(null);//设置为绝对定位
        //输入框
        JScrollPane inputScrollPane = new JScrollPane();
        inputScrollPane.setBounds(30, 20, 450, 500);
        inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);//总是显示垂直滚动条，即使内容行数很少；
        //	inputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)  ;  //在需要的时候再显示水平滚动条。此为默认值。

        container.add(inputScrollPane);
        JTextArea textArea = new JTextArea();
        inputScrollPane.setViewportView(textArea);
        //结果输出框
        JScrollPane onputScrollPane = new JScrollPane();
        onputScrollPane.setToolTipText("");
        onputScrollPane.setBackground(SystemColor.menu);
        onputScrollPane.setBounds(520, 20, 650, 600);
        onputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        container.add(onputScrollPane);
        //结果显示表格
        String[] resultTableTitleList = new String[]{"行号", "Token", "Token类型", "种别码"};
        JTable resultTable = new JTable(new DefaultTableModel(new Object[][]{}, resultTableTitleList)){
            public boolean isCellEditable(int row, int column) {
                return false;
            }// 表格不允许被编辑
        };
        //resultTable.removeEditor();
        //分析按钮
        resultTable.setForeground(Color.BLACK);
        resultTable.setFillsViewportHeight(true);
        resultTable.setBackground(new Color(255, 255, 255));
        onputScrollPane.setViewportView(resultTable);
        JButton analysisButton = new JButton("词法分析");
        analysisButton.setFont(new Font("宋体", Font.BOLD, 22));

        analysisButton.addActionListener(e -> {
            Analysis analysis = new Analysis(textArea.getText());
            List<Token> tokenList = analysis.analyze();
            DefaultTableModel resultTableModel = (DefaultTableModel) resultTable.getModel();
            resultTableModel.setRowCount(0);
            for (Token token : tokenList) {
                resultTableModel.addRow(new Object[]{token.lineNumber, token.value, token.type, token.categoryCode});
            }
            if (resultTable.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "请输入正确文本", "Warning", JOptionPane.PLAIN_MESSAGE);
            }
        });
        analysisButton.setBounds(160, 540, 130, 50);
        container.add(analysisButton);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Gui();
    }
}
/*
int36 a=A7,b=5TU,z=A+B;
int c=10;
 z=z+10;
  c=c+1;
 */