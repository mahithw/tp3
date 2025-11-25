package tests;

import entityClasses.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>Title: StudentPostTests</p>
 * <p>Description: Unit tests for the Post class verifying CRUD, validation,
 * and logical behavior. These tests ensure that the implementation of
 * Students User Stories is correct.</p>
 *
 * <p>Requirements Covered:</p>
 * <ul>
 *   <li>CRUD operations (create, read, update, delete)</li>
 *   <li>Validation for title, content, and thread</li>
 *   <li>Reply count logic</li>
 *   <li>Timestamps update correctly</li>
 *   <li>Summary and formatted output correctness</li>
 * </ul>
 */
public class StudentPostTests {

    private Post post;

    @BeforeEach
    void setup() {
        post = new Post("student1", "Valid Title", "This is a valid content body.", "General");
    }

    /** Tests that a Post object is created and initialized correctly. */
    @Test
    void testCreatePost() {
        assertNotNull(post.getPostId());
        assertEquals("student1", post.getAuthorUsername());
        assertEquals("Valid Title", post.getTitle());
        assertEquals("General", post.getThread());
        assertFalse(post.isDeleted());
        assertTrue(post.getReplyCount() == 0);
    }

    /** Tests that getter methods return accurate data. */
    @Test
    void testReadPost() {
        assertEquals("Valid Title", post.getTitle());
        assertEquals("This is a valid content body.", post.getContent());
        assertNotNull(post.getCreatedAt());
        assertNotNull(post.getUpdatedAt());
    }

    /** Tests updates to title, content, and thread. */
    @Test
    void testUpdatePost() {
        post.setTitle("Updated Title");
        post.setContent("Updated content that is definitely long enough.");
        post.setThread("Homework");
        assertEquals("Updated Title", post.getTitle());
        assertEquals("Homework", post.getThread());
        assertTrue(post.getUpdatedAt().isAfter(post.getCreatedAt()));
    }

    /** Tests the delete and restore functions. */
    @Test
    void testDeleteAndRestorePost() {
        post.markAsDeleted();
        assertTrue(post.isDeleted());
        post.restore();
        assertFalse(post.isDeleted());
    }

    /** Tests reply count increment and decrement logic. */
    @Test
    void testReplyCountIncrementDecrement() {
        post.incrementReplyCount();
        post.incrementReplyCount();
        assertEquals(2, post.getReplyCount());
        post.decrementReplyCount();
        assertEquals(1, post.getReplyCount());
        post.decrementReplyCount();
        assertEquals(0, post.getReplyCount());
    }

    /** Tests validation of title and content inputs. */
    @Test
    void testInvalidTitleContentValidation() {
        assertThrows(IllegalArgumentException.class,
                () -> post.setTitle("a")); // too short
        assertThrows(IllegalArgumentException.class,
                () -> post.setContent("short")); // too short
        assertThrows(IllegalArgumentException.class,
                () -> post.setAuthorUsername("")); // invalid author
    }

    /** Tests default thread assignment and updates. */
    @Test
    void testThreadDefaultsAndUpdates() {
        Post p = new Post("student2", "Another Post", "This content is valid.", "");
        assertEquals(Post.DEFAULT_THREAD, p.getThread());
        p.setThread("Q&A");
        assertEquals("Q&A", p.getThread());
    }

    /** Tests that timestamps update when fields change. */
    @Test
    void testTimestampsUpdate() throws InterruptedException {
        LocalDateTime before = post.getUpdatedAt();
        Thread.sleep(10);
        post.setTitle("Changed Again");
        assertTrue(post.getUpdatedAt().isAfter(before));
    }

    /** Tests summary and toString formatting correctness. */
    @Test
    void testSummaryAndToString() {
        String summary = post.getSummary();
        assertTrue(summary.contains("Valid Title"));
        assertTrue(summary.contains("student1"));
        String s = post.toString();
        assertTrue(s.contains("Post[ID="));
    }
}
