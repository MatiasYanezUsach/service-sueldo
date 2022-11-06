package proyecto.mingeso.microservicesueldo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empleado {
    private String rut;
    private String apellidos;
    private String nombres;
    @JsonFormat(pattern="yyyy/MM/dd")
    private LocalDate fecha_nac;
    private String categoria;
    @JsonFormat(pattern="yyyy/MM/dd")
    private LocalDate fecha_in;
}