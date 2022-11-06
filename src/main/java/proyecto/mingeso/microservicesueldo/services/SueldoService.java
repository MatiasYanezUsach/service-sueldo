package proyecto.mingeso.microservicesueldo.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import proyecto.mingeso.microservicesueldo.entities.SueldoEntity;
import proyecto.mingeso.microservicesueldo.model.Empleado;
import proyecto.mingeso.microservicesueldo.model.Justificado;
import proyecto.mingeso.microservicesueldo.model.Marcas;
import proyecto.mingeso.microservicesueldo.model.Solicitud;
import proyecto.mingeso.microservicesueldo.repositories.SueldoRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

@Service
public class SueldoService {
    @Autowired
    SueldoRepository sueldoRepository;
    @Autowired
    RestTemplate restTemplate;

    public Empleado empleadoFindByRut(String rut_dado){
        Empleado empleado = restTemplate.getForObject("http://localhost:8090/empleado/byRut/" + rut_dado, Empleado.class);
        return empleado;
    }
    public ArrayList<Justificado> justificadoFindByRut(String rut_dado){
        ArrayList<Justificado> justificativos = restTemplate.getForObject("http://localhost:8091/justificativo/byRut/" + rut_dado, ArrayList.class);
        return justificativos;
    }
    public ArrayList<Solicitud> solicitudFindByRut(String rut_dado){
        ArrayList<Solicitud> solicitudes = restTemplate.getForObject("http://localhost:8092/solicitud/byRut/" + rut_dado, ArrayList.class);
        return solicitudes;
    }
    public ArrayList<Marcas> marcasFindByRut(String rut_dado){
        ArrayList<Marcas> marcas = restTemplate.getForObject("http://localhost:8093/marcas/byRut/" + rut_dado, ArrayList.class);
        return marcas;
    }
    int findeMes = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
    int mesActual = Calendar.getInstance().get(Calendar.MONTH)+1;
    int anioActual = Calendar.getInstance().get(Calendar.YEAR);
    LocalDate inicioMes = LocalDate.of(anioActual, mesActual, 1);
    LocalDate finMes = LocalDate.of(anioActual,mesActual, findeMes);
    public long diasHabiles(LocalDate inicioMes, LocalDate finalMes){
        Calendar fechaActual = Calendar.getInstance();
        fechaActual.setTime(java.sql.Date.valueOf(inicioMes));
        int diasSemana = fechaActual.get(Calendar.DAY_OF_WEEK);
        fechaActual.add(Calendar.DAY_OF_WEEK, -diasSemana);
        Calendar fechaActual2 = Calendar.getInstance();
        fechaActual2.setTime(java.sql.Date.valueOf(finalMes));
        int diasSemana2 = fechaActual2.get(Calendar.DAY_OF_WEEK);
        fechaActual2.add(Calendar.DAY_OF_WEEK, -diasSemana2);
        long dias = (fechaActual2.getTimeInMillis()-fechaActual.getTimeInMillis())/(1000*60*60*24);
        long diasTrabajables = dias-(dias*2/7);
        if (diasSemana == Calendar.SUNDAY) {
            diasSemana = Calendar.MONDAY;
        }
        if (diasSemana2 == Calendar.SUNDAY) {
            diasSemana2 = Calendar.MONDAY;
        }
        return diasTrabajables-diasSemana+diasSemana2+1;
    }
    public String nombreCompletoEmpleado (Empleado empleado){
        String apellidos = empleado.getApellidos();
        String nombre = empleado.getNombres();
        return apellidos+" "+nombre;
    }
    public double calcularSueldoFijoMensual (Empleado empleado){
        double sueldoMensual = 0;
        if (Objects.equals(empleado.getCategoria(), "A")){
            sueldoMensual = 1700000;
        }
        if (Objects.equals(empleado.getCategoria(), "B")){
            sueldoMensual = 1200000;
        }
        if (Objects.equals(empleado.getCategoria(), "C")){
            sueldoMensual = 800000;
        }
        return sueldoMensual;
    }

    public double montoPagoHorasExtras(Empleado empleado, ArrayList<Solicitud> registroSolicitudes, ArrayList<Marcas> registroHoras ){
        double montoPorHora = 0;
        double categoria = 0;
        int horasExtras;
        int cantidadSolicitudes = 0;
        int cantidadMarcas = 0;
        LocalTime horaSalida = LocalTime.parse("18:00");
        for(Solicitud solicitudes : registroSolicitudes) {
            cantidadSolicitudes++;
        }
        for(Marcas marcas : registroHoras) {
            cantidadMarcas++;
        }

        if (cantidadSolicitudes <= 0) {
            return montoPorHora;
        }
        if (Objects.equals(empleado.getCategoria(), "A")){
            categoria = 25000;
        }
        if (Objects.equals(empleado.getCategoria(), "B")) {
            categoria = 20000;
        }
        if (Objects.equals(empleado.getCategoria(), "C")){
            categoria = 10000;
        }
        for (int i = 0; i < cantidadSolicitudes; i++) {
            for (int j = 0; j < cantidadMarcas; j++) {
                if (registroSolicitudes.get(i).getFecha_cubridora().equals(registroHoras.get(j).getFecha())) {
                    horasExtras = registroHoras.get(j).getHora().getHour() - horaSalida.getHour();
                    if (horasExtras >= 1) {
                        montoPorHora = montoPorHora + (categoria * horasExtras);
                    }
                }
            }
        }
        return montoPorHora;
    }
    public int calcularAniosServicio (Empleado empleado) {
        int anioActual = LocalDate.now().getYear();
        int aniosServicio = anioActual - empleado.getFecha_in().getYear();
        if (aniosServicio < 0){
            aniosServicio = 0;
            return aniosServicio;
        }
        else{
            return aniosServicio;
        }
    }
    public double montoBonificacionAniosServicio(Empleado empleado){
        int aniosServicio = calcularAniosServicio(empleado);
        double sueldoMensual = calcularSueldoFijoMensual (empleado);
        double montoBonificacion = 0;
        if (aniosServicio < 5){
            return montoBonificacion;
        }
        if(aniosServicio < 10){
            montoBonificacion = Math.floor(sueldoMensual*0.05);
        }
        if(aniosServicio >= 10 && aniosServicio < 15){
            montoBonificacion = Math.floor(sueldoMensual*0.08);
        }
        if(aniosServicio >= 15 && aniosServicio < 20){
            montoBonificacion = Math.floor(sueldoMensual*0.11);
        }
        if(aniosServicio >= 20 && aniosServicio < 25){
            montoBonificacion = Math.floor(sueldoMensual*0.14);
        }
        if(aniosServicio >= 25){
            montoBonificacion = Math.floor(sueldoMensual*0.17);
        }
        return montoBonificacion;
    }
    public double montoDescuentosAtrasos(Empleado empleado, ArrayList<Justificado> registroJustificados, ArrayList<Marcas> registroHoras, double marcasPorDiaEnElMes) {
        double montoPorAtraso = 0;
        double sueldoMensual = calcularSueldoFijoMensual (empleado);
        int cantidadJustificativos = 0;
        int reservaJustivicativos = 0;
        int cantidadMarcas = 0;
        for(Justificado justificativos : registroJustificados) {
            cantidadJustificativos++;
        }
        for(Marcas marcas : registroHoras) {
            cantidadMarcas++;
        }
        reservaJustivicativos = cantidadJustificativos;
        for(int i = 0; i < cantidadMarcas; i++) {
            if (registroHoras.get(i).getHora().getHour() == 8){
                if (registroHoras.get(i).getHora().getMinute() > 10 && registroHoras.get(i).getHora().getMinute() <= 25){
                    montoPorAtraso = montoPorAtraso + sueldoMensual*0.01;
                }
                if (registroHoras.get(i).getHora().getMinute() > 25 && registroHoras.get(i).getHora().getMinute() <= 45){
                    montoPorAtraso = montoPorAtraso + sueldoMensual*0.03;
                }
                if (registroHoras.get(i).getHora().getMinute() > 45){
                    montoPorAtraso = montoPorAtraso + sueldoMensual*0.06;
                }
            }
            else if (registroHoras.get(i).getHora().getHour() == 9){
                if (registroHoras.get(i).getHora().getMinute() > 0 && registroHoras.get(i).getHora().getMinute() <= 10) {
                    montoPorAtraso = montoPorAtraso + sueldoMensual * 0.06;
                }
                if (registroHoras.get(i).getHora().getMinute() > 10) {
                    montoPorAtraso = montoPorAtraso + sueldoMensual * 0.15;
                }
            }
            else{
                if (registroHoras.get(i).getHora().getHour() > 9 && registroHoras.get(i).getHora().getHour() < 18){
                    montoPorAtraso = montoPorAtraso + sueldoMensual * 0.15;
                }
            }
            marcasPorDiaEnElMes = marcasPorDiaEnElMes - 1;
        }
        marcasPorDiaEnElMes = Math.ceil(marcasPorDiaEnElMes/2);
        for(int i = 0; i < cantidadJustificativos; i++) {
            for(int j = 0; j < cantidadMarcas; j++){
                if(registroJustificados.get(i).getFecha_cubridora().equals(registroHoras.get(j).getFecha()) && registroHoras.get(j).getHora().getHour() < 18 && registroHoras.get(j).getHora().getHour() >= 9){
                    montoPorAtraso = montoPorAtraso - (sueldoMensual * 0.15);
                    reservaJustivicativos = reservaJustivicativos -1;
                }
            }
        }
        if(marcasPorDiaEnElMes > 0 && reservaJustivicativos == 0){
            montoPorAtraso = montoPorAtraso + (sueldoMensual * 0.15)*marcasPorDiaEnElMes;
        }
        if(marcasPorDiaEnElMes > reservaJustivicativos && reservaJustivicativos > 0){
            montoPorAtraso = montoPorAtraso + (sueldoMensual * 0.15)*(marcasPorDiaEnElMes - reservaJustivicativos);
        }
        return montoPorAtraso;
    }
    public double calcularSueldoBruto(Empleado empleado, ArrayList<Solicitud> registroSolicitudes,ArrayList<Marcas> registroHoras, ArrayList<Justificado> registroJustificados,double marcasPorDiaEnElMes ){
        double descuentos = montoDescuentosAtrasos(empleado,registroJustificados,registroHoras,marcasPorDiaEnElMes);
        double bonificaciones = montoBonificacionAniosServicio(empleado);
        double montoHoraExtra = montoPagoHorasExtras(empleado, registroSolicitudes, registroHoras);
        double sueldoFijo = calcularSueldoFijoMensual(empleado);
        return bonificaciones + montoHoraExtra + (sueldoFijo - descuentos);
    }
    public double calcularCotizacionPrevisional(Empleado empleado, ArrayList<Solicitud> registroSolicitudes,ArrayList<Marcas> registroHoras, ArrayList<Justificado> registroJustificados,double marcasPorDiaEnElMes){
        double sueldoBruto = calcularSueldoBruto(empleado,registroSolicitudes,registroHoras,registroJustificados,marcasPorDiaEnElMes);
        return sueldoBruto * 0.1;
    }
    public double calcularCotizacionPlanSalud(Empleado empleado,ArrayList<Solicitud> registroSolicitudes,ArrayList<Marcas> registroHoras, ArrayList<Justificado> registroJustificados,double marcasPorDiaEnElMes){
        double sueldoBruto = calcularSueldoBruto(empleado,registroSolicitudes,registroHoras,registroJustificados,marcasPorDiaEnElMes);
        return sueldoBruto * 0.08;
    }
    public double calcularSueldoFinal(Empleado empleado, ArrayList<Solicitud> registroSolicitudes,ArrayList<Marcas> registroHoras, ArrayList<Justificado> registroJustificados,double marcasPorDiaEnElMes){
        double sueldoBruto = calcularSueldoBruto(empleado,registroSolicitudes,registroHoras,registroJustificados,marcasPorDiaEnElMes);
        double cotizacionPlanSalud = calcularCotizacionPlanSalud(empleado,registroSolicitudes,registroHoras,registroJustificados,marcasPorDiaEnElMes);
        double cotizacionPrevisional = calcularCotizacionPrevisional(empleado,registroSolicitudes,registroHoras,registroJustificados,marcasPorDiaEnElMes);
        return (sueldoBruto-cotizacionPrevisional)-cotizacionPlanSalud;
    }
    public SueldoEntity crearSueldoEmpleado (Empleado empleado){
        double marcasPorDiaEnElMes = ((double) diasHabiles(inicioMes,finMes)) * 2;
        ArrayList<Solicitud> registroSolicitudes = solicitudFindByRut(empleado.getRut());
        ArrayList<Marcas> registroHoras = marcasFindByRut(empleado.getRut());
        ArrayList<Justificado> registroJustificados = justificadoFindByRut(empleado.getRut());
        SueldoEntity nuevoSueldo = new SueldoEntity();
        nuevoSueldo.setRut(empleado.getRut());
        nuevoSueldo.setNombre_empleado(nombreCompletoEmpleado(empleado));
        nuevoSueldo.setCategoria(empleado.getCategoria());
        nuevoSueldo.setAnios_servicio(calcularAniosServicio(empleado));
        nuevoSueldo.setSueldo_fijo_mensual(calcularSueldoFijoMensual(empleado));
        nuevoSueldo.setMonto_bonificacion_anios_servicio(montoBonificacionAniosServicio(empleado));
        nuevoSueldo.setMonto_pago_horas_extras(montoPagoHorasExtras(empleado, registroSolicitudes, registroHoras));
        nuevoSueldo.setMonto_descuentos(montoDescuentosAtrasos(empleado,registroJustificados,registroHoras,marcasPorDiaEnElMes));
        nuevoSueldo.setSueldo_bruto(calcularSueldoBruto(empleado,registroSolicitudes,registroHoras,registroJustificados,marcasPorDiaEnElMes));
        nuevoSueldo.setCotizacion_previsional(calcularCotizacionPrevisional(empleado,registroSolicitudes,registroHoras,registroJustificados,marcasPorDiaEnElMes));
        nuevoSueldo.setCotizacion_salud(calcularCotizacionPlanSalud(empleado,registroSolicitudes,registroHoras,registroJustificados,marcasPorDiaEnElMes));
        nuevoSueldo.setMonto_sueldo_final(calcularSueldoFinal(empleado,registroSolicitudes,registroHoras,registroJustificados,marcasPorDiaEnElMes));
        return nuevoSueldo;
    }
    public void sueldosGenerales (ArrayList<Empleado> empleados){
        int cantidadEmpleados = 0;
        for(Empleado cantEmpleados : empleados) {
            cantidadEmpleados++;
        }
        for(int i = 0; i < cantidadEmpleados; i++){
            SueldoEntity nuevoSueldo = crearSueldoEmpleado(empleados.get(i));
            sueldoRepository.save(nuevoSueldo);
        }
    }
    public ArrayList<SueldoEntity> obtenerPlanilla(){
        return (ArrayList<SueldoEntity>) sueldoRepository.findAll();
    }
}
