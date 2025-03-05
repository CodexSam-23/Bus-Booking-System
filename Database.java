import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;


public class BusBookingApp {
    private static CardLayout cardLayout;
    private static JPanel cardPanel;
    private static JFrame frame;
    private static JTextField usernameField;
    private static JPasswordField passwordField;
    private static Connection connection;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame = new JFrame("Bus Booking System");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 300);

                cardLayout = new CardLayout();
                cardPanel = new JPanel(cardLayout);

                createLoginScreen();
                createBookingScreen();
                createAndShowGUI();

                frame.setVisible(true);
            }
        });
    }

    private static void createLoginScreen() {
        JPanel loginPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();

                if (authenticate(username, new String(password))) {
                    cardLayout.show(cardPanel, "bookingScreen");
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid username or password. Try again.");
                }
            }
        });

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(new JLabel()); // Empty label for spacing
        loginPanel.add(loginButton);

        cardPanel.add(loginPanel, "loginScreen");
    }

    private static void createBookingScreen() {
        JPanel bookingPanel = new JPanel(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        bookingPanel.setPreferredSize(new Dimension(400, 300));

        JLabel nameLabel = new JLabel("Name of User:");
        JTextField nameField = new JTextField();

        JLabel sourceLabel = new JLabel("Source:");
        String[] sources = {"Pune", "Mumbai", "Nashik", "Delhi", "Nagpur", "Ahmednagar"};
        JComboBox<String> sourceComboBox = new JComboBox<>(sources);

        JLabel destinationLabel = new JLabel("Destination:");
        String[] destinations = {"Mumbai", "Pune", "Nashik", "Delhi", "Nagpur", "Ahmednagar"};
        JComboBox<String> destinationComboBox = new JComboBox<>(destinations);

        JLabel passengersLabel = new JLabel("No. of Passengers:");
        JTextField passengersField = new JTextField();

        JLabel timeLabel = new JLabel("Time:");
        String[] times = {"6:00 am", "8:00 am", "12:00 pm", "2:00 pm", "5:00 pm", "7:00 pm"};
        JComboBox<String> timeComboBox = new JComboBox<>(times);

        JButton bookButton = new JButton("BOOK");

        JTextArea ticketSummary = new JTextArea();
        ticketSummary.setEditable(false);

        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String source = sourceComboBox.getSelectedItem().toString();
                String destination = destinationComboBox.getSelectedItem().toString();
                String passengers = passengersField.getText();
                String time = timeComboBox.getSelectedItem().toString();

                if (name.isEmpty() || passengers.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
                } else {
                    int fare = 200 * Integer.parseInt(passengers);

                    String summary = "Name of User: " + name + "\n" +
                            "Source: " + source + "\n" +
                            "Destination: " + destination + "\n" +
                            "No. of Passengers: " + passengers + "\n" +
                            "Time: " + time + "\n" +
                            "Payable Amount: $" + fare;

                    ticketSummary.setText(summary);

                    // Insert booking data into the database
                    insertBookingData(name, source, destination, passengers, time, fare);
                }
            }
        });

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(sourceLabel);
        panel.add(sourceComboBox);
        panel.add(destinationLabel);
        panel.add(destinationComboBox);
        panel.add(passengersLabel);
        panel.add(passengersField);
        panel.add(timeLabel);
        panel.add(timeComboBox);
        panel.add(bookButton);

        bookingPanel.add(panel, BorderLayout.NORTH);
        bookingPanel.add(new JScrollPane(ticketSummary), BorderLayout.CENTER);

        cardPanel.add(bookingPanel, "bookingScreen");
    }

    private static void createAndShowGUI() {
        frame.add(cardPanel);
    }

    private static boolean authenticate(String username, String password) {
        connectToDatabase();

        try {
            String query = "SELECT * FROM users WHERE username= ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return true; // Matching user found in the database
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // No matching user found or an error occurred
    }

    private static void connectToDatabase() {
        String url = "jdbc:mysql://localhost:3306/BusBookingApp";
        String user = "root";
        String password = "Harish@1306";

        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertBookingData(String name, String source, String destination, String passengers, String time, int fare) {
        connectToDatabase();

        try {
            String insertQuery = "INSERT INTO BusBookingDetails(name, source, destination, passengers, time, fare) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, source);
            preparedStatement.setString(3, destination);
            preparedStatement.setInt(4, Integer.parseInt(passengers));
            preparedStatement.setString(5, time);
            preparedStatement.setInt(6, fare);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Data inserted successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
