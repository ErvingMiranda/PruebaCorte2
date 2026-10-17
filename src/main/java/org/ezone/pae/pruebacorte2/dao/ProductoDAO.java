package org.ezone.pae.pruebacorte2.dao;

import org.ezone.pae.pruebacorte2.database.ConexionBD;
import org.ezone.pae.pruebacorte2.model.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    // Consulta los productos aplicando solo los filtros que el usuario haya indicado.
    public List<Producto> buscarProductos(
            String texto,
            String categoria,
            boolean soloConExistencias
    ) throws SQLException {

        List<Producto> productos = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
                SELECT
                    id_producto,
                    codigo,
                    nombre,
                    categoria,
                    precio,
                    stock
                FROM inventario.producto
                WHERE 1 = 1
                """);

        if (texto != null && !texto.isBlank()) {
            sql.append("""
                    AND (nombre ILIKE ? OR codigo ILIKE ?)
                    """);
        }

        if (categoria != null && !categoria.isBlank()) {
            sql.append("""
                    AND categoria = ?
                    """);
        }

        if (soloConExistencias) {
            sql.append("""
                    AND stock > 0
                    """);
        }

        sql.append("""
                ORDER BY codigo
                """);

        try (
                Connection conexion =
                        ConexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql.toString())
        ) {

            int parametro = 1;

            if (texto != null && !texto.isBlank()) {
                String busqueda = "%" + texto.trim() + "%";

                ps.setString(parametro++, busqueda);
                ps.setString(parametro++, busqueda);
            }

            if (categoria != null && !categoria.isBlank()) {
                ps.setString(parametro, categoria);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(
                            crearProductoDesdeResultSet(rs)
                    );
                }
            }

        } catch (SQLException e) {
            registrarError("buscar productos", e);
            throw e;
        }

        return productos;
    }


    // Comprueba si un código ya existe; al actualizar puede excluirse el ID del producto seleccionado.
    public boolean existeCodigo(
            String codigo,
            Integer idProducto
    ) throws SQLException {

        String sql;

        if (idProducto == null) {
            sql = """
                    SELECT 1
                    FROM inventario.producto
                    WHERE codigo = ?
                    """;
        } else {
            sql = """
                    SELECT 1
                    FROM inventario.producto
                    WHERE codigo = ?
                      AND id_producto <> ?
                    """;
        }

        try (
                Connection conexion =
                        ConexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(1, codigo);

            if (idProducto != null) ps.setInt(2, idProducto);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            registrarError("comprobar código", e);
            throw e;
        }
    }


    // Inserta un producto nuevo y devuelve cuántas filas fueron afectadas por el INSERT.
    public int guardarProducto(Producto producto) throws SQLException {

        String sql = """
                INSERT INTO inventario.producto
                (
                    codigo,
                    nombre,
                    categoria,
                    precio,
                    stock
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection conexion =
                        ConexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setString(3, producto.getCategoria());
            ps.setBigDecimal(4, producto.getPrecio());
            ps.setInt(5, producto.getStock());

            return ps.executeUpdate();

        } catch (SQLException e) {
            registrarError("guardar producto", e);
            throw e;
        }
    }


    // Actualiza por id_producto para asegurar que solo se modifique el registro seleccionado.
    public int actualizarProducto(Producto producto) throws SQLException {

        String sql = """
                UPDATE inventario.producto
                SET
                    codigo = ?,
                    nombre = ?,
                    categoria = ?,
                    precio = ?,
                    stock = ?
                WHERE id_producto = ?
                """;

        try (
                Connection conexion =
                        ConexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setString(3, producto.getCategoria());
            ps.setBigDecimal(4, producto.getPrecio());
            ps.setInt(5, producto.getStock());
            ps.setInt(6, producto.getIdProducto());

            return ps.executeUpdate();

        } catch (SQLException e) {
            registrarError("actualizar producto", e);
            throw e;
        }
    }


    // Elimina por id_producto y devuelve las filas afectadas para comprobar que el registro aún existía.
    public int eliminarProducto(int idProducto) throws SQLException {

        String sql = """
                DELETE FROM inventario.producto
                WHERE id_producto = ?
                """;

        try (
                Connection conexion =
                        ConexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, idProducto);

            return ps.executeUpdate();

        } catch (SQLException e) {
            registrarError("eliminar producto", e);
            throw e;
        }
    }


    // Convierte la fila actual del ResultSet en un objeto Producto.
    private Producto crearProductoDesdeResultSet(
            ResultSet rs
    ) throws SQLException {

        return new Producto(
                rs.getInt("id_producto"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getString("categoria"),
                rs.getBigDecimal("precio"),
                rs.getInt("stock")
        );
    }


    // Registra la operación que falló y su SQLState para facilitar el diagnóstico sin mostrarlo en la interfaz.
    private void registrarError(
            String operacion,
            SQLException e
    ) {

        System.err.println(
                "Error al " + operacion +
                        " | SQLState: " + e.getSQLState()
        );
    }
}
