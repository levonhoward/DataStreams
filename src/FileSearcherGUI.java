import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileSearcherGUI extends JFrame
{
    JPanel mainPnl, topPnl, bottomPnl, headerPnl, textLabelsPnl, textPnl, searchPnl, buttonPnl;
    JLabel headerLbl, textFileLbl, searchAreaLbl, searchBarLbl;
    JTextArea textFileTA, searchAreaTA;
    JScrollPane textFileSP, searchAreaSP;
    JTextField searchBarTF;
    JButton loadBtn, fileSearchBtn, quitBtn;
    ArrayList<String> loadedFile;

    public FileSearcherGUI()
    {
        // Build file list
        loadedFile = new ArrayList<>();

        // Build GUI
        mainPnl = new JPanel();
        mainPnl.setLayout(new BorderLayout());
        topPnl = new JPanel();
        topPnl.setLayout(new BorderLayout());
        bottomPnl = new JPanel();
        bottomPnl.setLayout(new BorderLayout());

        buildHeaderPanel();
        topPnl.add(headerPnl, BorderLayout.NORTH);
        buildTextLabelsPanel();
        topPnl.add(textLabelsPnl, BorderLayout.CENTER);
        buildTextPanel();
        topPnl.add(textPnl, BorderLayout.SOUTH);
        buildSearchPanel();
        bottomPnl.add(searchPnl, BorderLayout.NORTH);
        buildButtonPanel();
        bottomPnl.add(buttonPnl, BorderLayout.CENTER);

        // Finalize main panel
        mainPnl.add(topPnl, BorderLayout.NORTH);
        mainPnl.add(bottomPnl, BorderLayout.CENTER);
        add(mainPnl);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        setSize((screenSize.width / 4) * 3, (screenSize.height / 4) * 3);
        setLocation(screenSize.width / 8, screenSize.height / 8);
        setTitle("File Searcher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void buildHeaderPanel()
    {
        headerPnl = new JPanel();
        headerLbl = new JLabel("File Searcher");
        headerLbl.setFont(new Font("Arial", Font.PLAIN, 72));
        headerPnl.add(headerLbl);
    }

    private void buildTextLabelsPanel()
    {
        textLabelsPnl = new JPanel();
        textLabelsPnl.setLayout(new GridLayout(1, 2));

        textFileLbl = new JLabel("Text File");
        textFileLbl.setFont(new Font("Arial", Font.BOLD, 36));
        textLabelsPnl.add(textFileLbl);

        searchAreaLbl = new JLabel("Filtered Search");
        searchAreaLbl.setFont(new Font("Arial", Font.BOLD, 36));
        textLabelsPnl.add(searchAreaLbl);
    }

    private void buildTextPanel()
    {
        textPnl = new JPanel();
        textPnl.setLayout(new GridLayout(1, 2));

        textFileTA = new JTextArea(20, 10);
        textFileTA.setEditable(false);
        textFileTA.setFont(new Font("Times New Roman", Font.PLAIN, 24));
        textFileSP = new JScrollPane(textFileTA);
        textPnl.add(textFileSP);

        searchAreaTA = new JTextArea(20, 10);
        searchAreaTA.setEditable(false);
        searchAreaTA.setFont(new Font("Times New Roman", Font.PLAIN, 24));
        searchAreaSP = new JScrollPane(searchAreaTA);
        textPnl.add(searchAreaSP);
    }

    private void buildSearchPanel()
    {
        searchPnl = new JPanel();
        searchPnl.setLayout(new BorderLayout());
        searchBarLbl = new JLabel("Search...");
        searchBarLbl.setFont(new Font("Arial", Font.ITALIC, 36));
        searchPnl.add(searchBarLbl, BorderLayout.NORTH);
        searchBarTF = new JTextField(20);
        searchBarTF.setFont(new Font("Arial", Font.PLAIN, 24));
        searchPnl.add(searchBarTF, BorderLayout.CENTER);
    }

    private void buildButtonPanel()
    {
        buttonPnl = new JPanel();
        buttonPnl.setLayout(new GridLayout(1, 3));

        loadBtn = new JButton("Load File");
        loadBtn.setFont(new Font("Arial", Font.BOLD, 36));
        loadBtn.addActionListener((ActionEvent ae) ->
        {
            loadFile();
        });
        buttonPnl.add(loadBtn);

        fileSearchBtn = new JButton("Search");
        fileSearchBtn.setFont(new Font("Arial", Font.BOLD, 36));
        fileSearchBtn.addActionListener((ActionEvent ae) ->
        {
            searchFile();
        });
        buttonPnl.add(fileSearchBtn);

        quitBtn = new JButton("Quit");
        quitBtn.setFont(new Font("Arial", Font.BOLD, 36));
        quitBtn.addActionListener((ActionEvent ae) ->
        {
            if (JOptionPane.showConfirmDialog(mainPnl, "Are you sure you want to quit?", "Quit?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
                System.exit(0);
            }
        });
        buttonPnl.add(quitBtn);
    }

    private void loadFile()
    {

        JFileChooser chooser = new JFileChooser();
        Path selectedFile;

        File workingDirectory = new File(System.getProperty("user.dir"));
        chooser.setCurrentDirectory(workingDirectory);

        if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile().toPath();

            // Clear last file
            loadedFile.clear();
            textFileTA.setText("");
            searchAreaTA.setText("");

            try (Stream<String> lines = Files.lines(selectedFile))
            {
                lines.forEach(line ->
                {
                    loadedFile.add(line);
                    textFileTA.append(line + "\n");
                });
            }
            catch (IOException e)
            {
                textFileTA.append("File not found! " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
        {
            JOptionPane.showMessageDialog(mainPnl, "No file selected!", "ERROR: No File To Read!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchFile()
    {
        String search = searchBarTF.getText();

        if (loadedFile.isEmpty())
        {
            JOptionPane.showMessageDialog(mainPnl, "Load a file before searching!", "ERROR: No File To Search!", JOptionPane.ERROR_MESSAGE);
        }
        else if (search.isEmpty())
        {
            JOptionPane.showMessageDialog(mainPnl, "Enter a search term before searching!", "ERROR: No Term To Search For!", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            searchAreaTA.setText("");

            ArrayList<String> searchResults = loadedFile.stream()
                    .filter(line -> line.contains(search))
                    .collect(Collectors.toCollection(ArrayList::new));

            if (searchResults.isEmpty())
            {
                JOptionPane.showMessageDialog(mainPnl, ("No matches for '" + search + "' in the loaded file."), "No Matches Found", JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
                searchResults.forEach(line -> searchAreaTA.append(line + "\n"));
            }
        }
    }
}
