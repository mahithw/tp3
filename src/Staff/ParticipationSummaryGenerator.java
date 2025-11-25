package Staff;

import entityClasses.Post;
import entityClasses.Reply;

import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>Title: ParticipationSummaryGenerator Class</p>
 * 
 * <p>Description: Generates participation statistics for instructional team grading.
 * This class analyzes student discussion posts and replies to determine whether each
 * student has answered questions from at least N different classmates.</p>
 * 
 * <p>The core functionality helps instructors quickly identify which students meet
 * participation requirements and which need reminders to engage with more peers.</p>
 * 
 * <p>Copyright: Student Discussion System © 2025</p>
 * 
 * @version 1.00 — 2025-11-24 Initial implementation for TP3
 * 
 * <p>Tested by: ParticipationSummaryGeneratorTest - JUnit test suite with boundary
 * value and coverage tests</p>
 * 
 * <p>Satisfies User Story: "As an instructor, I need to see which students have
 * answered questions from at least 3 different classmates so I can fairly assess
 * participation."</p>
 */
public class ParticipationSummaryGenerator {
    
    private int requiredDistinctStudents;
    
    /**
     * Creates a participation summary generator with specified requirement.
     * 
     * @param requiredDistinctStudents How many different classmates a student must answer
     *                                 to meet the participation requirement (typically 3)
     * 
     * @throws IllegalArgumentException if requiredDistinctStudents is less than 1
     */
    public ParticipationSummaryGenerator(int requiredDistinctStudents) {
        if (requiredDistinctStudents < 1) {
            throw new IllegalArgumentException("Required distinct students must be at least 1");
        }
        this.requiredDistinctStudents = requiredDistinctStudents;
    }
    
    /**
     * Builds a map showing which students each student has answered.
     * This is the core analysis method that processes all posts and replies.
     * 
     * Algorithm:
     * 1. Build a map from postId to the post's author (the "asker")
     * 2. For each reply in the date range:
     *    - Find who wrote the reply (the "answerer")
     *    - Find who wrote the original post (the "asker")
     *    - Track that answerer replied to asker
     * 3. Return map: answerer -> set of askers they replied to
     * 
     * @param posts All posts in the system
     * @param replies All replies in the system
     * @param startDate Only count replies on or after this date (null = no start limit)
     * @param endDate Only count replies on or before this date (null = no end limit)
     * 
     * @return Map from student username to set of distinct classmates they answered
     * 
     * @throws IllegalArgumentException if posts or replies lists are null
     */
    public Map<String, Set<String>> buildAnswerMap(
            List<Post> posts,
            List<Reply> replies,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        
        if (posts == null) {
            throw new IllegalArgumentException("Posts list cannot be null");
        }
        if (replies == null) {
            throw new IllegalArgumentException("Replies list cannot be null");
        }
        
        // Step 1: Map postId -> post author (the person who asked the question)
        // We ignore deleted posts because we can't credit replies to deleted posts
        Map<String, String> postIdToAuthor = new HashMap<>();
        for (Post post : posts) {
            if (!post.isDeleted()) {
                postIdToAuthor.put(post.getPostId(), post.getAuthorUsername());
            }
        }
        
        // Step 2: Build map of answerer -> set of people they answered
        Map<String, Set<String>> answerMap = new HashMap<>();
        
        for (Reply reply : replies) {
            // Ignore deleted replies - they don't count toward participation
            if (reply.isDeleted()) {
                continue;
            }
            
            // Check if reply is within the date range
            LocalDateTime replyTime = reply.getCreatedAt();
            if (startDate != null && replyTime.isBefore(startDate)) {
                continue;
            }
            if (endDate != null && replyTime.isAfter(endDate)) {
                continue;
            }
            
            String answerer = reply.getAuthorUsername();
            String postId = reply.getPostId();
            String asker = postIdToAuthor.get(postId);
            
            // Skip if we can't find the original post (might have been deleted)
            if (asker == null) {
                continue;
            }
            
            // Don't count self-replies - students answering their own posts
            // This is because we want peer interaction, not self-discussion
            if (answerer.equalsIgnoreCase(asker)) {
                continue;
            }
            
            // Add the asker to the set of people this answerer has replied to
            answerMap.computeIfAbsent(answerer, k -> new HashSet<>()).add(asker);
        }
        
        return answerMap;
    }
    
    /**
     * Generates the final participation summary from the answer map.
     * Converts the raw data into ParticipationRow objects that show whether
     * each student meets the requirement.
     * 
     * @param answerMap Map from student to set of classmates they answered
     *                  (typically from buildAnswerMap)
     * 
     * @return Map from username to ParticipationRow with their status
     * 
     * @throws IllegalArgumentException if answerMap is null
     */
    public Map<String, ParticipationRow> buildSummary(Map<String, Set<String>> answerMap) {
        if (answerMap == null) {
            throw new IllegalArgumentException("Answer map cannot be null");
        }
        
        Map<String, ParticipationRow> summary = new HashMap<>();
        
        for (Map.Entry<String, Set<String>> entry : answerMap.entrySet()) {
            String studentUsername = entry.getKey();
            int distinctCount = entry.getValue().size();
            boolean meets = (distinctCount >= requiredDistinctStudents);
            
            ParticipationRow row = new ParticipationRow(studentUsername, distinctCount, meets);
            summary.put(studentUsername, row);
        }
        
        return summary;
    }
    
    /**
     * Convenience method that performs full analysis in one call.
     * Combines buildAnswerMap and buildSummary.
     * 
     * @param posts All posts in the system
     * @param replies All replies in the system
     * @param startDate Only count replies on or after this date (null = no limit)
     * @param endDate Only count replies on or before this date (null = no limit)
     * 
     * @return Map from username to ParticipationRow showing their status
     */
    public Map<String, ParticipationRow> generateSummary(
            List<Post> posts,
            List<Reply> replies,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        
        Map<String, Set<String>> answerMap = buildAnswerMap(posts, replies, startDate, endDate);
        return buildSummary(answerMap);
    }
    
    /**
     * Gets the current requirement for distinct students.
     * 
     * @return Number of distinct classmates students must answer
     */
    public int getRequiredDistinctStudents() {
        return requiredDistinctStudents;
    }
    
    /**
     * Updates the requirement for distinct students.
     * This allows instructors to adjust standards.
     * 
     * @param requiredDistinctStudents New requirement value
     * 
     * @throws IllegalArgumentException if value is less than 1
     */
    public void setRequiredDistinctStudents(int requiredDistinctStudents) {
        if (requiredDistinctStudents < 1) {
            throw new IllegalArgumentException("Required distinct students must be at least 1");
        }
        this.requiredDistinctStudents = requiredDistinctStudents;
    }
}
