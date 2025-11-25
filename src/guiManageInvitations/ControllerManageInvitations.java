package guiManageInvitations;

import java.util.List;
import java.util.Optional;

import database.Database;
import javafx.scene.control.ButtonType;

/*******
 * <p> Title: ControllerManageInvitations Class. </p>
 * 
 * <p> Description: The Java/FX-based Manage Invitations Controller. This class 
 * provides the controller actions based on the user's use of the JavaFX GUI 
 * widgets defined by the View class.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Mahith
 * 
 * @version 1.00		2025-09-30 Initial version
 *  
 */

public class ControllerManageInvitations {
    
    /*-*******************************************************************************************
    User Interface Actions for this page
    
    This controller is not a class that gets instantiated. Rather, it is a collection of protected
    static methods that can be called by the View (which is a singleton instantiated object) and 
    the Model.
    */
    
    // Reference for the in-memory database so this package has access
    private static Database theDatabase = applicationMain.FoundationsMain.database;
    
    /**********
     * <p> Method: refreshInvitationList() </p>
     * 
     * <p> Description: Protected method to refresh the invitation list by fetching 
     * all invitations from the database and updating the table view.</p>
     */
    protected static void refreshInvitationList() {
        // Clear the current data
        ViewManageInvitations.invitationData.clear();
        
        // Get all invitations from database
        List<ModelManageInvitations.InvitationRecord> invitations = 
            ModelManageInvitations.getAllInvitations(theDatabase);
        
        // Debug output
        System.out.println("Invitation list refreshed. Total invitations: " + invitations.size());
        for (ModelManageInvitations.InvitationRecord inv : invitations) {
            System.out.println("  Code: " + inv.getCode() + ", Email: " + 
                inv.getEmailAddress() + ", Role: " + inv.getRole());
        }
        
        // Add to observable list for display
        ViewManageInvitations.invitationData.addAll(invitations);
        
        // Force table refresh
        ViewManageInvitations.table_Invitations.refresh();
    }
    
    /**********
     * <p> Method: deleteSelectedInvitation() </p>
     * 
     * <p> Description: Protected method to delete the selected invitation from the 
     * database after user confirmation.</p>
     */
    protected static void deleteSelectedInvitation() {
        // Get the selected invitation from the table
        ModelManageInvitations.InvitationRecord selected = 
            ViewManageInvitations.table_Invitations.getSelectionModel().getSelectedItem();
        
        // Check if an invitation is selected
        if (selected == null) {
            ViewManageInvitations.alertError.setContentText(
                "Please select an invitation to delete.");
            ViewManageInvitations.alertError.showAndWait();
            return;
        }
        
        // Confirm deletion with the user
        ViewManageInvitations.alertDeleteConfirm.setContentText(
            "Are you sure you want to delete the invitation for:\n" +
            selected.getEmailAddress() + " (" + selected.getRole() + ")?");
        
        Optional<ButtonType> result = ViewManageInvitations.alertDeleteConfirm.showAndWait();
        
        // If user confirmed, proceed with deletion
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Delete from database
            boolean success = ModelManageInvitations.deleteInvitation(
                theDatabase, selected.getCode());
            
            if (success) {
                // Show success message
                ViewManageInvitations.alertDeleteSuccess.setContentText(
                    "Invitation successfully deleted for: " + selected.getEmailAddress());
                ViewManageInvitations.alertDeleteSuccess.showAndWait();
                
                // Refresh the list to show updated data
                refreshInvitationList();
                
                System.out.println("Deleted invitation: " + selected.getCode() + 
                    " for " + selected.getEmailAddress());
            } else {
                // Show error message
                ViewManageInvitations.alertError.setContentText(
                    "Failed to delete invitation. Please try again.");
                ViewManageInvitations.alertError.showAndWait();
            }
        }
    }
    
    /**********
     * <p> Method: returnToAdminHome() </p>
     * 
     * <p> Description: Protected method to return to the Admin Home page.</p>
     */
    protected static void returnToAdminHome() {
        guiAdminHome.ViewAdminHome.displayAdminHome(
            ViewManageInvitations.theStage, 
            ViewManageInvitations.theUser);
    }
}