package com.larukita.principal;

import com.larukita.dao.ReservaDAO;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Main {

    public static void main(String[] args) {
        ReservaDAO reservaDAO = new ReservaDAO();
        int opcion = 0;

        do {
            String menu = """
                    MENU LA RUKITA
                    1. Crear reserva
                    2. Consultar reservas por cliente
                    3. Actualizar estado de reserva
                    4. Eliminar reserva
                    5. Salir
                    """;

            String entradaMenu = JOptionPane.showInputDialog(menu);

            if (entradaMenu == null) {
                JOptionPane.showMessageDialog(null, "Programa finalizado.");
                break;
            }

            if (entradaMenu.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debes ingresar una opcion numerica.");
                continue;
            }

            try {
                opcion = Integer.parseInt(entradaMenu);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Solo debes ingresar numeros.");
                continue;
            }

            switch (opcion) {

                case 1 -> {
                    String inputCliente = JOptionPane.showInputDialog("Ingrese el ID del cliente:");
                    if (inputCliente == null || inputCliente.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "El ID del cliente es obligatorio.");
                        break;
                    }

                    long idCliente;
                    try {
                        idCliente = Long.parseLong(inputCliente);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "El ID del cliente debe ser numerico.");
                        break;
                    }

                    String inputUsuario = JOptionPane.showInputDialog("Ingrese el ID del usuario que crea la reserva:");
                    if (inputUsuario == null || inputUsuario.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "El ID del usuario es obligatorio.");
                        break;
                    }

                    long idUsuario;
                    try {
                        idUsuario = Long.parseLong(inputUsuario);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "El ID del usuario debe ser numerico.");
                        break;
                    }

                    String inputHabitacion = JOptionPane.showInputDialog("Ingrese el ID de la habitacion:");
                    if (inputHabitacion == null || inputHabitacion.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "El ID de la habitacion es obligatorio.");
                        break;
                    }

                    long idHabitacion;
                    try {
                        idHabitacion = Long.parseLong(inputHabitacion);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "El ID de la habitacion debe ser numerico.");
                        break;
                    }

                    String inputPrecio = JOptionPane.showInputDialog("Ingrese el precio por noche aplicado:");
                    if (inputPrecio == null || inputPrecio.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "El precio es obligatorio.");
                        break;
                    }

                    double precioNoche;
                    try {
                        precioNoche = Double.parseDouble(inputPrecio);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "El precio debe ser un numero.");
                        break;
                    }

                    if (precioNoche <= 0) {
                        JOptionPane.showMessageDialog(null, "El precio debe ser mayor a 0.");
                        break;
                    }

                    double impuestosPct = 19;

                    reservaDAO.crearReserva(idCliente, idUsuario, idHabitacion, precioNoche, impuestosPct);
                    JOptionPane.showMessageDialog(null, reservaDAO.getMensaje());
                }

                case 2 -> {
                    String inputCliente = JOptionPane.showInputDialog("Ingrese el ID del cliente a consultar:");

                    if (inputCliente == null || inputCliente.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "El ID del cliente es obligatorio.");
                        break;
                    }

                    long idClienteConsulta;
                    try {
                        idClienteConsulta = Long.parseLong(inputCliente);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "El ID del cliente debe ser numerico.");
                        break;
                    }

                    String resultado = reservaDAO.consultarReservasPorCliente(idClienteConsulta);

                    JTextArea areaTexto = new JTextArea(resultado);
                    areaTexto.setEditable(false);
                    areaTexto.setLineWrap(true);
                    areaTexto.setWrapStyleWord(true);

                    JScrollPane scroll = new JScrollPane(areaTexto);
                    scroll.setPreferredSize(new java.awt.Dimension(650, 350));

                    JOptionPane.showMessageDialog(
                            null,
                            scroll,
                            "Reservas del cliente",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }

                case 3 -> {
                    String inputReserva = JOptionPane.showInputDialog("Ingrese el ID de la reserva a actualizar:");
                    if (inputReserva == null || inputReserva.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "El ID de la reserva es obligatorio.");
                        break;
                    }

                    long idReserva;
                    try {
                        idReserva = Long.parseLong(inputReserva);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "El ID de la reserva debe ser numerico.");
                        break;
                    }

                    String nuevoEstado = JOptionPane.showInputDialog("""
                            Ingrese el nuevo estado:
                            PENDIENTE
                            CONFIRMADA
                            CANCELADA
                            """);

                    if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "El estado es obligatorio.");
                        break;
                    }

                    nuevoEstado = nuevoEstado.trim().toUpperCase();

                    reservaDAO.actualizarEstadoReserva(idReserva, nuevoEstado);
                    JOptionPane.showMessageDialog(null, reservaDAO.getMensaje());
                }

                case 4 -> {
                    String inputReserva = JOptionPane.showInputDialog("Ingrese el ID de la reserva a eliminar:");
                    if (inputReserva == null || inputReserva.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "El ID de la reserva es obligatorio.");
                        break;
                    }

                    long idReserva;
                    try {
                        idReserva = Long.parseLong(inputReserva);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "El ID de la reserva debe ser numerico.");
                        break;
                    }

                    int confirmacion = JOptionPane.showConfirmDialog(
                            null,
                            "¿Seguro que desea eliminar la reserva " + idReserva + "?",
                            "Confirmar eliminacion",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (confirmacion != JOptionPane.YES_OPTION) {
                        JOptionPane.showMessageDialog(null, "Eliminacion cancelada.");
                        break;
                    }

                    reservaDAO.eliminarReserva(idReserva);
                    JOptionPane.showMessageDialog(null, reservaDAO.getMensaje());
                }

                case 5 -> JOptionPane.showMessageDialog(null, "Saliendo del sistema...");

                default -> JOptionPane.showMessageDialog(null, "Opcion no valida.");
            }

        } while (opcion != 5);
    }
}