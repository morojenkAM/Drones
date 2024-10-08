package ro.developmentfactory.thedrones.repository;

import org.springframework.data.repository.CrudRepository;
import ro.developmentfactory.thedrones.repository.entity.DroneStatus;

import java.util.UUID;


public interface DroneStatusRepository extends CrudRepository<DroneStatus, UUID> {
 }
