import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class QuickLinkShortenerGUI extends JFrame implements ActionListener {
    private JTextField urlInputField;
    private JTextField shortUrlField;
    private JTextArea outputArea;
    private Map<String, String> urlMap;
    private int idCounter;

    public QuickLinkShortenerGUI() {
        setTitle("QuickLink Shortener");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Load URL mappings from file
        urlMap = new HashMap<>();
        idCounter = 1;
        loadUrlMappings();

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));

        JLabel urlInputLabel = new JLabel("Enter Long URL:");
        urlInputField = new JTextField();

        JLabel shortUrlLabel = new JLabel("Shortened URL:");
        shortUrlField = new JTextField();
        shortUrlField.setEditable(false);

        JButton shortenButton = new JButton("Shorten URL");
        shortenButton.addActionListener(this);

        inputPanel.add(urlInputLabel);
        inputPanel.add(urlInputField);
        inputPanel.add(shortUrlLabel);
        inputPanel.add(shortUrlField);
        inputPanel.add(new JLabel());
        inputPanel.add(shortenButton);

        add(inputPanel, BorderLayout.NORTH);

        // Output Area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        add(scrollPane, BorderLayout.CENTER);

        // Control Buttons
        JPanel controlPanel = new JPanel();

        JButton retrieveButton = new JButton("Retrieve Original URL");
        retrieveButton.addActionListener(this);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(this);

        controlPanel.add(retrieveButton);
        controlPanel.add(clearButton);

        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("Shorten URL")) {
            shortenUrl();
        } else if (command.equals("Retrieve Original URL")) {
            retrieveOriginalUrl();
        } else if (command.equals("Clear")) {
            clearFields();
        }
    }

    private void shortenUrl() {
        String longUrl = urlInputField.getText();

        if (longUrl.isEmpty() || !isValidUrl(longUrl)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid URL.");
            return;
        }

        String shortUrl = "http://short.ly/" + idCounter++;
        urlMap.put(shortUrl, longUrl);
        shortUrlField.setText(shortUrl);
        saveUrlMappings();
        outputArea.append("Shortened URL: " + shortUrl + " -> " + longUrl + "\n");
    }

    private void retrieveOriginalUrl() {
        String shortUrl = shortUrlField.getText();

        if (shortUrl.isEmpty() || !urlMap.containsKey(shortUrl)) {
            JOptionPane.showMessageDialog(this, "Short URL not found.");
            return;
        }

        String longUrl = urlMap.get(shortUrl);
        JOptionPane.showMessageDialog(this, "Original URL: " + longUrl);
    }

    private void clearFields() {
        urlInputField.setText("");
        shortUrlField.setText("");
    }

    private boolean isValidUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    private void saveUrlMappings() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("urlMappings.txt"))) {
            for (Map.Entry<String, String> entry : urlMap.entrySet()) {
                writer.println(entry.getKey() + ":" + entry.getValue());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving URL mappings: " + e.getMessage());
        }
    }

    private void loadUrlMappings() {
        try (BufferedReader reader = new BufferedReader(new FileReader("urlMappings.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    urlMap.put(parts[0], parts[1]);
                    idCounter++;
                }
            }
        } catch (IOException e) {
            // Ignore if the file does not exist
        }
    }

    public static void main(String[] args) {
        new QuickLinkShortenerGUI();
    }
}

