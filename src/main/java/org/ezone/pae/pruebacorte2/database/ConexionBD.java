package org.ezone.pae.pruebacorte2.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {
    // Archivo externo con la URL, el usuario y la contraseña para no dejar las credenciales en el código.
    private static final String ARCHIVO_CONFIGURACION = "database.properties";

    private ConexionBD() {}

    // Carga la configuración local y devuelve una nueva conexión a PostgreSQL.
    public static Connection getConnection() throws SQLException {
        Properties propiedades = new Properties();

        // Abre database.properties para leer la configuración local.
        try (FileInputStream archivo = new FileInputStream(ARCHIVO_CONFIGURACION)) {
            propiedades.load(archivo);
        } catch (IOException e) {
            throw new SQLException("No se pudo cargar la configuración de la base de datos.", e);
        }

        // Carga desde propiedades la URL, el usuario y la contraseña de la conexión.
        String url = propiedades.getProperty("db.url");
        String usuario = propiedades.getProperty("db.user");
        String contrasena = propiedades.getProperty("db.password");

        // Si falta una propiedad se detiene antes de intentar abrir la conexión.
        if (url == null || usuario == null || contrasena == null) {
            throw new SQLException("La configuración de la base de datos está incompleta.");
        }

        return DriverManager.getConnection(url, usuario, contrasena);
    }
}
