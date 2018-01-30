package io.xnzr.maped;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class JFilePicker extends JPanel {
    private String textFieldLabel;
    private String buttonLabel;

    private JLabel label;
    private JTextField textField;
    private JButton button;

    private JFileChooser fileChooser;
    private int mode;

    private ArrayList<IDelegade> callbackList;

    static final int MODE_OPEN = 1;
    static final int MODE_SAVE = 2;

    JFilePicker(String textFieldLabel, String buttonLabel) {
        this.textFieldLabel = textFieldLabel;
        this.buttonLabel = buttonLabel;

        fileChooser = new JFileChooser();

        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        // creates the GUI
        label = new JLabel(textFieldLabel);

        textField = new JTextField(30);
        button = new JButton(buttonLabel);

        callbackList = new ArrayList<>();

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                doAction();
            }
        });

        add(label);
        add(textField);
        add(button);

    }

    void addCallback(IDelegade callback) {
        callbackList.add(callback);
    }

    void doAction() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            textField.setText(fileChooser.getSelectedFile().getAbsolutePath());

            for (IDelegade c: callbackList) {
                c.call(fileChooser.getSelectedFile().getAbsolutePath());
            }
        }
    }



    void setAcceptAllFileFilterUsed(boolean value) {
        fileChooser.setAcceptAllFileFilterUsed(value);
    }

    void addFileTypeFilter(String extension, String description) {
        FileTypeFilter filter = new FileTypeFilter(extension, description);
        fileChooser.addChoosableFileFilter(filter);
    }

    void setMode(int mode) {
        this.mode = mode;
    }

    void setCurrentDirectory(String path) {
        Path p = Paths.get(path);
        if (Files.exists(p)) {
            if (!Files.isDirectory(p)) {
                p = p.getParent();
            }
            fileChooser.setCurrentDirectory(p.toFile());
        }
    }

    public String getSelectedFilePath() {
        return textField.getText();
    }

    public JFileChooser getFileChooser() {
        return this.fileChooser;
    }
}
