module FoundationsF25 {
	requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;      // if you use FXML
    requires java.sql;
    requires com.h2database;   // H2 jar added earlier
    requires org.junit.jupiter.api;
    requires java.base;
    requires java.logging;

    opens applicationMain to javafx.graphics, javafx.fxml;
    opens guiManageInvitations to javafx.base, javafx.graphics;
    opens guiAdminHome to javafx.fxml, javafx.graphics;
    opens guiUserLogin to javafx.fxml, javafx.graphics;
    opens guiStudentHome to javafx.base, javafx.graphics;  // ADD THIS LINE

    // If other modules need to use these APIs at compile-time, also export them:
    exports applicationMain;
    exports guiAdminHome;
    // (exports are optional for your use case; opens is the key for reflection)
}