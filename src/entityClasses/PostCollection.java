package entityClasses;

import database.Database;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Title: PostCollection Class</p>
 * 
 * <p>Description: DB-backed collection for Post objects. Provides CRUD operations
 * (Create, Read, Update, Delete) for posts, integrated with H2 database.</p>
 * 
 * <p>Copyright:
 * Student Discussion System © 2025</p>
 * 
 * @version 3.00 — 2025-10-17
 */
public class PostCollection {

    private List<Post> posts = new ArrayList<>();
    private static final Database db = new Database();

    public PostCollection() {
        try {
            db.connectToDatabase();
            refreshFromDb();
        } catch (SQLException e) {
            System.err.println("*** ERROR connecting to DB in PostCollection()");
            e.printStackTrace();
        }
    }

    public PostCollection(List<Post> seed) {
        this.posts = new ArrayList<>(seed);
    }

    /* ====================== INTERNAL ====================== */

    private void refreshFromDb() {
        try {
            this.posts = db.getAllPosts();
        } catch (SQLException e) {
            System.err.println("*** ERROR refreshFromDb in PostCollection");
            e.printStackTrace();
            if (this.posts == null) this.posts = new ArrayList<>();
        }
    }

    private List<Post> copyCache() {
        return new ArrayList<>(posts);
    }

    /* ======================= CREATE ======================= */

    public boolean addPost(Post post) {
        if (post == null) throw new IllegalArgumentException("Cannot add null post.");
        if (findPostById(post.getPostId()) != null)
            throw new IllegalArgumentException("Post with ID " + post.getPostId() + " already exists.");

        try {
            db.savePost(post);
        } catch (SQLException e) {
            System.err.println("*** ERROR saving post to DB: " + post.getPostId());
            e.printStackTrace();
            return false;
        }
        posts.add(post);
        return true;
    }

    public Post createPost(String authorUsername, String title, String content, String thread) {
        String th = (thread == null || thread.trim().isEmpty()) ? "General" : thread.trim();
        Post post = new Post(authorUsername, title, content, th);
        addPost(post);
        return post;
    }

    /* ======================== READ ======================== */

    public List<Post> getAllPosts() {
        refreshFromDb();
        return copyCache();
    }

    public List<Post> getActivePosts() {
        refreshFromDb();
        return posts.stream().filter(p -> !p.isDeleted()).collect(Collectors.toList());
    }

    public Post findPostById(String postId) {
        if (postId == null || postId.trim().isEmpty()) return null;
        for (Post p : posts) if (p.getPostId().equals(postId)) return p;
        refreshFromDb();
        for (Post p : posts) if (p.getPostId().equals(postId)) return p;
        return null;
    }

    public PostCollection getPostsByThread(String thread) {
        if (thread == null || thread.trim().isEmpty()) return new PostCollection();
        refreshFromDb();
        String key = thread.trim().toLowerCase();
        List<Post> list = posts.stream()
                .filter(p -> p.getThread() != null && p.getThread().trim().toLowerCase().equals(key))
                .collect(Collectors.toList());
        return new PostCollection(list);
    }

    public PostCollection getActivePostsByThread(String thread) {
        if (thread == null || thread.trim().isEmpty()) return new PostCollection();
        refreshFromDb();
        String key = thread.trim().toLowerCase();
        List<Post> list = posts.stream()
                .filter(p -> !p.isDeleted())
                .filter(p -> p.getThread() != null && p.getThread().trim().toLowerCase().equals(key))
                .collect(Collectors.toList());
        return new PostCollection(list);
    }

    public PostCollection getPostsByAuthor(String username) {
        if (username == null || username.trim().isEmpty()) return new PostCollection();
        refreshFromDb();
        String key = username.trim().toLowerCase();
        List<Post> list = posts.stream()
                .filter(p -> p.getAuthorUsername() != null &&
                        p.getAuthorUsername().trim().toLowerCase().equals(key))
                .collect(Collectors.toList());
        return new PostCollection(list);
    }

    public PostCollection searchPosts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty())
            throw new IllegalArgumentException("Search keyword cannot be null or empty.");

        String term = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
        List<Post> matches = new ArrayList<>();

        try (java.sql.Connection conn =
                     java.sql.DriverManager.getConnection("jdbc:h2:~/FoundationDatabase", "sa", "");
             java.sql.PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM posts WHERE (LOWER(title) LIKE ? OR LOWER(content) LIKE ? OR LOWER(thread) LIKE ?) " +
                             "AND isDeleted = FALSE ORDER BY createdAt DESC")) {

            ps.setString(1, term);
            ps.setString(2, term);
            ps.setString(3, term);

            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Post p = new Post();
                p.__setPostId(rs.getString("postId"));
                p.setAuthorUsername(rs.getString("authorUsername"));
                p.setTitle(rs.getString("title"));
                p.setContent(rs.getString("content"));
                p.setThread(rs.getString("thread"));
                p.__setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                p.__setUpdatedAt(rs.getTimestamp("updatedAt").toLocalDateTime());
                p.__setDeleted(rs.getBoolean("isDeleted"));
                p.__setReplyCount(rs.getInt("replyCount"));
                matches.add(p);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PostCollection(matches);
    }

    /* ======================= UPDATE ======================= */

    public boolean updatePostTitle(String postId, String newTitle) {
        Post post = findPostById(postId);
        if (post == null) throw new IllegalArgumentException("Post not found.");
        if (post.isDeleted()) throw new IllegalArgumentException("Cannot update a deleted post.");
        post.setTitle(newTitle);
        try {
            db.updatePostTitle(postId, newTitle);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePostContent(String postId, String newContent) {
        Post post = findPostById(postId);
        if (post == null) throw new IllegalArgumentException("Post not found.");
        if (post.isDeleted()) throw new IllegalArgumentException("Cannot update a deleted post.");
        post.setContent(newContent);
        try {
            db.updatePostContent(postId, newContent);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePostThread(String postId, String newThread) {
        Post post = findPostById(postId);
        if (post == null) throw new IllegalArgumentException("Post not found.");
        if (post.isDeleted()) throw new IllegalArgumentException("Cannot update a deleted post.");
        String th = (newThread == null || newThread.trim().isEmpty()) ? "General" : newThread.trim();
        post.setThread(th);
        try {
            db.updatePostThread(postId, th);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ======================= DELETE ======================= */

    public boolean deletePost(String postId) {
        Post post = findPostById(postId);
        if (post == null) throw new IllegalArgumentException("Post not found.");
        if (post.isDeleted()) throw new IllegalArgumentException("Already deleted.");
        try {
            db.markPostDeleted(postId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        post.markAsDeleted();
        return true;
    }
}
