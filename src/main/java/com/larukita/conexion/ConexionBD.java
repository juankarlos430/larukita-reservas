package com.larukita.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL = "jdbc:mysql://localhost:3306/larukita_hotel";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // coloca tu contraseña si tienes

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Para compatibilidad con tu DAO
    public static Connection conectar() throws SQLException {
        return getConnection();
    }
}