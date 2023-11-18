package sit.int221.sastt3.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
@Getter
@Setter
@Entity
@Table(name = "subscription")
@IdClass(SubscriptionId.class)
public class Subscribe {

    @Id
    @ManyToOne
    @JoinColumn(name = "categoryId", referencedColumnName = "categoryId")
    private Category category;

    @Id
    @Column(name = "subscriberEmail")
    private String subscriberEmail;


    @JsonProperty("category")
    public Integer getSubCategoryId() {
        if (category != null) {
            return category.getId();
        } else {
            return null;
        }
    }

    @Column(name = "createdOn",insertable = false,updatable = false)
    private ZonedDateTime createdOn;

    @Column(name = "updatedOn",insertable = false,updatable = false)
    private ZonedDateTime updatedOn;

    // Constructors, getters, setters, etc.
}
