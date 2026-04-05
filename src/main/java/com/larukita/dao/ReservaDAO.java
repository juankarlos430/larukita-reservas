package com.larukita.dao;

import com.larukita.conexion.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReservaDAO {

    private String mensaje = "";

    public boolean crearReserva(long idCliente, long idUsuario, long idHabitacion, double precioNoche, double impuestosPct) {

        String sqlExisteCliente = "SELECT COUNT(*) FROM cliente WHERE id_cliente = ?";
        String sqlExisteUsuario = "SELECT COUNT(*) FROM usuario WHERE id_usuario = ?";
        String sqlExisteHabitacion = "SELECT COUNT(*) FROM habitacion WHERE id_hab = ?";

        String sqlValidarDisponibilidad = """
                SELECT COUNT(*)
                FROM reserva r
                INNER JOIN reserva_habitacion rh ON r.id_reserva = rh.id_reserva
                WHERE rh.id_hab = ?
                AND (
                    ('2026-04-10' BETWEEN r.check_in_previsto AND r.check_out_previsto)
                    OR
                    ('2026-04-12' BETWEEN r.check_in_previsto AND r.check_out_previsto)
                    OR
                    (r.check_in_previsto BETWEEN '2026-04-10' AND '2026-04-12')
                )
                """;

        String sqlReserva = """
                INSERT INTO reserva (id_cliente, id_usuario_creo, check_in_previsto, check_out_previsto, estado, origen)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        String sqlDetalle = """
                INSERT INTO reserva_habitacion (id_reserva, id_hab, precio_noche_aplicada, impuestos_pct, descuento_pct)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionBD.conectar()) {

            if (conexion == null) {
                mensaje = "No fue posible conectar con la base de datos.";
                return false;
            }

            conexion.setAutoCommit(false);

            if (!existeRegistro(conexion, sqlExisteCliente, idCliente)) {
                mensaje = "El cliente no existe.";
                conexion.rollback();
                return false;
            }

            if (!existeRegistro(conexion, sqlExisteUsuario, idUsuario)) {
                mensaje = "El usuario no existe.";
                conexion.rollback();
                return false;
            }

            if (!existeRegistro(conexion, sqlExisteHabitacion, idHabitacion)) {
                mensaje = "La habitacion no existe.";
                conexion.rollback();
                return false;
            }

            try (PreparedStatement psValidar = conexion.prepareStatement(sqlValidarDisponibilidad)) {
                psValidar.setLong(1, idHabitacion);

                try (ResultSet rsVal = psValidar.executeQuery()) {
                    if (rsVal.next() && rsVal.getInt(1) > 0) {
                        mensaje = "La habitacion ya esta ocupada en esas fechas.";
                        conexion.rollback();
                        return false;
                    }
                }
            }

            long idReservaGenerado;

            try (PreparedStatement psReserva = conexion.prepareStatement(sqlReserva, Statement.RETURN_GENERATED_KEYS)) {

                psReserva.setLong(1, idCliente);
                psReserva.setLong(2, idUsuario);
                psReserva.setDate(3, java.sql.Date.valueOf("2026-04-10"));
                psReserva.setDate(4, java.sql.Date.valueOf("2026-04-12"));
                psReserva.setString(5, "PENDIENTE");
                psReserva.setString(6, "WEB");

                int filas = psReserva.executeUpdate();

                if (filas == 0) {
                    mensaje = "No se pudo crear la reserva.";
                    conexion.rollback();
                    return false;
                }

                try (ResultSet rs = psReserva.getGeneratedKeys()) {
                    if (rs.next()) {
                        idReservaGenerado = rs.getLong(1);
                    } else {
                        mensaje = "No se pudo obtener el ID de la reserva.";
                        conexion.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement psDetalle = conexion.prepareStatement(sqlDetalle)) {
                psDetalle.setLong(1, idReservaGenerado);
                psDetalle.setLong(2, idHabitacion);
                psDetalle.setDouble(3, precioNoche);
                psDetalle.setDouble(4, impuestosPct);
                psDetalle.setDouble(5, 0);

                int filasDetalle = psDetalle.executeUpdate();

                if (filasDetalle == 0) {
                    mensaje = "No se pudo crear el detalle de la reserva.";
                    conexion.rollback();
                    return false;
                }
            }

            conexion.commit();
            mensaje = "Reserva creada correctamente.";
            return true;

        } catch (Exception e) {
            mensaje = "Error: " + e.getMessage();
            return false;
        }
    }

    public String consultarReservasPorCliente(long idCliente) {

        String sql = """
                SELECT r.id_reserva,
                       h.numero,
                       r.check_in_previsto,
                       r.check_out_previsto,
                       r.estado,
                       rh.precio_noche_aplicada,
                       rh.impuestos_pct,
                       rh.descuento_pct,
                       r.fecha_reserva
                FROM reserva r
                INNER JOIN reserva_habitacion rh ON r.id_reserva = rh.id_reserva
                INNER JOIN habitacion h ON rh.id_hab = h.id_hab
                WHERE r.id_cliente = ?
                ORDER BY r.id_reserva
                """;

        StringBuilder resultado = new StringBuilder();

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setLong(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {

                resultado.append("===== RESERVAS DEL CLIENTE =====\n\n");

                boolean hayDatos = false;

                while (rs.next()) {
                    hayDatos = true;

                    resultado.append("ID Reserva: ").append(rs.getLong("id_reserva")).append("\n");
                    resultado.append("Habitacion: ").append(rs.getString("numero")).append("\n");
                    resultado.append("Check-in: ").append(rs.getDate("check_in_previsto")).append("\n");
                    resultado.append("Check-out: ").append(rs.getDate("check_out_previsto")).append("\n");
                    resultado.append("Estado: ").append(rs.getString("estado")).append("\n");
                    resultado.append("Precio noche: ").append(rs.getDouble("precio_noche_aplicada")).append("\n");
                    resultado.append("Impuestos %: ").append(rs.getDouble("impuestos_pct")).append("\n");
                    resultado.append("Descuento %: ").append(rs.getDouble("descuento_pct")).append("\n");
                    resultado.append("Fecha reserva: ").append(rs.getTimestamp("fecha_reserva")).append("\n");
                    resultado.append("-----------------------------------\n");
                }

                if (!hayDatos) {
                    resultado.append("Este cliente no tiene reservas.");
                }
            }

        } catch (Exception e) {
            return "Error al consultar: " + e.getMessage();
        }

        return resultado.toString();
    }

    public boolean actualizarEstadoReserva(long idReserva, String nuevoEstado) {

        if (!nuevoEstado.equals("PENDIENTE") &&
                !nuevoEstado.equals("CONFIRMADA") &&
                !nuevoEstado.equals("CANCELADA")) {
            mensaje = "Estado no valido. Use: PENDIENTE, CONFIRMADA o CANCELADA.";
            return false;
        }

        String sqlExisteReserva = "SELECT COUNT(*) FROM reserva WHERE id_reserva = ?";
        String sqlActualizar = "UPDATE reserva SET estado = ? WHERE id_reserva = ?";

        try (Connection conexion = ConexionBD.conectar()) {

            if (conexion == null) {
                mensaje = "No fue posible conectar con la base de datos.";
                return false;
            }

            if (!existeRegistro(conexion, sqlExisteReserva, idReserva)) {
                mensaje = "La reserva no existe.";
                return false;
            }

            try (PreparedStatement ps = conexion.prepareStatement(sqlActualizar)) {
                ps.setString(1, nuevoEstado);
                ps.setLong(2, idReserva);

                int filas = ps.executeUpdate();

                if (filas > 0) {
                    mensaje = "Estado de la reserva actualizado correctamente.";
                    return true;
                } else {
                    mensaje = "No se pudo actualizar la reserva.";
                    return false;
                }
            }

        } catch (Exception e) {
            mensaje = "Error al actualizar: " + e.getMessage();
            return false;
        }
    }

    public boolean eliminarReserva(long idReserva) {

        String sqlExisteReserva = "SELECT COUNT(*) FROM reserva WHERE id_reserva = ?";
        String sqlEliminarDetalle = "DELETE FROM reserva_habitacion WHERE id_reserva = ?";
        String sqlEliminarReserva = "DELETE FROM reserva WHERE id_reserva = ?";

        try (Connection conexion = ConexionBD.conectar()) {

            if (conexion == null) {
                mensaje = "No fue posible conectar con la base de datos.";
                return false;
            }

            conexion.setAutoCommit(false);

            if (!existeRegistro(conexion, sqlExisteReserva, idReserva)) {
                mensaje = "La reserva no existe.";
                conexion.rollback();
                return false;
            }

            try (PreparedStatement psDetalle = conexion.prepareStatement(sqlEliminarDetalle)) {
                psDetalle.setLong(1, idReserva);
                psDetalle.executeUpdate();
            }

            try (PreparedStatement psReserva = conexion.prepareStatement(sqlEliminarReserva)) {
                psReserva.setLong(1, idReserva);

                int filas = psReserva.executeUpdate();

                if (filas == 0) {
                    mensaje = "No se pudo eliminar la reserva.";
                    conexion.rollback();
                    return false;
                }
            }

            conexion.commit();
            mensaje = "Reserva eliminada correctamente.";
            return true;

        } catch (Exception e) {
            mensaje = "Error al eliminar: " + e.getMessage();
            return false;
        }
    }

    private boolean existeRegistro(Connection conexion, String sql, long id) throws Exception {
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }

    public String getMensaje() {
        return mensaje;
    }
}