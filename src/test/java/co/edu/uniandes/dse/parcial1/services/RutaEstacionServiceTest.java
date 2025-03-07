package co.edu.uniandes.dse.parcial1.services;


import co.edu.uniandes.dse.parcial1.entities.EstacionEntity;
import co.edu.uniandes.dse.parcial1.entities.RutaEntity;
import co.edu.uniandes.dse.parcial1.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.parcial1.repositories.EstacionRepository;
import co.edu.uniandes.dse.parcial1.repositories.RutaRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@Import({RutaEntity.class, RutaEstacionService.class})
public class RutaEstacionServiceTest {

    @Autowired
    private RutaEstacionService service;
    @Autowired
    private EstacionRepository estacionRepository;

    @Autowired
    private TestEntityManager entityManager;
    private PodamFactory factory = new PodamFactoryImpl();
    private RutaEntity ruta = new RutaEntity();
    private EstacionEntity estacion = new EstacionEntity();
    private List<RutaEntity> rutas = new ArrayList<>();
    @Autowired
    private RutaEstacionService rutaEstacionService;

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from RutaEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from EstacionEntity").executeUpdate();

    }
    private void insertData() {
        ruta = factory.manufacturePojo(RutaEntity.class);
        estacion = factory.manufacturePojo(EstacionEntity.class);
        entityManager.persist(ruta);
        entityManager.persist(estacion);

        for (int i = 0; i < 3; i++) {
            RutaEntity ruta = factory.manufacturePojo(RutaEntity.class);
            entityManager.persist(ruta);
            rutas.add(ruta);
            estacion.getRutas().add(ruta);
        }
    }
    @Test
    void testAddEstacionRutaExitoso() throws EntityNotFoundException, IllegalArgumentException {
        RutaEntity result = service.addEstacionRuta(ruta.getId(), estacion.getId());
        assertNotNull(result);
        assertEquals(ruta.getId(), result.getId());

    }

    @Test
    void testAddEstacionRutaEstacionNoExiste() {
        assertThrows(EntityNotFoundException.class, () ->
                service.addEstacionRuta(ruta.getId(), 0L));

    }

    @Test
    void testAddEstacionRutaRutaNoExiste() {
        assertThrows(EntityNotFoundException.class, () ->
                service.addEstacionRuta(0L, estacion.getId()));

    }
    @Test
    void testAddEstacionRutaMasDeDosCirculares() throws EntityNotFoundException {
        EstacionEntity estacion = factory.manufacturePojo(EstacionEntity.class);
        estacion.setCapacidad(80);

        for (int i = 0; i < 2; i++) {
            RutaEntity rutaCircular = factory.manufacturePojo(RutaEntity.class);
            rutaCircular.setTipo("Circular");
            entityManager.persist(rutaCircular);
            service.addEstacionRuta(rutaCircular.getId(), estacion.getId());

        }

        RutaEntity nuevaRuta = factory.manufacturePojo(RutaEntity.class);
        nuevaRuta.setTipo("Circular");
        entityManager.persist(nuevaRuta);

        assertThrows(IllegalArgumentException.class, () -> {
            rutaEstacionService.addEstacionRuta(nuevaRuta.getId(), estacion.getId());
        });

    }
    @Test
    void testRemoveEstacionRutaExitoso() throws EntityNotFoundException {
        service.removeEstaionRuta(ruta.getId(), estacion.getId());
        EstacionEntity estacionEntity = estacionRepository.findById(estacion.getId()).get();
        assertFalse(estacionEntity.getRutas().contains(ruta));
    }

    @Test
    void testRemoveEstacionRutaEstacionNoExiste() {
        assertThrows(EntityNotFoundException.class, () ->
                service.removeEstaionRuta(ruta.getId(), 0L));

    }

    @Test
    void testRemoveEstacionRutaRutaNoExiste() {
        assertThrows(EntityNotFoundException.class, () ->
                service.removeEstaionRuta(0L, estacion.getId()));

    }

    @Test
    void testRemoveEstacionRutaMenosDeUnaNocturna() {
        RutaEntity rutaNocturna = factory.manufacturePojo(RutaEntity.class);
        rutaNocturna.setTipo("Nocturna");
        entityManager.persist(rutaNocturna);
        estacion.getRutas().add(rutaNocturna);

        assertThrows(EntityNotFoundException.class, () -> {
            rutaEstacionService.removeEstaionRuta(rutaNocturna.getId(), estacion.getId());
        });
    }
}
