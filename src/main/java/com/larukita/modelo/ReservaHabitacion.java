package com.larukita.modelo;

public class ReservaHabitacion {

    private long idReserva;
    private long idHab;
    private double precioNocheAplicada;
    private double impuestosPct;

    public ReservaHabitacion() {
    }

    public long getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(long idReserva) {
        this.idReserva = idReserva;
    }

    public long getIdHab() {
        return idHab;
    }

    public void setIdHab(long idHab) {
        this.idHab = idHab;
    }

    public double getPrecioNocheAplicada() {
        return precioNocheAplicada;
    }

    public void setPrecioNocheAplicada(double precioNocheAplicada) {
        this.precioNocheAplicada = precioNocheAplicada;
    }

    public double getImpuestosPct() {
        return impuestosPct;
    }

    public void setImpuestosPct(double impuestosPct) {
        this.impuestosPct = impuestosPct;
    }
}