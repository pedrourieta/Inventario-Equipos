package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve", "SpellCheckingInspection"})
public class InventarioFrame extends JFrame {

    private DefaultTableModel modeloTabla;
    private JTable tabla;

    private int idSeleccionado = -1;

    public InventarioFrame() {
        configurarVentana();
        crearComponentes();
        cargarDatosDesdeBD();
    }

    private void configurarVentana() {
        setTitle("Inventario de Equipos de Cómputo");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void crearComponentes() {
        add(crearPanelTabla(), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);
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

        ListSelectionModel selectionModel = tabla.getSelectionModel();
        selectionModel.addListSelectionListener(e -> seleccionarFila());

        return new JScrollPane(tabla);
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel();

        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnActualizar = new JButton("Actualizar lista");

        btnAgregar.addActionListener(e -> abrirFormularioAgregar());
        btnEditar.addActionListener(e -> abrirFormularioEditar());
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
        btnActualizar.addActionListener(e -> cargarDatosDesdeBD());

        panel.add(btnAgregar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnActualizar);

        return panel;
    }

    private void abrirFormularioAgregar() {
        EquipoFormFrame formulario = new EquipoFormFrame(this::cargarDatosDesdeBD);
        formulario.setVisible(true);
    }

    private void abrirFormularioEditar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un equipo de la tabla para editarlo.");
            return;
        }

        int fila = tabla.getSelectedRow();

        Equipo equipo = new Equipo(
                idSeleccionado,
                modeloTabla.getValueAt(fila, 1).toString(),
                modeloTabla.getValueAt(fila, 2).toString(),
                modeloTabla.getValueAt(fila, 3).toString(),
                modeloTabla.getValueAt(fila, 4).toString(),
                modeloTabla.getValueAt(fila, 5).toString(),
                modeloTabla.getValueAt(fila, 6).toString(),
                modeloTabla.getValueAt(fila, 7).toString()
        );

        EquipoFormFrame formulario = new EquipoFormFrame(equipo, this::cargarDatosDesdeBD);
        formulario.setVisible(true);
    }

    private void eliminarSeleccionado() {
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
                idSeleccionado = -1;

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
            }
        }
    }

    private void seleccionarFila() {
        int fila = tabla.getSelectedRow();
        idSeleccionado = (fila == -1) ? -1 : (int) modeloTabla.getValueAt(fila, 0);
    }

    // Se deja con visibilidad de paquete para que EquipoFormFrame pueda
    // llamarla vía referencia de método (this::cargarDatosDesdeBD).
    void cargarDatosDesdeBD() {
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
}
