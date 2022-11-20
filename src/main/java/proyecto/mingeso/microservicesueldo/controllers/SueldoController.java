package proyecto.mingeso.microservicesueldo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proyecto.mingeso.microservicesueldo.entities.SueldoEntity;
import proyecto.mingeso.microservicesueldo.model.Empleado;
import proyecto.mingeso.microservicesueldo.services.SueldoService;

import java.util.ArrayList;

@RestController
@RequestMapping("/sueldo")
@CrossOrigin(origins = "http://localhost:3000")
public class SueldoController {
    @Autowired
    SueldoService sueldoService;

    @GetMapping
    public ResponseEntity<ArrayList<SueldoEntity>> crearPlanilla() {
        ArrayList<Empleado> empleados = sueldoService.empleados();
        sueldoService.sueldosGenerales(empleados);
        ArrayList<SueldoEntity> sueldos = sueldoService.obtenerPlanilla();
        if(sueldos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.ok(sueldos);
        }
    }
}
