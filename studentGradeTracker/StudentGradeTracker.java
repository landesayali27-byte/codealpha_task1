import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// ==========================================
// 1. DATA MODEL
// ==========================================
class StudentRecord {
    private final String id;
    private final String name;
    private final double score;

    public StudentRecord(String id, String name, double score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getScore() { return score; }

    public String getLetterGrade() {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }
}

// ==========================================
// 2. MAIN TRACKER GUI
// ==========================================
public class StudentGradeTracker extends JFrame {

    // Palette Colors
    private static final Color BG_DARK = new Color(20, 22, 31);
    private static final Color CARD_BG = new Color(29, 33, 47);
    private static final Color TABLE_BG = new Color(24, 27, 39);
    private static final Color ACCENT_BLUE = new Color(79, 110, 247);
    private static final Color ACCENT_GREEN = new Color(52, 211, 153);
    private static final Color ACCENT_RED = new Color(248, 113, 113);
    private static final Color TEXT_WHITE = new Color(240, 243, 250);
    private static final Color TEXT_MUTED = new Color(155, 162, 180);
    private static final Color BORDER_COLOR = new Color(45, 52, 74);

    // Dynamic In-Memory Storage (ArrayList)
    private final List<StudentRecord> studentList = new ArrayList<>();

    // UI Badges & Controls
    private JLabel totalStudentsBadge;
    private JLabel avgScoreBadge;
    private JLabel highestScoreBadge;
    private JLabel lowestScoreBadge;

    private JTextField idInput;
    private JTextField nameInput;
    private JTextField scoreInput;

    private DefaultTableModel tableModel;
    private JTable studentTable;

    public StudentGradeTracker() {
        setTitle("Student Grade Tracker & Analytics");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 680);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(15, 15));

        // Top Statistics Dashboard Cards
        add(createMetricCardsPanel(), BorderLayout.NORTH);

        // Center Content: Left (Input Form), Right (Student Roster Table)
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 18, 0));
        mainPanel.setBackground(BG_DARK);
        mainPanel.setBorder(new EmptyBorder(0, 20, 10, 20));

        mainPanel.add(createInputFormPanel());
        mainPanel.add(createTablePanel());
        add(mainPanel, BorderLayout.CENTER);

        // Seed with initial demo data
        seedInitialData();
        refreshUI();
    }

    private void seedInitialData() {
        studentList.add(new StudentRecord("STU-101", "Aarav Sharma", 88.5));
        studentList.add(new StudentRecord("STU-102", "Priya Patel", 94.0));
        studentList.add(new StudentRecord("STU-103", "Rohan Mehta", 72.0));
        studentList.add(new StudentRecord("STU-104", "Ananya Verma", 58.5));
    }

    // ==========================================
    // TOP ANALYTICS BADGES
    // ==========================================
    private JPanel createMetricCardsPanel() {
        JPanel header = new JPanel(new GridLayout(1, 4, 12, 0));
        header.setBackground(BG_DARK);
        header.setBorder(new EmptyBorder(20, 20, 10, 20));

        totalStudentsBadge = createStatBadge("TOTAL STUDENTS", "0");
        avgScoreBadge = createStatBadge("AVERAGE SCORE", "0.0");
        highestScoreBadge = createStatBadge("HIGHEST SCORE", "0.0");
        lowestScoreBadge = createStatBadge("LOWEST SCORE", "0.0");

        header.add(totalStudentsBadge);
        header.add(avgScoreBadge);
        header.add(highestScoreBadge);
        header.add(lowestScoreBadge);

        return header;
    }

    private JLabel createStatBadge(String title, String value) {
        JLabel badge = new JLabel(formatBadgeHtml(title, value), SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setBackground(CARD_BG);
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(12, 6, 12, 6)
        ));
        return badge;
    }

    private String formatBadgeHtml(String title, String val) {
        return "<html><center><span style='font-size:9px; color:#9BA2B4; letter-spacing:1px;'>" + title + "</span><br>"
                + "<span style='font-size:17px; font-weight:bold; color:#F0F3FA;'>" + val + "</span></center></html>";
    }

    // ==========================================
    // LEFT PANEL: ADD STUDENT FORM
    // ==========================================
    private JPanel createInputFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("Enroll / Record Grade");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(TEXT_WHITE);

        idInput = createStyledTextField();
        nameInput = createStyledTextField();
        scoreInput = createStyledTextField();

        JButton addButton = new JButton("+ Add Student");
        styleButton(addButton, ACCENT_BLUE, TEXT_WHITE);
        addButton.addActionListener(e -> handleAddStudent());

        JButton summaryButton = new JButton("View Summary Report");
        styleButton(summaryButton, new Color(48, 56, 82), TEXT_WHITE);
        summaryButton.addActionListener(e -> showSummaryReport());

        panel.add(title);
        panel.add(Box.createVerticalStrut(18));
        panel.add(createFieldLabel("Student Roll/ID:"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(idInput);
        panel.add(Box.createVerticalStrut(12));
        panel.add(createFieldLabel("Student Name:"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(nameInput);
        panel.add(Box.createVerticalStrut(12));
        panel.add(createFieldLabel("Score / Marks (0 - 100):"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(scoreInput);
        panel.add(Box.createVerticalStrut(22));
        panel.add(addButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(summaryButton);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(TEXT_MUTED);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        field.setPreferredSize(new Dimension(Integer.MAX_VALUE, 36));
        field.setBackground(new Color(18, 20, 28));
        field.setForeground(TEXT_WHITE);
        field.setCaretColor(TEXT_WHITE);
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        return field;
    }

    // ==========================================
    // RIGHT PANEL: ROSTER TABLE
    // ==========================================
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel title = new JLabel("Class Roster & Records");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(TEXT_WHITE);

        String[] cols = {"Roll ID", "Student Name", "Score", "Grade"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        studentTable = new JTable(tableModel);
        studentTable.setBackground(TABLE_BG);
        studentTable.setForeground(TEXT_WHITE);
        studentTable.setGridColor(BORDER_COLOR);
        studentTable.setRowHeight(30);
        studentTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        studentTable.setSelectionBackground(new Color(48, 56, 82));
        studentTable.setSelectionForeground(TEXT_WHITE);

        studentTable.getTableHeader().setBackground(new Color(36, 42, 60));
        studentTable.getTableHeader().setForeground(TEXT_WHITE);
        studentTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        studentTable.setDefaultRenderer(Object.class, centerRenderer);

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.getViewport().setBackground(TABLE_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));

        JButton deleteBtn = new JButton("Remove Selected Record");
        styleButton(deleteBtn, ACCENT_RED, TEXT_WHITE);
        deleteBtn.addActionListener(e -> handleDeleteStudent());

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(deleteBtn, BorderLayout.SOUTH);

        return panel;
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 38));
    }

    // ==========================================
    // LOGIC & DATA CALCULATIONS
    // ==========================================
    private void handleAddStudent() {
        String id = idInput.getText().trim();
        String name = nameInput.getText().trim();
        String scoreStr = scoreInput.getText().trim();

        if (id.isEmpty() || name.isEmpty() || scoreStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all student fields.", "Input Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Duplicate ID check
        for (StudentRecord s : studentList) {
            if (s.getId().equalsIgnoreCase(id)) {
                JOptionPane.showMessageDialog(this, "A student with Roll ID '" + id + "' already exists!", "Duplicate ID", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        double score;
        try {
            score = Double.parseDouble(scoreStr);
            if (score < 0 || score > 100) {
                JOptionPane.showMessageDialog(this, "Score must be between 0 and 100.", "Invalid Range", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Score must be a valid number.", "Invalid Format", JOptionPane.ERROR_MESSAGE);
            return;
        }

        studentList.add(new StudentRecord(id, name, score));
        idInput.setText("");
        nameInput.setText("");
        scoreInput.setText("");

        refreshUI();
    }

    private void handleDeleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record from the table to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        studentList.removeIf(s -> s.getId().equals(id));
        refreshUI();
    }

    private void refreshUI() {
        // Reload Table
        tableModel.setRowCount(0);
        for (StudentRecord s : studentList) {
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getName(),
                    String.format("%.2f", s.getScore()),
                    s.getLetterGrade()
            });
        }

        // Recalculate Summary Metrics
        if (studentList.isEmpty()) {
            totalStudentsBadge.setText(formatBadgeHtml("TOTAL STUDENTS", "0"));
            avgScoreBadge.setText(formatBadgeHtml("AVERAGE SCORE", "0.0"));
            highestScoreBadge.setText(formatBadgeHtml("HIGHEST SCORE", "0.0"));
            lowestScoreBadge.setText(formatBadgeHtml("LOWEST SCORE", "0.0"));
            return;
        }

        double sum = 0;
        double maxScore = Double.MIN_VALUE;
        double minScore = Double.MAX_VALUE;

        for (StudentRecord s : studentList) {
            double sc = s.getScore();
            sum += sc;
            if (sc > maxScore) maxScore = sc;
            if (sc < minScore) minScore = sc;
        }

        double avg = sum / studentList.size();

        totalStudentsBadge.setText(formatBadgeHtml("TOTAL STUDENTS", String.valueOf(studentList.size())));
        avgScoreBadge.setText(formatBadgeHtml("AVERAGE SCORE", String.format("%.2f", avg)));
        highestScoreBadge.setText(formatBadgeHtml("HIGHEST SCORE", String.format("%.1f", maxScore)));
        lowestScoreBadge.setText(formatBadgeHtml("LOWEST SCORE", String.format("%.1f", minScore)));
    }

    private void showSummaryReport() {
        if (studentList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No records to report.", "Empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int countA = 0, countB = 0, countC = 0, countD = 0, countF = 0;
        StudentRecord topper = studentList.get(0);
        StudentRecord lowest = studentList.get(0);

        for (StudentRecord s : studentList) {
            if (s.getScore() > topper.getScore()) topper = s;
            if (s.getScore() < lowest.getScore()) lowest = s;

            switch (s.getLetterGrade()) {
                case "A": countA++; break;
                case "B": countB++; break;
                case "C": countC++; break;
                case "D": countD++; break;
                default: countF++; break;
            }
        }

        String report = String.format(
                "SUMMARY GRADE REPORT\n" +
                "-----------------------------------------\n" +
                "Total Students Enrolled : %d\n" +
                "Class Average           : %.2f%%\n\n" +
                "Top Performer           : %s (%.1f)\n" +
                "Lowest Performer        : %s (%.1f)\n\n" +
                "Grade Distribution:\n" +
                "  • A (90 - 100) : %d\n" +
                "  • B (80 - 89)  : %d\n" +
                "  • C (70 - 79)  : %d\n" +
                "  • D (60 - 69)  : %d\n" +
                "  • F (< 60)     : %d\n" +
                "-----------------------------------------",
                studentList.size(),
                studentList.stream().mapToDouble(StudentRecord::getScore).average().orElse(0.0),
                topper.getName(), topper.getScore(),
                lowest.getName(), lowest.getScore(),
                countA, countB, countC, countD, countF
        );

        JTextArea textArea = new JTextArea(report);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setBackground(new Color(24, 27, 39));
        textArea.setForeground(TEXT_WHITE);
        textArea.setMargin(new Insets(10, 10, 10, 10));

        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Class Summary Report", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentGradeTracker app = new StudentGradeTracker();
            app.setVisible(true);
        });
    }
}