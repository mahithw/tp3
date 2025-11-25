package guiStudentHome;

import entityClasses.Post;
import entityClasses.PostCollection;
import entityClasses.Reply;
import entityClasses.ReplyCollection;
import entityClasses.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import java.lang.reflect.Method;
import java.util.*;

/*******
 * <p>Title: ControllerStudentHome Class.</p>
 *
 * <p>Description: Controller for Student Home with posts, replies, edit/delete
 * support, thread filtering, and logout.</p>
 *
 * @version 2.30 — 2025-10-17
 */
public class ControllerStudentHome {

    private final User currentUser;
    private final PostCollection postCollection = new PostCollection();
    private final ReplyCollection replyCollection = new ReplyCollection();

    private final Map<String, Set<String>> replyReads = new HashMap<>();
    private List<Post> cachedPosts = new ArrayList<>();
    private ViewStudentHome view;

    public ControllerStudentHome(User loggedIn) {
        this.currentUser = loggedIn;
    }

    public void initialize(ViewStudentHome v) {
        this.view = v;

        v.btnCreatePost.setOnAction(e -> onCreatePost());
        v.btnSearch.setOnAction(e -> onSearch());
        v.btnShowAll.setOnAction(e -> {
            v.tfSearch.clear();
            v.tfThreadFilter.clear();
            refreshPosts(true);
        });
        v.cbMyPostsOnly.setOnAction(e -> refreshPosts(false));
        v.btnLogout.setOnAction(e -> performLogout());
        v.tfThreadFilter.textProperty().addListener((obs, o, n) -> refreshPosts(false));

        refreshPosts(true);
    }

    /* ===================== CREATE POST ===================== */
    private void onCreatePost() {
        String title = safe(view.tfPostTitle.getText()).trim();
        String content = safe(view.taPostContent.getText()).trim();
        String thread = safe(view.tfThreadName.getText()).trim();

        if (title.isEmpty()) {
            alertError("Validation", "Post title cannot be empty.");
            return;
        }
        if (content.isEmpty()) {
            alertError("Validation", "Post content cannot be empty.");
            return;
        }

        if (thread.isEmpty()) thread = "General";

        postCollection.createPost(usernameOf(currentUser), title, content, thread);

        view.tfThreadName.clear();
        view.tfPostTitle.clear();
        view.taPostContent.clear();

        refreshPosts(true);
        toast("Post created successfully in thread: " + thread);
    }

    /* ===================== SEARCH ===================== */
    private void onSearch() {
        String keyword = safe(view.tfSearch.getText()).trim();
        try {
            if (keyword.isEmpty()) {
                refreshPosts(true);
                return;
            }

            cachedPosts = postCollection.searchPosts(keyword).getActivePosts();
            if (view.cbMyPostsOnly.isSelected()) {
                cachedPosts = filterMine(cachedPosts);
            }
            renderFeed(cachedPosts);
        } catch (IllegalArgumentException ex) {
            alertError("Search Error", ex.getMessage());
        }
    }

    /* ===================== REFRESH ===================== */
    private void refreshPosts(boolean reload) {
        if (reload) cachedPosts = postCollection.getActivePosts();

        String threadFilter = safe(view.tfThreadFilter.getText()).trim();
        if (!threadFilter.isEmpty()) {
            cachedPosts = cachedPosts.stream()
                    .filter(p -> p.getThread().equalsIgnoreCase(threadFilter))
                    .toList();
        }

        if (view.cbMyPostsOnly.isSelected()) {
            cachedPosts = filterMine(cachedPosts);
        }

        renderFeed(cachedPosts);
    }

    /* ===================== RENDER FEED ===================== */
    private void renderFeed(List<Post> posts) {
        VBox feed = view.vbFeed;
        feed.getChildren().clear();

        if (posts.isEmpty()) {
            Label none = new Label("No posts found.");
            none.setPadding(new Insets(12));
            feed.getChildren().add(none);
            return;
        }

        for (Post p : posts) feed.getChildren().add(buildPostCard(p));
    }

    private Node buildPostCard(Post post) {
        Label lblTitle = new Label(post.isDeleted() ? "[Deleted] " + post.getTitle() : post.getTitle());
        lblTitle.setFont(Font.font("System", 16));

        Label lblThread = new Label("#" + post.getThread());
        lblThread.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");

        Label lblAuthor = new Label("by " + post.getAuthorUsername());
        lblAuthor.setStyle("-fx-opacity: 0.75;");

        int replyCount = replyCollection.countRepliesForPost(post.getPostId());

        Label lblReplies = new Label(replyCount + " replies");
        lblReplies.setStyle("-fx-opacity: 0.75;");

        Button btnReply = new Button("Reply");
        Button btnEdit = new Button("Edit");
        Button btnDelete = new Button("Delete");

        btnDelete.setDisable(!post.getAuthorUsername().equalsIgnoreCase(usernameOf(currentUser)));
        btnEdit.setDisable(!post.getAuthorUsername().equalsIgnoreCase(usernameOf(currentUser)));

        Button btnToggle = new Button("Show Replies");

        VBox repliesBox = new VBox(6);
        repliesBox.setPadding(new Insets(8, 8, 0, 16));
        repliesBox.setVisible(false);
        repliesBox.setManaged(false);

        btnToggle.setOnAction(e -> {
            boolean show = !repliesBox.isVisible();
            repliesBox.setVisible(show);
            repliesBox.setManaged(show);
            btnToggle.setText(show ? "Hide Replies" : "Show Replies");
            if (show) populateReplies(post, repliesBox);
        });

        btnReply.setOnAction(e -> onReply(post));
        btnDelete.setOnAction(e -> onDeletePost(post));
        btnEdit.setOnAction(e -> onEditPost(post));

        VBox meta = new VBox(2, lblTitle, lblThread, lblAuthor);
        HBox top = new HBox(10, meta, lblReplies);
        top.setAlignment(Pos.CENTER_LEFT);

        Label lblContent = new Label(post.getContent());
        lblContent.setWrapText(true);

        VBox card = new VBox(6, top, lblContent, new HBox(8, btnReply, btnEdit, btnToggle, btnDelete), repliesBox);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #f6f6f6; -fx-background-radius: 12;");

        return card;
    }

    private void populateReplies(Post post, VBox repliesBox) {
        repliesBox.getChildren().clear();
        List<Reply> replies = replyCollection.getActiveRepliesByPostId(post.getPostId()).getAllReplies();

        if (replies.isEmpty()) {
            repliesBox.getChildren().add(new Label("No replies yet."));
            return;
        }

        for (Reply r : replies) {
            boolean unread = isUnread(r);

            Label meta = new Label(r.getAuthorUsername());
            Label content = new Label(r.getContent());
            content.setWrapText(true);

            Button btnEdit = new Button("Edit");
            Button btnDelete = new Button("Delete");
            btnEdit.setDisable(!r.getAuthorUsername().equalsIgnoreCase(usernameOf(currentUser)));
            btnDelete.setDisable(!r.getAuthorUsername().equalsIgnoreCase(usernameOf(currentUser)));

            btnEdit.setOnAction(e -> onEditReply(r, post));
            btnDelete.setOnAction(e -> onDeleteReply(r, post));

            HBox actions = new HBox(6, btnEdit, btnDelete);
            actions.setAlignment(Pos.CENTER_LEFT);

            VBox block = new VBox(4, meta, content, actions);
            block.setPadding(new Insets(6));
            if (unread)
                block.setStyle("-fx-background-color: rgba(10,132,255,0.08); -fx-background-radius: 8;");
            block.setOnMouseClicked(e -> markRead(r));

            repliesBox.getChildren().add(block);
        }
    }

    /* ===================== REPLY ACTIONS ===================== */
    private void onReply(Post post) {
        Dialog<String> dlg = new Dialog<>();
        dlg.setTitle("Reply to: " + post.getTitle());
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextArea ta = new TextArea();
        ta.setPromptText("Write your reply...");
        ta.setPrefRowCount(5);
        dlg.getDialogPane().setContent(ta);
        dlg.setResultConverter(bt -> bt == ButtonType.OK ? ta.getText() : null);

        Optional<String> result = dlg.showAndWait();
        if (result.isEmpty()) return;

        String text = safe(result.get()).trim();
        if (text.isEmpty()) {
            alertError("Validation", "Reply cannot be empty.");
            return;
        }

        replyCollection.createReply(post.getPostId(), usernameOf(currentUser), text);
        refreshPosts(false);
        toast("Reply added.");
    }

    private void onEditReply(Reply reply, Post post) {
        TextInputDialog dialog = new TextInputDialog(reply.getContent());
        dialog.setTitle("Edit Reply");
        dialog.setHeaderText("Edit your reply");
        dialog.setContentText("Reply:");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(newText -> {
            if (newText.trim().isEmpty()) {
                alertError("Validation", "Reply cannot be empty.");
                return;
            }
            replyCollection.updateReplyContent(reply.getReplyId(), newText.trim());
            toast("Reply updated.");
            refreshPosts(false);
        });
    }

    private void onDeleteReply(Reply reply, Post post) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this reply?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                replyCollection.deleteReply(reply.getReplyId());
                toast("Reply deleted.");
                refreshPosts(false);
            }
        });
    }

    /* ===================== POST ACTIONS ===================== */
    private void onEditPost(Post post) {
        TextInputDialog dialog = new TextInputDialog(post.getContent());
        dialog.setTitle("Edit Post");
        dialog.setHeaderText("Edit your post content");
        dialog.setContentText("Content:");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(newText -> {
            if (newText.trim().isEmpty()) {
                alertError("Validation", "Content cannot be empty.");
                return;
            }
            postCollection.updatePostContent(post.getPostId(), newText.trim());
            toast("Post updated.");
            refreshPosts(false);
        });
    }

    private void onDeletePost(Post post) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Post");
        confirm.setHeaderText("Are you sure you want to delete this post?");
        confirm.setContentText("Replies will remain visible.");
        Optional<ButtonType> res = confirm.showAndWait();

        if (res.isPresent() && res.get() == ButtonType.OK) {
            postCollection.deletePost(post.getPostId());
            refreshPosts(true);
            toast("Post deleted.");
        }
    }

    /* ===================== LOGOUT ===================== */
    private void performLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to logout?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                view.theStage.close();
                applicationMain.FoundationsMain.main(null);
            }
        });
    }

    /* ===================== HELPERS ===================== */
    private String safe(String s) { return s == null ? "" : s; }
    private void toast(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
    private void alertError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle(title);
        a.setHeaderText(null);
        a.showAndWait();
    }
    private String usernameOf(User u) {
        if (u == null) return "";
        try {
            Method m = u.getClass().getMethod("getUsername");
            Object val = m.invoke(u);
            return val == null ? "" : String.valueOf(val);
        } catch (Exception ignore) {
            try {
                Method m = u.getClass().getMethod("getUserName");
                Object val = m.invoke(u);
                return val == null ? "" : String.valueOf(val);
            } catch (Exception ignore2) {
                return "";
            }
        }
    }
    private boolean isUnread(Reply r) {
        String me = usernameOf(currentUser);
        return !replyReads.getOrDefault(r.getReplyId(), Set.of()).contains(me);
    }
    private void markRead(Reply r) {
        String me = usernameOf(currentUser);
        replyReads.computeIfAbsent(r.getReplyId(), k -> new HashSet<>()).add(me);
    }
    private List<Post> filterMine(List<Post> posts) {
        String me = usernameOf(currentUser);
        return posts.stream()
                .filter(p -> p.getAuthorUsername().equalsIgnoreCase(me))
                .toList();
    }
}
