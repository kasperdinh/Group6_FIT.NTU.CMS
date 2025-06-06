package group6.fit_ntu_cms.repositories;

import group6.fit_ntu_cms.models.EventModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventModel, Long> {
    Optional<EventModel> findByFilePath(String filePath);

    Optional<EventModel> findByEventImage(String eventImage);

}