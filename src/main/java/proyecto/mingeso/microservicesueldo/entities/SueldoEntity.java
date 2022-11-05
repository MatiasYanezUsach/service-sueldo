package proyecto.mingeso.microservicesueldo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "planilla_de_sueldos")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SueldoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_sueldo", nullable = false)
    private Long id_sueldo;
    private String rut;
    private String nombre_empleado;
    private String categoria;
    private int  anios_servicio;
    private double sueldo_fijo_mensual;
    private double monto_bonificacion_anios_servicio;
    private double monto_pago_horas_extras;
    private double monto_descuentos;
    private double sueldo_bruto;
    private double cotizacion_previsional;
    private double cotizacion_salud;
    private double monto_sueldo_final;
}