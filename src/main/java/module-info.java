module ch.admin.bar.siardsuite {
  requires javafx.controls;
  requires javafx.fxml;


  opens ch.admin.bar.siardsuite.presenter to javafx.fxml;
  exports ch.admin.bar.siardsuite;
}
