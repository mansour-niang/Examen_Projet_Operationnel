package sn.unchk.Projet_Examen_PO.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.unchk.Projet_Examen_PO.entity.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
