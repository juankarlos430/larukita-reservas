package com.larukita.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.larukita.conexion.ConexionBD;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/reserva")
public class ReservaServlet extends HttpServlet {

    // 🔴 POST → GUARDAR RESERVA
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String checkin = request.getParameter("checkin");
        String checkout = request.getParameter("checkout");
        String adultos = request.getParameter("adultos");
        String tipo = request.getParameter("tipo");

        try {
            Connection conn = ConexionBD.getConnection();

            String sql = "INSERT INTO reserva (id_cliente, check_in_previsto, check_out_previsto) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, 1); // cliente fijo
            ps.setString(2, checkin);
            ps.setString(3, checkout);

            ps.executeUpdate();

            // 🔥 redirige para mostrar lista
            response.sendRedirect("reserva");

        } catch (Exception e) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            out.println("<h2>Error:</h2>");
            out.println("<pre>" + e.getMessage() + "</pre>");
        }
    }

    // 🔴 GET → LISTAR RESERVAS
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Connection conn = ConexionBD.getConnection();

            String sql = "SELECT id_reserva, check_in_previsto, check_out_previsto FROM reserva ORDER BY id_reserva DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            out.println("<h1>Reservas guardadas</h1>");

            out.println("<table border='1'>");
            out.println("<tr><th>ID</th><th>Check-in</th><th>Check-out</th></tr>");

            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("id_reserva") + "</td>");
                out.println("<td>" + rs.getString("check_in_previsto") + "</td>");
                out.println("<td>" + rs.getString("check_out_previsto") + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");

            out.println("<br>");
            out.println("<a href='reservas.jsp'>Nueva reserva</a>");

        } catch (Exception e) {
            out.println("<h2>Error:</h2>");
            out.println("<pre>" + e.getMessage() + "</pre>");
        }
    }
}