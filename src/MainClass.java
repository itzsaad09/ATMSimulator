import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

// ATM class to store ATM details and balance
class ATM {
    private String atmNumber;
    private String pin;
    private double balance;

    public ATM(String atmNumber, String pin) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet result = null;
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection("jdbc:mysql://localhost/atmsimulator", "root", "");
                String query = "SELECT * FROM `data` WHERE `atmNo` = '"+atmNumber+"'";
                pst = con.prepareStatement(query);
                result = pst.executeQuery();
                if (result.next()) {
                    double amount = result.getDouble(3);
                    balance = amount;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (pst != null) {
                    try {
                        pst.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        this.atmNumber = atmNumber;
        this.pin = pin;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getAtmNumber() {
        return atmNumber;
    }

    public String getPin() {
        return pin;
    }
}

// Interface defining ATM operations
interface AtmOperationInterf {
    void viewBalance();
    void withdrawAmount(double withdrawAmount);
    void depositAmount(double depositAmount);
}

// Transaction class to store transaction details
class Transaction {
    private double amount;
    private String type;

    public Transaction(double amount, String type) {
        this.amount = amount;
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }
}

// Implementation of ATM operations
class AtmOperationImpl implements AtmOperationInterf {
    private ATM atm;
    private List<Transaction> miniStatement = new ArrayList<>();

    public AtmOperationImpl(ATM atm) {
        this.atm = atm;
    }

    @Override
    public void viewBalance() {
        JOptionPane.showMessageDialog(null, "Available Balance is: " + atm.getBalance());
    }

    @Override
    public void withdrawAmount(double withdrawAmount) {
        if (withdrawAmount > 0) {
            if (withdrawAmount <= atm.getBalance()) {
                miniStatement.add(new Transaction(withdrawAmount, "Withdrawn"));
                
                Connection con = null;
                PreparedStatement pst = null;

                try {
                    atm.setBalance(atm.getBalance() - withdrawAmount);
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    con = DriverManager.getConnection("jdbc:mysql://localhost/atmsimulator", "root", "");
                    String query = "UPDATE `data` SET `atmNo`='"+atm.getAtmNumber()+"',`pin`='"+atm.getPin()+"',`amount`='"+atm.getBalance()+"'WHERE `atmNo`='"+atm.getAtmNumber()+"'";
                    pst = con.prepareStatement(query);
                    pst.execute();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (pst != null) {
                        try {
                            pst.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if (con != null) {
                        try {
                            con.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
                
                JOptionPane.showMessageDialog(null, "Collect the Cash: " + withdrawAmount);
                viewBalance();
            } else {
                JOptionPane.showMessageDialog(null, "Insufficient Balance!!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please enter a positive amount.");
        }
    }

    @Override
    public void depositAmount(double depositAmount) {
        if (depositAmount > 0) {
            Connection con = null;
            PreparedStatement pst = null;

            try {
                atm.setBalance(atm.getBalance() + depositAmount);
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection("jdbc:mysql://localhost/atmsimulator", "root", "");
                String query = "UPDATE `data` SET `atmNo`='"+atm.getAtmNumber()+"',`pin`='"+atm.getPin()+"',`amount`='"+atm.getBalance()+"'WHERE `atmNo`='"+atm.getAtmNumber()+"'";
                pst = con.prepareStatement(query);
                pst.execute();
                miniStatement.add(new Transaction(depositAmount, "Deposited"));
                JOptionPane.showMessageDialog(null, "Deposit Successful");
                viewBalance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (pst != null) {
                    try {
                        pst.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please enter a positive amount.");
        }
    }
}

class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String fileName) {
        backgroundImage = new ImageIcon(fileName).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
    }
}

// Main class to run the ATM simulation
public class MainClass {
    public static void main(String[] args) {
        JFrame frame = new JFrame("ATM Machine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        BackgroundPanel backgroundPanel = new BackgroundPanel("E:\\Programs\\Java\\ATM Simulator\\credit-card-operation-is-used-bank-atm.jpg");
        backgroundPanel.setLayout(null);
        
        JPanel panel = new JPanel();
        panel.setBounds(70,50,240,50);
        panel.setBackground(new Color (179, 200, 207));

        JLabel welcome = new JLabel("Welcome to ATM Machine!!!", SwingConstants.CENTER);
        welcome.setForeground(new Color(97, 94, 252));
        welcome.setFont(new Font("Segoe UI Black", Font.PLAIN, 15));
        welcome.setBounds(70,50,240,50);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(150, 150, 80, 50);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String enteredAtmNumber;
                String enteredPin;

                JTextField atmNumberField = new JTextField();
                JPasswordField pinField = new JPasswordField();
                Object[] fields = {"ATM Number:", atmNumberField, "PIN:", pinField};

                int option = JOptionPane.showConfirmDialog(null, fields, "Login", JOptionPane.OK_CANCEL_OPTION);

                enteredAtmNumber = atmNumberField.getText();
                enteredPin = new String(pinField.getPassword());

                if (option == JOptionPane.OK_OPTION) {
                    if (enteredAtmNumber.equals("") || enteredPin.equals("")) {
                        JOptionPane.showMessageDialog(null, "Please Enter Atm Number or Pin.");
                    } else {
                        Connection con = null;
                        PreparedStatement pst = null;
                        ResultSet result = null;

                        try {
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            con = DriverManager.getConnection("jdbc:mysql://localhost/atmsimulator", "root", "");
                            String query = "SELECT * FROM data WHERE atmNo=? AND pin=?";
                            pst = con.prepareStatement(query);
                            pst.setString(1, enteredAtmNumber);
                            pst.setString(2, enteredPin);
                            result = pst.executeQuery();
                            if (result.next()) {
                                // Create an ATM object with the user-provided credentials
                                ATM atm = new ATM(enteredAtmNumber, enteredPin);
                                AtmOperationInterf op = new AtmOperationImpl(atm);
                                // Show operations panel
                                frame.getContentPane().removeAll();
                                frame.getContentPane().add(createOperationsPanel(op));
                                frame.revalidate();
                                frame.repaint();
                            } else {
                                JOptionPane.showMessageDialog(null, "ERROR!! ATM No Not Found.");
                            }
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, ex);
                        } catch (ClassNotFoundException ex) {
                            ex.printStackTrace();                        }
                    }   
                }
            }
        });

        backgroundPanel.add(welcome);
        backgroundPanel.add(panel);
        backgroundPanel.add(loginButton);
        
        frame.add(backgroundPanel);
        frame.setVisible(true);
    }

    private static JPanel createOperationsPanel(AtmOperationInterf op) {
        JPanel panel = new JPanel(new GridLayout(4, 1));

        JButton viewBalanceButton = new JButton("View Balance");
        viewBalanceButton.setBackground(new Color(97, 94, 252));
        viewBalanceButton.setForeground(Color.WHITE);
        
        JButton withdrawButton = new JButton("Withdraw Amount");
        withdrawButton.setBackground(Color.WHITE);
        withdrawButton.setForeground(new Color(97, 94, 252));
        
        JButton depositButton = new JButton("Deposit Amount");
        depositButton.setBackground(new Color(97, 94, 252));
        depositButton.setForeground(Color.WHITE);
        
        JButton exitButton = new JButton("Exit");
        exitButton.setBackground(Color.WHITE);
        exitButton.setForeground(new Color(97, 94, 252));

        viewBalanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                op.viewBalance();
            }
        });

        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String amount = JOptionPane.showInputDialog("Enter amount to withdraw:");
                if (amount != null && !amount.isEmpty()) {
                    try {
                        double withdrawAmount = Double.parseDouble(amount);
                        op.withdrawAmount(withdrawAmount);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Please enter a valid amount.");
                    }
                }
            }
        });

        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String amount = JOptionPane.showInputDialog("Enter amount to deposit:");
                if (amount != null && !amount.isEmpty()) {
                    try {
                        double depositAmount = Double.parseDouble(amount);
                        op.depositAmount(depositAmount);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Please enter a valid amount.");
                    }
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Collect your ATM Card\nThank you for using the ATM Machine!!");
                System.exit(0);
            }
        });

        panel.add(viewBalanceButton);
        panel.add(withdrawButton);
        panel.add(depositButton);
        panel.add(exitButton);

        return panel;
    }
}