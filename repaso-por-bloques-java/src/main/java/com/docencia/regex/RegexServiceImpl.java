package com.docencia.regex;

public class RegexServiceImpl implements RegexService {

    @Override
    public Boolean validarDni(String dni) {
        if (dni == null || dni.isBlank()){
            return false;
        }
        String patron = "[0-8]{8}[A-Z]";
        return dni.matches(patron);
    }

    @Override
    public Boolean validarEmail(String email) {
        if (email == null || email.isBlank()){
            return false;
        }
        String patron = "^[a-z0-9]+@[a-z]+\\.[a-z]+$";
        return email.matches(patron);
    }

    @Override
    public Boolean validarTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()){
            return false;
        }
        String patron = "^612[0-9]{6}$";
        return telefono.matches(patron);
    }

    @Override
    public Boolean validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
        return false;
        }
        String patron = "^[A-Za-záéíóú]+[A-Za-záéíóú\\s]+$";
        return nombre.matches(patron);
        }

    @Override
    public Boolean validarCodigoPostal(String codigoPostal) {
        if (codigoPostal == null || codigoPostal.isBlank()){
            return false;
        }
        String patron = "^[0-9]{5}$";
        return codigoPostal.matches(patron);
    }

    @Override
    public Boolean validarMatricula(String matricula) {
        if (matricula == null || matricula.isBlank()){
            return false;
        }
        String patron = "^[0-9]{4}[A-Z]{3}$";
        return matricula.matches(patron);
    }

    }



