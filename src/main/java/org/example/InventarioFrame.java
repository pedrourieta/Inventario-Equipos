package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve", "SpellCheckingInspection"})
public class InventarioFrame extends JFrame {

    private DefaultTableModel modeloTabla;
    private JTable tabla;

    private JTextField txtNombre;
    private JComboBox<String> cmbTipo;
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JTextField txtNumeroSerie;
    private JComboBox<String> cmbEstado;
    private JTextField txtUbicacion;

    private int idSeleccionado = -1;

    public InventarioFrame() {
        configurarVentana();
        crearComponentes();
        cargarDatosDesdeBD();
    }

    private void configurarVentana() {
        setTitle("Inventario de Equipos de Cómputo");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void crearComponentes() {
        add(crearPanelFormulario(), BorderLayout.NORTH);
        add(crearPanelTabla(), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridLayout(2, 8, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Datos del equipo"));

        String[] tipos = {"Laptop", "Escritorio", "Monitor", "Impresora", "Servidor", "Otro"};
        String[] estados = {"Disponible", "En uso", "Mantenimiento", "Baja"};

        txtNombre = new JTextField();
        cmbTipo = new JComboBox<>(tipos);
        txtMarca = new JTextField();
        txtModelo = new JTextField();
        txtNumeroSerie = new JTextField();
        cmbEstado = new JComboBox<>(estados);
        txtUbicacion = new JTextField();

        panel.add(new JLabel("Nombre:"));
        panel.add(new JLabel("Tipo:"));
        panel.add(new JLabel("Marca:"));
        panel.add(new JLabel("Modelo:"));
        panel.add(new JLabel("N° de serie:"));
        panel.add(new JLabel("Estado:"));
        panel.add(new JLabel("Ubicación:"));
        panel.add(new JLabel(""));

        panel.add(txtNombre);
        panel.add(cmbTipo);
        panel.add(txtMarca);
        panel.add(txtModelo);
        panel.add(txtNumeroSerie);
        panel.add(cmbEstado);
        panel.add(txtUbicacion);
        panel.add(new JLabel(""));

        return panel;
    }

    private JScrollPane crearPanelTabla() {
        String[] columnas = {"ID", "Nombre", "Tipo", "Marca", "Modelo", "N° de serie", "Estado", "Ubicación"};

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(24);

        // Se mantiene el parámetro e por compatibilidad con versiones anteriores de Java
        @SuppressWarnings("unused")
        ListSelectionModel selectionModel = tabla.getSelectionModel();
        selectionModel.addListSelectionListener(e -> seleccionarFila());

        return new JScrollPane(tabla);
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel();

        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar = new JButton("Guardar cambios");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        btnAgregar.addActionListener(e -> agregarEquipo());
        btnEditar.addActionListener(e -> editarEquipo());
        btnEliminar.addActionListener(e -> eliminarEquipo());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        panel.add(btnAgregar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnLimpiar);

        return panel;
    }

    private void agregarEquipo() {
        if (camposValidos()) return;

        String sql = "INSERT INTO equipos (nombre, tipo, marca, modelo, numero_serie, estado, ubicacion) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, txtNombre.getText().trim());
            pstmt.setString(2, (String) cmbTipo.getSelectedItem());
            pstmt.setString(3, txtMarca.getText().trim());
            pstmt.setString(4, txtModelo.getText().trim());
            pstmt.setString(5, txtNumeroSerie.getText().trim());
            pstmt.setString(6, (String) cmbEstado.getSelectedItem());
            pstmt.setString(7, txtUbicacion.getText().trim());

            pstmt.executeUpdate();
            cargarDatosDesdeBD();
            limpiarCampos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar: " + e.getMessage());
        }
    }

    private void editarEquipo() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un equipo de la tabla para editarlo.");
            return;
        }
        if (camposValidos()) return;

        String sql = "UPDATE equipos SET nombre=?, tipo=?, marca=?, modelo=?, numero_serie=?, estado=?, ubicacion=? WHERE id=?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, txtNombre.getText().trim());
            pstmt.setString(2, (String) cmbTipo.getSelectedItem());
            pstmt.setString(3, txtMarca.getText().trim());
            pstmt.setString(4, txtModelo.getText().trim());
            pstmt.setString(5, txtNumeroSerie.getText().trim());
            pstmt.setString(6, (String) cmbEstado.getSelectedItem());
            pstmt.setString(7, txtUbicacion.getText().trim());
            pstmt.setInt(8, idSeleccionado);

            pstmt.executeUpdate();
            cargarDatosDesdeBD();
            limpiarCampos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al editar: " + e.getMessage());
        }
    }

    private void eliminarEquipo() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un equipo de la tabla para eliminarlo.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Seguro que quieres eliminar este equipo?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM equipos WHERE id=?";
            try (Connection conn = ConexionBD.conectar();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, idSeleccionado);
                pstmt.executeUpdate();
                cargarDatosDesdeBD();
                limpiarCampos();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
            }
        }
    }

    private void seleccionarFila() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return;

        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        cmbTipo.setSelectedItem(modeloTabla.getValueAt(fila, 2).toString());
        txtMarca.setText(modeloTabla.getValueAt(fila, 3).toString());
        txtModelo.setText(modeloTabla.getValueAt(fila, 4).toString());
        txtNumeroSerie.setText(modeloTabla.getValueAt(fila, 5).toString());
        cmbEstado.setSelectedItem(modeloTabla.getValueAt(fila, 6).toString());
        txtUbicacion.setText(modeloTabla.getValueAt(fila, 7).toString());
    }

    private void cargarDatosDesdeBD() {
        modeloTabla.setRowCount(0);
        String sql = "SELECT * FROM equipos";

        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] fila = {
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("tipo"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getString("numero_serie"),
                        rs.getString("estado"),
                        rs.getString("ubicacion")
                };
                modeloTabla.addRow(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
        }
    }

    private boolean camposValidos() {
        if (txtNombre.getText().trim().isEmpty() || txtMarca.getText().trim().isEmpty()
                || txtModelo.getText().trim().isEmpty() || txtNumeroSerie.getText().trim().isEmpty()
                || txtUbicacion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Llena todos los campos antes de continuar.");
            return true;
        }
        return false;
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        cmbTipo.setSelectedIndex(0);
        txtMarca.setText("");
        txtModelo.setText("");
        txtNumeroSerie.setText("");
        cmbEstado.setSelectedIndex(0);
        txtUbicacion.setText("");
        idSeleccionado = -1;
        tabla.clearSelection();
    }
}