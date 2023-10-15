package sit.int221.sastt3.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    @Column(name = "userId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "username", nullable = false, length =45,unique = true)
    private String username;
    @Column(name = "password",updatable = false)
    private String password;
    @Column(name = "name",length = 100,unique = true)
    private String name;
    @Column(name = "email",length = 150,unique = true)
    private String email;
    @Column (name = "role")
    private String role;
    @Column (name = "createdOn",insertable = false,updatable = false)
    private ZonedDateTime createdOn;
    @Column (name = "updatedOn",insertable = false,updatable = false)
    private ZonedDateTime updatedOn;

    @JsonIgnore
    @OneToMany(mappedBy = "announcementOwnerUser")
    private Set<Announcement> announcementsUser = new LinkedHashSet<>();


}
