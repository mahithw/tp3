package guiStudentHome;

import applicationMain.FoundationsMain;
import entityClasses.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p>Title: ViewStudentHome Class.</p>
 *
 * <p>Description: Displays the Student Home GUI, providing the interface for
 * students to create posts, view posts, search, reply, edit, and delete posts.</p>
 *
 * <p>Copyright: Student Discussion System © 2025</p>
 *
 * @version 2.30 — 2025-10-17
 */
public class ViewStudentHome {

    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;

    private ControllerStudentHome controller;

    // Labels and fields
    protected static Label label_PageTitle = new Label("Student Discussion System");
    protected static TextField tfSearch = new TextField();
    protected static Button btnSearch = new Button("Search");
    protected static Button btnShowAll = new Button("Show All");
    protected static CheckBox cbMyPostsOnly = new CheckBox("My posts only");
    protected static TextField tfThreadFilter = new TextField();
    protected static Button btnLogout = new Button("Logout");

    // Create post area
    protected static TextField tfThreadName = new TextField();
    protected static TextField tfPostTitle = new TextField();
    protected static TextArea taPostContent = new TextArea();
    protected static Button btnCreatePost = new Button("Create Post");

    // Feed
    protected static VBox vbFeed = new VBox(10);
    protected static ScrollPane spFeed = new ScrollPane();

    public static void displayStudentHome(Stage stage, User user) {
        ViewStudentHome v = new ViewStudentHome();
        v.start(stage, user);
    }

    public void start(Stage stage, User user) {
        theStage = stage;
        theUser = user;

        controller = new ControllerStudentHome(user);
        theRootPane = buildRootPane();

        Scene scene = new Scene(theRootPane, 950, 700);
        stage.setScene(scene);
        stage.setTitle("Student Home — " + user.getUserName());
        stage.show();

        controller.initialize(this);
    }

    private Pane buildRootPane() {
        BorderPane root = new BorderPane();
        root.setTop(buildTop());
        root.setCenter(buildCenter());
        root.setBottom(buildBottom());
        return root;
    }

    private Pane buildTop() {
        label_PageTitle.setFont(new Font("Arial", 22));
        tfSearch.setPromptText("Search posts...");
        tfThreadFilter.setPromptText("Filter by thread...");
        HBox.setHgrow(tfSearch, Priority.ALWAYS);

        HBox topBar = new HBox(10,
                label_PageTitle, tfSearch, btnSearch, btnShowAll,
                tfThreadFilter, cbMyPostsOnly, btnLogout
        );
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);
        return topBar;
    }

    private ScrollPane buildCenter() {
        vbFeed.setPadding(new Insets(8));
        spFeed.setFitToWidth(true);
        spFeed.setContent(vbFeed);
        return spFeed;
    }

    private Pane buildBottom() {
        tfThreadName.setPromptText("Enter thread name (optional, defaults to 'General')");
        tfPostTitle.setPromptText("Post title");
        taPostContent.setPromptText("Write your question or comment...");
        taPostContent.setPrefRowCount(4);

        VBox newPostBox = new VBox(8, tfThreadName, tfPostTitle, taPostContent, btnCreatePost);
        newPostBox.setPadding(new Insets(10));
        newPostBox.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ccc; -fx-border-radius: 8;");

        Label lblNewPost = new Label("Create a new post");
        lblNewPost.setFont(Font.font("System", 14));

        VBox bottom = new VBox(6, lblNewPost, newPostBox);
        bottom.setPadding(new Insets(10));
        bottom.setStyle("-fx-background-color: #fafafa;");
        return bottom;
    }
}
