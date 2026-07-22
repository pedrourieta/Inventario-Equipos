package org.example;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve", "SpellCheckingInspection"})
public class EquipoFormFrame extends JFrame {

    private JTextField txtNombre;
    private JComboBox<String> cmbTipo;
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JTextField txtNumeroSerie;
    private JComboBox<String> cmbEstado;
    private JTextField txtUbicacion;

    private final boolean modoEdicion;
    private final int idEquipo;
    private final Runnable alGuardar;

    // Constructor para agregar un equipo nuevo
    public EquipoFormFrame(Runnable alGuardar) {
        this.modoEdicion = false;
        this.idEquipo = -1;
        this.alGuardar = alGuardar;
        configurarVentana("Agregar equipo");
        crearComponentes();
    }

    // Constructor para editar un equipo existente
    public EquipoFormFrame(Equipo equipo, Runnable alGuardar) {
        this.modoEdicion = true;
        this.idEquipo = equipo.getId();
        this.alGuardar = alGuardar;
        configurarVentana("Editar equipo");
        crearComponentes();
        precargarDatos(equipo);
    }

    private void configurarVentana(String titulo) {
        setTitle(titulo);
        setSize(380, 380);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
    }

    private void crearComponentes() {
        add(crearPanelFormulario(), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

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
        panel.add(txtNombre);
        panel.add(new JLabel("Tipo:"));
        panel.add(cmbTipo);
        panel.add(new JLabel("Marca:"));
        panel.add(txtMarca);
        panel.add(new JLabel("Modelo:"));
        panel.add(txtModelo);
        panel.add(new JLabel("N° de serie:"));
        panel.add(txtNumeroSerie);
        panel.add(new JLabel("Estado:"));
        panel.add(cmbEstado);
        panel.add(new JLabel("Ubicación:"));
        panel.add(txtUbicacion);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel();

        JButton btnGuardar = new JButton(modoEdicion ? "Guardar cambios" : "Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());

        panel.add(btnGuardar);
        panel.add(btnCancelar);

        return panel;
    }

    private void precargarDatos(Equipo equipo) {
        txtNombre.setText(equipo.getNombre());
        cmbTipo.setSelectedItem(equipo.getTipo());
        txtMarca.setText(equipo.getMarca());
        txtModelo.setText(equipo.getModelo());
        txtNumeroSerie.setText(equipo.getNumeroSerie());
        cmbEstado.setSelectedItem(equipo.getEstado());
        txtUbicacion.setText(equipo.getUbicacion());
    }

    private void guardar() {
        if (!camposValidos()) return;

        if (modoEdicion) {
            actualizarEquipo();
        } else {
            insertarEquipo();
        }
    }

    private void insertarEquipo() {
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

            if (alGuardar != null) alGuardar.run();
            dispose();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar: " + e.getMessage());
        }
    }

    private void actualizarEquipo() {
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
            pstmt.setInt(8, idEquipo);

            pstmt.executeUpdate();

            if (alGuardar != null) alGuardar.run();
            dispose();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al editar: " + e.getMessage());
        }
    }

    private boolean camposValidos() {
        if (txtNombre.getText().trim().isEmpty() || txtMarca.getText().trim().isEmpty()
                || txtModelo.getText().trim().isEmpty() || txtNumeroSerie.getText().trim().isEmpty()
                || txtUbicacion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Llena todos los campos antes de continuar.");
            return false;
        }
        return true;
    }
}
