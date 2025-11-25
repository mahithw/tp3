package entityClasses;

/*******
 * <p> Title: User Class </p>
 * 
 * <p> Description: This User class represents a user entity in the system.  It contains the user's
 *  details such as userName, password, and roles being played. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 2.00 2025-11-24 Added Staff role for TP3
 * 
 */ 

public class User {
	
	/*
	 * These are the private attributes for this entity object
	 */
    private String userName;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private String preferredFirstName;
    private String emailAddress;
    private boolean adminRole;
    private boolean role1;
    private boolean role2;
    private boolean Student;
    private boolean staffRole;  // ⭐ NEW: Added for TP3 grading functionality
    
    
    /*****
     * <p> Method: User() </p>
     * 
     * <p> Description: This default constructor is not used in this system. </p>
     */
    public User() {
    	
    }

    
    /*****
     * <p> Method: User(String userName, String password, boolean r1, boolean r2,
     * 		boolean r3, boolean r4, boolean r5) </p>
     * 
     * <p> Description: This constructor is used to establish user entity objects. </p>
     * 
     * @param userName specifies the account userName for this user
     * 
     * @param password specifies the account password for this user
     * 
     * @param r1 specifies the the Admin attribute (TRUE or FALSE) for this user
     * 
     * @param r2 specifies the the Role1 attribute (TRUE or FALSE) for this user
     * 
     * @param r3 specifies the the Role2 attribute (TRUE or FALSE) for this user
     * 
     * @param r4 specifies the the Student attribute (TRUE or FALSE) for this user
     * 
     * @param r5 specifies the the Staff attribute (TRUE or FALSE) for this user
     * 
     */
    // Constructor to initialize a new User object with userName, password, and role.
    public User(String userName, String password, String fn, String mn, String ln, String pfn, 
    		String ea, boolean r1, boolean r2, boolean r3, boolean r4, boolean r5) {
        this.userName = userName;
        this.password = password;
        this.firstName = fn;
        this.middleName = mn;
        this.lastName = ln;
        this.preferredFirstName = pfn;
        this.emailAddress = ea;
        this.adminRole = r1;
        this.role1 = r2;
        this.role2 = r3;
        this.Student = r4;
        this.staffRole = r5;  // ⭐ NEW: Initialize staff role
    }
    
    // ⭐ NEW: Overloaded constructor for backward compatibility (4 roles)
    public User(String userName, String password, String fn, String mn, String ln, String pfn, 
    		String ea, boolean r1, boolean r2, boolean r3, boolean r4) {
        this(userName, password, fn, mn, ln, pfn, ea, r1, r2, r3, r4, false);
    }

    
    /*****
     * <p> Method: void setAdminRole(boolean role) </p>
     * 
     * <p> Description: This setter defines the Admin role attribute. </p>
     * 
     * @param role is a boolean that specifies if this user in playing the Admin role.
     * 
     */
    // Sets the role of the Admin user.
    public void setAdminRole(boolean role) {
    	this.adminRole=role;
    }

    
    /*****
     * <p> Method: void setRole1User(boolean role) </p>
     * 
     * <p> Description: This setter defines the role1 attribute. </p>
     * 
     * @param role is a boolean that specifies if this user in playing role1.
     * 
     */
    // Sets the role1 user.
    public void setRole1User(boolean role) {
    	this.role1=role;
    }

    
    /*****
     * <p> Method: void setRole2User(boolean role) </p>
     * 
     * <p> Description: This setter defines the role2 attribute. </p>
     * 
     * @param role is a boolean that specifies if this user in playing role2.
     * 
     */
    // Sets the role2 user.
    public void setRole2User(boolean role) {
    	this.role2=role;
    }
    
    /*****
     * <p> Method: void setStudentUser(boolean role) </p>
     * 
     * <p> Description: This setter defines the Student attribute. </p>
     * 
     * @param role is a boolean that specifies if this user in playing Student role.
     * 
     */
    // Sets the Student user.
    public void setStudentUser(boolean role) {
    	this.Student=role;
    }
    
    /*****
     * <p> Method: void setStaffUser(boolean role) </p>
     * 
     * <p> Description: This setter defines the Staff attribute. </p>
     * 
     * @param role is a boolean that specifies if this user in playing Staff role.
     * 
     */
    // ⭐ NEW: Sets the Staff user.
    public void setStaffUser(boolean role) {
    	this.staffRole=role;
    }

    
    /*****
     * <p> Method: String getUserName() </p>
     * 
     * <p> Description: This getter returns the UserName. </p>
     * 
     * @return a String of the UserName
     * 
     */
    // Gets the current value of the UserName.
    public String getUserName() { return userName; }

    
    /*****
     * <p> Method: String getPassword() </p>
     * 
     * <p> Description: This getter returns the Password. </p>
     * 
     * @return a String of the password
	 *
     */
    // Gets the current value of the Password.
    public String getPassword() { return password; }

    
    /*****
     * <p> Method: String getFirstName() </p>
     * 
     * <p> Description: This getter returns the FirstName. </p>
     * 
     * @return a String of the FirstName
	 *
     */
    // Gets the current value of the FirstName.
    public String getFirstName() { return firstName; }

    
    /*****
     * <p> Method: String getMiddleName() </p>
     * 
     * <p> Description: This getter returns the MiddleName. </p>
     * 
     * @return a String of the MiddleName
	 *
     */
    // Gets the current value of the MiddleName.
    public String getMiddleName() { return middleName; }

    
    /*****
     * <p> Method: String getLasteName() </p>
     * 
     * <p> Description: This getter returns the LastName. </p>
     * 
     * @return a String of the LastName
	 *
     */
    // Gets the current value of the LastName.
    public String getLastName() { return lastName; }

    
    /*****
     * <p> Method: String getPreferredFirstName() </p>
     * 
     * <p> Description: This getter returns the PreferredFirstName. </p>
     * 
     * @return a String of the PreferredFirstName
	 *
     */
    // Gets the current value of the PreferredFirstName.
    public String getPreferredFirstName() { return preferredFirstName; }

    
    /*****
     * <p> Method: String getEmailAddress() </p>
     * 
     * <p> Description: This getter returns the EmailAddress. </p>
     * 
     * @return a String of the EmailAddress
	 *
     */
    // Gets the current value of the EmailAddress.
    public String getEmailAddress() { return emailAddress; }

    public void setUserName(String s) { userName = s; }
    public void setPassword(String s) { password = s; }
    public void setFirstName(String s) { firstName = s; }
    public void setMiddleName(String s) { middleName = s; }
    public void setLastName(String s) { lastName = s; }
    public void setPreferredFirstName(String s) { preferredFirstName = s; }
    public void setEmailAddress(String s) { emailAddress = s; }

    
    /*****
     * <p> Method: String getAdminRole() </p>
     * 
     * <p> Description: This getter returns the value of the Admin role attribute. </p>
     * 
     * @return a String of "TRUE" or "FALSE" based on state of the attribute
	 *
     */
    // Gets the current value of the Admin role attribute.
    public boolean getAdminRole() { return adminRole; }

    
    /*****
     * <p> Method: String getRole1() </p>
     * 
     * <p> Description: This getter returns the value of the role1 attribute. </p>
     * 
     * @return a String of "TRUE" or "FALSE" based on state of the attribute
	 *
     */
    // Gets the current value of the role1 attribute.
	public boolean getNewRole1() { return role1; }

    
    /*****
     * <p> Method: String getRole2() </p>
     * 
     * <p> Description: This getter returns the value of the role2 attribute. </p>
     * 
     * @return a String of "TRUE" or "FALSE" based on state of the attribute
	 *
     */
    // Gets the current value of the role2 attribute.
    public boolean getNewRole2() { return role2; }
    
    /*****
     * <p> Method: String getStudent() </p>
     * 
     * <p> Description: This getter returns the value of the Student attribute. </p>
     * 
     * @return a String of "TRUE" or "FALSE" based on state of the attribute
	 *
     */
    // Gets the current value of the Student attribute.
    public boolean getNewStudent() { return Student; }
    
    /*****
     * <p> Method: boolean getStaffRole() </p>
     * 
     * <p> Description: This getter returns the value of the Staff role attribute. </p>
     * 
     * @return true if this user plays a Staff role, else false
	 *
     */
    // ⭐ NEW: Gets the current value of the Staff role attribute.
    public boolean getStaffRole() { return staffRole; }

        
    /*****
     * <p> Method: int getNumRoles() </p>
     * 
     * <p> Description: This getter returns the number of roles this user plays (0 - 5). </p>
     * 
     * @return a value 0 - 5 of the number of roles this user plays
	 *
     */
    // Gets the number of roles this user plays.
    public int getNumRoles() {
    	int numRoles = 0;
    	if (adminRole) numRoles++;
    	if (role1) numRoles++;
    	if (role2) numRoles++;
    	if (Student) numRoles++;
    	if (staffRole) numRoles++;  // ⭐ NEW: Count staff role
    	return numRoles;
    }
}
