module org.ezone.pae.pruebacorte2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.ezone.pae.pruebacorte2 to javafx.fxml;
    exports org.ezone.pae.pruebacorte2;
}