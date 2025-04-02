package Proveedores;

public class Proveedores {

    private int id_proveedor;
    private String nombre;
    private String contacto;
    private String productos_suministrados;

    public Proveedores(int id_proveedor, String nombre, String contacto, String productos_suministrados) {
        this.id_proveedor = id_proveedor;
        this.nombre = nombre;
        this.contacto = contacto;
        this.productos_suministrados = productos_suministrados;
    }

    public int getId_proveedor() {
        return id_proveedor;
    }

    public void setId_proveedor(int id_proveedor) {
        this.id_proveedor = id_proveedor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getProductos_suministrados() {
        return productos_suministrados;
    }

    public void setProductos_suministrados(String productos_suministrados) {
        this.productos_suministrados = productos_suministrados;
    }
}