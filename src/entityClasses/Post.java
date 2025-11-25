package entityClasses;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>Title: Post Class</p>
 *
 * <p>Description: Represents a student post/question in the discussion system.
 * This class encapsulates all the data associated with a single post including
 * content, author information, timestamps, and thread categorization.</p>
 *
 * <p>Copyright: Student Discussion System © 2025</p>
 *
 * @author
 * @version 1.01 2025-10-16
 *          - Short 5-char IDs
 *          - Console print on creation
 *          - Added setters used by DB hydration
 */
public class Post {

    // Constants for validation
    public static final int MIN_TITLE_LENGTH = 3;
    public static final int MAX_TITLE_LENGTH = 200;
    public static final int MIN_CONTENT_LENGTH = 10;
    public static final int MAX_CONTENT_LENGTH = 5000;
    public static final String DEFAULT_THREAD = "General";

    // Attributes
    private String postId;              // Unique identifier for the post (5-char)
    private String authorUsername;      // Username of the post creator
    private String title;               // Title/subject of the post
    private String content;             // Main content/body of the post
    private String thread;              // Discussion thread category
    private LocalDateTime createdAt;    // Timestamp when post was created
    private LocalDateTime updatedAt;    // Timestamp of last update
    private boolean isDeleted;          // Soft delete flag
    private int replyCount;             // Number of replies to this post

    private static final String ID_PREFIX = "P-";
    private static final SecureRandom RNG = new SecureRandom();
    private static final char[] ALPHANUM =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    /** Generate a short 5-character ID (upper-case alphanumeric). */
    private static String gen5() {
        char[] buf = new char[5];
        for (int i = 0; i < buf.length; i++) buf[i] = ALPHANUM[RNG.nextInt(ALPHANUM.length)];
        return new String(buf);
    }

    /** Default constructor - creates an empty post with generated ID. */
    public Post() {
        this.postId = ID_PREFIX + gen5();
        this.thread = DEFAULT_THREAD;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
        this.replyCount = 0;
        System.out.println("New Post ID: " + this.postId);
    }

    /** Parameterized constructor for creating a new post */
    public Post(String authorUsername, String title, String content, String thread) {
        this();
        setAuthorUsername(authorUsername);
        setTitle(title);
        setContent(content);
        setThread(thread);
    }

    // Getters
    public String getPostId() { return postId; }
    public String getAuthorUsername() { return authorUsername; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getThread() { return thread; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public boolean isDeleted() { return isDeleted; }
    public int getReplyCount() { return replyCount; }

    // Setters used by application logic
    public void setAuthorUsername(String authorUsername) {
        if (authorUsername == null || authorUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Author username cannot be null or empty.");
        }
        this.authorUsername = authorUsername.trim();
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty())
            throw new IllegalArgumentException("Post title cannot be null or empty.");
        String trimmed = title.trim();
        if (trimmed.length() < MIN_TITLE_LENGTH)
            throw new IllegalArgumentException("Post title must be at least " + MIN_TITLE_LENGTH + " characters.");
        if (trimmed.length() > MAX_TITLE_LENGTH)
            throw new IllegalArgumentException("Post title cannot exceed " + MAX_TITLE_LENGTH + " characters.");
        this.title = trimmed;
        this.updatedAt = LocalDateTime.now();
    }

    public void setContent(String content) {
        if (content == null || content.trim().isEmpty())
            throw new IllegalArgumentException("Post content cannot be null or empty.");
        String trimmed = content.trim();
        if (trimmed.length() < MIN_CONTENT_LENGTH)
            throw new IllegalArgumentException("Post content must be at least " + MIN_CONTENT_LENGTH + " characters.");
        if (trimmed.length() > MAX_CONTENT_LENGTH)
            throw new IllegalArgumentException("Post content cannot exceed " + MAX_CONTENT_LENGTH + " characters.");
        this.content = trimmed;
        this.updatedAt = LocalDateTime.now();
    }

    public void setThread(String thread) {
        if (thread == null || thread.trim().isEmpty()) {
            this.thread = DEFAULT_THREAD;
        } else {
            this.thread = thread.trim();
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsDeleted() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void restore() {
        this.isDeleted = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementReplyCount() {
        this.replyCount++;
    }

    public void decrementReplyCount() {
        if (this.replyCount > 0) this.replyCount--;
    }

    public String getFormattedCreatedAt() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return createdAt.format(f);
    }

    public String getFormattedUpdatedAt() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return updatedAt.format(f);
    }

    public String getSummary() {
        String status = isDeleted ? "[DELETED] " : "";
        String preview = content.length() > 100 ? content.substring(0, 97) + "..." : content;
        return String.format("%s%s\nBy: %s | Thread: %s | Replies: %d | %s",
                status, title, authorUsername, thread, replyCount, getFormattedCreatedAt());
    }

    @Override
    public String toString() {
        return String.format(
                "Post[ID=%s, Author=%s, Title=%s, Thread=%s, Replies=%d, Deleted=%s]",
                postId, authorUsername, title, thread, replyCount, isDeleted);
    }

    /* ===== Setters for DB hydration (package-private) ===== */
    public void __setPostId(String id) { this.postId = id; }
    public void __setCreatedAt(LocalDateTime t) { this.createdAt = t; }
    public void __setUpdatedAt(LocalDateTime t) { this.updatedAt = t; }
    public void __setDeleted(boolean d) { this.isDeleted = d; }
    public void __setReplyCount(int c) { this.replyCount = Math.max(0, c); }

}
