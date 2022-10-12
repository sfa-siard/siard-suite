module ch.admin.bar.siardsuite {
  requires javafx.controls;
  requires javafx.fxml;
  requires MaterialFX;
  requires java.prefs;
  requires java.sql;
  requires siardapi;
  requires siardcmd;
  requires enterutils;

  opens ch.admin.bar.siardsuite.presenter to javafx.fxml;
  opens ch.admin.bar.siardsuite.presenter.archive to javafx.fxml;
  opens ch.admin.bar.siardsuite.presenter.tree to javafx.fxml;
  opens ch.admin.bar.siardsuite.component to javafx.fxml;
  opens ch.admin.bar.siardsuite.presenter.open to javafx.fxml;
  opens ch.admin.bar.siardsuite.db;
  exports ch.admin.bar.siardsuite;
}
