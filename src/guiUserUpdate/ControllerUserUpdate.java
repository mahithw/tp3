package guiUserUpdate;

import entityClasses.User;
import javafx.stage.Stage;
import validate.EmailValidator;
import validate.EmailValidator.ValidationResult;

public class ControllerUserUpdate {
/*-********************************************************************************************

The Controller for ViewUserUpdate

**********************************************************************************************/

/**********
* <p> Title: ControllerUserUpdate Class</p>
*
* <p> Description: This static class supports the actions initiated by the ViewUserUpdate
* class. In this case, there is just one method, no constructors, and no attributes.</p>
*
*/

/*-********************************************************************************************

The User Interface Actions for this page

**********************************************************************************************/


/**********
* <p> Method: public goToUserHomePage(Stage theStage, User theUser) </p>
*
* <p> Description: This method is called when the user has clicked on the button to
* proceed to the user's home page.
*
* @param theStage specifies the JavaFX Stage for next next GUI page and it's methods
*
* @param theUser specifies the user so we go to the right page and so the right information
*/
protected static void goToUserHomePage(Stage theStage, User theUser) {

	// Get the roles the user selected during login
	int theRole = applicationMain.FoundationsMain.activeHomePage;

	// Use that role to proceed to that role's home page
	switch (theRole) {
	case 1:
		guiAdminHome.ViewAdminHome.displayAdminHome(theStage, theUser);
		break;
	case 2:
		guiRole1.ViewRole1Home.displayRole1Home(theStage, theUser);
		break;
	case 3:
		guiRole2.ViewRole2Home.displayRole2Home(theStage, theUser);
		break;
	case 4:
		new guiStudentHome.ViewStudentHome().displayStudentHome( theStage, theUser);
		break;
	default:
		System.out.println("*** ERROR *** UserUpdate goToUserHome has an invalid role: " +
			theRole);
		System.exit(0);
	}
}

/**********
* <p> Method: validateEmail(String email) </p>
*
* <p> Description: Validates an email address using the EmailValidator utility class.
* Returns true if valid, false otherwise and displays an error alert.
*
* @param email The email address to validate
* @return true if email is valid, false otherwise
*/
protected static boolean validateEmail(String email) {
	ValidationResult result = EmailValidator.validateEmail(email);
	
	if (!result.isValid()) {
		// Display error alert
		javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
			javafx.scene.control.Alert.AlertType.ERROR);
		alert.setTitle("Invalid Email");
		alert.setHeaderText("Email Validation Error");
		alert.setContentText(result.getErrorMessage());
		alert.showAndWait();
		return false;
	}
	
	return true;
}

/**********
* <p> Method: validatePassword(String password) </p>
*
* <p> Description: Validates a password according to security requirements.
*
* @param password The password to validate
* @return null if valid, or error message string if invalid
*/
public static String validatePassword(String password) {
	if (password == null || password.length() < 8) {
		return "Password must be at least 8 characters";
	}

	boolean hasUpper = false;
	boolean hasLower = false;
	boolean hasDigit = false;
	boolean hasSpecial = false;

	for (char c : password.toCharArray()) {
		if (Character.isUpperCase(c)) hasUpper = true;
		else if (Character.isLowerCase(c)) hasLower = true;
		else if (Character.isDigit(c)) hasDigit = true;
		else hasSpecial = true;
	}

	if (!hasUpper) return "Password must contain an uppercase letter";
	if (!hasLower) return "Password must contain a lowercase letter";
	if (!hasDigit) return "Password must contain a digit";
	if (!hasSpecial) return "Password must contain a special character";

	return null; // Password is valid
}
}