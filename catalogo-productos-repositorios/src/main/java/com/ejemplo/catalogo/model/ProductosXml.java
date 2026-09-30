package com.ejemplo.catalogo.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "productos") // Buscas por ese nombre el elemento padre
public class ProductosXml {

    @JacksonXmlElementWrapper(useWrapping = false) // Cruza informacion
    @JacksonXmlProperty(localName = "producto") // Dentro de productos construye producto, las veces que haya producto
    public List<Producto> productos;
    
    public ProductosXml(){
        productos = new ArrayList<>();
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public List<Producto> getProductos() {
        return productos;
    }
}
