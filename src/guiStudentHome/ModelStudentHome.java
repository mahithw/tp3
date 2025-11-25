package guiStudentHome;

import entityClasses.ReplyCollection;

/*******
 * <p> Title: ModelStudentHome Class. </p>
 *
 * <p> Description: The StudentHome Page Model for HW2. 
 * This class holds the in-memory ReplyCollection for managing replies 
 * from the Student UI (non-persistent). </p>
 *
 * <p> Copyright: Student Discussion System © 2025 </p>
 *
 * @author Your Name
 * @version 1.01 2025-10-16 Add ReplyCollection support for reply UI
 */
public class ModelStudentHome {

    // In-memory storage for replies (not persisted in database)
    protected static ReplyCollection allReplies = new ReplyCollection();
}
