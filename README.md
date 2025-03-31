# Ferreteria-XP-Team1

## Modelo Entidad-Relación (MER)

```mermaid
erDiagram
    CLIENTES ||--o{ ORDENES : "realiza"
    CLIENTES {
        int id_cliente PK
        string nombre
        string telefono
        string direccion
    }

    ORDENES ||--|{ DETALLES_ORDEN : "contiene"
    ORDENES {
        int id_orden PK
        date fecha
        decimal total
        string estado
    }

    PRODUCTOS }o--|| DETALLES_ORDEN : "incluye"
    PRODUCTOS {
        int id_producto PK
        string nombre
        decimal precio
        int stock
    }

    PROVEEDORES ||--o{ PRODUCTOS : "suministra"
    PROVEEDORES {
        int id_proveedor PK
        string nombre
        string contacto
    }

    EMPLEADOS ||--o{ ORDENES : "gestiona"
    EMPLEADOS {
        int id_empleado PK
        string nombre
        string rol
    }