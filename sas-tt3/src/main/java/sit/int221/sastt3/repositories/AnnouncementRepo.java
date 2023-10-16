package sit.int221.sastt3.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sit.int221.sastt3.DTO.PageDTO;
import sit.int221.sastt3.entities.Announcement;
import sit.int221.sastt3.entities.User;

import java.util.List;

public interface AnnouncementRepo extends JpaRepository<Announcement,Integer> {
    List<Announcement> findAllByOrderByIdDesc();
    List<Announcement> findAllByAnnouncementDisplayEqualsOrderByIdDesc(String displayValue);
    List<Announcement> findAllByCategoryIdEqualsOrderByIdDesc(Integer id);
    @Query("SELECT u FROM User u WHERE u.username=:username")
    User findByUsername(String username);
    @Query("SELECT a FROM Announcement a WHERE a.announcementOwner=:own order by a.id desc ")
    List<Announcement> findByAnnouncementOwner(Integer own);
    @Query("SELECT a FROM Announcement a WHERE a.id=:own")
    Announcement findByAnnouncementOwnerId(Integer own);

}
