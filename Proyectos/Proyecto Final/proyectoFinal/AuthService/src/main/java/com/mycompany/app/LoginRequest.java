package com.mycompany.app;

public class LoginRequest {
    private String curp;
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String curp, String password) {
        this.curp = curp;
        this.password = password;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}