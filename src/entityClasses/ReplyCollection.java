package entityClasses;

import database.Database;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Title: ReplyCollection Class</p>
 *
 * <p>Description: Manages replies with CRUD operations,
 * persisted in the H2 database. Each reply is shared among all users.</p>
 *
 * <p>Copyright:
 * Student Discussion System © 2025</p>
 *
 * @version 2.10 — 2025-10-17 (DB integrated + unread count)
 */
public class ReplyCollection {

    private List<Reply> replies;
    private static final Database db = new Database();

    public ReplyCollection() {
        this.replies = new ArrayList<>();
        try {
            db.connectToDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        refreshFromDb();
    }

    public ReplyCollection(List<Reply> replies) {
        this.replies = new ArrayList<>(replies);
    }

    // ==================== REFRESH ====================

    /**
     * Reloads replies from the database into the local list.
     */
    public void refreshFromDb() {
        List<String> dbRepliesRaw = new ArrayList<>();
        try {
            // This just logs all replies — could be expanded to build Reply objects
            // if you extend your Database.getRepliesForPost() to return structured data.
            // For now, we simply reset our in-memory list.
            this.replies.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================== CREATE ====================

    public boolean addReply(Reply reply) {
        if (reply == null)
            throw new IllegalArgumentException("Cannot add null reply.");
        if (findReplyById(reply.getReplyId()) != null)
            throw new IllegalArgumentException("Reply with ID " + reply.getReplyId() + " already exists.");

        boolean added = replies.add(reply);
        if (added) {
            try {
                db.saveReply(reply.getReplyId(), reply.getPostId(),
                        reply.getAuthorUsername(), reply.getContent());
            } catch (SQLException e) {
                System.err.println("*** ERROR saving reply to DB ***");
                e.printStackTrace();
            }
        }
        return added;
    }

    public Reply createReply(String postId, String authorUsername, String content) {
        Reply reply = new Reply(postId, authorUsername, content);
        addReply(reply);
        return reply;
    }

    // ==================== READ ====================

    public List<Reply> getAllReplies() {
        return new ArrayList<>(replies);
    }

    /**
     * Gets all active (non-deleted) replies for a given post.
     * Reloads them from DB each time.
     */
    public ReplyCollection getActiveRepliesByPostId(String postId) {
        List<Reply> activeReplies = new ArrayList<>();
        try {
            List<String> dbReplies = db.getRepliesForPost(postId);
            for (String text : dbReplies) {
                // Optionally print for debug:
                System.out.println(text);
                // Minimal in-memory Reply creation
                Reply r = new Reply(postId, "Unknown", text);
                activeReplies.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Combine with any in-memory replies not deleted
        activeReplies.addAll(
            replies.stream()
                    .filter(r -> r.getPostId().equals(postId))
                    .filter(r -> !r.isDeleted())
                    .collect(Collectors.toList())
        );

        return new ReplyCollection(activeReplies);
    }

    public Reply findReplyById(String replyId) {
        return replies.stream()
                .filter(r -> r.getReplyId().equals(replyId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Count total replies for a post.
     */
    public int countRepliesForPost(String postId) {
        if (postId == null || postId.trim().isEmpty()) return 0;
        try {
            return db.getRepliesForPost(postId).size();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Count unread replies for a specific user (persistent)
     */
    public int getUnreadCountForUser(String postId, String username) {
        try {
            return db.getUnreadReplyCount(postId, username);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ==================== DELETE ====================

    public boolean deleteReply(String replyId) {
        try {
            db.markReplyDeleted(replyId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateReplyContent(String replyId, String newContent) {
        if (newContent == null || newContent.trim().isEmpty())
            throw new IllegalArgumentException("New content cannot be empty.");
        try {
            db.updateReplyContent(replyId, newContent);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== UTILS ====================

    public int size() {
        return replies.size();
    }

    public int activeReplyCount() {
        return (int) replies.stream().filter(r -> !r.isDeleted()).count();
    }

    public boolean isEmpty() {
        return replies.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("ReplyCollection[Total=%d, Active=%d, Deleted=%d]",
                size(), activeReplyCount(), size() - activeReplyCount());
    }
}
