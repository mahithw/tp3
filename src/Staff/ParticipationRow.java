package Staff;

/**
 * <p>Title: ParticipationRow Class</p>
 * 
 * <p>Description: Represents a single student's participation summary
 * showing how many distinct classmates they have answered.</p>
 * 
 * <p>This class is used by the instructional team to quickly assess
 * whether students meet the participation requirement of answering
 * questions from at least 3 different classmates.</p>
 * 
 * <p>Copyright: Student Discussion System © 2025</p>
 * 
 * @version 1.00 — 2025-11-24 Initial implementation for TP3
 * 
 * <p>Tested by: ParticipationRowTest - JUnit test suite</p>
 */
public class ParticipationRow {
    
    private String studentUsername;
    private int distinctStudentsAnswered;
    private boolean meetsRequirement;
    
    /**
     * Creates a participation summary row for a student.
     * 
     * @param studentUsername The username of the student being assessed
     * @param distinctStudentsAnswered Number of different classmates this student answered
     * @param meetsRequirement Whether this student meets the participation requirement (typically 3+)
     * 
     * @throws IllegalArgumentException if studentUsername is null or empty
     * @throws IllegalArgumentException if distinctStudentsAnswered is negative
     */
    public ParticipationRow(String studentUsername, int distinctStudentsAnswered, boolean meetsRequirement) {
        if (studentUsername == null || studentUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Student username cannot be null or empty");
        }
        if (distinctStudentsAnswered < 0) {
            throw new IllegalArgumentException("Distinct students answered cannot be negative");
        }
        
        this.studentUsername = studentUsername;
        this.distinctStudentsAnswered = distinctStudentsAnswered;
        this.meetsRequirement = meetsRequirement;
    }
    
    // ==================== GETTERS ====================
    
    /**
     * Gets the username of the student.
     * 
     * @return The student's username
     */
    public String getStudentUsername() {
        return studentUsername;
    }
    
    /**
     * Gets the count of distinct classmates this student has answered.
     * 
     * @return Number of different students this student replied to
     */
    public int getDistinctStudentsAnswered() {
        return distinctStudentsAnswered;
    }
    
    /**
     * Checks if student meets the participation requirement.
     * 
     * @return true if student answered questions from enough different classmates, false otherwise
     */
    public boolean meetsRequirement() {
        return meetsRequirement;
    }
    
    // ==================== UTILITY ====================
    
    /**
     * Returns a formatted string representation of this participation row.
     * Used for displaying summary data to instructors.
     * 
     * @return Formatted string with student info and participation status
     */
    @Override
    public String toString() {
        String status = meetsRequirement ? "MEETS" : "NEEDS MORE";
        return String.format("%-20s | Answered: %2d distinct students | Status: %s",
                             studentUsername, distinctStudentsAnswered, status);
    }
    
    /**
     * Checks equality based on student username only.
     * Used for testing and collections management.
     * 
     * @param obj Object to compare with
     * @return true if both objects represent the same student
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ParticipationRow other = (ParticipationRow) obj;
        return studentUsername.equals(other.studentUsername);
    }
    
    /**
     * Generates hash code based on student username.
     * 
     * @return Hash code for this object
     */
    @Override
    public int hashCode() {
        return studentUsername.hashCode();
    }
}
