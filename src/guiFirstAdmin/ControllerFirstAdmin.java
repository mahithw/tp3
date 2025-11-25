package guiFirstAdmin;

import java.sql.SQLException;
import database.Database;
import entityClasses.User;
import javafx.stage.Stage;

public class ControllerFirstAdmin {
	/*-********************************************************************************************

	The controller attributes for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/
	
	private static String adminUsername = "";
	private static String adminPassword1 = "";
	private static String adminPassword2 = "";		
	protected static Database theDatabase = applicationMain.FoundationsMain.database;		

	/*-********************************************************************************************

	The User Interface Actions for this page
	
	*/
	
	
	/**********
	 * <p> Method: setAdminUsername() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the username field in the
	 * View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminUsername() {
		adminUsername = ViewFirstAdmin.text_AdminUsername.getText();
	}
	
	
	/**********
	 * <p> Method: setAdminPassword1() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the password 1 field in
	 * the View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminPassword1() {
		adminPassword1 = ViewFirstAdmin.text_AdminPassword1.getText();
		ViewFirstAdmin.label_PasswordsDoNotMatch.setText("");
	}
	
	
	/**********
	 * <p> Method: setAdminPassword2() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the password 2 field in
	 * the View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminPassword2() {
		adminPassword2 = ViewFirstAdmin.text_AdminPassword2.getText();		
		ViewFirstAdmin.label_PasswordsDoNotMatch.setText("");
	}
	
	
	/**********
	 * <p> Method: validateAdminUsername() </p>
	 * 
	 * <p> Description: Validates the username according to the following rules:
	 * - Length: 4-16 characters
	 * - Must start with a letter (alphabet character)
	 * - Can contain letters, numbers, and special characters (-, _, .)
	 * - No consecutive special characters
	 * - Cannot end with a special character
	 * </p>
	 * 
	 * @return true if username is valid, false otherwise
	 */
	protected static boolean validateAdminUsername() {
		// Check if username is between 4-16 characters
		if (adminUsername.length() < 4 || adminUsername.length() > 16) {
			ViewFirstAdmin.label_PasswordsDoNotMatch.setText("Username must be 4-16 characters long.");
			return false;
		}

		// Check if username starts with a letter (not a number)
		if (!Character.isLetter(adminUsername.charAt(0))) {
			ViewFirstAdmin.label_PasswordsDoNotMatch.setText("Username must start with a letter.");
			return false;
		}

		// Check if username ends with a special character
		char lastChar = adminUsername.charAt(adminUsername.length() - 1);
		if (lastChar == '-' || lastChar == '_' || lastChar == '.') {
			ViewFirstAdmin.label_PasswordsDoNotMatch.setText("Username cannot end with a special character.");
			return false;
		}

		// Check if username contains only allowed characters (letters, numbers, -, _, .)
		if (!adminUsername.matches("^[a-zA-Z0-9\\-_.]+$")) {
			ViewFirstAdmin.label_PasswordsDoNotMatch.setText("Username can only contain letters, numbers, -, _, and .");
			return false;
		}

		// Check for consecutive special characters
		if (adminUsername.matches(".*[\\-_.]{2,}.*")) {
			ViewFirstAdmin.label_PasswordsDoNotMatch.setText("Username cannot contain consecutive special characters.");
			return false;
		}

		return true;
	}


	/**********
	 * <p> Method: validateAdminPassword() </p>
	 * 
	 * <p> Description: Validates the password according to the following rules:
	 * - Length: 8-32 characters
	 * - Must start with alphanumeric character
	 * - Must contain at least one uppercase letter
	 * - Must contain at least one lowercase letter
	 * - Must contain at least one number
	 * - Must contain at least one special character
	 * </p>
	 * 
	 * @return true if password is valid, false otherwise
	 */
	protected static boolean validateAdminPassword() {
		// Check if password is between 8-32 characters
		if (adminPassword1.length() < 8 || adminPassword1.length() > 32) {
			ViewFirstAdmin.label_PasswordsDoNotMatch.setText("Password must be 8-32 characters long.");
			return false;
		}

		// Check if password starts with alphanumeric character
		if (!Character.isLetterOrDigit(adminPassword1.charAt(0))) {
			ViewFirstAdmin.label_PasswordsDoNotMatch.setText("Password must start with a letter or number.");
			return false;
		}

		// Check for at least one uppercase letter
		if (!adminPassword1.matches(".*[A-Z].*")) {
			ViewFirstAdmin.label_PasswordsDoNotMatch.setText("Password must contain at least one uppercase letter.");
			return false;
		}

		// Check for at least one lowercase letter
		if (!adminPassword1.matches(".*[a-z].*")) {
			ViewFirstAdmin.label_PasswordsDoNotMatch.setText("Password must contain at least one lowercase letter.");
			return false;
		}

		// Check for at least one number
		if (!adminPassword1.matches(".*[0-9].*")) {
			ViewFirstAdmin.label_PasswordsDoNotMatch.setText("Password must contain at least one number.");
			return false;
		}

		// Check for at least one special character
		if (!adminPassword1.matches(".*[!@#$%^&*()\\-_=+`~\\[\\]{}\\\\|;:'\",.<>?/].*")) {
			ViewFirstAdmin.label_PasswordsDoNotMatch.setText("Password must contain at least one special character.");
			return false;
		}

		return true;
	}


	/**********
	 * <p> Method: doSetupAdmin() </p>
	 * 
	 * <p> Description: This method is called when the user presses the button to set up the Admin
	 * account.  It start by trying to establish a new user and placing that user into the
	 * database.  If that is successful, we proceed to the UserUpdate page.</p>
	 * 
	 */
	protected static void doSetupAdmin(Stage ps, int r) {
		// Make sure the two passwords are the same
		if (adminPassword1.compareTo(adminPassword2) == 0) {
			// Validate username and password
			if (validateAdminUsername() && validateAdminPassword()) {
				// Create the user and proceed
				User user = new User(adminUsername, adminPassword1, "", "", "", "", "", true, false, false, false);
				try {
					// Create a new User object with admin role and register in the database
					theDatabase.register(user);
				} catch (SQLException e) {
					System.err.println("*** ERROR *** Database error trying to register a user: " + e.getMessage());
					e.printStackTrace();
					System.exit(0);
				}

				// User was established in the database, so navigate to the User Update Page
				guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewFirstAdmin.theStage, user);
			}
		} else {
			// The passwords do not match, clear the passwords and display a warning
			ViewFirstAdmin.text_AdminPassword1.setText("");
			ViewFirstAdmin.text_AdminPassword2.setText("");
			ViewFirstAdmin.label_PasswordsDoNotMatch.setText("The two passwords must match. Please try again!");
		}
	}


	
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */
	protected static void performQuit() {
		System.out.println("Perform Quit");
		System.exit(0);
	}	
}