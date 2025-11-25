package guiUserLogin;

import database.Database;
import entityClasses.User;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class ControllerUserLogin {
	
	/*-********************************************************************************************

	The User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/


	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	private static Stage theStage;	
	
	/**********
	 * <p> Method: public doLogin() </p>
	 * 
	 * <p> Description: This method is called when the user has clicked on the Login button. This
	 * method checks the username and password to see if they are valid.  If so, it then logs that
	 * user in my determining which role to use.
	 * 
	 * The method reaches batch to the view page and to fetch the information needed rather than
	 * passing that information as parameters.
	 * 
	 */	
	protected static void doLogin(Stage ts) {
		theStage = ts;
		String username = ViewUserLogin.text_Username.getText();
		String password = ViewUserLogin.text_Password.getText();
    	boolean loginResult = false;
    	
		// Fetch the user and verify the username
     	if (theDatabase.getUserAccountDetails(username) == false) {
     		// Don't provide too much information.  Don't say the username is invalid or the
     		// password is invalid.  Just say the pair is invalid.
    		ViewUserLogin.alertUsernamePasswordError.setContentText(
    				"Incorrect username/password. Try again!");
    		ViewUserLogin.alertUsernamePasswordError.showAndWait();
    		return;
    	}
		System.out.println("*** Username is valid");
		// === BEGIN OTP HANDLING ===
		// Trying OTP first: If the user typed an OTP into the password box, accept it once and force reset
		if (theDatabase.verifyAndConsumeOtpByUsername(username, password)) {
		    theDatabase.setMustResetOnNextLogin(username, true);
		    if (showForcePasswordChangeDialog(username)) {
		        theDatabase.setMustResetOnNextLogin(username, false);
		        Alert ok = new Alert(Alert.AlertType.INFORMATION);
		        ok.setHeaderText("Password Updated");
		        ok.setContentText("Your password was updated. Please sign in again.");
		        ok.showAndWait();
		        // Return to a fresh login screen popup; does not continue to normal login/dispatch
		        ViewUserLogin.displayUserLogin(theStage);
		    }
		    return;
		}

		// If a reset is required (OTP was used earlier but user didn't finish), force it now
		if (theDatabase.isMustResetOnNextLogin(username)) {
		    if (showForcePasswordChangeDialog(username)) {
		        theDatabase.setMustResetOnNextLogin(username, false);
		        Alert ok = new Alert(Alert.AlertType.INFORMATION);
		        ok.setHeaderText("Password Updated");
		        ok.setContentText("Your password was updated. Please sign in again.");
		        ok.showAndWait();
		        ViewUserLogin.displayUserLogin(theStage);
		    }
		    return;
		}
		// === END OTP HANDLING ===
		
		// Check to see that the login password matches the account password
    	String actualPassword = theDatabase.getCurrentPassword();
    	
    	if (password.compareTo(actualPassword) != 0) {
    		ViewUserLogin.alertUsernamePasswordError.setContentText(
    				"Incorrect username/password. Try again!");
    		ViewUserLogin.alertUsernamePasswordError.showAndWait();
    		return;
    	}
		System.out.println("*** Password is valid for this user");
		
		// Establish this user's details
    	User user = new User(username, password, theDatabase.getCurrentFirstName(), 
    			theDatabase.getCurrentMiddleName(), theDatabase.getCurrentLastName(), 
    			theDatabase.getCurrentPreferredFirstName(), theDatabase.getCurrentEmailAddress(), 
    			theDatabase.getCurrentAdminRole(), 
    			theDatabase.getCurrentNewRole1(), theDatabase.getCurrentNewRole2(), theDatabase.getCurrentNewStudent());
    	
    	// See which home page dispatch to use
		int numberOfRoles = theDatabase.getNumberOfRoles(user);		
		System.out.println("*** The number of roles: "+ numberOfRoles);
		if (numberOfRoles == 1) {
			// Single Account Home Page - The user has no choice here
			
			// Admin role
			if (user.getAdminRole()) {
				loginResult = theDatabase.loginAdmin(user);
				if (loginResult) {
					guiAdminHome.ViewAdminHome.displayAdminHome(theStage, user);
				}
			} else if (user.getNewRole1()) {
				loginResult = theDatabase.loginRole1(user);
				if (loginResult) {
					guiRole1.ViewRole1Home.displayRole1Home(theStage, user);
				}
			} else if (user.getNewRole2()) {
				loginResult = theDatabase.loginRole2(user);
				if (loginResult) {
					guiRole2.ViewRole2Home.displayRole2Home(theStage, user);
				}
			} else if (user.getNewStudent()) {
				loginResult = theDatabase.loginStudent(user);
				if (loginResult) {
					new guiStudentHome.ViewStudentHome().displayStudentHome(theStage, user);
				}
			} else if (user.getStaffRole()) { 
				loginResult = theDatabase.loginStaff(user); 
				if (loginResult) {
					guiStaffHome.ViewStaffHome.displayStaffHome(theStage, user); }
			} else {
				System.out.println("***** UserLogin goToUserHome request has an invalid role");
			}
		} else if (numberOfRoles > 1) {
			// Multiple Account Home Page - The user chooses which role to play
			System.out.println("*** Going to displayMultipleRoleDispatch");
			guiMultipleRoleDispatch.ViewMultipleRoleDispatch.
				displayMultipleRoleDispatch(theStage, user);
		}
	}
	
		
	/**********
	 * <p> Method: setup() </p>
	 * 
	 * <p> Description: This method is called to reset the page and then populate it with new
	 * content.</p>
	 * 
	 */
	protected static void doSetupAccount(Stage theStage, String invitationCode) {
		guiNewAccount.ViewNewAccount.displayNewAccount(theStage, invitationCode);
	}

	
	/**********
	 * <p> Method: public performQuit() </p>
	 * 
	 * <p> Description: This method is called when the user has clicked on the Quit button.  Doing
	 * this terminates the execution of the application.  All important data must be stored in the
	 * database, so there is no cleanup required.  (This is important so we can minimize the impact
	 * of crashed.)
	 * 
	 */	
	protected static void performQuit() {
		System.out.println("Perform Quit");
		System.exit(0);
	}	
	
	// === BEGIN helper function for OTP handling ===
	private static boolean showForcePasswordChangeDialog(String username) {
	    Dialog<ButtonType> dialog = new Dialog<>();
	    dialog.setTitle("Set New Password");
	    dialog.setHeaderText("Enter and confirm your new password.");

	    PasswordField p1 = new PasswordField();
	    p1.setPromptText("New password");
	    PasswordField p2 = new PasswordField();
	    p2.setPromptText("Confirm new password");
	    
	    Label errorLabel = new Label("");
	    errorLabel.setStyle("-fx-text-fill: red;");

	    GridPane grid = new GridPane();
	    grid.setHgap(8);
	    grid.setVgap(8);
	    grid.setPadding(new Insets(16));
	    grid.add(new Label("New password:"), 0, 0);
	    grid.add(p1, 1, 0);
	    grid.add(new Label("Confirm password:"), 0, 1);
	    grid.add(p2, 1, 1);
	    grid.add(errorLabel, 1, 2);

	    dialog.getDialogPane().setContent(grid);
	    ButtonType okBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
	    dialog.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

	    final Button saveButton = (Button) dialog.getDialogPane().lookupButton(okBtn);
	    saveButton.setDisable(true);
	    
	    Runnable validator = () -> {
	        String a = p1.getText();
	        String b = p2.getText();
	        
	        if (a == null || a.isEmpty()) {
	            errorLabel.setText("");
	            saveButton.setDisable(true);
	            return;
	        }
	        
	        // Use centralized validation from ControllerUserUpdate
	        String validationError = guiUserUpdate.ControllerUserUpdate.validatePassword(a);
	        if (validationError != null) {
	            errorLabel.setText(validationError);
	            saveButton.setDisable(true);
	            return;
	        }
	        
	        if (!a.equals(b)) {
	            errorLabel.setText("Passwords do not match");
	            saveButton.setDisable(true);
	            return;
	        }
	        
	        errorLabel.setText("");
	        saveButton.setDisable(false);
	    };
	    
	    p1.textProperty().addListener((obs, a, b) -> validator.run());
	    p2.textProperty().addListener((obs, a, b) -> validator.run());

	    ButtonType result = dialog.showAndWait().orElse(ButtonType.CANCEL);
	    if (result != okBtn) return false;

	    String newPw = p1.getText();
	    boolean ok = theDatabase.updatePassword(username, newPw);
	    if (!ok) {
	        Alert err = new Alert(Alert.AlertType.ERROR);
	        err.setHeaderText("Could not update password");
	        err.setContentText("Please try again.");
	        err.showAndWait();
	    }
	    return ok;
	}
	// === END helper function for OTP handling ===

}
