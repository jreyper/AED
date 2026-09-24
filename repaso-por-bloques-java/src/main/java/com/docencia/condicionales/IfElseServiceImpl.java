package com.docencia.condicionales;

public class IfElseServiceImpl implements IfElseService {

    @Override
    public String clasificarEdad(Integer edad) {
        if (edad == null || edad <= 0) {
            throw new IllegalArgumentException("La edad no es valida");
        }
        if (edad <= 17) {
            return "MENOR";
        }
        return "ADULTO";
    }

    @Override
    public String evaluarNota(Integer nota) {
        if (nota == null || nota < 1) {
            throw new IllegalArgumentException();
        }
        if (nota < 5){
            return "SUSPENSO";
        }
        return "APROBADO";
    }

    @Override
    public Boolean puedeAcceder(Boolean activo, Integer edad) {
        if (activo == null || edad == null || edad <= 0){
            throw new IllegalArgumentException();
        }
        if (edad > 17){
            return true;
        }
        return false;
    }

    @Override
    public String clasificarTemperatura(Double temperatura) {
        if (temperatura == null || temperatura <= 0){
            throw new IllegalArgumentException();
        }
        if (temperatura > 25){
            return "CALOR";
        }
        return "ESTA BIEN";
    }

    @Override
    public String calcularResultadoComparacion(Integer primero, Integer segundo) {
        if (primero == null || primero <= 0|| segundo == null || segundo <= 0){
            throw new IllegalArgumentException();
        }
        if (primero == segundo){
            return "IGUALES";
        }
        return "NO SON IGUALES";
    }

}
