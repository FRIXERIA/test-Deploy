package sit.int221.sastt3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sit.int221.sastt3.entities.Category;
import sit.int221.sastt3.entities.Subscribe;
import sit.int221.sastt3.entities.SubscriptionId;
import sit.int221.sastt3.entities.User;

import java.util.List;

public interface SubscribeRepo extends CustomRepository <Subscribe,SubscriptionId> {
    @Query("SELECT s FROM Subscribe s WHERE s.subscriberEmail=:email")
    List<Subscribe> findByEmail(String email);

    @Query("SELECT s FROM Subscribe s WHERE s.category.id =:id")
    List<Subscribe> findByCategory(Integer id);

}
