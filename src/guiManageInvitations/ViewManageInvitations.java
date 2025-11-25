package guiManageInvitations;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;

/*******
 * <p> Title: ViewManageInvitations Class. </p>
 * 
 * <p> Description: The Java/FX-based Manage Invitations Page. This class provides 
 * the JavaFX GUI widgets that enable an admin to view and manage outstanding 
 * invitations.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Mahith
 * 
 * @version 1.00		2025-09-30 Initial version
 *  
 */

public class ViewManageInvitations {
    
    /*-*******************************************************************************************
    Attributes
    */
    
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;
    
    // GUI Area 1: Page title and instructions
    protected static Label label_PageTitle = new Label();
    protected static Label label_Instructions = new Label();
    private static Line line_Separator1 = new Line(20, 95, width-20, 95);
    
    // GUI Area 2: Table to display invitations
    protected static TableView<ModelManageInvitations.InvitationRecord> table_Invitations = 
        new TableView<>();
    protected static ObservableList<ModelManageInvitations.InvitationRecord> invitationData = 
        FXCollections.observableArrayList();
    
    // GUI Area 3: Action buttons
    protected static Button button_DeleteInvitation = new Button("Delete Selected Invitation");
    protected static Button button_Refresh = new Button("Refresh List");
    
    // GUI Area 4: Navigation
    private static Line line_Separator2 = new Line(20, 525, width-20, 525);
    protected static Button button_Return = new Button("Return to Admin Home");
    
    // Alerts
    protected static Alert alertDeleteConfirm = new Alert(AlertType.CONFIRMATION);
    protected static Alert alertDeleteSuccess = new Alert(AlertType.INFORMATION);
    protected static Alert alertError = new Alert(AlertType.ERROR);
    
    // Singleton and shared attributes
    private static ViewManageInvitations theView;
    private static Database theDatabase = applicationMain.FoundationsMain.database;
    protected static Stage theStage;
    private static Pane theRootPane;
    protected static User theUser;
    private static Scene theManageInvitationsScene;
    
    /*-*******************************************************************************************
    Constructors
    */
    
    /**********
     * <p> Method: displayManageInvitations(Stage ps, User user) </p>
     * 
     * <p> Description: This method is the single entry point from outside this package to cause
     * the Manage Invitations page to be displayed.</p>
     * 
     * @param ps specifies the JavaFX Stage to be used for this GUI and its methods
     * @param user specifies the User for this GUI and its methods
     */
    public static void displayManageInvitations(Stage ps, User user) {
        theStage = ps;
        theUser = user;
        
        // If not yet established, populate the static aspects of the GUI
        if (theView == null) theView = new ViewManageInvitations();
        
        // Refresh the invitation list
        ControllerManageInvitations.refreshInvitationList();
        
        // Set the title for the window and display the page
        theStage.setTitle("CSE 360 Foundation Code: Manage Invitations");
        theStage.setScene(theManageInvitationsScene);
        theStage.show();
    }
    
    /**********
     * <p> Method: ViewManageInvitations() </p>
     * 
     * <p> Description: This method initializes all the elements of the graphical user interface.
     * This method determines the location, size, font, color, and change and event handlers for
     * each GUI object.</p>
     * 
     */
    private ViewManageInvitations() {
        // Create the Pane for the list of widgets and the Scene for the window
        theRootPane = new Pane();
        theManageInvitationsScene = new Scene(theRootPane, width, height);
        
        // GUI Area 1: Title and instructions
        label_PageTitle.setText("Manage Invitations");
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);
        
        label_Instructions.setText("Select an invitation and click 'Delete' to revoke it");
        setupLabelUI(label_Instructions, "Arial", 16, width, Pos.CENTER, 0, 55);
        
        // GUI Area 2: Table setup
        setupInvitationsTable();
        
        // GUI Area 3: Action buttons
        setupButtonUI(button_DeleteInvitation, "Dialog", 16, 250, Pos.CENTER, 20, 470);
        button_DeleteInvitation.setOnAction((event) -> 
            {ControllerManageInvitations.deleteSelectedInvitation();});
        
        setupButtonUI(button_Refresh, "Dialog", 16, 250, Pos.CENTER, 290, 470);
        button_Refresh.setOnAction((event) -> 
            {ControllerManageInvitations.refreshInvitationList();});
        
        // GUI Area 4: Return button
        setupButtonUI(button_Return, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Return.setOnAction((event) -> 
            {ControllerManageInvitations.returnToAdminHome();});
        
        // Setup alerts
        alertDeleteConfirm.setTitle("Confirm Delete");
        alertDeleteConfirm.setHeaderText("Delete Invitation");
        
        alertDeleteSuccess.setTitle("Success");
        alertDeleteSuccess.setHeaderText("Invitation Deleted");
        
        alertError.setTitle("Error");
        alertError.setHeaderText("Operation Error");
        
        // Add all elements to pane
        theRootPane.getChildren().addAll(
            label_PageTitle,
            label_Instructions,
            line_Separator1,
            table_Invitations,
            button_DeleteInvitation,
            button_Refresh,
            line_Separator2,
            button_Return
        );
    }
    
    /*-*******************************************************************************************
    Helper methods
    */
    
    /**********
     * <p> Method: setupInvitationsTable() </p>
     * 
     * <p> Description: Setup the invitations table with columns for code, email, and role.</p>
     */
    @SuppressWarnings("unchecked")
    private void setupInvitationsTable() {
        table_Invitations.setLayoutX(20);
        table_Invitations.setLayoutY(110);
        table_Invitations.setPrefWidth(width - 40);
        table_Invitations.setPrefHeight(340);
        table_Invitations.setEditable(false);
        
        // Create columns with proper sizing
        TableColumn<ModelManageInvitations.InvitationRecord, String> codeColumn = 
            new TableColumn<>("Invitation Code");
        codeColumn.setPrefWidth(200);
        codeColumn.setMinWidth(150);
        codeColumn.setMaxWidth(250);
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        
        TableColumn<ModelManageInvitations.InvitationRecord, String> emailColumn = 
            new TableColumn<>("Email Address");
        emailColumn.setPrefWidth(350);
        emailColumn.setMinWidth(250);
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));
        
        TableColumn<ModelManageInvitations.InvitationRecord, String> roleColumn = 
            new TableColumn<>("Role");
        roleColumn.setPrefWidth(150);
        roleColumn.setMinWidth(100);
        roleColumn.setMaxWidth(200);
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        table_Invitations.setItems(invitationData);
        table_Invitations.getColumns().addAll(codeColumn, emailColumn, roleColumn);
    }
    
    /**********
     * Private local method to initialize the standard fields for a label
     * 
     * @param l		The Label object to be initialized
     * @param ff	The font to be used
     * @param f		The size of the font to be used
     * @param w		The width of the Label
     * @param p		The alignment (e.g. left, centered, or right)
     * @param x		The location from the left edge (x axis)
     * @param y		The location from the top (y axis)
     */
    private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }
    
    /**********
     * Private local method to initialize the standard fields for a button
     * 
     * @param b		The Button object to be initialized
     * @param ff	The font to be used
     * @param f		The size of the font to be used
     * @param w		The width of the Button
     * @param p		The alignment (e.g. left, centered, or right)
     * @param x		The location from the left edge (x axis)
     * @param y		The location from the top (y axis)
     */
    private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
}