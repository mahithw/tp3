package guiStaffHome;

import javafx.scene.control.Alert.AlertType;

/*******
 * <p> Title: ControllerStaffHome Class. </p>
 * 
 * <p> Description: The controller for the Staff Home Page.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @author TP3 Team
 * 
 * @version 1.00		2025-11-24 Initial version for TP3
 *  
 */

public class ControllerStaffHome {

	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated. Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/

	
	/**********
	 * <p> Method: performViewGrading() </p>
	 * 
	 * <p> Description: This method is called when the staff member clicks on the 
	 * "View Participation Grading" button. It opens the grading dashboard where staff can
	 * assess student participation in discussions. </p>
	 * 
	 */
	protected static void performViewGrading() {
		// Open the grading view
		staff.GradingView.displayGradingView(
			ViewStaffHome.theStage, 
			ViewStaffHome.theUser
		);
	}
	
	
	/**********
	 * <p> Method: performViewDiscussions() </p>
	 * 
	 * <p> Description: This method is called when the staff member clicks on the 
	 * "View Student Discussions" button. This is currently a stub that could be implemented
	 * to show all student discussions in a read-only view. </p>
	 * 
	 */
	protected static void performViewDiscussions() {
		// This is a placeholder for future functionality
		// Could show a read-only view of all student discussions
		ViewStaffHome.alertNotImplemented.setContentText(
			"The 'View Student Discussions' feature is not yet implemented.\n\n" +
			"This feature could provide staff with a read-only view of all " +
			"student discussion threads for monitoring and assessment purposes."
		);
		ViewStaffHome.alertNotImplemented.showAndWait();
	}
	
	
	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with an invitation code can
	 * start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewStaffHome.theStage);
	}
	
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program. It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
