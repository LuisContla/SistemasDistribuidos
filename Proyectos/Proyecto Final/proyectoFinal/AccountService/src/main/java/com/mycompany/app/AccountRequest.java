package com.mycompany.app;

public class AccountRequest {
        private String curp;
        private String destino;
        private double monto;

        public String getCurp() { return curp; }
        public void setCurp(String curp) { this.curp = curp; }
        
        public String getDestino() { return destino; }
        public void setDestino(String destino) { this.destino = destino; }

        public double getMonto() { return monto; }
        public void setMonto(double monto) { this.monto = monto; }
    }