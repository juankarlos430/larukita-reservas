package com.larukita.modelo;

import java.sql.Timestamp;

public class Reserva {

    private long idReserva;
    private long idCliente;
    private long idUsuarioCreo;
    private Timestamp fechaReserva;

    public Reserva() {
    }

    public long getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(long idReserva) {
        this.idReserva = idReserva;
    }

    public long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(long idCliente) {
        this.idCliente = idCliente;
    }

    public long getIdUsuarioCreo() {
        return idUsuarioCreo;
    }

    public void setIdUsuarioCreo(long idUsuarioCreo) {
        this.idUsuarioCreo = idUsuarioCreo;
    }

    public Timestamp getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(Timestamp fechaReserva) {
        this.fechaReserva = fechaReserva;
    }
}