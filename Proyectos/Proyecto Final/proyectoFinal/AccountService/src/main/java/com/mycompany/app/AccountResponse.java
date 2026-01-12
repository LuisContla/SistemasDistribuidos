package com.mycompany.app;

public class AccountResponse {
    private String mensaje;
    private double saldoActual;

    public AccountResponse(String mensaje, double saldoActual) {
        this.mensaje = mensaje;
        this.saldoActual = saldoActual;
    }

    public String getMensaje() { return mensaje; }
    public double getSaldoActual() { return saldoActual; }
}