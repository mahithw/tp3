package Staff;

import entityClasses.Reply;
import entityClasses.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * <p>Title: GradingView Class</p>
 * 
 * <p>Description: JavaFX GUI for instructional team to view and analyze
 * student participation. Provides a dashboard showing which students have
 * answered questions from the required number of classmates.</p>
 * 
 * <p>Features:
 * - Summary table showing all students and their participation status
 * - Color coding: green for meeting requirement, red for not meeting
 * - Date range filtering for weekly/custom assessments
 * - Drill-down view to see which students each student answered
 * - Display of actual reply content for verification</p>
 * 
 * <p>Copyright: Student Discussion System © 2025</p>
 * 
 * @version 1.00 — 2025-11-24 Initial implementation for TP3
 * 
 * <p>Tested by: Manual testing documented in Manual Tests.pdf</p>
 * 
 * <p>Satisfies User Stories:
 * - "As an instructor, I need a clear visual display of participation status"
 * - "As an instructor, I need to filter by date to assess weekly participation"
 * - "As an instructor, I need to drill down to verify student responses"</p>
 */
public class GradingView {
    
    private Stage stage;
    private GradingViewController controller;
    
    // UI Components
    private Label titleLabel;
    private Label summaryLabel;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private Button refreshButton;
    private Button clearFilterButton;
    private TableView<ParticipationRow> summaryTable;
    private TextArea detailArea;
    
    /**
     * Displays the grading view for an instructor.
     * 
     * @param stage The primary stage for this view
     * @param user The instructor user
     */
    public static void displayGradingView(Stage stage, User user) {
        GradingView view = new GradingView();
        view.start(stage, user);
    }
    
    /**
     * Initializes and shows the grading view.
     * 
     * @param stage The stage to use
     * @param user The instructor user
     */
    public void start(Stage stage, User user) {
        this.stage = stage;
        this.controller = new GradingViewController(user, 3); // Require 3 distinct students
        
        BorderPane root = new BorderPane();
        root.setTop(buildTopSection());
        root.setCenter(buildCenterSection());
        root.setBottom(buildBottomSection());
        
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Grading Dashboard — " + user.getUserName());
        stage.show();
        
        // Initial data load
        refreshData();
    }
    
    /**
     * Builds the top section with title and summary statistics.
     * 
     * @return HBox containing title and stats
     */
    private Pane buildTopSection() {
        titleLabel = new Label("Participation Grading Dashboard");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        summaryLabel = new Label("Loading...");
        summaryLabel.setFont(Font.font("Arial", 14));
        
        VBox topBox = new VBox(10, titleLabel, summaryLabel);
        topBox.setPadding(new Insets(15));
        topBox.setAlignment(Pos.CENTER);
        
        return topBox;
    }
    
    /**
     * Builds the center section with filter controls and summary table.
     * 
     * @return VBox containing filters and table
     */
    private Pane buildCenterSection() {
        // Filter controls
        Label filterLabel = new Label("Date Range Filter:");
        filterLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");
        
        endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");
        
        refreshButton = new Button("Apply Filter");
        refreshButton.setOnAction(e -> applyDateFilter());
        
        clearFilterButton = new Button("Clear Filter");
        clearFilterButton.setOnAction(e -> clearDateFilter());
        
        HBox filterBox = new HBox(10, filterLabel, startDatePicker, endDatePicker, 
                                  refreshButton, clearFilterButton);
        filterBox.setPadding(new Insets(10));
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        // Summary table
        summaryTable = createSummaryTable();
        
        VBox centerBox = new VBox(10, filterBox, summaryTable);
        centerBox.setPadding(new Insets(10));
        VBox.setVgrow(summaryTable, Priority.ALWAYS);
        
        return centerBox;
    }
    
    /**
     * Creates the summary table showing all students.
     * 
     * @return Configured TableView
     */
    private TableView<ParticipationRow> createSummaryTable() {
        TableView<ParticipationRow> table = new TableView<>();
        
        // Student column
        TableColumn<ParticipationRow, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(new PropertyValueFactory<>("studentUsername"));
        studentCol.setPrefWidth(200);
        
        // Count column
        TableColumn<ParticipationRow, Integer> countCol = new TableColumn<>("Distinct Students Answered");
        countCol.setCellValueFactory(new PropertyValueFactory<>("distinctStudentsAnswered"));
        countCol.setPrefWidth(200);
        
        // Status column
        TableColumn<ParticipationRow, Boolean> statusCol = new TableColumn<>("Meets Requirement?");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("meetsRequirement"));
        statusCol.setPrefWidth(150);
        
        // Custom cell factory for status column (color coding)
        statusCol.setCellFactory(column -> new TableCell<ParticipationRow, Boolean>() {
            @Override
            protected void updateItem(Boolean meets, boolean empty) {
                super.updateItem(meets, empty);
                if (empty || meets == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(meets ? "YES" : "NO");
                    setStyle(meets ? "-fx-text-fill: green; -fx-font-weight: bold;" 
                                   : "-fx-text-fill: red; -fx-font-weight: bold;");
                }
            }
        });
        
        table.getColumns().addAll(studentCol, countCol, statusCol);
        
        // Row click handler - show details
        table.setRowFactory(tv -> {
            TableRow<ParticipationRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    ParticipationRow rowData = row.getItem();
                    showStudentDetails(rowData.getStudentUsername());
                }
            });
            return row;
        });
        
        return table;
    }
    
    /**
     * Builds the bottom section with detail display area.
     * 
     * @return VBox containing detail area
     */
    private Pane buildBottomSection() {
        Label detailLabel = new Label("Student Details (double-click a row above):");
        detailLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        detailArea = new TextArea();
        detailArea.setEditable(false);
        detailArea.setPrefHeight(150);
        detailArea.setWrapText(true);
        
        VBox bottomBox = new VBox(5, detailLabel, detailArea);
        bottomBox.setPadding(new Insets(10));
        
        return bottomBox;
    }
    
    /**
     * Refreshes the data display with current settings.
     * Updates both the table and summary statistics.
     */
    private void refreshData() {
        controller.refreshSummary();
        
        List<ParticipationRow> rows = controller.getSortedSummary();
        summaryTable.getItems().setAll(rows);
        
        // Update summary statistics
        int meeting = controller.getCountMeeting();
        int notMeeting = controller.getCountNotMeeting();
        int total = meeting + notMeeting;
        int required = controller.getRequiredDistinctStudents();
        
        String summaryText = String.format(
            "Total Students: %d | Meeting Requirement: %d | Need More: %d | Required: %d distinct",
            total, meeting, notMeeting, required
        );
        summaryLabel.setText(summaryText);
    }
    
    /**
     * Applies the date filter based on user input.
     */
    private void applyDateFilter() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        
        controller.setDateFilter(start, end);
        refreshData();
        
        detailArea.setText("Filter applied. Double-click a student row to see details.");
    }
    
    /**
     * Clears the date filter to show all-time data.
     */
    private void clearDateFilter() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        controller.clearDateFilter();
        refreshData();
        
        detailArea.setText("Filter cleared. Showing all-time participation.");
    }
    
    /**
     * Shows detailed information about a specific student.
     * Displays which classmates they answered and sample replies.
     * 
     * @param studentUsername The student to analyze
     */
    private void showStudentDetails(String studentUsername) {
        StringBuilder details = new StringBuilder();
        details.append("===== DETAILS FOR: ").append(studentUsername).append(" =====\n\n");
        
        // Get which students they answered
        Set<String> studentsAnswered = controller.getStudentsAnswered(studentUsername);
        details.append("Answered questions from ").append(studentsAnswered.size())
               .append(" distinct classmates:\n");
        
        for (String classmate : studentsAnswered) {
            details.append("  - ").append(classmate).append("\n");
        }
        
        // Get sample replies
        details.append("\n----- Sample Replies -----\n\n");
        List<Reply> replies = controller.getRepliesFromStudent(studentUsername);
        
        int maxSamples = Math.min(3, replies.size());
        for (int i = 0; i < maxSamples; i++) {
            Reply reply = replies.get(i);
            details.append("Reply ").append(i + 1).append(":\n");
            details.append("  Post ID: ").append(reply.getPostId()).append("\n");
            details.append("  Date: ").append(reply.getCreatedAt()).append("\n");
            details.append("  Content: ").append(reply.getContent()).append("\n\n");
        }
        
        if (replies.size() > maxSamples) {
            details.append("... and ").append(replies.size() - maxSamples)
                   .append(" more replies\n");
        }
        
        detailArea.setText(details.toString());
    }
}
