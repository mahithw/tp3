package guiStaffHome;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;

/*******
 * <p> Title: ViewStaffHome Class. </p>
 * 
 * <p> Description: The Java/FX-based Staff Home Page.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @author TP3 Team
 * 
 * @version 1.00		2025-11-24 Initial version for TP3
 *  
 */

public class ViewStaffHome {
	
	/*-*******************************************************************************************

	Attributes
	
	*/
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// GUI Area 1: This is the title and current user information
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");
	
	// This is a separator
	private static Line line_Separator1 = new Line(20, 95, width-20, 95);
	
	// GUI Area 2: Status information section
	protected static Label label_SystemStatus = new Label("Staff Dashboard");
	protected static Label label_WelcomeMessage = new Label();
	protected static Label label_RoleDescription = new Label(
		"As a staff member, you can view student participation and grade discussions.");
	
	// This is a separator
	private static Line line_Separator2 = new Line(20, 235, width-20, 235);
	
	// GUI Area 3: Main staff action buttons
	protected static Label label_StaffActions = new Label("Staff Actions");
	protected static Button button_ViewGrading = new Button("View Participation Grading");
	protected static Button button_ViewDiscussions = new Button("View Student Discussions");
	protected static Alert alertNotImplemented = new Alert(AlertType.INFORMATION);
	
	// This is a separator
	private static Line line_Separator3 = new Line(20, 425, width-20, 425);
	
	// GUI Area 4: Navigation buttons (logout, quit)
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewStaffHome theView;
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;
	
	public static Scene theStaffHomeScene = null;


	/*-*******************************************************************************************

	Constructors
	
	*/

	/**********
	 * <p> Method: displayStaffHome(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Staff Home page to be displayed.
	 * 
	 * It first sets up very shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User who is currently logged in
	 *
	 */
	public static void displayStaffHome(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI by creating the 
		// singleton instance of this class
		if (theView == null) theView = new ViewStaffHome();
		
		// Populate the dynamic aspects of the GUI with the data from the user
		setupPageContent();
		
		// Set the title for the window
		theStage.setTitle("CSE 360 Foundation Code: Staff Home Page");
		
		// Show the page
		theStage.setScene(theStaffHomeScene);
		theStage.show();
	}

	
	/**********
	 * <p> Method: ViewStaffHome() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object. </p>
	 * 
	 * This is a singleton, so this is performed just once. Subsequent uses fill in the changeable
	 * fields using the displayStaffHome method.</p>
	 * 
	 */
	public ViewStaffHome() {
		
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theStaffHomeScene = new Scene(theRootPane, width, height);
		
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1: Page title and user details
		label_PageTitle.setText("Staff Home Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((event) -> 
			{guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser); });
		
		// GUI Area 2: Status and welcome section
		setupLabelUI(label_SystemStatus, "Arial", 24, width, Pos.CENTER, 0, 115);
		
		setupLabelUI(label_WelcomeMessage, "Arial", 18, width, Pos.CENTER, 0, 155);
		
		setupLabelUI(label_RoleDescription, "Arial", 16, width-80, Pos.CENTER, 40, 185);
		label_RoleDescription.setWrapText(true);
		
		// GUI Area 3: Staff action buttons
		setupLabelUI(label_StaffActions, "Arial", 22, 300, Pos.BASELINE_LEFT, 20, 255);
		
		setupButtonUI(button_ViewGrading, "Dialog", 18, 350, Pos.CENTER, 225, 295);
		button_ViewGrading.setOnAction((event) -> 
			{ControllerStaffHome.performViewGrading(); });
		
		setupButtonUI(button_ViewDiscussions, "Dialog", 18, 350, Pos.CENTER, 225, 345);
		button_ViewDiscussions.setOnAction((event) -> 
			{ControllerStaffHome.performViewDiscussions(); });
		
		// Setup the not implemented alert
		alertNotImplemented.setTitle("Not Implemented");
		alertNotImplemented.setHeaderText("Feature Not Available");
		
		// GUI Area 4: Navigation buttons
		setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
		button_Logout.setOnAction((event) -> {ControllerStaffHome.performLogout(); });
    
		setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
		button_Quit.setOnAction((event) -> {ControllerStaffHome.performQuit(); });
		
		// Add all the widgets to the root pane
		theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
			label_SystemStatus, label_WelcomeMessage, label_RoleDescription, line_Separator2,
			label_StaffActions, button_ViewGrading, button_ViewDiscussions, line_Separator3,
			button_Logout, button_Quit
		);
	}	

	
	/*-*******************************************************************************************

	Helper methods used to minimize the number of lines of code needed above
	
	*/

	/**********
	 * <p> Method: setupPageContent() </p>
	 * 
	 * <p> Description: This method sets up the dynamic content that changes based on the current
	 * user and system state. </p>
	 * 
	 */
	private static void setupPageContent() {
		// Set the user details
		String userName = theUser.getUserName();
		String preferredName = theUser.getPreferredFirstName();
		if (preferredName == null || preferredName.trim().isEmpty()) {
			preferredName = theUser.getFirstName();
		}
		
		label_UserDetails.setText("User: " + userName);
		label_WelcomeMessage.setText("Welcome, " + preferredName + "!");
	}
	
	/**********
	 * <p> Method: setupLabelUI() </p>
	 * 
	 * <p> Description: Helper method to configure Label UI properties. </p>
	 * 
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
			double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	/**********
	 * <p> Method: setupButtonUI() </p>
	 * 
	 * <p> Description: Helper method to configure Button UI properties. </p>
	 * 
	 */
	protected static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,
			double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}
}
