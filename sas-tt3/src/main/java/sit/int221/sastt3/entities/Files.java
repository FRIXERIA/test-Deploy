package sit.int221.sastt3.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "files")
public class Files {
    @Id
    @Column(name = "idFiles")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer idFiles ;
    @Column(name = "IdAnnounce")
    Integer IdAnnounce;
    @Column(name = "fileName")
    String fileName ;
}
