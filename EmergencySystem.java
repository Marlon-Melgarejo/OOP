import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;


public class EmergencySystem {
    // Database to store user information
    private static final HashMap<String, User> usersDB = new HashMap<>();
    private static String loggedInUser = null; // Track the currently logged-in user
    private static CardLayout cardLayout = new CardLayout(); // CardLayout instance
    private static JPanel mainPanel = new JPanel(cardLayout); // Main panel to hold all cards (screens)

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EmergencySystem::showMainMenu);
    }

    // Method to show the main menu
    private static void showMainMenu() {
        JFrame frame = new JFrame("BARANGAY EMERGENCY RESPONSE SYSTEM");
        frame.setSize(750, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize the main panel (with CardLayout)
        mainPanel.removeAll();

        // Main menu panel
        JPanel mainMenuPanel = new JPanel();
        mainMenuPanel.setLayout(new GridLayout(5, 1));
        JLabel simLabel = new JLabel("     SIM Number:");
        JTextField simField = new JTextField();
        JLabel passwordLabel = new JLabel("     Password:");
        JPasswordField passwordField = new JPasswordField();
        JLabel errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setForeground(Color.RED);

        JButton loginButton = new JButton("Login");
        JButton signUpButton = new JButton("Sign Up");
        JButton exitButton = new JButton("Exit");

        loginButton.addActionListener(e -> {
            String simNumber = simField.getText();
            String password = new String(passwordField.getPassword());

            if (usersDB.containsKey(simNumber) && usersDB.get(simNumber).password.equals(password)) {
                loggedInUser = simNumber;
                showLoginAnimation(frame);
            } else {
                errorLabel.setText("Invalid SIM number or password.");
            }
        });

        signUpButton.addActionListener(e -> {
            showSignUp();
        });

        exitButton.addActionListener(e -> System.exit(0));

        mainMenuPanel.add(simLabel);
        mainMenuPanel.add(simField);
        mainMenuPanel.add(passwordLabel);
        mainMenuPanel.add(passwordField);
        mainMenuPanel.add(errorLabel);
        mainMenuPanel.add(loginButton);
        mainMenuPanel.add(signUpButton);
        mainMenuPanel.add(exitButton);

        mainPanel.add(mainMenuPanel, "MainMenu"); // Add to CardLayout
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    // Method to show the sign-up form
    private static void showSignUp() {
        // Create the sign-up panel
        JPanel signUpPanel = new JPanel();
        signUpPanel.setLayout(new GridLayout(10, 1));

        JLabel simLabel = new JLabel("  SIM Number (11 digits):");
        JTextField simField = new JTextField();
        JLabel usernameLabel = new JLabel("  Username:");
        JTextField usernameField = new JTextField();
        JLabel addressLabel = new JLabel("  Address:");
        JTextField addressField = new JTextField();
        JLabel hotlineLabel = new JLabel("  Hotline (Optional):");
        JTextField hotlineField = new JTextField();
        JLabel passwordLabel = new JLabel("  Password (minimum 8 characters):");
        JPasswordField passwordField = new JPasswordField();
        JLabel confirmPasswordLabel = new JLabel("  Confirm Password:");
        JPasswordField confirmPasswordField = new JPasswordField();
        JLabel errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setForeground(Color.RED);
        JLabel successLabel = new JLabel("", SwingConstants.CENTER);
        successLabel.setForeground(Color.GREEN);

        JButton signUpButton = new JButton("Sign Up");
        JButton backButton = new JButton("Back");

        signUpButton.addActionListener(e -> {
            String simNumber = simField.getText();
            String username = usernameField.getText();
            String address = addressField.getText();
            String hotline = hotlineField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (simNumber.length() != 11 || !simNumber.matches("\\d+")) {
                errorLabel.setText("SIM number must be 11 digits.");
                return;
            }
            if (password.length() < 8) {
                errorLabel.setText("Password must be at least 8 characters.");
                return;
            }
            if (!password.equals(confirmPassword)) {
                errorLabel.setText("Passwords do not match.");
                return;
            }
            usersDB.put(simNumber, new User(username, address, hotline, password));
            successLabel.setText("Sign-up successful! You can now choose an option below.");

            // Show post sign-up options
            int choice = JOptionPane.showOptionDialog(
                null,
                "Sign-up successful! What do you want to do next?",
                "Sign Up Success",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Login Now", "Back to Main Menu"},
                null
            );

            if (choice == 0) { // Login Now
                loggedInUser = simNumber;
                showLoginAnimation(null);
            } else if (choice == 1) { // Back to Main Menu
                showMainMenu();
            }
        });

        backButton.addActionListener(e -> showMainMenu());

        signUpPanel.add(simLabel);
        signUpPanel.add(simField);
        signUpPanel.add(usernameLabel);
        signUpPanel.add(usernameField);
        signUpPanel.add(addressLabel);
        signUpPanel.add(addressField);
        signUpPanel.add(hotlineLabel);
        signUpPanel.add(hotlineField);
        signUpPanel.add(passwordLabel);
        signUpPanel.add(passwordField);
        signUpPanel.add(confirmPasswordLabel);
        signUpPanel.add(confirmPasswordField);
        signUpPanel.add(errorLabel);
        signUpPanel.add(successLabel);
        signUpPanel.add(signUpButton);
        signUpPanel.add(backButton);

        mainPanel.add(signUpPanel, "SignUp");
        cardLayout.show(mainPanel, "SignUp");
    }

    // Method to show the login animation
    private static void showLoginAnimation(JFrame parentFrame) {
        // Create the login animation panel
        JPanel animationPanel = new JPanel();
        animationPanel.setLayout(new GridLayout(2, 1));

        JLabel loadingLabel = new JLabel("Logging in...", SwingConstants.CENTER);
        JLabel animationLabel = new JLabel("|", SwingConstants.CENTER);
        animationLabel.setFont(new Font("Arial", Font.BOLD, 40));

        animationPanel.add(loadingLabel);
        animationPanel.add(animationLabel);

        mainPanel.add(animationPanel, "LoginAnimation");
        cardLayout.show(mainPanel, "LoginAnimation");

        Timer timer = new Timer(100, new ActionListener() {
            int count = 0;
            String[] directions = {"|", "/", "-", "\\"};

            @Override
            public void actionPerformed(ActionEvent e) {
                animationLabel.setText(directions[count % directions.length]);
                count++;

                // Stop animation after 3 seconds and show the post-login menu
                if (count == 30) {
                    ((Timer) e.getSource()).stop();
                    showPostLoginMenu();
                }
            }
        });
        timer.start();
    }
    
    private static void showLogoutAnimation() {
        JPanel animationPanel = new JPanel();
        animationPanel.setLayout(new GridLayout(2, 1));
    
        JLabel loadingLabel = new JLabel("Logging out...", SwingConstants.CENTER);
        JLabel animationLabel = new JLabel("|", SwingConstants.CENTER);
        animationLabel.setFont(new Font("Arial", Font.BOLD, 40));
    
        animationPanel.add(loadingLabel);
        animationPanel.add(animationLabel);
    
        mainPanel.add(animationPanel, "LogoutAnimation");
        cardLayout.show(mainPanel, "LogoutAnimation");
    
        Timer timer = new Timer(100, new ActionListener() {
            int count = 0;
            String[] directions = {"|", "/", "-", "\\"};
    
            @Override
            public void actionPerformed(ActionEvent e) {
                animationLabel.setText(directions[count % directions.length]);
                count++;
    
                // Stop animation after 3 seconds and go to Main Menu
                if (count == 30) {
                    ((Timer) e.getSource()).stop();
                    cardLayout.show(mainPanel, "MainMenu"); // Switch back to MainMenu
                }
            }
        });
    
        timer.start();
    }
    

    // Method to show the post-login menu
    private static void showPostLoginMenu() {
        // Create the post-login menu panel
        JPanel postLoginPanel = new JPanel();
        postLoginPanel.setLayout(new GridLayout(4, 1));

        JButton emergencyChatButton = new JButton("Emergency Chat");
        emergencyChatButton.addActionListener(e -> showEmergencyChat());

        JButton profileButton = new JButton("Profile");
        profileButton.addActionListener(e -> showProfile());

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            loggedInUser = null; // Clear the logged-in user
            showLogoutAnimation();
        });

        postLoginPanel.add(emergencyChatButton);
        postLoginPanel.add(profileButton);
        postLoginPanel.add(logoutButton);

        mainPanel.add(postLoginPanel, "PostLoginMenu");
        cardLayout.show(mainPanel, "PostLoginMenu");
    }
    

    // Method to show the emergency chat
    private static void showEmergencyChat() {
    JPanel emergencyChatPanel = new JPanel();
    emergencyChatPanel.setLayout(new BorderLayout());

    JTextArea chatArea = new JTextArea();
    chatArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(chatArea);
    emergencyChatPanel.add(scrollPane, BorderLayout.CENTER);

    // Panel to hold both the input field and back button
    JPanel bottomPanel = new JPanel();
    bottomPanel.setLayout(new GridBagLayout());  // Use GridBagLayout for flexible positioning
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL; // Make the button fill horizontally

    JTextField inputField = new JTextField();
    inputField.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));  // Ensures the input field takes full width
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;  // Allow input field to stretch horizontally
    bottomPanel.add(inputField, gbc);

    // English and Tagalog responses
    String[] englishResponses = {
        "Help is on the way. Please remain calm.",
        "Barangay patrol is dispatched to your location.",
        "Stay safe! Assistance will arrive shortly.",
        "We have alerted the authorities. Please stay where you are.",
        "Help is on its way. Keep your phone nearby for updates.",
        "Emergency responders have been notified. Hold tight!",
        "Stay calm. The nearest patrol is heading to you now.",
        "We’re here to help. A team is on its way.",
        "Help will arrive soon. Please avoid any danger.",
        "Your report has been received. Assistance is en route."
    };
    
    String[] tagalogResponses = {
        "Parating na po ang tulong. Kalma lang po, okay?",
        "Papunta na po ang barangay patrol. Hintay lang po kayo diyan.",
        "Stay lang po kayo sa ligtas na lugar, parating na po kami!",
        "May darating na po na tulong. Huwag po kayong mag-panic.",
        "Narespondehan na po ang report ninyo. Sandali lang po, on the way na po.",
        "Andyan na po ang team! Konting tiis lang po, parating na po.",
        "Na-inform na po sila. Huwag po kayong mag-alala, tuloy-tuloy na po 'to.",
        "Relax lang po, may paparating na po para tumulong sa inyo.",
        "Malapit na po ang barangay patrol sa inyo. Huwag po kayong aalis, ha?",
        "Nakuha na po namin ang report ninyo. Hintay lang po kayo diyan!"
    };

    // Action listener for the input field
    inputField.addActionListener(e -> {
        String userMessage = inputField.getText();
        inputField.setText("");

        // Get current date and time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Append user's message with timestamp
        chatArea.append("[" + now.format(formatter) + "] You: " + userMessage + "\n");

        
        String response;
        if (Math.random() > 0.5) {  // Randomly choose between English and Tagalog responses
            response = englishResponses[(int) (Math.random() * englishResponses.length)];
        } else {
            response = tagalogResponses[(int) (Math.random() * tagalogResponses.length)];
        }

        chatArea.append("[" + now.format(formatter) + "] B.E.R.M.S : " + response + "\n");
    });

    JButton backButton = new JButton("Back");
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1.0;  // Allow back button to stretch horizontally
    backButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));  // Full width button
    backButton.addActionListener(e -> showPostLoginMenu());
    bottomPanel.add(backButton, gbc);

    emergencyChatPanel.add(bottomPanel, BorderLayout.SOUTH);

    mainPanel.add(emergencyChatPanel, "EmergencyChat");
    cardLayout.show(mainPanel, "EmergencyChat");
}
    
    // Method to show the user's profile
    private static void showProfile() {
        User user = usersDB.get(loggedInUser);

        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new GridLayout(7, 1));

        JLabel usernameLabel = new JLabel(" Username: " + user.username);
        JTextField usernameField = new JTextField(user.username);
        JLabel addressLabel = new JLabel(" Address: " + user.address);
        JLabel hotlineLabel = new JLabel(" Hotline: " + (user.hotline.isEmpty() ? "N/A" : user.hotline));
        JLabel passwordLabel = new JLabel(" Password: ******** ");
        JPasswordField passwordField = new JPasswordField(user.password);

        JButton saveButton = new JButton("Save Changes");
        JButton backButton = new JButton("Back");

        saveButton.addActionListener(e -> {
            String newUsername = usernameField.getText();
            String newPassword = new String(passwordField.getPassword());

            // Update user data
            user.username = newUsername;
            user.password = newPassword;

            JOptionPane.showMessageDialog(profilePanel, "Profile updated successfully!");
        });

        backButton.addActionListener(e -> showPostLoginMenu());

        profilePanel.add(usernameLabel);
        profilePanel.add(usernameField);
        profilePanel.add(addressLabel);
        profilePanel.add(hotlineLabel);
        profilePanel.add(passwordLabel);
        profilePanel.add(passwordField);
        profilePanel.add(saveButton);
        profilePanel.add(backButton);

        mainPanel.add(profilePanel, "Profile");
        cardLayout.show(mainPanel, "Profile");
    }

    // User class to store user data
    static class User {
        String username;
        String address;
        String hotline;
        String password;

        User(String username, String address, String hotline, String password) {
            this.username = username;
            this.address = address;
            this.hotline = hotline;
            this.password = password;
        }
    }
}