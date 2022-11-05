package proyecto.mingeso.microservicesueldo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto.mingeso.microservicesueldo.entities.SueldoEntity;

@Repository
public interface SueldoRepository extends JpaRepository<SueldoEntity, Long> {

}