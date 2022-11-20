package proyecto.mingeso.microservicesueldo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Marcas {
    private Long id_marca;
    @JsonFormat(pattern="yyyy/MM/dd")
    private LocalDate fecha;
    @JsonFormat(pattern="HH:mm")
    private LocalTime hora;
    private String rut_empleado;
}
