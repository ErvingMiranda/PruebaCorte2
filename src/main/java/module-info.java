module org.ezone.pae.pruebacorte2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;

    exports org.ezone.pae.pruebacorte2;

    opens org.ezone.pae.pruebacorte2.controller to javafx.fxml;
    opens org.ezone.pae.pruebacorte2.model to javafx.base;
}