package co.edu.uniandes.dse.parcial1.services;


import co.edu.uniandes.dse.parcial1.entities.EstacionEntity;
import co.edu.uniandes.dse.parcial1.entities.RutaEntity;
import co.edu.uniandes.dse.parcial1.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.parcial1.repositories.EstacionRepository;
import co.edu.uniandes.dse.parcial1.repositories.RutaRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class RutaEstacionService {

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private EstacionRepository estacionRepository;


    @Transactional
    public RutaEntity addEstacionRuta(Long rutaId, Long estacionId) throws EntityNotFoundException, IllegalArgumentException {
        log.info("Inicia proceso de asociar una ruta a una estacion");
        Optional<RutaEntity> ruta = rutaRepository.findById(rutaId);
        if (ruta.isEmpty()) {
            throw new EntityNotFoundException("No se encontro la ruta");
        }
        Optional<EstacionEntity> estacion = estacionRepository.findById(estacionId);
        if (estacion.isEmpty()) {
            throw new EntityNotFoundException("No se encontro la estacion");
        }

        if (estacion.get().getCapacidad() < 100 && ruta.get().getTipo().equals("Circular")) {
            int cantidadRutas = 0;
            for (RutaEntity rutaEstacion : estacion.get().getRutas()) {
                if (rutaEstacion.getTipo().equals("Circular")) {
                    cantidadRutas++;

                }}
            if (cantidadRutas > 2) {
                throw new IllegalArgumentException("No se puede agregar la ruta a la estacion poque ya tiene 2 rutas circulares");


            }

        }
        estacion.get().getRutas().add(ruta.get());
        log.info("Fin proceso de agregar una ruta a una estacion");
        return ruta.get();

    }

    @Transactional
    public void removeEstaionRuta(Long rutaId, Long estacionId) throws EntityNotFoundException {
        log.info("Inicia proceso de remover una ruta de una estacion");
        Optional<RutaEntity> ruta = rutaRepository.findById(rutaId);
        if (ruta.isEmpty()) {
            throw new EntityNotFoundException("No se encontro la ruta");
        }
        Optional<EstacionEntity> estacion = estacionRepository.findById(estacionId);
        if (estacion.isEmpty()) {
            throw new EntityNotFoundException("No se encontro la estacion");
        }

        if (ruta.get().getTipo().equals("Nocturna")) {
            int cantidadRutas = 0;
            for (RutaEntity rutaEstacion : estacion.get().getRutas()) {
                if (rutaEstacion.getTipo().equals("Nocturna")) {
                    cantidadRutas++;
                }
            }
            if (cantidadRutas == 1) {
                throw new EntityNotFoundException("No se puede eliminar la ruta nocturna de la estacion porque es la unica ruta nocturna asignada a la estacion");
            }
        estacion.get().getRutas().remove(ruta.get());
        log.info("Fin proceso de remover una ruta de una estacion");
    }
}
}
