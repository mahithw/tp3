package guiManageInvitations;

import java.util.List;

import database.Database;

/*******
 * <p> Title: ModelManageInvitations Class. </p>
 * 
 * <p> Description: The Manage Invitations Page Model. This class provides data 
 * handling methods for managing invitations including fetching all invitations 
 * and deleting selected invitations.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Mahith
 * 
 * @version 1.00		2025-09-30 Initial version
 *  
 */

public class ModelManageInvitations {
    
    /*-*******************************************************************************************
    Inner Classes
    */
    
    /**********
     * <p> Class: InvitationRecord </p>
     * 
     * <p> Description: Inner class to represent a single invitation record with 
     * code, email address, and role. This class is used by the TableView to display 
     * invitation data.</p>
     */
    public static class InvitationRecord {
        private String code;
        private String emailAddress;
        private String role;
        
        /**
         * Constructor for InvitationRecord
         * 
         * @param code The invitation code
         * @param emailAddress The email address associated with the invitation
         * @param role The role assigned to this invitation
         */
        public InvitationRecord(String code, String emailAddress, String role) {
            this.code = code;
            this.emailAddress = emailAddress;
            this.role = role;
        }
        
        /**
         * Get the invitation code
         * @return the code
         */
        public String getCode() {
            return code;
        }
        
        /**
         * Get the email address
         * @return the email address
         */
        public String getEmailAddress() {
            return emailAddress;
        }
        
        /**
         * Get the role
         * @return the role
         */
        public String getRole() {
            return role;
        }
    }
    
    /*-*******************************************************************************************
    Data Access Methods
    */
    
    /**********
     * <p> Method: getAllInvitations(Database database) </p>
     * 
     * <p> Description: Get all invitations from the database.</p>
     * 
     * @param database The database instance to query
     * @return a list of all invitation records
     */
    public static List<InvitationRecord> getAllInvitations(Database database) {
        return database.getAllInvitationRecords();
    }
    
    /**********
     * <p> Method: deleteInvitation(Database database, String code) </p>
     * 
     * <p> Description: Delete an invitation by its code.</p>
     * 
     * @param database The database instance to modify
     * @param code The invitation code to delete
     * @return true if deletion was successful, false otherwise
     */
    public static boolean deleteInvitation(Database database, String code) {
        try {
            // Use the existing removeInvitationAfterUse method from Database
            database.removeInvitationAfterUse(code);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting invitation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}