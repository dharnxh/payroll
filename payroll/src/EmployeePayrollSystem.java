import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class EmployeePayrollSystem extends JFrame {

    private JTextField nameField, employeeIdField, departmentField, basicSalaryField, hraField, taField, bonusesField;
    private JTextField providentFundField, taxField, searchField;
    private JComboBox<String> departmentFilterBox;

    public EmployeePayrollSystem() {
        super("Employee Payroll Management System");
        initializeUI();
    }

    private void initializeUI() {
        Font font = new Font("Arial", Font.PLAIN, 14);

        // Fields for employee details
        nameField = createStyledTextField();
        employeeIdField = createStyledTextField();
        departmentField = createStyledTextField();
        basicSalaryField = createStyledTextField();
        hraField = createStyledTextField();
        taField = createStyledTextField();
        bonusesField = createStyledTextField();
        providentFundField = createStyledTextField();
        taxField = createStyledTextField();
        searchField = createStyledTextField();

        // Dropdown for department filter
        departmentFilterBox = new JComboBox<>(new String[]{"All", "HR", "Finance", "IT", "Sales"});

        // Buttons
        JButton submitButton = new JButton("Add Employee");
        JButton allDataButton = new JButton("Show Employee Data");
        JButton searchButton = new JButton("Search Employee");
        JButton markAttendanceButton = new JButton("Mark Attendance");

        // Design the panel with fields and buttons
        JPanel inputPanel = new JPanel(new GridLayout(12, 2, 10, 10));
        inputPanel.setBackground(new Color(240, 248, 255));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.setFont(font);

        inputPanel.add(new JLabel("Employee ID:")); inputPanel.add(employeeIdField);
        inputPanel.add(new JLabel("Name:")); inputPanel.add(nameField);
        inputPanel.add(new JLabel("Department:")); inputPanel.add(departmentField);
        inputPanel.add(new JLabel("Basic Salary:")); inputPanel.add(basicSalaryField);
        inputPanel.add(new JLabel("HRA:")); inputPanel.add(hraField);
        inputPanel.add(new JLabel("TA:")); inputPanel.add(taField);
        inputPanel.add(new JLabel("Bonuses:")); inputPanel.add(bonusesField);
        inputPanel.add(new JLabel("Provident Fund:")); inputPanel.add(providentFundField);
        inputPanel.add(new JLabel("Tax:")); inputPanel.add(taxField);
        inputPanel.add(new JLabel("Search by Name/ID:")); inputPanel.add(searchField);
        inputPanel.add(new JLabel("Filter by Department:")); inputPanel.add(departmentFilterBox);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.add(submitButton);
        buttonPanel.add(allDataButton);
        buttonPanel.add(searchButton);


        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        submitButton.addActionListener(e -> addEmployee());
        allDataButton.addActionListener(e -> showEmployeeData());
        searchButton.addActionListener(e -> searchEmployee());


        getContentPane().setBackground(new Color(225, 245, 254));
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(200);
        field.setBackground(new Color(255, 255, 245));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        return field;
    }

    private void addEmployee() {
        String employeeId = employeeIdField.getText();
        String name = nameField.getText();
        String department = departmentField.getText();
        double basicSalary = Double.parseDouble(basicSalaryField.getText());
        double hra = Double.parseDouble(hraField.getText());
        double ta = Double.parseDouble(taField.getText());
        double bonuses = Double.parseDouble(bonusesField.getText());
        double providentFund = Double.parseDouble(providentFundField.getText());

        double tax = calculateTax(basicSalary);

        try (Connection conn = connectToDatabase()) {
            if (conn != null) {
                String query = "INSERT INTO employee_info (employee_id, name, department, basic_salary, hra, ta, bonuses, provident_fund, tax) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, employeeId);
                    stmt.setString(2, name);
                    stmt.setString(3, department);
                    stmt.setDouble(4, basicSalary);
                    stmt.setDouble(5, hra);
                    stmt.setDouble(6, ta);
                    stmt.setDouble(7, bonuses);
                    stmt.setDouble(8, providentFund);
                    stmt.setDouble(9, tax);

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Employee added successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Error adding employee.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    private double calculateTax(double basicSalary) {
        return basicSalary > 50000 ? basicSalary * 0.2 : basicSalary * 0.1;
    }

    private void showEmployeeData() {
        JFrame tableFrame = new JFrame("Employee Data");
        tableFrame.setSize(900, 500);
        tableFrame.setLocationRelativeTo(null);

        String[] columnNames = {
                "Employee ID", "Name", "Department", "Basic Salary", "HRA", "TA", "Bonuses", "Provident Fund", "Tax"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        table.setRowHeight(24);
        table.setFont(new Font("Arial", Font.PLAIN, 13));

        try (Connection conn = connectToDatabase()) {
            if (conn != null) {
                String query = "SELECT * FROM employee_info";
                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        Vector<String> row = new Vector<>();
                        row.add(rs.getString("employee_id"));
                        row.add(rs.getString("name"));
                        row.add(rs.getString("department"));
                        row.add(rs.getString("basic_salary"));
                        row.add(rs.getString("hra"));
                        row.add(rs.getString("ta"));
                        row.add(rs.getString("bonuses"));
                        row.add(rs.getString("provident_fund"));
                        row.add(rs.getString("tax"));
                        model.addRow(row);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }

        JScrollPane scrollPane = new JScrollPane(table);
        tableFrame.add(scrollPane, BorderLayout.CENTER);
        tableFrame.setVisible(true);
    }

    private void searchEmployee() {
        String searchTerm = searchField.getText().trim();
        String selectedDepartment = departmentFilterBox.getSelectedItem().toString();

        JFrame searchFrame = new JFrame("Search Results");
        searchFrame.setSize(800, 400);
        searchFrame.setLocationRelativeTo(null);

        String[] columnNames = {"Employee ID", "Name", "Department", "Basic Salary", "HRA", "TA", "Bonuses", "Provident Fund", "Tax"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        table.setRowHeight(22);
        table.setFont(new Font("Arial", Font.PLAIN, 13));

        try (Connection conn = connectToDatabase()) {
            if (conn != null) {
                String query = "SELECT * FROM employee_info WHERE (employee_id LIKE ? OR name LIKE ?)";
                if (!"All".equals(selectedDepartment)) {
                    query += " AND department = ?";
                }

                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, "%" + searchTerm + "%");
                    stmt.setString(2, "%" + searchTerm + "%");
                    if (!"All".equals(selectedDepartment)) {
                        stmt.setString(3, selectedDepartment);
                    }

                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        Vector<String> row = new Vector<>();
                        row.add(rs.getString("employee_id"));
                        row.add(rs.getString("name"));
                        row.add(rs.getString("department"));
                        row.add(rs.getString("basic_salary"));
                        row.add(rs.getString("hra"));
                        row.add(rs.getString("ta"));
                        row.add(rs.getString("bonuses"));
                        row.add(rs.getString("provident_fund"));
                        row.add(rs.getString("tax"));
                        model.addRow(row);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }

        JScrollPane scrollPane = new JScrollPane(table);
        searchFrame.add(scrollPane);
        searchFrame.setVisible(true);
    }


    private Connection connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/EmployeePayroll";
            String username = "root";
            String password = "nithu0428";
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EmployeePayrollSystem::new);
    }
}
