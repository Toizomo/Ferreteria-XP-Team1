package Empleados;

import java.time.LocalDate;

public class Empleados {
    private int id_empleado;
    private String nombre;
    private String dni;
    private String telefono;
    private String email;
    private String cargo;
    private double salario;
    private LocalDate fecha_contratacion;
    private String estado;
    private String usuario;
    private String contrasena;

    // Constructor completo
    public Empleados(int id_empleado, String nombre, String dni, String telefono, String email, String cargo,
                     double salario, LocalDate fecha_contratacion, String estado, String usuario, String contrasena) {
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public LocalDate getFecha_contratacion() {
        return fecha_contratacion;
    }

    public void setFecha_contratacion(LocalDate fecha_contratacion) {
        this.fecha_contratacion = fecha_contratacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
