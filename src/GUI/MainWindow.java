package GUI;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import com.jgoodies.looks.windows.WindowsLookAndFeel;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

public class MainWindow extends JFrame {

    private JFrame parent;

    private JPanel sourceDirSelectPanel;
    private JLabel sourceDirSelectLabel;
    private JTextField sourceDirTextField;
    private JButton sourceDirBrowseButton;
    private JFileChooser sourceDirBrowse;
    private JButton sourceDirAddButton;

    private JPanel sourceDirsPanel;
    private CustomTableModel tableData;
    private JTable sourceDirTable;
    private JScrollPane sourceDirTableScrollPane;

    private JPanel destinationDirPanel;
    private JLabel destinationDirLabel;
    private JTextField destinationDirTextField;
    private JFileChooser destinationDirBrowse;
    private JButton destinationDirBrowseButton;

    private JPanel settingsPanel;
    private JLabel maxSizeLabel;
    private JTextField maxSizeField;

    private JPanel buttonPanel;
    private JButton generateButton;

    private JPanel progressPanel;
    private JProgressBar generateProgress;

    private JPanel logPanel;
    private JTextArea log;
    private JScrollPane logScroll;

    public MainWindow() {
        super("Random Playlist Generator");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException ex) {
            return;
        } catch (ClassNotFoundException e) {
            return;
        } catch (InstantiationException e) {
            return;
        } catch (IllegalAccessException e) {
            return;
        }

        parent = this;
        initComponents();
        buildPanels();
        addActionListeners();

        Container content = getContentPane();
        setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(sourceDirSelectPanel);
        content.add(sourceDirsPanel);
        content.add(destinationDirPanel);
        content.add(settingsPanel);
        content.add(buttonPanel);
        content.add(progressPanel);
        content.add(logPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        sourceDirSelectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sourceDirSelectLabel = new JLabel("Source Directory");
        sourceDirTextField = new JTextField("C:\\", 25);
        sourceDirBrowseButton = new JButton("Browse");
        sourceDirBrowse = new JFileChooser();
        sourceDirAddButton = new JButton("Add");

        sourceDirsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tableData = new CustomTableModel();
        sourceDirTable = new JTable(tableData);
        sourceDirTableScrollPane = new JScrollPane(sourceDirTable);

        destinationDirPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        destinationDirLabel = new JLabel("Destination Directory");
        destinationDirTextField = new JTextField("D:\\", 25);
        destinationDirBrowse = new JFileChooser();
        destinationDirBrowseButton = new JButton("Browse");

        settingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        maxSizeLabel = new JLabel("Max Size(Mb)");
        maxSizeField = new JTextField("700", 8);

        buttonPanel = new JPanel(new FlowLayout());
        generateButton = new JButton("Generate");

        progressPanel = new JPanel(new FlowLayout());
        generateProgress = new JProgressBar(0, 100);

        logPanel = new JPanel(new FlowLayout());
        log = new JTextArea();
        logScroll = new JScrollPane(log);
    }

    private void addActionListeners() {
        sourceDirBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sourceDirBrowse.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = sourceDirBrowse.showOpenDialog(null);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File selectedSource = sourceDirBrowse.getSelectedFile();

                    if (selectedSource != null) {
                        sourceDirTextField.setText(selectedSource.getAbsolutePath());
                        DirectoryRow newRow = new DirectoryRow(selectedSource.getAbsolutePath(), "0");
                        tableData.addRow(newRow);
                    }
                }
            }
        });

        sourceDirAddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = sourceDirTextField.getText();

                if (!filePath.equals("")) {
                    DirectoryRow newRow = new DirectoryRow(filePath, "0");
                    tableData.addRow(newRow);
                }
            }
        });

        destinationDirBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                destinationDirBrowse.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = destinationDirBrowse.showOpenDialog(null);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File selectedSource = destinationDirBrowse.getSelectedFile();

                    if (selectedSource != null) {
                        destinationDirTextField.setText(selectedSource.getAbsolutePath());
                    }
                }
            }
        });

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GeneratorController genController = new GeneratorController(generateProgress, log, tableData);

                // Build sourceList
                ArrayList<File> sourceList = new ArrayList<File>();
                for (int i = 0; i < tableData.getRowCount(); i++) {
                    String dirPath = (String)tableData.getValueAt(i, 0);
                    File dir = new File(dirPath);

                    if (dir.exists()) {
                        sourceList.add(dir);
                    }
                }

                if (sourceList.isEmpty()) {
                    JOptionPane.showMessageDialog(parent,"Please add at least one source directory.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (destinationDirTextField.getText().equals("")) {
                    JOptionPane.showMessageDialog(parent,"Please select a destination directory", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                File destinationDir = new File(destinationDirTextField.getText());

                genController.GeneratePlaylist(sourceList, destinationDir, Long.parseLong(maxSizeField.getText()));
            }
        });

    }

    private void buildPanels() {
        sourceDirSelectPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sourceDirSelectPanel.add(sourceDirSelectLabel);
        sourceDirSelectPanel.add(sourceDirTextField);
        sourceDirSelectPanel.add(sourceDirBrowseButton);
        sourceDirSelectPanel.add(sourceDirAddButton);

        tableData.setColumnWidths(sourceDirTable.getColumnModel());
        sourceDirTableScrollPane.setPreferredSize(new Dimension(530, 100));
        sourceDirsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sourceDirsPanel.add(sourceDirTableScrollPane);

        destinationDirPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        destinationDirPanel.add(destinationDirLabel);
        destinationDirPanel.add(destinationDirTextField);
        destinationDirPanel.add(destinationDirBrowseButton);

        settingsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        settingsPanel.add(maxSizeLabel);
        settingsPanel.add(maxSizeField);

        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.add(generateButton);

        progressPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        generateProgress.setPreferredSize(new Dimension(500, 18));
        generateProgress.setStringPainted(true);
        progressPanel.add(generateProgress);

        logPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        log.setPreferredSize(new Dimension(500, 100));
        log.append("Welcome to the Random Playlist Generator!");
        logPanel.add(logScroll);
    }

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
    }
}
