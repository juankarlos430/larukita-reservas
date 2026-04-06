<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Reservas</title>
    <link rel="stylesheet" href="styles.css">
</head>

<body>
<img src="img/reservashotel.png" width="400">
<h1>Reservas La Rukita</h1>

<form action="reserva" method="post">

    <label>Check-in:</label>
    <input type="date" name="checkin" required>
    <br><br>

    <label>Check-out:</label>
    <input type="date" name="checkout" required>
    <br><br>

    <label>Adultos:</label>
    <select name="adultos">
        <option value="1">1</option>
        <option value="2">2</option>
    </select>
    <br><br>

    <label>Tipo habitación:</label>
    <select name="tipo">
        <option value="estandar">Estándar</option>
        <option value="deluxe">Deluxe</option>
    </select>
    <br><br>

    <button type="submit">Enviar</button>

</form>

</body>
</html>