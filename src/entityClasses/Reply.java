package entityClasses;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>Title: Reply Class</p>
 *
 * <p>Description: Represents a reply to a post in the discussion system.
 * This class encapsulates the data associated with a single reply.</p>
 *
 * <p>Copyright:
 * Student Discussion System © 2025</p>
 *
 * @version 1.02 — 2025-10-16
 * Added: getSummary(), originalPostDeleted flag
 */
public class Reply {

    public static final int MIN_CONTENT_LENGTH = 5;
    public static final int MAX_CONTENT_LENGTH = 3000;

    private String replyId;             // 5-char ID with prefix
    private String postId;
    private String authorUsername;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;

    // Optional field for showing message if original post was deleted
    private boolean originalPostDeleted = false;

    private static final String ID_PREFIX = "R-";
    private static final java.util.Random RNG = new SecureRandom();
    private static final char[] ALPHANUM =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    private static String gen5() {
        char[] b = new char[5];
        for (int i = 0; i < b.length; i++) b[i] = ALPHANUM[RNG.nextInt(ALPHANUM.length)];
        return new String(b);
    }

    public Reply() {
        this.replyId = ID_PREFIX + gen5();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
        System.out.println("New Reply ID: " + this.replyId);
    }

    public Reply(String postId, String authorUsername, String content) {
        this();
        setPostId(postId);
        setAuthorUsername(authorUsername);
        setContent(content);
    }

    // ===================== GETTERS =====================
    public String getReplyId() { return replyId; }
    public String getPostId() { return postId; }
    public String getAuthorUsername() { return authorUsername; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public boolean isDeleted() { return isDeleted; }
    public boolean isOriginalPostDeleted() { return originalPostDeleted; }

    // ===================== SETTERS =====================
    public void setPostId(String postId) {
        if (postId == null || postId.trim().isEmpty())
            throw new IllegalArgumentException("Post ID cannot be null or empty.");
        this.postId = postId.trim();
    }

    public void setAuthorUsername(String authorUsername) {
        if (authorUsername == null || authorUsername.trim().isEmpty())
            throw new IllegalArgumentException("Author username cannot be null or empty.");
        this.authorUsername = authorUsername.trim();
    }

    public void setContent(String content) {
        if (content == null || content.trim().isEmpty())
            throw new IllegalArgumentException("Reply content cannot be null or empty.");
        String trimmed = content.trim();
        if (trimmed.length() < MIN_CONTENT_LENGTH)
            throw new IllegalArgumentException("Reply content must be at least " + MIN_CONTENT_LENGTH + " characters.");
        if (trimmed.length() > MAX_CONTENT_LENGTH)
            throw new IllegalArgumentException("Reply content cannot exceed " + MAX_CONTENT_LENGTH + " characters.");
        this.content = trimmed;
        this.updatedAt = LocalDateTime.now();
    }

    public void setOriginalPostDeleted(boolean flag) {
        this.originalPostDeleted = flag;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void restore() {
        this.isDeleted = false;
        this.updatedAt = LocalDateTime.now();
    }

    public String getFormattedCreatedAt() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return createdAt.format(f);
    }

    public String getFormattedUpdatedAt() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return updatedAt.format(f);
    }

    // ===================== SUMMARY =====================
    /** 
     * Returns a formatted summary of the reply content for display in alerts.
     * Shows author, timestamp, and content preview.
     */
    public String getSummary() {
        if (isDeleted)
            return "(This reply was deleted.)";

        StringBuilder sb = new StringBuilder();
        sb.append("Reply ID: ").append(replyId).append("\n");
        sb.append("By: ").append(authorUsername).append("\n");
        sb.append("At: ").append(getFormattedCreatedAt()).append("\n");

        if (originalPostDeleted)
            sb.append(" Original Post Deleted\n");

        sb.append("\n");
        String preview = content.length() > 150 ? content.substring(0, 147) + "..." : content;
        sb.append(preview);

        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("Reply[ID=%s, PostID=%s, Author=%s, Deleted=%s]",
                replyId, postId, authorUsername, isDeleted);
    }

    // ===== Setters for DB hydration (package-private) =====
    public void __setReplyId(String id) { this.replyId = id; }
    public void __setCreatedAt(LocalDateTime t) { this.createdAt = t; }
    public void __setUpdatedAt(LocalDateTime t) { this.updatedAt = t; }
    public void __setDeleted(boolean d) { this.isDeleted = d; }
}
