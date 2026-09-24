package com.docencia.condicionales;

public class SwitchServiceImpl implements SwitchService {

    @Override
    public String obtenerNombreDia(Integer numeroDia) {
        if (numeroDia == null || numeroDia <= 0 || numeroDia > 7) {
            throw new IllegalArgumentException();
        }
        switch (numeroDia) {
            case 1:
                return "LUNES";
            case 2:
                return "MARTES";
            case 3:
                return "MIERCOLES";
            case 4:
                return "JUEVES";
            case 5:
                return "VIERNES";
            case 6:
                return "SABADO";
            case 7:
                return "DOMINGO";
        }
        return "INVALIDO";
    }

    @Override
    public Double calcularDescuentoPorTipo(String tipoCliente, Double importe) {
        if (tipoCliente == null || tipoCliente.isBlank() || importe == null || importe <= 0) {
            throw new IllegalArgumentException();
        }
        switch (tipoCliente) {
            case "VIP":
                return importe * 0.20;
            case "NORMAL":
                return importe * 0;
        }
        return null;
    }

    @Override
    public String obtenerMensajeEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException();
        }
        switch (estado) {
            case "ACTIVO":
                return "Elemento activo";
            case "DESACTIVO":
                return  "Elemento desactivo";
        }
        return null;
    }

    @Override
    public Integer obtenerDiasDelMes(Integer mes) {
        if (mes == null || mes <= 0 || mes > 31){
            throw new IllegalArgumentException();
        }
        switch (mes){
            case 1:
                return 31;
            case 2:
                return 28;
        }
        return null;
    }

    @Override
    public String obtenerCategoriaProducto(String codigo) {
        if (codigo == null || codigo.isBlank()){
            throw new IllegalArgumentException();
        }
        switch (codigo){
            case "T001":
                return "TECNOLOGIA";
        }
        return null;
    }

}
