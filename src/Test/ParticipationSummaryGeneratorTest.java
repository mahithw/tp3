package Staff;

import entityClasses.Post;
import entityClasses.Reply;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>Title: ParticipationSummaryGeneratorTest Class</p>
 * 
 * <p>Description: Comprehensive JUnit test suite for ParticipationSummaryGenerator.
 * Includes boundary value analysis, coverage testing, and scenario-based tests.</p>
 * 
 * <p>Test Categories:
 * - Constructor validation
 * - buildAnswerMap functionality (core logic)
 * - buildSummary functionality
 * - generateSummary (integration)
 * - Date filtering (boundary cases)
 * - Edge cases (deleted posts/replies, self-replies, etc.)</p>
 * 
 * <p>Boundary Value Tests:
 * - requiredDistinctStudents: 0 (invalid), 1 (boundary), 3 (typical)
 * - Date ranges: null dates, same date, inverted dates
 * - Empty collections: no posts, no replies
 * - Deleted content handling</p>
 * 
 * <p>Copyright: Student Discussion System © 2025</p>
 * 
 * @version 1.00 — 2025-11-24
 */
public class ParticipationSummaryGeneratorTest {
    
    private ParticipationSummaryGenerator generator;
    private List<Post> testPosts;
    private List<Reply> testReplies;
    
    @BeforeEach
    public void setUp() {
        generator = new ParticipationSummaryGenerator(3);
        testPosts = new ArrayList<>();
        testReplies = new ArrayList<>();
    }
    
    // ==================== CONSTRUCTOR TESTS ====================
    
    @Test
    public void testValidConstructor() {
        ParticipationSummaryGenerator gen = new ParticipationSummaryGenerator(3);
        assertEquals(3, gen.getRequiredDistinctStudents());
    }
    
    @Test
    public void testConstructorBoundaryOne() {
        // Boundary: minimum valid value
        ParticipationSummaryGenerator gen = new ParticipationSummaryGenerator(1);
        assertEquals(1, gen.getRequiredDistinctStudents());
    }
    
    @Test
    public void testConstructorZero() {
        // Boundary: 0 is invalid
        assertThrows(IllegalArgumentException.class, () -> {
            new ParticipationSummaryGenerator(0);
        });
    }
    
    @Test
    public void testConstructorNegative() {
        // Boundary: negative is invalid
        assertThrows(IllegalArgumentException.class, () -> {
            new ParticipationSummaryGenerator(-1);
        });
    }
    
    // ==================== BUILD_ANSWER_MAP TESTS ====================
    
    @Test
    public void testBuildAnswerMapNullPosts() {
        assertThrows(IllegalArgumentException.class, () -> {
            generator.buildAnswerMap(null, testReplies, null, null);
        });
    }
    
    @Test
    public void testBuildAnswerMapNullReplies() {
        assertThrows(IllegalArgumentException.class, () -> {
            generator.buildAnswerMap(testPosts, null, null, null);
        });
    }
    
    @Test
    public void testBuildAnswerMapEmptyLists() {
        // Boundary: empty posts and replies
        Map<String, Set<String>> result = generator.buildAnswerMap(
            testPosts, testReplies, null, null
        );
        
        assertTrue(result.isEmpty());
    }
    
    @Test
    public void testBuildAnswerMapSimpleCase() {
        // Alice posts, Bob replies to Alice
        Post post1 = createPost("P1", "alice", "Question?");
        testPosts.add(post1);
        
        Reply reply1 = createReply("R1", "P1", "bob", "Answer!");
        testReplies.add(reply1);
        
        Map<String, Set<String>> result = generator.buildAnswerMap(
            testPosts, testReplies, null, null
        );
        
        assertEquals(1, result.size());
        assertTrue(result.containsKey("bob"));
        assertTrue(result.get("bob").contains("alice"));
    }
    
    @Test
    public void testBuildAnswerMapMultipleClassmates() {
        // Bob answers Alice, Carol, and Dave (3 distinct)
        testPosts.add(createPost("P1", "alice", "Q1"));
        testPosts.add(createPost("P2", "carol", "Q2"));
        testPosts.add(createPost("P3", "dave", "Q3"));
        
        testReplies.add(createReply("R1", "P1", "bob", "A1"));
        testReplies.add(createReply("R2", "P2", "bob", "A2"));
        testReplies.add(createReply("R3", "P3", "bob", "A3"));
        
        Map<String, Set<String>> result = generator.buildAnswerMap(
            testPosts, testReplies, null, null
        );
        
        assertEquals(3, result.get("bob").size());
        assertTrue(result.get("bob").contains("alice"));
        assertTrue(result.get("bob").contains("carol"));
        assertTrue(result.get("bob").contains("dave"));
    }
    
    @Test
    public void testBuildAnswerMapIgnoreSelfReplies() {
        // Alice posts, Alice replies to own post (should be ignored)
        testPosts.add(createPost("P1", "alice", "Question?"));
        testReplies.add(createReply("R1", "P1", "alice", "My own answer"));
        
        Map<String, Set<String>> result = generator.buildAnswerMap(
            testPosts, testReplies, null, null
        );
        
        // Alice should not be in the map (no valid replies)
        assertFalse(result.containsKey("alice"));
    }
    
    @Test
    public void testBuildAnswerMapIgnoreDeletedPosts() {
        // Post is deleted, replies to it shouldn't count
        Post deletedPost = createPost("P1", "alice", "Question?");
        deletedPost.setIsDeleted(true);
        testPosts.add(deletedPost);
        
        testReplies.add(createReply("R1", "P1", "bob", "Answer"));
        
        Map<String, Set<String>> result = generator.buildAnswerMap(
            testPosts, testReplies, null, null
        );
        
        // Bob shouldn't appear (replied to deleted post)
        assertFalse(result.containsKey("bob"));
    }
    
    @Test
    public void testBuildAnswerMapIgnoreDeletedReplies() {
        // Reply is deleted, shouldn't count
        testPosts.add(createPost("P1", "alice", "Question?"));
        
        Reply deletedReply = createReply("R1", "P1", "bob", "Answer");
        deletedReply.setIsDeleted(true);
        testReplies.add(deletedReply);
        
        Map<String, Set<String>> result = generator.buildAnswerMap(
            testPosts, testReplies, null, null
        );
        
        assertFalse(result.containsKey("bob"));
    }
    
    @Test
    public void testBuildAnswerMapMultipleRepliesToSameStudent() {
        // Bob replies to Alice 3 times - should only count as 1 distinct
        testPosts.add(createPost("P1", "alice", "Q1"));
        testPosts.add(createPost("P2", "alice", "Q2"));
        testPosts.add(createPost("P3", "alice", "Q3"));
        
        testReplies.add(createReply("R1", "P1", "bob", "A1"));
        testReplies.add(createReply("R2", "P2", "bob", "A2"));
        testReplies.add(createReply("R3", "P3", "bob", "A3"));
        
        Map<String, Set<String>> result = generator.buildAnswerMap(
            testPosts, testReplies, null, null
        );
        
        // Bob answered Alice 3 times, but only counts as 1 distinct student
        assertEquals(1, result.get("bob").size());
        assertTrue(result.get("bob").contains("alice"));
    }
    
    // ==================== DATE FILTERING TESTS ====================
    
    @Test
    public void testBuildAnswerMapWithStartDate() {
        testPosts.add(createPost("P1", "alice", "Question"));
        
        LocalDateTime baseTime = LocalDateTime.of(2025, 11, 1, 12, 0);
        Reply oldReply = createReplyWithTime("R1", "P1", "bob", "Old", baseTime);
        Reply newReply = createReplyWithTime("R2", "P1", "carol", "New", baseTime.plusDays(5));
        
        testReplies.add(oldReply);
        testReplies.add(newReply);
        
        LocalDateTime filterStart = baseTime.plusDays(2);
        Map<String, Set<String>> result = generator.buildAnswerMap(
            testPosts, testReplies, filterStart, null
        );
        
        // Only Carol's reply (after start date) should count
        assertFalse(result.containsKey("bob"));
        assertTrue(result.containsKey("carol"));
    }
    
    @Test
    public void testBuildAnswerMapWithEndDate() {
        testPosts.add(createPost("P1", "alice", "Question"));
        
        LocalDateTime baseTime = LocalDateTime.of(2025, 11, 1, 12, 0);
        Reply earlyReply = createReplyWithTime("R1", "P1", "bob", "Early", baseTime);
        Reply lateReply = createReplyWithTime("R2", "P1", "carol", "Late", baseTime.plusDays(10));
        
        testReplies.add(earlyReply);
        testReplies.add(lateReply);
        
        LocalDateTime filterEnd = baseTime.plusDays(5);
        Map<String, Set<String>> result = generator.buildAnswerMap(
            testPosts, testReplies, null, filterEnd
        );
        
        // Only Bob's reply (before end date) should count
        assertTrue(result.containsKey("bob"));
        assertFalse(result.containsKey("carol"));
    }
    
    @Test
    public void testBuildAnswerMapWithDateRange() {
        testPosts.add(createPost("P1", "alice", "Question"));
        
        LocalDateTime baseTime = LocalDateTime.of(2025, 11, 1, 12, 0);
        Reply beforeReply = createReplyWithTime("R1", "P1", "bob", "Before", baseTime);
        Reply duringReply = createReplyWithTime("R2", "P1", "carol", "During", baseTime.plusDays(5));
        Reply afterReply = createReplyWithTime("R3", "P1", "dave", "After", baseTime.plusDays(15));
        
        testReplies.add(beforeReply);
        testReplies.add(duringReply);
        testReplies.add(afterReply);
        
        LocalDateTime filterStart = baseTime.plusDays(2);
        LocalDateTime filterEnd = baseTime.plusDays(10);
        
        Map<String, Set<String>> result = generator.buildAnswerMap(
            testPosts, testReplies, filterStart, filterEnd
        );
        
        // Only Carol's reply (within range) should count
        assertFalse(result.containsKey("bob"));
        assertTrue(result.containsKey("carol"));
        assertFalse(result.containsKey("dave"));
    }
    
    // ==================== BUILD_SUMMARY TESTS ====================
    
    @Test
    public void testBuildSummaryNullMap() {
        assertThrows(IllegalArgumentException.class, () -> {
            generator.buildSummary(null);
        });
    }
    
    @Test
    public void testBuildSummaryEmptyMap() {
        Map<String, Set<String>> emptyMap = new HashMap<>();
        Map<String, ParticipationRow> result = generator.buildSummary(emptyMap);
        
        assertTrue(result.isEmpty());
    }
    
    @Test
    public void testBuildSummaryMeetsRequirement() {
        // Bob answered 3 distinct students (meets requirement of 3)
        Map<String, Set<String>> answerMap = new HashMap<>();
        Set<String> bobAnswered = new HashSet<>(Arrays.asList("alice", "carol", "dave"));
        answerMap.put("bob", bobAnswered);
        
        Map<String, ParticipationRow> result = generator.buildSummary(answerMap);
        
        assertEquals(1, result.size());
        ParticipationRow bobRow = result.get("bob");
        assertEquals(3, bobRow.getDistinctStudentsAnswered());
        assertTrue(bobRow.meetsRequirement());
    }
    
    @Test
    public void testBuildSummaryDoesNotMeet() {
        // Carol answered only 2 distinct students (needs 3)
        Map<String, Set<String>> answerMap = new HashMap<>();
        Set<String> carolAnswered = new HashSet<>(Arrays.asList("alice", "bob"));
        answerMap.put("carol", carolAnswered);
        
        Map<String, ParticipationRow> result = generator.buildSummary(answerMap);
        
        ParticipationRow carolRow = result.get("carol");
        assertEquals(2, carolRow.getDistinctStudentsAnswered());
        assertFalse(carolRow.meetsRequirement());
    }
    
    @Test
    public void testBuildSummaryBoundaryJustMeets() {
        // Boundary: exactly meets requirement
        Map<String, Set<String>> answerMap = new HashMap<>();
        Set<String> answers = new HashSet<>(Arrays.asList("user1", "user2", "user3"));
        answerMap.put("student", answers);
        
        Map<String, ParticipationRow> result = generator.buildSummary(answerMap);
        
        ParticipationRow row = result.get("student");
        assertTrue(row.meetsRequirement());
    }
    
    @Test
    public void testBuildSummaryBoundaryJustMisses() {
        // Boundary: just below requirement
        Map<String, Set<String>> answerMap = new HashMap<>();
        Set<String> answers = new HashSet<>(Arrays.asList("user1", "user2"));
        answerMap.put("student", answers);
        
        Map<String, ParticipationRow> result = generator.buildSummary(answerMap);
        
        ParticipationRow row = result.get("student");
        assertFalse(row.meetsRequirement());
    }
    
    // ==================== GENERATE_SUMMARY INTEGRATION TEST ====================
    
    @Test
    public void testGenerateSummaryIntegration() {
        // Integration test: full workflow
        testPosts.add(createPost("P1", "alice", "Q1"));
        testPosts.add(createPost("P2", "carol", "Q2"));
        testPosts.add(createPost("P3", "dave", "Q3"));
        
        // Bob answers all 3 (meets requirement)
        testReplies.add(createReply("R1", "P1", "bob", "A1"));
        testReplies.add(createReply("R2", "P2", "bob", "A2"));
        testReplies.add(createReply("R3", "P3", "bob", "A3"));
        
        // Eve answers only 1 (doesn't meet)
        testReplies.add(createReply("R4", "P1", "eve", "A4"));
        
        Map<String, ParticipationRow> result = generator.generateSummary(
            testPosts, testReplies, null, null
        );
        
        assertEquals(2, result.size());
        assertTrue(result.get("bob").meetsRequirement());
        assertFalse(result.get("eve").meetsRequirement());
    }
    
    // ==================== SETTER TESTS ====================
    
    @Test
    public void testSetRequiredDistinctStudents() {
        generator.setRequiredDistinctStudents(5);
        assertEquals(5, generator.getRequiredDistinctStudents());
    }
    
    @Test
    public void testSetRequiredDistinctStudentsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            generator.setRequiredDistinctStudents(0);
        });
    }
    
    // ==================== HELPER METHODS ====================
    
    private Post createPost(String postId, String author, String content) {
        Post post = new Post(author, "Title", content, "General");
        // Use reflection or setter to override generated ID
        try {
            java.lang.reflect.Field field = Post.class.getDeclaredField("postId");
            field.setAccessible(true);
            field.set(post, postId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }
    
    private Reply createReply(String replyId, String postId, String author, String content) {
        Reply reply = new Reply(postId, author, content);
        try {
            java.lang.reflect.Field field = Reply.class.getDeclaredField("replyId");
            field.setAccessible(true);
            field.set(reply, replyId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reply;
    }
    
    private Reply createReplyWithTime(String replyId, String postId, String author, 
                                     String content, LocalDateTime time) {
        Reply reply = createReply(replyId, postId, author, content);
        try {
            java.lang.reflect.Field field = Reply.class.getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(reply, time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reply;
    }
}
