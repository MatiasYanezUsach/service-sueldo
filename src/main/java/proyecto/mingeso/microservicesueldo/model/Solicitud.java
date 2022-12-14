package proyecto.mingeso.microservicesueldo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Solicitud {
    private Long id_solicitud;
    @JsonFormat(pattern="yyyy/MM/dd")
    private LocalDate fecha_cubridora;
    private String rut_empleado;
}