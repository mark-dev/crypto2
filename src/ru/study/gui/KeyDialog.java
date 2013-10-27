package ru.study.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: markdev
 * Date: 10/28/13
 * Time: 12:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class KeyDialog extends JDialog {
    public KeyDialog(Frame owner, String text) {
        super(owner);

        setTitle("Key info");
        final JDialog self = this;
        setLayout(new GridLayout(2, 1));
        JScrollPane sp = new JScrollPane(new JTextArea(text) {{
            setVisible(false);
        }});
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(sp);
        add(new JButton("close") {{
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    self.setVisible(false);
                }
            });
            setPreferredSize(new Dimension(80, 40));
        }});
        setLocationRelativeTo(owner);
        setPreferredSize(new Dimension(400, 200));
        pack();
         repaint();
    }
}
