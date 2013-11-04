package ru.study.gui;


import ru.study.crypto.CryptoWrapper;
import ru.study.crypto.RSACipher;
import ru.study.crypto.TooBigPlainMessageLen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;


/**
 * Created by Mark
 */
public class MainFrame extends JFrame {

    private static final int RSA_KEY_LEN = 2048;

    private MainFrame() {
        setTitle("crypto2");
        initGUI();
        initCryptoWrapper();
    }

    private void initCryptoWrapper() {
        cryptoWrapper = new CryptoWrapper(new RSACipher(RSA_KEY_LEN));
    }

    private void initGUI() {
        Color lightBlue = new Color(51, 204, 255);   // light blue
        Color lightYellow = new Color(255, 255, 215);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //init
        panelMain = new JPanel();
        panelBottom = new JPanel();
        buttonsVerticalPanel = new JPanel();
        utilsVerticalPanel = new JPanel();
        buttonEncrypt = new JButton("encrypt");
        buttonDecrypt = new JButton("decrypt");
        buttonFromFile = new JButton("open file");
        buttonRoll = new JButton("<- flip");
        buttonGetKey = new JButton("key?");
        buttonSaveResult = new JButton("save");
        fieldInput = new JTextArea("");
        fieldResult = new JTextArea("");
        fieldResult.setEditable(false);
        //set bg Color

        panelMain.setBackground(lightBlue);
        buttonEncrypt.setBackground(lightBlue);
        buttonsVerticalPanel.setBackground(lightBlue);
        utilsVerticalPanel.setBackground(lightBlue);
        panelBottom.setBackground(lightBlue);
        //preffered size
        fieldInput.setPreferredSize(new Dimension(250, 100));
        fieldResult.setPreferredSize(new Dimension(250, 100));
        fieldInput.setLineWrap(true);
        fieldInput.setWrapStyleWord(true);
        fieldResult.setLineWrap(true);
        fieldResult.setWrapStyleWord(true);
        //layouts
        setLayout(new BorderLayout());
        buttonsVerticalPanel.setLayout(new GridLayout(3, 1));
        utilsVerticalPanel.setLayout(new GridLayout(3, 1));
        panelMain.setLayout(new FlowLayout());
        // action listeners
        buttonEncrypt.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                buttonEncryptActionPefrormed();
            }
        });
        buttonRoll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonRollActionPerformed();
            }
        });
        buttonDecrypt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttonDecryptActionPerformed();
            }
        });
        buttonFromFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttonFromFileActionPerformed();
            }
        });
        buttonSaveResult.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttonSaveResultActionPerformed();
            }
        });
        buttonGetKey.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttonGetKeyActionPefrormed();

            }
        });
        fieldInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                lastAction = LastAction.INPUT_EDITED;
            }
        });
        // add components to panels

        buttonsVerticalPanel.add(buttonEncrypt);
        buttonsVerticalPanel.add(buttonDecrypt);
        buttonsVerticalPanel.add(buttonFromFile);
        panelMain.add(buttonsVerticalPanel);
        utilsVerticalPanel.add(buttonRoll);
        utilsVerticalPanel.add(buttonGetKey);
        panelMain.add(utilsVerticalPanel);
        panelMain.add(new JScrollPane(fieldInput) {{
            setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        }});
        panelMain.add(new JScrollPane(fieldResult) {{
            setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        }});
        panelMain.add(buttonSaveResult);

        //add panels to frame
        getContentPane().add(panelMain, BorderLayout.CENTER);
        getContentPane().add(panelBottom, BorderLayout.SOUTH);
        fieldInput.requestFocusInWindow();
        pack();
    }

    private void formatInput() {
        switch (lastAction) {
            case FILE_SELECTED: {
                fieldInput.setText("<file " + openedFile.getName() + ">");
                break;
            }

        }
    }

    private void buttonGetKeyActionPefrormed() {
        final JFrame f = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new KeyDialog(f, cryptoWrapper.getKey()).setVisible(true);
            }
        });

    }

    private void buttonRollActionPerformed() {
        if (fieldInput.getText().startsWith("<file")) {
            lastAction = LastAction.FLIP_FILE;
        } else {
            lastAction = LastAction.FLIP_TEXT;
        }
        String tmp = fieldResult.getText();
        fieldInput.setText(tmp);
        input = output.clone();
        output = null;
        if (!fieldInput.getText().isEmpty()) {
            buttonDecryptActionPerformed();
        }
    }


    private void buttonSaveResultActionPerformed() {
        JFileChooser chooser = new JFileChooser();
        int retrival = chooser.showSaveDialog(null);
        if (retrival == JFileChooser.APPROVE_OPTION) {
            try {
                byte[] bytes = output;
                System.out.println("Bytes for writing: " + Arrays.toString(bytes));
                BufferedOutputStream bos;
                FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile());
                bos = new BufferedOutputStream(fos);
                bos.write(bytes);
                bos.flush();
                bos.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    private void buttonFromFileActionPerformed() {

        JFileChooser fileopen = new JFileChooser();
        int ret = fileopen.showDialog(null, "specify file");
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileopen.getSelectedFile();
            openedFile = file;
            lastAction = LastAction.FILE_SELECTED;
            formatInput();
        }
    }


    private void buttonDecryptActionPerformed() {
        try {
            if (lastAction == LastAction.FILE_SELECTED) {
                output = cryptoWrapper.decrypt(openedFile);
                lastAction = LastAction.FILE_DECRYPT;

            } else if (lastAction == LastAction.FLIP_FILE) {
                output = cryptoWrapper.decrypt(input);
                lastAction = LastAction.FILE_DECRYPT;
            } else {
                output = cryptoWrapper.decrypt(input);
                lastAction = LastAction.TEXT_DECRYPT;
            }
            formatOutput();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getStackTrace());
        }
    }

    private void formatOutput() {
        switch (lastAction) {
            case FILE_DECRYPT: {
                fieldResult.setText("<decrypted " + openedFile.getName() + ">");
                break;
            }
            case TEXT_DECRYPT: {
                fieldResult.setText(new String(output));
                break;
            }
            case TEXT_ENCRYPT: {
                fieldResult.setText(cryptoWrapper.toHex(output));
                break;
            }
            case FILE_ENCRYPT: {
                fieldResult.setText("<encrypted " + openedFile.getName() + ">");
                break;
            }
        }
    }


    private void buttonEncryptActionPefrormed() {
        try {
            if (lastAction == LastAction.FILE_SELECTED) {
                input = cryptoWrapper.readBytesFromFile(openedFile);
                output = cryptoWrapper.encrypt(input);
                lastAction = LastAction.FILE_ENCRYPT;
            } else {
                if (lastAction == LastAction.INPUT_EDITED) {
                    input = fieldInput.getText().getBytes();
                }
                output = cryptoWrapper.encrypt(input);
                lastAction = LastAction.TEXT_ENCRYPT;

            }
            formatOutput();
        } catch (TooBigPlainMessageLen e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getStackTrace());
            repaint();
        }
    }

    public static void main(String[] args) throws
            ClassNotFoundException,
            UnsupportedLookAndFeelException,
            InstantiationException,
            IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new Thread(new Runnable() {
            @Override
            public void run() {
                new MainFrame().setVisible(true);
            }
        }).start();
    }

    enum LastAction {
        TEXT_ENCRYPT,
        FILE_ENCRYPT,
        TEXT_DECRYPT,
        FILE_DECRYPT,
        FILE_SELECTED,
        FLIP_FILE,
        FLIP_TEXT,
        INPUT_EDITED
    }

    private LastAction lastAction;
    private byte[] input;
    private byte[] output;
    private File openedFile;
    //-----------------------
    private JPanel panelMain;
    private JPanel buttonsVerticalPanel;
    private JPanel utilsVerticalPanel;
    private JPanel panelBottom;

    private JButton buttonEncrypt;
    private JButton buttonDecrypt;
    private JButton buttonFromFile;
    private JButton buttonRoll;
    private JButton buttonSaveResult;
    private JButton buttonGetKey;
    private JTextArea fieldResult;
    private JTextArea fieldInput;
    private CryptoWrapper cryptoWrapper;

}

