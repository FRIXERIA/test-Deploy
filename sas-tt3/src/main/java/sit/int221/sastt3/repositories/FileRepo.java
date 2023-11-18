package sit.int221.sastt3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sit.int221.sastt3.entities.Category;
import sit.int221.sastt3.entities.Files;
import sit.int221.sastt3.entities.Subscribe;

import java.util.List;

public interface FileRepo extends JpaRepository<Files,Integer> {
    @Query("SELECT f FROM Files f WHERE f.IdAnnounce=:id")
    List<Files> findByIdAnnounce (Integer id);

    @Query("SELECT f FROM Files f WHERE f.IdAnnounce <> :id")
    List<Files> findByNotIdAnnounce(Integer id);
    @Query("SELECT f FROM Files f WHERE f.fileName=:name")
    List<Files> findByNameFiles (String name);
}
