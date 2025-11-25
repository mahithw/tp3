package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import entityClasses.User;

/*******
 * <p> Title: Database Class. </p>
 * 
 * <p> Description: This is an in-memory database built on H2.  Detailed documentation of H2 can
 * be found at https://www.h2database.com/html/main.html (Click on "PDF (2MP) for a PDF of 438 pages
 * on the H2 main page.)  This class leverages H2 and provides numerous special supporting methods.
 * </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 2.00		2025-04-29 Updated and expanded from the version produce by on a previous
 * 							version by Pravalika Mukkiri and Ishwarya Hidkimath Basavaraj
 */

/*
 * The Database class is responsible for establishing and managing the connection to the database,
 * and performing operations such as user registration, login validation, handling invitation 
 * codes, and numerous other database related functions.
 */
public class Database {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	//  Shared variables used within this class
	private Connection connection = null;		// Singleton to access the database 
	private Statement statement = null;			// The H2 Statement is used to construct queries
	
	// These are the easily accessible attributes of the currently logged-in user
	// This is only useful for single user applications
	private String currentUsername;
	private String currentPassword;
	private String currentFirstName;
	private String currentMiddleName;
	private String currentLastName;
	private String currentPreferredFirstName;
	private String currentEmailAddress;
	private boolean currentAdminRole;
	private boolean currentNewRole1;
	private boolean currentNewRole2;
	private boolean currentNewStudent;
	private boolean currentStaffRole;

	/*******
	 * <p> Method: Database </p>
	 * 
	 * <p> Description: The default constructor used to establish this singleton object.</p>
	 * 
	 */
	
	public Database () {
		
	}
	
	
/*******
 * <p> Method: connectToDatabase </p>
 * 
 * <p> Description: Used to establish the in-memory instance of the H2 database from secondary
 *		storage.</p>
 *
 * @throws SQLException when the DriverManager is unable to establish a connection
 * 
 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
		
	}

	
/*******
 * <p> Method: createTables </p>
 * 
 * <p> Description: Used to create new instances of the two database tables used by this class.</p>
 * 
 */
	// Add these table creation statements to the createTables() method in Database.java

	// Add these table creation statements to the createTables() method in Database.java

	private void createTables() throws SQLException {
	    // Existing user table
	    String userTable = "CREATE TABLE IF NOT EXISTS userDB ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "userName VARCHAR(255) UNIQUE, "
	            + "password VARCHAR(255), "
	            + "firstName VARCHAR(255), "
	            + "middleName VARCHAR(255), "
	            + "lastName VARCHAR (255), "
	            + "preferredFirstName VARCHAR(255), "
	            + "emailAddress VARCHAR(255), "
	            + "adminRole BOOL DEFAULT FALSE, "
	            + "newRole1 BOOL DEFAULT FALSE, "
	            + "newRole2 BOOL DEFAULT FALSE)";
	    statement.execute(userTable);
	    
	    // Existing invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "emailAddress VARCHAR(255), "
	            + "role VARCHAR(10))";
	    statement.execute(invitationCodesTable);
	    
	    // Existing ALTER statements
	    statement.execute("ALTER TABLE userDB ADD COLUMN IF NOT EXISTS otp VARCHAR(255)");
	    statement.execute("ALTER TABLE userDB ADD COLUMN IF NOT EXISTS otpExpiresAt BIGINT");
	    statement.execute("ALTER TABLE userDB ADD COLUMN IF NOT EXISTS mustResetOnNextLogin BOOL DEFAULT FALSE");
	    statement.execute("ALTER TABLE userDB ADD COLUMN IF NOT EXISTS newStudent BOOL DEFAULT FALSE");
		statement.execute("ALTER TABLE userDB ADD COLUMN IF NOT EXISTS staffRole BOOL DEFAULT FALSE");

	    // ========== ADD THESE NEW TABLES ==========
	    
	    // Posts table
	    String postsTable = "CREATE TABLE IF NOT EXISTS posts ("
	            + "postId VARCHAR(20) PRIMARY KEY, "
	            + "authorUsername VARCHAR(255), "
	            + "title VARCHAR(200), "
	            + "content VARCHAR(5000), "
	            + "thread VARCHAR(100), "
	            + "createdAt TIMESTAMP, "
	            + "updatedAt TIMESTAMP, "
	            + "isDeleted BOOLEAN DEFAULT FALSE, "
	            + "replyCount INT DEFAULT 0)";
	    statement.execute(postsTable);
	    
	    // Replies table
	    String repliesTable = "CREATE TABLE IF NOT EXISTS replies ("
	            + "replyId VARCHAR(20) PRIMARY KEY, "
	            + "postId VARCHAR(20), "
	            + "authorUsername VARCHAR(255), "
	            + "content VARCHAR(3000), "
	            + "createdAt TIMESTAMP, "
	            + "updatedAt TIMESTAMP, "
	            + "isDeleted BOOLEAN DEFAULT FALSE)";
	    statement.execute(repliesTable);
	    
	    // Reply read tracking table
	    String replyReadsTable = "CREATE TABLE IF NOT EXISTS reply_reads ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "replyId VARCHAR(20), "
	            + "userName VARCHAR(255), "
	            + "readAt TIMESTAMP, "
	            + "UNIQUE(replyId, userName))";
	    statement.execute(replyReadsTable);
	}


/*******
 * <p> Method: isDatabaseEmpty </p>
 * 
 * <p> Description: If the user database has no rows, true is returned, else false.</p>
 * 
 * @return true if the database is empty, else it returns false
 * 
 */
	public boolean isDatabaseEmpty() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count") == 0;
			}
		}  catch (SQLException e) {
	        return false;
	    }
		return true;
	}
	
	
/*******
 * <p> Method: getNumberOfUsers </p>
 * 
 * <p> Description: Returns an integer .of the number of users currently in the user database. </p>
 * 
 * @return the number of user records in the database.
 * 
 */
	public int getNumberOfUsers() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
	        return 0;
	    }
		return 0;
	}

/*******
 * <p> Method: register(User user) </p>
 * 
 * <p> Description: Creates a new row in the database using the user parameter. </p>
 * 
 * @throws SQLException when there is an issue creating the SQL command or executing it.
 * 
 * @param user specifies a user object to be added to the database.
 * 
 */
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO userDB (userName, password, firstName, middleName, "
        + "lastName, preferredFirstName, emailAddress, adminRole, newRole1, newRole2, newStudent, staffRole) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			currentUsername = user.getUserName();
			pstmt.setString(1, currentUsername);
			
			currentPassword = user.getPassword();
			pstmt.setString(2, currentPassword);
			
			currentFirstName = user.getFirstName();
			pstmt.setString(3, currentFirstName);
			
			currentMiddleName = user.getMiddleName();			
			pstmt.setString(4, currentMiddleName);
			
			currentLastName = user.getLastName();
			pstmt.setString(5, currentLastName);
			
			currentPreferredFirstName = user.getPreferredFirstName();
			pstmt.setString(6, currentPreferredFirstName);
			
			currentEmailAddress = user.getEmailAddress();
			pstmt.setString(7, currentEmailAddress);
			
			currentAdminRole = user.getAdminRole();
			pstmt.setBoolean(8, currentAdminRole);
			
			currentNewRole1 = user.getNewRole1();
			pstmt.setBoolean(9, currentNewRole1);
			
			currentNewRole2 = user.getNewRole2();
			pstmt.setBoolean(10, currentNewRole2);
			
			currentNewStudent = user.getNewStudent();
			pstmt.setBoolean(11, currentNewStudent);

			boolean currentStaffRole = user.getStaffRole();
    		pstmt.setBoolean(12, currentStaffRole);
			
			pstmt.executeUpdate();
		}
		
	}
	
/*******
 *  <p> Method: List getUserList() </p>
 *  
 *  <P> Description: Generate an List of Strings, one for each user in the database,
 *  starting with "<Select User>" at the start of the list. </p>
 *  
 *  @return a list of userNames found in the database.
 */
	public List<String> getUserList () {
		List<String> userList = new ArrayList<String>();
		userList.add("<Select a User>");
		String query = "SELECT userName FROM userDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				userList.add(rs.getString("userName"));
			}
		} catch (SQLException e) {
	        return null;
	    }
//		System.out.println(userList);
		return userList;
	}

/*******
 * <p> Method: boolean loginAdmin(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Admin role.
 * 
 * @return true if the specified user has been logged in as an Admin else false.
 * 
 */
	public boolean loginAdmin(User user){
		// Validates an admin user's login credentials so the user can login in as an Admin.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "adminRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();	// If a row is returned, rs.next() will return true		
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
/*******
 * <p> Method: boolean loginRole1(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Student role.
 * 
 * @return true if the specified user has been logged in as an Student else false.
 * 
 */
	public boolean loginRole1(User user) {
		// Validates a student user's login credentials.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newRole1 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: boolean loginRole2(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * 		is the same as a row in the table for the username, password, and role. </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Reviewer role.
	 * 
	 * @return true if the specified user has been logged in as an Student else false.
	 * 
	 */
	// Validates a reviewer user's login credentials.
	public boolean loginRole2(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newRole2 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}
	
	/*******
	 * <p> Method: boolean loginRole2(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * 		is the same as a row in the table for the username, password, and role. </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Reviewer role.
	 * 
	 * @return true if the specified user has been logged in as an Student else false.
	 * 
	 */
	// Validates a reviewer user's login credentials.
	public boolean loginStudent(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newStudent = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: boolean loginStaff(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 *      is the same as a row in the table for the username, password, and role. </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Staff role.
	 * 
	 * @return true if the specified user has been logged in as Staff else false.
	 * 
	 */
	public boolean loginStaff(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "staffRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	/*******
	 * <p> Method: boolean doesUserExist(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username is  in the table. </p>
	 * 
	 * @param userName specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return true if the specified user is in the table else false.
	 * 
	 */
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}

	
	/*******
	 * <p> Method: int getNumberOfRoles(User user) </p>
	 * 
	 * <p> Description: Determine the number of roles a specified user plays. </p>
	 * 
	 * @param user specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return the number of roles this user plays (0 - 5).
	 * 
	 */	
	// Get the number of roles that this user plays
	public int getNumberOfRoles (User user) {
		int numberOfRoles = 0;
		if (user.getAdminRole()) numberOfRoles++;
		if (user.getNewRole1()) numberOfRoles++;
		if (user.getNewRole2()) numberOfRoles++;
		if (user.getNewStudent()) numberOfRoles++;
		return numberOfRoles;
	}	

	
	/*******
	 * <p> Method: String generateInvitationCode(String emailAddress, String role) </p>
	 * 
	 * <p> Description: Given an email address and a roles, this method establishes and invitation
	 * code and adds a record to the InvitationCodes table.  When the invitation code is used, the
	 * stored email address is used to establish the new user and the record is removed from the
	 * table.</p>
	 * 
	 * @param emailAddress specifies the email address for this new user.
	 * 
	 * @param role specified the role that this new user will play.
	 * 
	 * @return the code of six characters so the new user can use it to securely setup an account.
	 * 
	 */
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode(String emailAddress, String role) {
	    String code = UUID.randomUUID().toString().substring(0, 6); // Generate a random 6-character code
	    String query = "INSERT INTO InvitationCodes (code, emailaddress, role) VALUES (?, ?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.setString(2, emailAddress);
	        pstmt.setString(3, role);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return code;
	}

	
	/*******
	 * <p> Method: int getNumberOfInvitations() </p>
	 * 
	 * <p> Description: Determine the number of outstanding invitations in the table.</p>
	 *  
	 * @return the number of invitations in the table.
	 * 
	 */
	// Number of invitations in the database
	public int getNumberOfInvitations() {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return 0;
	}
	
	
	/*******
	 * <p> Method: List<InvitationRecord> getAllInvitationRecords() </p>
	 * 
	 * <p> Description: Get all invitation records from the database.</p>
	 * 
	 * @return a list of all invitation records
	 */
	public List<guiManageInvitations.ModelManageInvitations.InvitationRecord> getAllInvitationRecords() {
	    List<guiManageInvitations.ModelManageInvitations.InvitationRecord> invitations = 
	        new ArrayList<>();
	    
	    String query = "SELECT code, emailAddress, role FROM InvitationCodes ORDER BY emailAddress";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	            String code = rs.getString("code");
	            String email = rs.getString("emailAddress");
	            String role = rs.getString("role");
	            
	            invitations.add(new guiManageInvitations.ModelManageInvitations.InvitationRecord(
	                code, email, role));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return invitations;
	}
	
	/*******
	 * <p> Method: boolean emailaddressHasBeenUsed(String emailAddress) </p>
	 * 
	 * <p> Description: Determine if an email address has been user to establish a user.</p>
	 * 
	 * @param emailAddress is a string that identifies a user in the table
	 *  
	 * @return true if the email address is in the table, else return false.
	 * 
	 */
	// Check to see if an email address is already in the database
	public boolean emailaddressHasBeenUsed(String emailAddress) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE emailAddress = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        ResultSet rs = pstmt.executeQuery();
	        System.out.println(rs);
	        if (rs.next()) {
	            // Mark the code as used
	        	return rs.getInt("count")>0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
	/*******
	 * <p> Method: String getRoleGivenAnInvitationCode(String code) </p>
	 * 
	 * <p> Description: Get the role associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the role for the code or an empty string.
	 * 
	 */
	// Obtain the roles associated with an invitation code.
	public String getRoleGivenAnInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("role");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return "";
	}

	
	/*******
	 * <p> Method: String getEmailAddressUsingCode (String code ) </p>
	 * 
	 * <p> Description: Get the email addressed associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the email address for the code or an empty string.
	 * 
	 */
	// For a given invitation code, return the associated email address of an empty string
	public String getEmailAddressUsingCode (String code ) {
	    String query = "SELECT emailAddress FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("emailAddress");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return "";
	}
	
	
	/*******
	 * <p> Method: void removeInvitationAfterUse(String code) </p>
	 * 
	 * <p> Description: Remove an invitation record once it is used.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 */
	// Remove an invitation using an email address once the user account has been setup
	public void removeInvitationAfterUse(String code) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	int counter = rs.getInt(1);
	            // Only do the remove if the code is still in the invitation table
	        	if (counter > 0) {
        			query = "DELETE FROM InvitationCodes WHERE code = ?";
	        		try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
	        			pstmt2.setString(1, code);
	        			pstmt2.executeUpdate();
	        		}catch (SQLException e) {
	        	        e.printStackTrace();
	        	    }
	        	}
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return;
	}
	
	
	/*******
	 * <p> Method: String getFirstName(String username) </p>
	 * 
	 * <p> Description: Get the first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the first name of a user given that user's username 
	 *  
	 */
	// Get the First Name
	public String getFirstName(String username) {
		String query = "SELECT firstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	

	/*******
	 * <p> Method: void updateFirstName(String username, String firstName) </p>
	 * 
	 * <p> Description: Update the first name of a user given that user's username and the new
	 *		first name.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param firstName is the new first name for the user
	 *  
	 */
	// update the first name
	public void updateFirstName(String username, String firstName) {
	    String query = "UPDATE userDB SET firstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, firstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentFirstName = firstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	/*******
	 * <p> Method: String getMiddleName(String username) </p>
	 * 
	 * <p> Description: Get the middle name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the middle name of a user given that user's username 
	 *  
	 */
	// get the middle name
	public String getMiddleName(String username) {
		String query = "SELECT MiddleName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("middleName"); // Return the middle name if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}

	
	/*******
	 * <p> Method: void updateMiddleName(String username, String middleName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param middleName is the new middle name for the user
	 *  
	 */
	// update the middle name
	public void updateMiddleName(String username, String middleName) {
	    String query = "UPDATE userDB SET middleName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, middleName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentMiddleName = middleName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getLastName(String username) </p>
	 * 
	 * <p> Description: Get the last name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the last name of a user given that user's username 
	 *  
	 */
	// get he last name
	public String getLastName(String username) {
		String query = "SELECT LastName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("lastName"); // Return last name role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateLastName(String username, String lastName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param lastName is the new last name for the user
	 *  
	 */
	// update the last name
	public void updateLastName(String username, String lastName) {
	    String query = "UPDATE userDB SET lastName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, lastName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentLastName = lastName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getPreferredFirstName(String username) </p>
	 * 
	 * <p> Description: Get the preferred first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the preferred first name of a user given that user's username 
	 *  
	 */
	// get the preferred first name
	public String getPreferredFirstName(String username) {
		String query = "SELECT preferredFirstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the preferred first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updatePreferredFirstName(String username, String preferredFirstName) </p>
	 * 
	 * <p> Description: Update the preferred first name of a user given that user's username and
	 * 		the new preferred first name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param preferredFirstName is the new preferred first name for the user
	 *  
	 */
	// update the preferred first name of the user
	public void updatePreferredFirstName(String username, String preferredFirstName) {
	    String query = "UPDATE userDB SET preferredFirstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, preferredFirstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentPreferredFirstName = preferredFirstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getEmailAddress(String username) </p>
	 * 
	 * <p> Description: Get the email address of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the email address of a user given that user's username 
	 *  
	 */
	// get the email address
	public String getEmailAddress(String username) {
		String query = "SELECT emailAddress FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("emailAddress"); // Return the email address if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateEmailAddress(String username, String emailAddress) </p>
	 * 
	 * <p> Description: Update the email address name of a user given that user's username and
	 * 		the new email address.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param emailAddress is the new preferred first name for the user
	 *  
	 */
	// update the email address
	public void updateEmailAddress(String username, String emailAddress) {
	    String query = "UPDATE userDB SET emailAddress = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentEmailAddress = emailAddress;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	public boolean userExistsByEmail(String emailAddress) {
	    String q = "SELECT COUNT(*) FROM userDB WHERE emailAddress = ?";
	    try (PreparedStatement ps = connection.prepareStatement(q)) {
	        ps.setString(1, emailAddress);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) return rs.getInt(1) > 0;
	    } catch (SQLException e) { e.printStackTrace(); }
	    return false;
	}

	// Resolve username by email (handy for login flow / resets)
	public String getUsernameByEmail(String emailAddress) {
	    String q = "SELECT userName FROM userDB WHERE emailAddress = ? LIMIT 1";
	    try (PreparedStatement ps = connection.prepareStatement(q)) {
	        ps.setString(1, emailAddress);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) return rs.getString("userName");
	    } catch (SQLException e) { e.printStackTrace(); }
	    return null;
	}

	// Store/overwrite OTP for a user identified by email
	public boolean storeOneTimePasswordByEmail(String emailAddress,
	                                           String otp,
	                                           long expiresAtEpochMillis,
	                                           boolean mustResetOnNextLogin) {
	    String q = "UPDATE userDB SET otp = ?, otpExpiresAt = ?, mustResetOnNextLogin = ? " +
	               "WHERE emailAddress = ?";
	    try (PreparedStatement ps = connection.prepareStatement(q)) {
	        ps.setString(1, otp);                       // NOTE: for production, store a hash
	        ps.setLong(2, expiresAtEpochMillis);
	        ps.setBoolean(3, mustResetOnNextLogin);
	        ps.setString(4, emailAddress);
	        return ps.executeUpdate() >= 1;             // in case email isn't unique
	    } catch (SQLException e) { e.printStackTrace(); }
	    return false;
	}

	// Verify OTP by username and consume it (clear) if valid & not expired
	public boolean verifyAndConsumeOtpByUsername(String userName, String otpCandidate) {
	    String q = "SELECT otp, otpExpiresAt FROM userDB WHERE userName = ?";
	    try (PreparedStatement ps = connection.prepareStatement(q)) {
	        ps.setString(1, userName);
	        ResultSet rs = ps.executeQuery();
	        if (!rs.next()) return false;

	        String otp = rs.getString("otp");
	        long expiresAt = rs.getLong("otpExpiresAt");
	        long now = System.currentTimeMillis();

	        if (otp != null && otp.equals(otpCandidate) && now <= expiresAt) {
	            // clear OTP immediately so it cannot be reused
	            String clear = "UPDATE userDB SET otp = NULL, otpExpiresAt = NULL WHERE userName = ?";
	            try (PreparedStatement ps2 = connection.prepareStatement(clear)) {
	                ps2.setString(1, userName);
	                ps2.executeUpdate();
	            }
	            // keep mustResetOnNextLogin = true (set during admin action)
	            return true;
	        }
	    } catch (SQLException e) { e.printStackTrace(); }
	    return false;
	}

	// Force / clear the "must reset" flag
	public boolean setMustResetOnNextLogin(String userName, boolean value) {
	    String q = "UPDATE userDB SET mustResetOnNextLogin = ? WHERE userName = ?";
	    try (PreparedStatement ps = connection.prepareStatement(q)) {
	        ps.setBoolean(1, value);
	        ps.setString(2, userName);
	        return ps.executeUpdate() == 1;
	    } catch (SQLException e) { e.printStackTrace(); }
	    return false;
	}

	public boolean isMustResetOnNextLogin(String userName) {
	    String q = "SELECT mustResetOnNextLogin FROM userDB WHERE userName = ?";
	    try (PreparedStatement ps = connection.prepareStatement(q)) {
	        ps.setString(1, userName);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) return rs.getBoolean(1);
	    } catch (SQLException e) { e.printStackTrace(); }
	    return false;
	}

	// Update the real password after OTP login
	public boolean updatePassword(String userName, String newPassword) {
	    String q = "UPDATE userDB SET password = ? WHERE userName = ?";
	    try (PreparedStatement ps = connection.prepareStatement(q)) {
	        ps.setString(1, newPassword);  // NOTE: store a hash in production
	        ps.setString(2, userName);
	        return ps.executeUpdate() == 1;
	    } catch (SQLException e) { e.printStackTrace(); }
	    return false;
	}
	
	/*******
	 * <p> Method: boolean getUserAccountDetails(String username) </p>
	 * 
	 * <p> Description: Get all the attributes of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return true of the get is successful, else false
	 *  
	 */
	// get the attributes for a specified user

	public boolean getUserAccountDetails(String username) {
	    String query = "SELECT userName, password, firstName, middleName, lastName, preferredFirstName, "
	                 + "emailAddress, adminRole, newRole1, newRole2, newStudent "
	                 + "FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	            currentUsername = rs.getString("userName");
	            currentPassword = rs.getString("password");
	            currentFirstName = rs.getString("firstName");
	            currentMiddleName = rs.getString("middleName");
	            currentLastName = rs.getString("lastName");
	            currentPreferredFirstName = rs.getString("preferredFirstName");
	            currentEmailAddress = rs.getString("emailAddress");
	            currentAdminRole = rs.getBoolean("adminRole");
	            currentNewRole1 = rs.getBoolean("newRole1");
	            currentNewRole2 = rs.getBoolean("newRole2");
	            currentNewStudent = rs.getBoolean("newStudent");
				currentStaffRole = rs.getBoolean("staffRole");

	            // Optional debug line:
	            System.out.println("*** Fetching account data for user: " + username);
	            System.out.println("    Admin: " + currentAdminRole
	                             + ", Role1: " + currentNewRole1
	                             + ", Role2: " + currentNewRole2
	                             + ", Student: " + currentNewStudent
								 + ", Staff: " + currentStaffRole);
	            return true;
	        }
	    } catch (SQLException e) {
	        System.err.println("*** ERROR in getUserAccountDetails for user: " + username);
	        e.printStackTrace();
	    }
	    return false;
	}
	
	
	/*******
	 * <p> Method: boolean updateUserRole(String username, String role, String value) </p>
	 * 
	 * <p> Description: Update a specified role for a specified user's and set and update all the
	 * 		current user attributes.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param role is string that specifies the role to update
	 * 
	 * @param value is the string that specified TRUE or FALSE for the role
	 * 
	 * @return true if the update was successful, else false
	 *  
	 */
	// Update a users role
	public boolean updateUserRole(String username, String role, String value) {
		if (role.compareTo("Admin") == 0) {
			String query = "UPDATE userDB SET adminRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentAdminRole = true;
				else
					currentAdminRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Role1") == 0) {
			String query = "UPDATE userDB SET newRole1 = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewRole1 = true;
				else
					currentNewRole1 = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Role2") == 0) {
			String query = "UPDATE userDB SET newRole2 = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewRole2 = true;
				else
					currentNewRole2 = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		
		if (role.compareTo("Student") == 0) {
		    String query = "UPDATE userDB SET newStudent = ? WHERE username = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, value);
		        pstmt.setString(2, username);
		        pstmt.executeUpdate();
		        if (value.compareTo("true") == 0)
		            currentNewStudent = true;
		        else
		            currentNewStudent = false;
		        return true;
		    } catch (SQLException e) {
		        return false;
		    }
		}

		if (role.compareTo("Student") == 0) {
			String query = "UPDATE userDB SET newStudent = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewStudent = true;
				else
					currentNewStudent = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		
		return false;
	}
	
	/** Returns true if a user with this username exists. */
	public boolean userExists(String username) {
	    // Reuse existing method to avoid duplication
	    return doesUserExist(username);
	}

	/**
	 * Returns true if the given username is the last remaining Admin.
	 * Prevents removing the final admin account.
	 */
	public boolean isLastAdmin(String username) {
	    // 1) Is this user an admin at all?
	    final String isAdminSql = "SELECT adminRole FROM userDB WHERE userName = ?";
	    // 2) How many admins exist in total?
	    final String countAdminsSql = "SELECT COUNT(*) FROM userDB WHERE adminRole = TRUE";

	    try (
	        PreparedStatement isAdminStmt = connection.prepareStatement(isAdminSql);
	        Statement countStmt = connection.createStatement()
	    ) {
	        // Check whether target user is an admin
	        isAdminStmt.setString(1, username);
	        try (ResultSet rs = isAdminStmt.executeQuery()) {
	            if (!rs.next() || !rs.getBoolean(1)) {
	                // User is not an admin (or doesn't exist) -> cannot be the "last admin"
	                return false;
	            }
	        }

	        // Count total admins
	        try (ResultSet countRs = countStmt.executeQuery(countAdminsSql)) {
	            if (countRs.next()) {
	                int totalAdmins = countRs.getInt(1);
	                return totalAdmins <= 1; // true if this is the only admin
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // On error, fail-safe: do not block deletion unless we are sure
	}

	/**
	 * Deletes the user with the given username.
	 * @return true if a row was deleted; false otherwise.
	 */
	public boolean deleteUser(String username) {
	    final String sql = "DELETE FROM userDB WHERE userName = ?";
	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setString(1, username);
	        int rows = ps.executeUpdate();
	        return rows > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	
	public java.util.List<entityClasses.User> getAllUsersDetailed() {
	    java.util.List<entityClasses.User> list = new java.util.ArrayList<>();
	    final String sql = "SELECT userName, password, firstName, middleName, lastName, " +
	            "preferredFirstName, emailAddress, adminRole, newRole1, newRole2, newStudent FROM userDB";
	    
	    try (java.sql.PreparedStatement ps = connection.prepareStatement(sql);
	         java.sql.ResultSet rs = ps.executeQuery()) {
	        
	        while (rs.next()) {
	            String userName = rs.getString("userName");
	            String password = rs.getString("password");
	            String fn = rs.getString("firstName");
	            String mn = rs.getString("middleName");
	            String ln = rs.getString("lastName");
	            String pfn = rs.getString("preferredFirstName");
	            String email = rs.getString("emailAddress");
	            boolean rAdmin = rs.getBoolean("adminRole");
	            boolean r1 = rs.getBoolean("newRole1");
	            boolean r2 = rs.getBoolean("newRole2");
	            boolean r3 = rs.getBoolean("newStudent");
	            
	            entityClasses.User u = new entityClasses.User(
	                userName, password, fn, mn, ln, pfn, email, rAdmin, r1, r2, r3
	            );
	            list.add(u);
	        }
	    } catch (java.sql.SQLException e) {
	        e.printStackTrace();
	    }
	    return list;
	}
	
	// Attribute getters for the current user
	/*******
	 * <p> Method: String getCurrentUsername() </p>
	 * 
	 * <p> Description: Get the current user's username.</p>
	 * 
	 * @return the username value is returned
	 *  
	 */
	public String getCurrentUsername() { return currentUsername;};

	
	/*******
	 * <p> Method: String getCurrentPassword() </p>
	 * 
	 * <p> Description: Get the current user's password.</p>
	 * 
	 * @return the password value is returned
	 *  
	 */
	public String getCurrentPassword() { return currentPassword;};

	
	/*******
	 * <p> Method: String getCurrentFirstName() </p>
	 * 
	 * <p> Description: Get the current user's first name.</p>
	 * 
	 * @return the first name value is returned
	 *  
	 */
	public String getCurrentFirstName() { return currentFirstName;};

	
	/*******
	 * <p> Method: String getCurrentMiddleName() </p>
	 * 
	 * <p> Description: Get the current user's middle name.</p>
	 * 
	 * @return the middle name value is returned
	 *  
	 */
	public String getCurrentMiddleName() { return currentMiddleName;};

	
	/*******
	 * <p> Method: String getCurrentLastName() </p>
	 * 
	 * <p> Description: Get the current user's last name.</p>
	 * 
	 * @return the last name value is returned
	 *  
	 */
	public String getCurrentLastName() { return currentLastName;};

	
	/*******
	 * <p> Method: String getCurrentPreferredFirstName( </p>
	 * 
	 * <p> Description: Get the current user's preferred first name.</p>
	 * 
	 * @return the preferred first name value is returned
	 *  
	 */
	public String getCurrentPreferredFirstName() { return currentPreferredFirstName;};

	
	/*******
	 * <p> Method: String getCurrentEmailAddress() </p>
	 * 
	 * <p> Description: Get the current user's email address name.</p>
	 * 
	 * @return the email address value is returned
	 *  
	 */
	public String getCurrentEmailAddress() { return currentEmailAddress;};

	
	/*******
	 * <p> Method: boolean getCurrentAdminRole() </p>
	 * 
	 * <p> Description: Get the current user's Admin role attribute.</p>
	 * 
	 * @return true if this user plays an Admin role, else false
	 *  
	 */
	public boolean getCurrentAdminRole() { return currentAdminRole;};

	
	/*******
	 * <p> Method: boolean getCurrentNewRole1() </p>
	 * 
	 * <p> Description: Get the current user's Student role attribute.</p>
	 * 
	 * @return true if this user plays a Student role, else false
	 *  
	 */
	public boolean getCurrentNewRole1() { return currentNewRole1;};

	
	/*******
	 * <p> Method: boolean getCurrentNewRole2() </p>
	 * 
	 * <p> Description: Get the current user's Reviewer role attribute.</p>
	 * 
	 * @return true if this user plays a Reviewer role, else false
	 *  
	 */
	public boolean getCurrentNewRole2() { return currentNewRole2;};
	
	/*******
	 * <p> Method: boolean getCurrentNewRole2() </p>
	 * 
	 * <p> Description: Get the current user's Reviewer role attribute.</p>
	 * 
	 * @return true if this user plays a Reviewer role, else false
	 *  
	 */
	public boolean getCurrentNewStudent() { return currentNewStudent;};

	/*******
	 * <p> Method: boolean getCurrentStaffRole() </p>
	 * 
	 * <p> Description: Get the current user's Staff role attribute.</p>
	 * 
	 * @return true if this user plays a Staff role, else false
	 *  
	 */
	public boolean getCurrentStaffRole() { 
		return currentStaffRole;
	}

	
	/*******
	 * <p> Debugging method</p>
	 * 
	 * <p> Description: Debugging method that dumps the database of the console.</p>
	 * 
	 * @throws SQLException if there is an issues accessing the database.
	 * 
	 */
	// Dumps the database.
	public void dump() throws SQLException {
		String query = "SELECT * FROM userDB";
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData meta = resultSet.getMetaData();
		while (resultSet.next()) {
		for (int i = 0; i < meta.getColumnCount(); i++) {
		System.out.println(
		meta.getColumnLabel(i + 1) + ": " +
				resultSet.getString(i + 1));
		}
		System.out.println();
		}
		resultSet.close();
	}


	/*******
	 * <p> Method: void closeConnection()</p>
	 * 
	 * <p> Description: Closes the database statement and connection.</p>
	 * 
	 */
	// Closes the database statement and connection.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}
	
	// ========== POST CRUD METHODS ==========
	// Added these methods to the Database.java 

	
	public void savePost(entityClasses.Post post) throws SQLException {
	    String sql = "INSERT INTO posts (postId, authorUsername, title, content, thread, " +
	                 "createdAt, updatedAt, isDeleted, replyCount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, post.getPostId());
	        pstmt.setString(2, post.getAuthorUsername());
	        pstmt.setString(3, post.getTitle());
	        pstmt.setString(4, post.getContent());
	        pstmt.setString(5, post.getThread());
	        pstmt.setTimestamp(6, Timestamp.valueOf(post.getCreatedAt()));
	        pstmt.setTimestamp(7, Timestamp.valueOf(post.getUpdatedAt()));
	        pstmt.setBoolean(8, post.isDeleted());
	        pstmt.setInt(9, post.getReplyCount());
	        pstmt.executeUpdate();
	    }
	}

	/**
	 * Get all posts from the database
	 */
	public List<entityClasses.Post> getAllPosts() throws SQLException {
	    List<entityClasses.Post> posts = new ArrayList<>();
	    String sql = "SELECT * FROM posts ORDER BY createdAt DESC";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {
	        
	        while (rs.next()) {
	            entityClasses.Post post = new entityClasses.Post();
	            post.__setPostId(rs.getString("postId"));
	            post.setAuthorUsername(rs.getString("authorUsername"));
	            post.setTitle(rs.getString("title"));
	            post.setContent(rs.getString("content"));
	            post.setThread(rs.getString("thread"));
	            post.__setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
	            post.__setUpdatedAt(rs.getTimestamp("updatedAt").toLocalDateTime());
	            post.__setDeleted(rs.getBoolean("isDeleted"));
	            post.__setReplyCount(rs.getInt("replyCount"));
	            posts.add(post);
	        }
	    }
	    return posts;
	}
	
    /**
     * Checks if a post with this ID already exists.
     */
    public boolean postExists(String postId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM posts WHERE postId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, postId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

    /**
     * Retrieve a single post by its ID.
     */
    public entityClasses.Post getPostById(String postId) throws SQLException {
        String sql = "SELECT * FROM posts WHERE postId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, postId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                entityClasses.Post post = new entityClasses.Post();
                post.__setPostId(rs.getString("postId"));
                post.setAuthorUsername(rs.getString("authorUsername"));
                post.setTitle(rs.getString("title"));
                post.setContent(rs.getString("content"));
                post.setThread(rs.getString("thread"));
                post.__setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                post.__setUpdatedAt(rs.getTimestamp("updatedAt").toLocalDateTime());
                post.__setDeleted(rs.getBoolean("isDeleted"));
                post.__setReplyCount(rs.getInt("replyCount"));
                return post;
            }
        }
        return null;
    }


	/**
	 * Mark a post as deleted (soft delete)
	 */
	public void markPostDeleted(String postId) throws SQLException {
	    String sql = "UPDATE posts SET isDeleted = TRUE, updatedAt = ? WHERE postId = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
	        pstmt.setString(2, postId);
	        pstmt.executeUpdate();
	    }
	}

	/**
	 * Update reply count for a post
	 */
	public void updateReplyCount(String postId, int newCount) throws SQLException {
	    String sql = "UPDATE posts SET replyCount = ?, updatedAt = ? WHERE postId = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setInt(1, newCount);
	        pstmt.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
	        pstmt.setString(3, postId);
	        pstmt.executeUpdate();
	    }
	}

	// ========== REPLY CRUD METHODS ==========

	/**
	 * Save a reply to the database
	 */
	public void saveReply(String replyId, String postId, String authorUsername, String content) 
	        throws SQLException {
	    String sql = "INSERT INTO replies (replyId, postId, authorUsername, content, " +
	                 "createdAt, updatedAt, isDeleted) VALUES (?, ?, ?, ?, ?, ?, ?)";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, replyId);
	        pstmt.setString(2, postId);
	        pstmt.setString(3, authorUsername);
	        pstmt.setString(4, content);
	        Timestamp now = Timestamp.valueOf(java.time.LocalDateTime.now());
	        pstmt.setTimestamp(5, now);
	        pstmt.setTimestamp(6, now);
	        pstmt.setBoolean(7, false);
	        pstmt.executeUpdate();
	    }
	}

	/**
	 * Get all replies for a specific post
	 */
	public List<String> getRepliesForPost(String postId) throws SQLException {
	    List<String> replies = new ArrayList<>();
	    String sql = "SELECT * FROM replies WHERE postId = ? AND isDeleted = FALSE ORDER BY createdAt ASC";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, postId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	            String replyText = "Reply by " + rs.getString("authorUsername") + 
	                             " at " + rs.getTimestamp("createdAt") + ":\n" +
	                             rs.getString("content");
	            replies.add(replyText);
	        }
	    }
	    return replies;
	}
	

	// ========== REPLY READ/UNREAD TRACKING ==========

	/**
	 * Get replies for a post with read/unread status for a specific user
	 */
	public List<ReplyWithStatus> getRepliesWithReadStatus(String postId, String userName) 
	        throws SQLException {
	    List<ReplyWithStatus> replies = new ArrayList<>();
	    String sql = "SELECT r.*, " +
	                 "(SELECT COUNT(*) FROM reply_reads rr WHERE rr.replyId = r.replyId " +
	                 "AND rr.userName = ?) as isRead " +
	                 "FROM replies r WHERE r.postId = ? AND r.isDeleted = FALSE " +
	                 "ORDER BY r.createdAt ASC";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, userName);
	        pstmt.setString(2, postId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	            String replyId = rs.getString("replyId");
	            String author = rs.getString("authorUsername");
	            String content = rs.getString("content");
	            Timestamp created = rs.getTimestamp("createdAt");
	            boolean isRead = rs.getInt("isRead") > 0;
	            
	            replies.add(new ReplyWithStatus(replyId, author, content, created, isRead));
	        }
	    }
	    return replies;
	}

	/**
	 * Mark a reply as read by a user
	 */
	public void markReplyAsRead(String replyId, String userName) throws SQLException {
	    String sql = "INSERT INTO reply_reads (replyId, userName, readAt) VALUES (?, ?, ?) " +
	                 "ON DUPLICATE KEY UPDATE readAt = ?";
	    
	    Timestamp now = Timestamp.valueOf(java.time.LocalDateTime.now());
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, replyId);
	        pstmt.setString(2, userName);
	        pstmt.setTimestamp(3, now);
	        pstmt.setTimestamp(4, now);
	        pstmt.executeUpdate();
	    }
	}

	/**
	 * Get count of unread replies for a post by a specific user
	 */
	public int getUnreadReplyCount(String postId, String userName) throws SQLException {
	    String sql = "SELECT COUNT(*) FROM replies r " +
	                 "WHERE r.postId = ? AND r.isDeleted = FALSE " +
	                 "AND NOT EXISTS (SELECT 1 FROM reply_reads rr " +
	                 "WHERE rr.replyId = r.replyId AND rr.userName = ?)";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, postId);
	        pstmt.setString(2, userName);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getInt(1);
	        }
	    }
	    return 0;
	}
	
	// ==================== POST UPDATES ====================

	public void updatePostTitle(String postId, String newTitle) throws SQLException {
	    String sql = "UPDATE posts SET title = ?, updatedAt = ? WHERE postId = ?";
	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setString(1, newTitle);
	        ps.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
	        ps.setString(3, postId);
	        ps.executeUpdate();
	    }
	}

	public void updatePostContent(String postId, String newContent) throws SQLException {
	    String sql = "UPDATE posts SET content = ?, updatedAt = ? WHERE postId = ?";
	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setString(1, newContent);
	        ps.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
	        ps.setString(3, postId);
	        ps.executeUpdate();
	    }
	}

	public void updatePostThread(String postId, String newThread) throws SQLException {
	    String sql = "UPDATE posts SET thread = ?, updatedAt = ? WHERE postId = ?";
	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setString(1, newThread);
	        ps.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
	        ps.setString(3, postId);
	        ps.executeUpdate();
	    }
	}

	// ==================== REPLY UPDATES ====================

	public void updateReplyContent(String replyId, String newContent) throws SQLException {
	    String sql = "UPDATE replies SET content = ?, updatedAt = ? WHERE replyId = ?";
	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setString(1, newContent);
	        ps.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
	        ps.setString(3, replyId);
	        ps.executeUpdate();
	    }
	}

	public void markReplyDeleted(String replyId) throws SQLException {
	    String sql = "UPDATE replies SET isDeleted = TRUE, updatedAt = ? WHERE replyId = ?";
	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
	        ps.setString(2, replyId);
	        ps.executeUpdate();
	    }
	}

	public void deleteReplyPermanent(String replyId) throws SQLException {
	    String sql = "DELETE FROM replies WHERE replyId = ?";
	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setString(1, replyId);
	        ps.executeUpdate();
	    }
	}


	/**
	 * Inner class to hold reply information with read status
	 */
	public static class ReplyWithStatus {
	    public final String replyId;
	    public final String author;
	    public final String content;
	    public final Timestamp createdAt;
	    public final boolean isRead;
	    
	    public ReplyWithStatus(String replyId, String author, String content, 
	                          Timestamp createdAt, boolean isRead) {
	        this.replyId = replyId;
	        this.author = author;
	        this.content = content;
	        this.createdAt = createdAt;
	        this.isRead = isRead;
	    }
	}
}