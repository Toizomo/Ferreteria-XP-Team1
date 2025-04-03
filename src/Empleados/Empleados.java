package Empleados;

import java.time.LocalDate;

public class Empleados {
    private int id_empleado;
    private String nombre;
    private String dni;
    private String telefono;
    private String email;
    private String cargo; // ADMINISTRADOR, VENDEDOR, ALMACENERO, CAJERO, REPARTIDOR
    private double salario;
    private LocalDate fecha_contratacion;
    private String estado; // ACTIVO, INACTIVO
    private String usuario;
    private String contrasena;

    // Constructores
    public Empleados() {}

    public Empleados(int id_empleado, String nombre, String dni, String telefono,
                     String email, String cargo, double salario,
                     LocalDate fecha_contratacion, String estado,
                     String usuario, String contrasena) {
        this.id_empleado = id_empleado;
        this.nombre = nombre;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
        this.cargo = cargo;
        this.salario = salario;
        this.fecha_contratacion = fecha_contratacion;
        this.estado = estado;
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    // Getters y Setters
    public int getId_empleado() {
        return id_empleado;
    }

    public void setId_empleado(int id_empleado) {
        this.id_empleado = id_empleado;
    }

    // ... (implementar todos los getters y setters para los demás campos)

    // Método para verificar si el empleado tiene permiso de administrador
    public boolean esAdministrador() {
        return "ADMINISTRADOR".equals(this.cargo);
    }

    // Método para verificar si el empleado puede realizar ventas
    public boolean puedeVender() {
        return "ADMINISTRADOR".equals(this.cargo) || "VENDEDOR".equals(this.cargo) || "CAJERO".equals(this.cargo);
    }

    // Método para verificar si el empleado puede gestionar inventario
    public boolean puedeGestionarInventario() {
        return "ADMINISTRADOR".equals(this.cargo) || "ALMACENERO".equals(this.cargo);
    }
}