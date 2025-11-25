package guiAdminHome;

import database.Database;
import validate.EmailValidator;
import validate.EmailValidator.ValidationResult;


/*******
 * <p> Title: GUIAdminHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Home Page.  This class provides the controller actions
 * basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.  WHen those buttons
 * are pressed, an alert pops up to tell the user that the function associated with the button has
 * not been implemented. Also, be aware that What has been implemented may not work the way the
 * final product requires and there maybe defects in this code.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 *  
 */

public class ControllerAdminHome {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p> 
	 * 
	 * Title: performInvitation () Method. </p>
	 * 
	 * <p> Description: Protected method to send an email inviting a potential user to establish
	 * an account and a specific role. </p>
	 */
	protected static void performInvitation () {
		// Verify that the email address is valid - If not alert the user and return
		String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
		if (invalidEmailAddress(emailAddress)) {
			return;
		}
		
		// Check to ensure that we are not sending a second message with a new invitation code to
		// the same email address.  
		if (theDatabase.emailaddressHasBeenUsed(emailAddress)) {
			ViewAdminHome.alertEmailError.setContentText(
					"An invitation has already been sent to this email address.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}
		
		// Inform the user that the invitation has been sent and display the invitation code
		String theSelectedRole = (String) ViewAdminHome.combobox_SelectRole.getValue();
		String invitationCode = theDatabase.generateInvitationCode(emailAddress,
				theSelectedRole);
		String msg = "Code: " + invitationCode + " for role " + theSelectedRole + 
				" was sent to: " + emailAddress;
		System.out.println(msg);
		ViewAdminHome.alertEmailSent.setContentText(msg);
		ViewAdminHome.alertEmailSent.showAndWait();
		
		// Update the Admin Home pages status
		ViewAdminHome.text_InvitationEmailAddress.setText("");
		ViewAdminHome.label_NumberOfInvitations.setText("Number of outstanding invitations: " + 
				theDatabase.getNumberOfInvitations());
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: manageInvitations () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	/**********
	 * <p> 
	 * 
	 * Title: manageInvitations () Method. </p>
	 * 
	 * <p> Description: Protected method that displays the Manage Invitations page
	 * where admins can view and delete outstanding invitations. </p>
	 */
	protected static void manageInvitations () {
	    guiManageInvitations.ViewManageInvitations.displayManageInvitations(
	        ViewAdminHome.theStage, 
	        ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: setOnetimePassword () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void setOnetimePassword () {
	    String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
	    if (invalidEmailAddress(emailAddress)) {
	        return;
	    }
	    
	    if (!theDatabase.userExistsByEmail(emailAddress)) {
	        ViewAdminHome.alertEmailError.setContentText(
	                "No user account was found for: " + emailAddress);
	        ViewAdminHome.alertEmailError.showAndWait();
	        return;
	    }
	    
	    String otp = generateNumericOtp(8);
	    
	    long expiresAtEpochMillis = System.currentTimeMillis() + java.util.concurrent.TimeUnit.MINUTES.toMillis(30);
	    
	    boolean stored = theDatabase.storeOneTimePasswordByEmail(
	            emailAddress, otp, expiresAtEpochMillis, true);
	    
	    if (!stored) {
	        ViewAdminHome.alertEmailError.setContentText(
	                "Unable to set a one-time password for: " + emailAddress + ". Please try again.");
	        ViewAdminHome.alertEmailError.showAndWait();
	        return;
	    }
	    
	    String msg = "One-time password for " + emailAddress + " is: " + otp +
	            "\nOTP expires in 30 minutes and can be used only once.\n" +
	            "After logging in with OTP, user must set a new password and login again.";
	    System.out.println(msg);
	    ViewAdminHome.alertEmailSent.setContentText(msg);
	    ViewAdminHome.alertEmailSent.showAndWait();

	    // Clear the field
	    ViewAdminHome.text_InvitationEmailAddress.setText("");
	}

	private static String generateNumericOtp(int digits) {
	    java.security.SecureRandom rnd = new java.security.SecureRandom();
	    StringBuilder sb = new StringBuilder(digits);
	    for (int i = 0; i < digits; i++) sb.append(rnd.nextInt(10)); // 0..9
	    return sb.toString();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: deleteUser () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void deleteUser() {
	    // 1) Ask which user to delete
	    javafx.scene.control.TextInputDialog prompt = new javafx.scene.control.TextInputDialog();
	    prompt.setTitle("Delete User");
	    prompt.setHeaderText("Delete an Existing User");
	    prompt.setContentText("Enter the username to delete:");
	    java.util.Optional<String> result = prompt.showAndWait();
	    if (!result.isPresent()) return; // cancelled

	    String username = result.get().trim();

	    // 2) Validate input
	    if (username.isEmpty()) {
	        javafx.scene.control.Alert a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
	        a.setTitle("Delete User Error");
	        a.setHeaderText("Invalid Username");
	        a.setContentText("Username cannot be empty.");
	        a.showAndWait();
	        return;
	    }

	    // 3) Prevent deleting the currently logged-in account
	    try {
	        String currentUser = (ViewAdminHome.theUser != null) ? ViewAdminHome.theUser.getUserName() : null;
	        if (currentUser != null && currentUser.equalsIgnoreCase(username)) {
	            javafx.scene.control.Alert a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
	            a.setTitle("Delete User Error");
	            a.setHeaderText("Operation Not Allowed");
	            a.setContentText("You cannot delete the account you are currently logged in as.");
	            a.showAndWait();
	            return;
	        }

	        // 4) Verify user exists
	        if (!theDatabase.userExists(username)) {
	            javafx.scene.control.Alert a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
	            a.setTitle("Delete User Error");
	            a.setHeaderText("User Not Found");
	            a.setContentText("No user exists with username: " + username);
	            a.showAndWait();
	            return;
	        }

	        // 5) Optional: block deleting the last admin
	        if (theDatabase.isLastAdmin(username)) {
	            javafx.scene.control.Alert a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
	            a.setTitle("Delete User Error");
	            a.setHeaderText("Cannot Delete Last Admin");
	            a.setContentText("This user is the last Admin. Add another Admin before deleting.");
	            a.showAndWait();
	            return;
	        }

	        // 6) Confirm destructive action
	        javafx.scene.control.Alert confirm = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
	        confirm.setTitle("Confirm Deletion");
	        confirm.setHeaderText("Delete user: " + username + "?");
	        confirm.setContentText("This action cannot be undone.");
	        java.util.Optional<javafx.scene.control.ButtonType> choice = confirm.showAndWait();
	        if (!choice.isPresent() || choice.get() != javafx.scene.control.ButtonType.OK) return;

	        // 7) Delete via Database
	        boolean ok = theDatabase.deleteUser(username);
	        if (ok) {
	            javafx.scene.control.Alert done = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
	            done.setTitle("User Deleted");
	            done.setHeaderText("Success");
	            done.setContentText("User '" + username + "' has been deleted.");
	            done.showAndWait();

	            // Refresh UI to show updated user count
	            ViewAdminHome.label_NumberOfUsers.setText("Number of users: " + 
	                    theDatabase.getNumberOfUsers());
	        } else {
	            javafx.scene.control.Alert a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
	            a.setTitle("Delete User Error");
	            a.setHeaderText("Deletion Failed");
	            a.setContentText("Unable to delete user '" + username + "'.");
	            a.showAndWait();
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        javafx.scene.control.Alert a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
	        a.setTitle("Delete User Error");
	        a.setHeaderText("Unexpected Error");
	        a.setContentText("An error occurred while deleting the user.\n" + e.getMessage());
	        a.showAndWait();
	    }
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: listUsers () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void listUsers() {
	    try {
	        java.util.List<entityClasses.User> users = theDatabase.getAllUsersDetailed();

	        if (users == null || users.isEmpty()) {
	            ViewAdminHome.alertNotImplemented.setTitle("Users");
	            ViewAdminHome.alertNotImplemented.setHeaderText("List Users");
	            ViewAdminHome.alertNotImplemented.setContentText("No user accounts were found.");
	            ViewAdminHome.alertNotImplemented.showAndWait();
	            return;
	        }

	        // Build a tiny row model just for display
	        class Row {
	            final String username, name, email, roles;
	            Row(entityClasses.User u) {
	                this.username = u.getUserName() == null ? "" : u.getUserName();
	                String fn = u.getFirstName() == null ? "" : u.getFirstName();
	                String mn = u.getMiddleName() == null ? "" : u.getMiddleName();
	                String ln = u.getLastName() == null ? "" : u.getLastName();
	                String displayName = (fn + " " + (mn.isEmpty() ? "" : (mn + " ")) + ln).trim();
	                this.name = displayName.isEmpty() ? (u.getPreferredFirstName() == null ? "" : u.getPreferredFirstName()) : displayName;
	                this.email = u.getEmailAddress() == null ? "" : u.getEmailAddress();

	                java.util.List<String> r = new java.util.ArrayList<>(3);
	                if (u.getAdminRole()) r.add("Admin");
	                if (u.getNewRole1()) r.add("Role1");
	                if (u.getNewRole2()) r.add("Role2");
	                if (r.isEmpty()) r.add("—"); // no roles
	                this.roles = String.join(", ", r);
	            }
	        }

	        javafx.collections.ObservableList<Row> rows = javafx.collections.FXCollections.observableArrayList();
	        for (entityClasses.User u : users) rows.add(new Row(u));

	        // Table
	        javafx.scene.control.TableView<Row> table = new javafx.scene.control.TableView<>();
	        javafx.scene.control.TableColumn<Row, String> cUser = new javafx.scene.control.TableColumn<>("Username");
	        javafx.scene.control.TableColumn<Row, String> cName = new javafx.scene.control.TableColumn<>("Name");
	        javafx.scene.control.TableColumn<Row, String> cEmail = new javafx.scene.control.TableColumn<>("Email");
	        javafx.scene.control.TableColumn<Row, String> cRoles = new javafx.scene.control.TableColumn<>("Roles");

	        cUser.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().username));
	        cName.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().name));
	        cEmail.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().email));
	        cRoles.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().roles));

	        cUser.setPrefWidth(160);
	        cName.setPrefWidth(200);
	        cEmail.setPrefWidth(240);
	        cRoles.setPrefWidth(180);

	        table.getColumns().addAll(cUser, cName, cEmail, cRoles);
	        table.setItems(rows);
	        table.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

	        javafx.scene.control.Button closeBtn = new javafx.scene.control.Button("Close");
	        closeBtn.setOnAction(e -> ((javafx.stage.Stage) closeBtn.getScene().getWindow()).close());

	        javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(10, closeBtn);
	        buttons.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

	        javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(12,
	                new javafx.scene.control.Label("All Users (" + users.size() + ")"),
	                table,
	                buttons
	        );
	        root.setPadding(new javafx.geometry.Insets(12));

	        javafx.stage.Stage dialog = new javafx.stage.Stage();
	        dialog.setTitle("List Users");
	        dialog.initOwner(ViewAdminHome.theStage);
	        dialog.initModality(javafx.stage.Modality.WINDOW_MODAL);
	        dialog.setScene(new javafx.scene.Scene(root, 860, 520));
	        dialog.showAndWait();

	    } catch (Exception ex) {
	        System.out.println("\n*** ERROR ***: List Users failed: " + ex.getMessage());
	        ViewAdminHome.alertNotImplemented.setTitle("*** ERROR ***");
	        ViewAdminHome.alertNotImplemented.setHeaderText("List Users Failure");
	        ViewAdminHome.alertNotImplemented.setContentText("Listing users failed. See console for details.");
	        ViewAdminHome.alertNotImplemented.showAndWait();
	    }
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: addRemoveRoles () Method. </p>
	 * 
	 * <p> Description: Protected method that allows an admin to add and remove roles for any of
	 * the users currently in the system.  This is done by invoking the AddRemoveRoles Page. There
	 * is no need to specify the home page for the return as this can only be initiated by and
	 * Admin.</p>
	 */
	protected static void addRemoveRoles() {
		guiAddRemoveRoles.ViewAddRemoveRoles.displayAddRemoveRoles(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: invalidEmailAddress () Method. </p>
	 * 
	 * <p> Description: Protected method that validates an email address using the EmailValidator
	 * utility class. It checks for proper email format and displays appropriate error messages.</p>
	 * 
	 * @param emailAddress	This String holds what is expected to be an email address
	 * @return true if the email is invalid, false if it is valid
	 */
	protected static boolean invalidEmailAddress(String emailAddress) {
		// Use EmailValidator to validate the email
		ValidationResult result = EmailValidator.validateEmail(emailAddress);
		
		if (!result.isValid()) {
			// Display the specific error message from the validator
			ViewAdminHome.alertEmailError.setContentText(result.getErrorMessage());
			ViewAdminHome.alertEmailError.showAndWait();
			return true;
		}
		
		return false;
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performLogout () Method. </p>
	 * 
	 * <p> Description: Protected method that logs this user out of the system and returns to the
	 * login page for future use.</p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAdminHome.theStage);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performQuit () Method. </p>
	 * 
	 * <p> Description: Protected method that gracefully terminates the execution of the program.
	 * </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}