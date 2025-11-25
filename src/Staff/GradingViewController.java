package Staff;

import entityClasses.Post;
import entityClasses.PostCollection;
import entityClasses.Reply;
import entityClasses.ReplyCollection;
import entityClasses.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * <p>Title: GradingViewController Class</p>
 * 
 * <p>Description: Controller for the staff grading interface. Manages the business
 * logic for displaying participation summaries, filtering by date range, and
 * showing detailed student responses.</p>
 * 
 * <p>This controller follows MVC pattern and coordinates between the model
 * (PostCollection, ReplyCollection) and the view (GradingView).</p>
 * 
 * <p>Copyright: Student Discussion System © 2025</p>
 * 
 * @version 1.00 — 2025-11-24 Initial implementation for TP3
 * 
 * <p>Tested by: GradingViewControllerTest - JUnit test suite</p>
 * 
 * <p>Satisfies User Stories:
 * - "As an instructor, I need to view participation statistics"
 * - "As an instructor, I need to filter participation by date range"
 * - "As an instructor, I need to see which students answered which classmates"</p>
 */
public class GradingViewController {
    
    private User currentUser;
    private PostCollection postCollection;
    private ReplyCollection replyCollection;
    private ParticipationSummaryGenerator summaryGenerator;
    
    // Current filter settings
    private LocalDateTime filterStartDate;
    private LocalDateTime filterEndDate;
    
    // Cached summary data
    private Map<String, ParticipationRow> currentSummary;
    
    /**
     * Creates a grading view controller for the specified instructor.
     * 
     * @param currentUser The instructor user (must have appropriate role)
     * @param requiredDistinctStudents How many classmates students must answer (typically 3)
     * 
     * @throws IllegalArgumentException if currentUser is null
     */
    public GradingViewController(User currentUser, int requiredDistinctStudents) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Current user cannot be null");
        }
        
        this.currentUser = currentUser;
        this.postCollection = new PostCollection();
        this.replyCollection = new ReplyCollection();
        this.summaryGenerator = new ParticipationSummaryGenerator(requiredDistinctStudents);
        
        // Default: no date filter (show all time)
        this.filterStartDate = null;
        this.filterEndDate = null;
        this.currentSummary = null;
    }
    
    /**
     * Refreshes the participation summary based on current filter settings.
     * This method queries the database and regenerates statistics.
     * 
     * Why this exists: The summary can become stale as students post new replies,
     * so instructors need a way to get fresh data.
     */
    public void refreshSummary() {
        // Get all posts and replies from database
        List<Post> allPosts = postCollection.getAllPosts();
        List<Reply> allReplies = replyCollection.getAllReplies();
        
        // Generate summary with current date filters
        currentSummary = summaryGenerator.generateSummary(
            allPosts, 
            allReplies, 
            filterStartDate, 
            filterEndDate
        );
    }
    
    /**
     * Gets the current participation summary.
     * Returns cached data if available, otherwise generates fresh summary.
     * 
     * @return Map from username to their ParticipationRow
     */
    public Map<String, ParticipationRow> getSummary() {
        if (currentSummary == null) {
            refreshSummary();
        }
        return new HashMap<>(currentSummary); // Return defensive copy
    }
    
    /**
     * Gets a sorted list of participation rows for display.
     * Sorted by: 
     * 1. Students not meeting requirement (ascending by count)
     * 2. Students meeting requirement (descending by count)
     * 
     * This sorting helps instructors quickly identify students who need attention.
     * 
     * @return List of ParticipationRows sorted by priority
     */
    public List<ParticipationRow> getSortedSummary() {
        Map<String, ParticipationRow> summary = getSummary();
        List<ParticipationRow> rows = new ArrayList<>(summary.values());
        
        // Sort: non-meeting students first (by count ascending), 
        // then meeting students (by count descending)
        rows.sort((r1, r2) -> {
            // Both don't meet requirement: sort by count ascending (neediest first)
            if (!r1.meetsRequirement() && !r2.meetsRequirement()) {
                return Integer.compare(r1.getDistinctStudentsAnswered(), 
                                     r2.getDistinctStudentsAnswered());
            }
            
            // Both meet requirement: sort by count descending (most engaged first)
            if (r1.meetsRequirement() && r2.meetsRequirement()) {
                return Integer.compare(r2.getDistinctStudentsAnswered(), 
                                     r1.getDistinctStudentsAnswered());
            }
            
            // One meets, one doesn't: non-meeting comes first
            return r1.meetsRequirement() ? 1 : -1;
        });
        
        return rows;
    }
    
    /**
     * Sets the date range filter for participation analysis.
     * 
     * @param startDate Beginning of date range (inclusive), null for no start limit
     * @param endDate End of date range (inclusive), null for no end limit
     */
    public void setDateFilter(LocalDate startDate, LocalDate endDate) {
        // Convert LocalDate to LocalDateTime (start of day / end of day)
        this.filterStartDate = (startDate != null) ? 
            LocalDateTime.of(startDate, LocalTime.MIN) : null;
        this.filterEndDate = (endDate != null) ? 
            LocalDateTime.of(endDate, LocalTime.MAX) : null;
        
        // Clear cached summary since filter changed
        this.currentSummary = null;
    }
    
    /**
     * Clears the date filter to show all-time participation.
     */
    public void clearDateFilter() {
        this.filterStartDate = null;
        this.filterEndDate = null;
        this.currentSummary = null;
    }
    
    /**
     * Gets detailed information about which students a specific student answered.
     * Used to show drill-down view when instructor clicks on a student.
     * 
     * @param studentUsername The student to analyze
     * @return Set of usernames that this student replied to, or empty set if none
     */
    public Set<String> getStudentsAnswered(String studentUsername) {
        List<Post> allPosts = postCollection.getAllPosts();
        List<Reply> allReplies = replyCollection.getAllReplies();
        
        Map<String, Set<String>> answerMap = summaryGenerator.buildAnswerMap(
            allPosts, 
            allReplies, 
            filterStartDate, 
            filterEndDate
        );
        
        return answerMap.getOrDefault(studentUsername, new HashSet<>());
    }
    
    /**
     * Gets all replies from a specific student within the current filter range.
     * Used to show what the student actually wrote.
     * 
     * @param studentUsername The student whose replies to retrieve
     * @return List of Reply objects from this student
     */
    public List<Reply> getRepliesFromStudent(String studentUsername) {
        List<Reply> allReplies = replyCollection.getAllReplies();
        List<Reply> studentReplies = new ArrayList<>();
        
        for (Reply reply : allReplies) {
            if (reply.isDeleted()) continue;
            if (!reply.getAuthorUsername().equals(studentUsername)) continue;
            
            LocalDateTime replyTime = reply.getCreatedAt();
            if (filterStartDate != null && replyTime.isBefore(filterStartDate)) continue;
            if (filterEndDate != null && replyTime.isAfter(filterEndDate)) continue;
            
            studentReplies.add(reply);
        }
        
        return studentReplies;
    }
    
    /**
     * Gets count of students who don't meet the requirement.
     * Useful for dashboard summary statistics.
     * 
     * @return Number of students below the threshold
     */
    public int getCountNotMeeting() {
        Map<String, ParticipationRow> summary = getSummary();
        return (int) summary.values().stream()
                            .filter(row -> !row.meetsRequirement())
                            .count();
    }
    
    /**
     * Gets count of students who meet the requirement.
     * Useful for dashboard summary statistics.
     * 
     * @return Number of students at or above the threshold
     */
    public int getCountMeeting() {
        Map<String, ParticipationRow> summary = getSummary();
        return (int) summary.values().stream()
                            .filter(ParticipationRow::meetsRequirement)
                            .count();
    }
    
    /**
     * Gets the current requirement setting.
     * 
     * @return How many distinct students must be answered
     */
    public int getRequiredDistinctStudents() {
        return summaryGenerator.getRequiredDistinctStudents();
    }
    
    /**
     * Updates the requirement and refreshes the summary.
     * 
     * @param requiredDistinctStudents New requirement value
     */
    public void setRequiredDistinctStudents(int requiredDistinctStudents) {
        summaryGenerator.setRequiredDistinctStudents(requiredDistinctStudents);
        currentSummary = null; // Force refresh with new requirement
    }
}
