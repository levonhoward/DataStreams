import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;

public class FileSearcherGUI extends JFrame
{
    JPanel mainPnl, topPnl, bottomPnl, headerPnl, textPnl, searchPnl, buttonPnl;
    JLabel headerLbl, textFileLbl, searchAreaLbl, searchBarLbl;
    JTextArea textFileTA, searchAreaTA;
    JScrollPane textFileSP, searchAreaSP;
    JTextField searchBarTF;
    JButton loadBtn, fileSearchBtn, quitBtn;

    public FileSearcherGUI()
    {
        // Build GUI
        mainPnl = new JPanel();
        mainPnl.setLayout(new BorderLayout());
        topPnl = new JPanel();
        topPnl.setLayout(new BorderLayout());
        bottomPnl = new JPanel();
        bottomPnl.setLayout(new BorderLayout());

        buildHeaderPanel();
        topPnl.add(headerPnl, BorderLayout.NORTH);
        buildTextPanel();
        topPnl.add(textPnl, BorderLayout.SOUTH);
        buildSearchPanel();
        bottomPnl.add(searchPnl, BorderLayout.NORTH);
        buildButtonPanel();
        bottomPnl.add(buttonPnl, BorderLayout.SOUTH);

        // Finalize main panel
        mainPnl.add(topPnl, BorderLayout.NORTH);
        mainPnl.add(bottomPnl, BorderLayout.SOUTH);
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

    private void buildTextPanel()
    {
        textPnl = new JPanel();
        textPnl.setLayout(new GridLayout(2, 2));

        textFileLbl = new JLabel("Text File");
        textFileLbl.setFont(new Font("Arial", Font.BOLD, 24));
        textPnl.add(textFileLbl);

        searchAreaLbl = new JLabel("Filtered Search");
        searchAreaLbl.setFont(new Font("Arial", Font.BOLD, 24));
        textPnl.add(searchAreaLbl);

        textFileTA = new JTextArea(20, 10);
        textFileTA.setEditable(false);
        textFileTA.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        textFileSP = new JScrollPane(textFileTA);
        textPnl.add(textFileSP);

        searchAreaTA = new JTextArea(20, 10);
        searchAreaTA.setEditable(false);
        searchAreaTA.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        searchAreaSP = new JScrollPane(searchAreaTA);
        textPnl.add(searchAreaSP);
    }

    private void buildSearchPanel()
    {
        searchPnl = new JPanel();
        searchPnl.setLayout(new BorderLayout());
        searchBarLbl = new JLabel("Search...");
        searchBarLbl.setFont(new Font("Arial", Font.ITALIC, 24));
        searchPnl.add(searchBarLbl, BorderLayout.NORTH);
        searchBarTF = new JTextField(20);
        searchBarTF.setName("Search...");
        searchPnl.add(searchBarTF, BorderLayout.CENTER);
    }

    private void buildButtonPanel()
    {
        buttonPnl = new JPanel();
        buttonPnl.setLayout(new GridLayout(1, 3));

        loadBtn = new JButton("Load File");
        loadBtn.setFont(new Font("Arial", Font.BOLD, 24));
        loadBtn.addActionListener((ActionEvent ae) ->
        {
            loadFile();
        });
        buttonPnl.add(loadBtn);

        fileSearchBtn = new JButton("Search");
        fileSearchBtn.setFont(new Font("Arial", Font.BOLD, 24));
        fileSearchBtn.addActionListener((ActionEvent ae) ->
        {

        });
        buttonPnl.add(fileSearchBtn);

        quitBtn = new JButton("Quit");
        quitBtn.setFont(new Font("Arial", Font.BOLD, 24));
        quitBtn.addActionListener((ActionEvent ae) ->
        {
            if (JOptionPane.showConfirmDialog(mainPnl, "Are you sure you want to quit?", "Quit?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
                System.exit(0);
            }
        });
    }

    private void loadFile()
    {

        JFileChooser chooser = new JFileChooser();
        File selectedFile;

        File workingDirectory = new File(System.getProperty("user.dir"));
        chooser.setCurrentDirectory(workingDirectory);

        if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            Path file = selectedFile.toPath();

            try (InputStream in = new BufferedInputStream(Files.newInputStream(file, CREATE));
                 BufferedReader reader = new BufferedReader(new InputStreamReader(in));)
            {
                while (reader.ready())
                {
                    textFileTA.append(reader.readLine());
                }
            }
            catch (FileNotFoundException e)
            {
                textFileTA.append("File not found!");
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void searchFile()
    {
        Stream<String> lines = Stream.of(textFileTA.getText());
        lines.filter(line -> line.equals(searchBarTF.getText()));
    }
}
