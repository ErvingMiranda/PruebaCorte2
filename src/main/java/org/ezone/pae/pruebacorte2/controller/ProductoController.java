package org.ezone.pae.pruebacorte2.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.ezone.pae.pruebacorte2.dao.ProductoDAO;
import org.ezone.pae.pruebacorte2.model.Producto;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProductoController {

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtPrecio;

    @FXML
    private TextField txtStock;

    @FXML
    private ComboBox<String> cmbCategoria;

    @FXML
    private TextField txtFiltroTexto;

    @FXML
    private ComboBox<String> cmbFiltroCategoria;

    @FXML
    private CheckBox chkConExistencias;

    @FXML
    private TableView<Producto> tblProductos;

    @FXML
    private TableColumn<Producto, Integer> colId;

    @FXML
    private TableColumn<Producto, String> colCodigo;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, String> colCategoria;

    @FXML
    private TableColumn<Producto, BigDecimal> colPrecio;

    @FXML
    private TableColumn<Producto, Integer> colStock;

    @FXML
    private Label lblContador;


    private final ObservableList<Producto> listaProductos =
            FXCollections.observableArrayList();

    private final ProductoDAO productoDAO =
            new ProductoDAO();

    private Producto productoSeleccionado;


    @FXML
    private void initialize() {
        configurarTableView();
        configurarSeleccionTableView();
        cargarCategorias();
        cargarProductos(false);
    }


    private void configurarTableView() {
        colId.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getIdProducto()
                ).asObject()
        );

        colCodigo.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCodigo()
                )
        );

        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getNombre()
                )
        );

        colCategoria.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCategoria()
                )
        );

        colPrecio.setCellValueFactory(data ->
                new SimpleObjectProperty<>(
                        data.getValue().getPrecio()
                )
        );

        colStock.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getStock()
                ).asObject()
        );

        tblProductos.setItems(listaProductos);
    }


    private void configurarSeleccionTableView() {
        tblProductos.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        productoSeleccionado = newValue;
                        cargarProductoEnFormulario(newValue);
                    }
                });
    }


    private void cargarCategorias() {
        cmbCategoria.setItems(
                FXCollections.observableArrayList(
                        "Papelería",
                        "Tecnología",
                        "Accesorios"
                )
        );

        cmbFiltroCategoria.setItems(
                FXCollections.observableArrayList(
                        "Todas",
                        "Papelería",
                        "Tecnología",
                        "Accesorios"
                )
        );

        cmbFiltroCategoria.setValue("Todas");
    }


    private void cargarProductoEnFormulario(Producto producto) {
        txtCodigo.setText(producto.getCodigo());
        txtNombre.setText(producto.getNombre());
        cmbCategoria.setValue(producto.getCategoria());
        txtPrecio.setText(String.valueOf(producto.getPrecio()));
        txtStock.setText(String.valueOf(producto.getStock()));
    }


    private void cargarProductos(boolean avisarSinCoincidencias) {
        String categoria = cmbFiltroCategoria.getValue();

        if ("Todas".equals(categoria)) categoria = null;

        try {
            List<Producto> productos =
                    productoDAO.buscarProductos(
                            txtFiltroTexto.getText(),
                            categoria,
                            chkConExistencias.isSelected()
                    );

            listaProductos.setAll(productos);
            actualizarContador();

            if (avisarSinCoincidencias && productos.isEmpty()) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Sin coincidencias",
                        "No se encontraron productos con los filtros indicados."
                );
            }

        } catch (SQLException e) {
            mostrarErrorBaseDatos(
                    "No se pudieron cargar los productos."
            );
        }
    }


    @FXML
    private void buscarProductos() {
        cargarProductos(true);
    }


    @FXML
    private void limpiarFiltros() {
        txtFiltroTexto.clear();
        cmbFiltroCategoria.setValue("Todas");
        chkConExistencias.setSelected(false);

        cargarProductos(false);
    }


    @FXML
    private void recargarProductos() {
        cargarProductos(false);
    }


    @FXML
    private void nuevoProducto() {
        limpiarFormulario();
        txtCodigo.requestFocus();
    }


    @FXML
    private void guardarProducto() {
        normalizarFormulario();

        if (!validarFormulario()) return;

        String codigo = txtCodigo.getText();

        try {
            if (productoDAO.existeCodigo(codigo, null)) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Código duplicado",
                        "El código ingresado ya pertenece a otro producto."
                );

                txtCodigo.requestFocus();
                return;
            }

            Producto producto = crearProductoDesdeFormulario(0);

            int filasAfectadas =
                    productoDAO.guardarProducto(producto);

            if (filasAfectadas > 0) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Registro guardado",
                        "El producto fue registrado correctamente."
                );

                limpiarFormulario();
                cargarProductos(false);
            }

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Código duplicado",
                        "El código ingresado ya existe."
                );
            } else {
                mostrarErrorBaseDatos(
                        "No se pudo guardar el producto."
                );
            }
        }
    }


    @FXML
    private void actualizarProducto() {
        if (productoSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Producto no seleccionado",
                    "Seleccione un producto antes de actualizar."
            );

            return;
        }

        normalizarFormulario();

        if (!validarFormulario()) return;

        try {
            if (productoDAO.existeCodigo(
                    txtCodigo.getText(),
                    productoSeleccionado.getIdProducto()
            )) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Código duplicado",
                        "El código ingresado pertenece a otro producto."
                );

                txtCodigo.requestFocus();
                return;
            }

            Producto producto =
                    crearProductoDesdeFormulario(
                            productoSeleccionado.getIdProducto()
                    );

            int filasAfectadas =
                    productoDAO.actualizarProducto(producto);

            if (filasAfectadas > 0) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Registro actualizado",
                        "El producto fue actualizado correctamente."
                );

                limpiarFormulario();
                cargarProductos(false);

            } else {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Producto no encontrado",
                        "El producto ya no existe en la base de datos. Recargue el listado."
                );
            }

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Código duplicado",
                        "El código ingresado ya existe."
                );
            } else {
                mostrarErrorBaseDatos(
                        "No se pudo actualizar el producto."
                );
            }
        }
    }


    @FXML
    private void eliminarProducto() {
        if (productoSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Producto no seleccionado",
                    "Seleccione un producto antes de eliminar."
            );

            return;
        }

        Alert confirmacion =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText(
                "¿Desea eliminar el producto "
                        + productoSeleccionado.getNombre()
                        + "?"
        );

        Optional<ButtonType> respuesta =
                confirmacion.showAndWait();

        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) return;

        try {
            int filasAfectadas =
                    productoDAO.eliminarProducto(
                            productoSeleccionado.getIdProducto()
                    );

            if (filasAfectadas > 0) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Producto eliminado",
                        "El producto fue eliminado correctamente."
                );

                limpiarFormulario();
                cargarProductos(false);

            } else {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Producto no encontrado",
                        "El producto ya no existe en la base de datos. Recargue el listado."
                );
            }

        } catch (SQLException e) {
            mostrarErrorBaseDatos(
                    "No se pudo eliminar el producto."
            );
        }
    }


    private void normalizarFormulario() {
        txtCodigo.setText(
                txtCodigo.getText().trim().toUpperCase()
        );

        txtNombre.setText(
                txtNombre.getText().trim()
        );
    }


    private boolean validarFormulario() {
        String codigo = txtCodigo.getText();
        String nombre = txtNombre.getText();

        if (codigo.length() < 1 || codigo.length() > 10) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Código inválido",
                    "El código debe tener de 1 a 10 caracteres."
            );

            txtCodigo.requestFocus();
            return false;
        }

        if (nombre.length() < 3 || nombre.length() > 80) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Nombre inválido",
                    "El nombre debe tener de 3 a 80 caracteres."
            );

            txtNombre.requestFocus();
            return false;
        }

        if (cmbCategoria.getValue() == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Categoría inválida",
                    "Seleccione una categoría."
            );

            return false;
        }

        BigDecimal precio;

        try {
            precio =
                    new BigDecimal(
                            txtPrecio.getText().trim()
                    );

        } catch (NumberFormatException e) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Precio inválido",
                    "El precio debe contener un valor numérico."
            );

            txtPrecio.requestFocus();
            return false;
        }

        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Precio inválido",
                    "El precio debe ser mayor que 0."
            );

            txtPrecio.requestFocus();
            return false;
        }

        if (precio.scale() > 2) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Precio inválido",
                    "El precio puede tener como máximo 2 decimales."
            );

            txtPrecio.requestFocus();
            return false;
        }

        if (precio.compareTo(
                new BigDecimal("99999999.99")
        ) > 0) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Precio inválido",
                    "El precio máximo permitido es 99999999.99."
            );

            txtPrecio.requestFocus();
            return false;
        }

        try {
            int stock =
                    Integer.parseInt(
                            txtStock.getText().trim()
                    );

            if (stock < 0) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Stock inválido",
                        "El stock no puede ser menor que 0."
                );

                txtStock.requestFocus();
                return false;
            }

        } catch (NumberFormatException e) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Stock inválido",
                    "El stock debe ser un entero entre 0 y 2147483647."
            );

            txtStock.requestFocus();
            return false;
        }

        return true;
    }


    private Producto crearProductoDesdeFormulario(
            int idProducto
    ) {

        return new Producto(
                idProducto,
                txtCodigo.getText(),
                txtNombre.getText(),
                cmbCategoria.getValue(),
                new BigDecimal(
                        txtPrecio.getText().trim()
                ),
                Integer.parseInt(
                        txtStock.getText().trim()
                )
        );
    }


    private void limpiarFormulario() {
        productoSeleccionado = null;

        tblProductos.getSelectionModel().clearSelection();

        txtCodigo.clear();
        txtNombre.clear();
        cmbCategoria.setValue(null);
        txtPrecio.clear();
        txtStock.clear();
    }


    private void actualizarContador() {
        lblContador.setText(
                String.valueOf(listaProductos.size())
        );
    }


    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje
    ) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }


    private void mostrarErrorBaseDatos(
            String mensaje
    ) {

        mostrarAlerta(
                Alert.AlertType.ERROR,
                "Error de base de datos",
                mensaje
        );
    }
}