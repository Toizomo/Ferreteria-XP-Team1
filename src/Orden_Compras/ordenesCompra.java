package Orden_Compras;

public class ordenesCompra {
    int id_orden_compra;
    int id_cliente;
    int id_empleado;
    int id_producto;
    int total;
    String estado_orden;
    String fecha_compra;

    public ordenesCompra(int id_orden_compra, int id_cliente, int id_empleado, int id_producto, int total, String estado_orden, String fecha_compra) {
        this.id_orden_compra = id_orden_compra;
        this.id_cliente = id_cliente;
        this.id_empleado = id_empleado;
        this.id_producto = id_producto;
        this.total = total;
        this.estado_orden = estado_orden;
        this.fecha_compra = fecha_compra;
    }

    public int getId_orden_compra() {
        return id_orden_compra;
    }

    public void setId_orden_compra(int id_orden_compra) {
        this.id_orden_compra = id_orden_compra;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public int getId_empleado() {
        return id_empleado;
    }

    public void setId_empleado(int id_empleado) {
        this.id_empleado = id_empleado;
    }

    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getEstado_orden() {
        return estado_orden;
    }

    public void setEstado_orden(String estado_orden) {
        this.estado_orden = estado_orden;
    }

    public String getFecha_compra() {
        return fecha_compra;
    }

    public void setFecha_compra(String fecha_compra) {
        this.fecha_compra = fecha_compra;
    }
}
