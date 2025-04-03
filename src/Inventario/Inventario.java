package Inventario;

public class Inventario {
    private int id_producto;
    private String nombre_producto;
    private String categoria;
    private int cantidad_stock;
    private double precio_venta;
    private int id_proveedor;
    private String nombre_proveedor; // Para mostrar en la interfaz
    private int stock_minimo;
    private String estado;

    // Constructores
    public Inventario() {}

    public Inventario(int id_producto, String nombre_producto, String categoria,
                      int cantidad_stock, double precio_venta, int id_proveedor,
                      int stock_minimo, String estado) {
        this.id_producto = id_producto;
        this.nombre_producto = nombre_producto;
        this.categoria = categoria;
        this.cantidad_stock = cantidad_stock;
        this.precio_venta = precio_venta;
        this.id_proveedor = id_proveedor;
        this.stock_minimo = stock_minimo;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public String getNombre_producto() {
        return nombre_producto;
    }

    public void setNombre_producto(String nombre_producto) {
        this.nombre_producto = nombre_producto;
    }

    // ... (implementar todos los getters y setters para los demás campos)

    // Método para verificar si el stock está bajo
    public boolean isStockBajo() {
        return cantidad_stock < stock_minimo;
    }
}