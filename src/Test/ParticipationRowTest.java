package Staff;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>Title: ParticipationRowTest Class</p>
 * 
 * <p>Description: JUnit test suite for ParticipationRow.
 * Tests include boundary value analysis and coverage testing.</p>
 * 
 * <p>Test Coverage:
 * - Constructor validation (null checks, negative values)
 * - Getter methods
 * - toString formatting
 * - equals and hashCode</p>
 * 
 * <p>Boundary Value Tests:
 * - distinctStudentsAnswered: 0 (boundary), 1 (just above), negative (invalid)
 * - studentUsername: null, empty, whitespace, valid</p>
 * 
 * <p>Copyright: Student Discussion System © 2025</p>
 * 
 * @version 1.00 — 2025-11-24
 */
public class ParticipationRowTest {
    
    // ==================== CONSTRUCTOR TESTS ====================
    
    @Test
    public void testValidConstructor() {
        ParticipationRow row = new ParticipationRow("alice", 5, true);
        assertEquals("alice", row.getStudentUsername());
        assertEquals(5, row.getDistinctStudentsAnswered());
        assertTrue(row.meetsRequirement());
    }
    
    @Test
    public void testConstructorWithZeroCount() {
        // Boundary: 0 distinct students (valid but indicates no participation)
        ParticipationRow row = new ParticipationRow("bob", 0, false);
        assertEquals(0, row.getDistinctStudentsAnswered());
        assertFalse(row.meetsRequirement());
    }
    
    @Test
    public void testConstructorNullUsername() {
        // Boundary: null username should throw exception
        assertThrows(IllegalArgumentException.class, () -> {
            new ParticipationRow(null, 3, true);
        });
    }
    
    @Test
    public void testConstructorEmptyUsername() {
        // Boundary: empty username should throw exception
        assertThrows(IllegalArgumentException.class, () -> {
            new ParticipationRow("", 3, true);
        });
    }
    
    @Test
    public void testConstructorWhitespaceUsername() {
        // Boundary: whitespace-only username should throw exception
        assertThrows(IllegalArgumentException.class, () -> {
            new ParticipationRow("   ", 3, true);
        });
    }
    
    @Test
    public void testConstructorNegativeCount() {
        // Boundary: negative count should throw exception
        assertThrows(IllegalArgumentException.class, () -> {
            new ParticipationRow("charlie", -1, false);
        });
    }
    
    // ==================== GETTER TESTS ====================
    
    @Test
    public void testGetters() {
        ParticipationRow row = new ParticipationRow("diana", 4, true);
        assertEquals("diana", row.getStudentUsername());
        assertEquals(4, row.getDistinctStudentsAnswered());
        assertTrue(row.meetsRequirement());
    }
    
    // ==================== TO_STRING TESTS ====================
    
    @Test
    public void testToStringMeetsRequirement() {
        ParticipationRow row = new ParticipationRow("eve", 3, true);
        String result = row.toString();
        
        assertTrue(result.contains("eve"));
        assertTrue(result.contains("3"));
        assertTrue(result.contains("MEETS"));
    }
    
    @Test
    public void testToStringNeedsMore() {
        ParticipationRow row = new ParticipationRow("frank", 1, false);
        String result = row.toString();
        
        assertTrue(result.contains("frank"));
        assertTrue(result.contains("1"));
        assertTrue(result.contains("NEEDS MORE"));
    }
    
    // ==================== EQUALS AND HASHCODE TESTS ====================
    
    @Test
    public void testEqualsSameObject() {
        ParticipationRow row = new ParticipationRow("grace", 2, false);
        assertEquals(row, row);
    }
    
    @Test
    public void testEqualsSameUsername() {
        ParticipationRow row1 = new ParticipationRow("henry", 3, true);
        ParticipationRow row2 = new ParticipationRow("henry", 5, false);
        
        // Should be equal because username matches (even if counts differ)
        assertEquals(row1, row2);
        assertEquals(row1.hashCode(), row2.hashCode());
    }
    
    @Test
    public void testNotEqualsDifferentUsername() {
        ParticipationRow row1 = new ParticipationRow("iris", 3, true);
        ParticipationRow row2 = new ParticipationRow("jack", 3, true);
        
        assertNotEquals(row1, row2);
    }
    
    @Test
    public void testNotEqualsNull() {
        ParticipationRow row = new ParticipationRow("kate", 2, false);
        assertNotEquals(null, row);
    }
    
    @Test
    public void testNotEqualsDifferentClass() {
        ParticipationRow row = new ParticipationRow("leo", 4, true);
        assertNotEquals(row, "leo");
    }
    
    // ==================== EDGE CASE TESTS ====================
    
    @Test
    public void testLargeDistinctCount() {
        // Test with unusually large count (valid but rare)
        ParticipationRow row = new ParticipationRow("maria", 100, true);
        assertEquals(100, row.getDistinctStudentsAnswered());
    }
    
    @Test
    public void testUsernameWithSpecialCharacters() {
        // Test username with special characters (valid in some systems)
        ParticipationRow row = new ParticipationRow("user_123", 2, false);
        assertEquals("user_123", row.getStudentUsername());
    }
    
    @Test
    public void testMeetsRequirementFalseWithHighCount() {
        // Edge case: high count but flagged as not meeting
        // (This could happen if requirement was raised after data collection)
        ParticipationRow row = new ParticipationRow("nina", 10, false);
        assertEquals(10, row.getDistinctStudentsAnswered());
        assertFalse(row.meetsRequirement());
    }
}
