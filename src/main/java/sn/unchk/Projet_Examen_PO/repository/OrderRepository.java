package sn.unchk.Projet_Examen_PO.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import sn.unchk.Projet_Examen_PO.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Override
    @EntityGraph(attributePaths = {"client", "details", "details.product"})
    List<Order> findAll();

    @Override
    @EntityGraph(attributePaths = {"client", "details", "details.product"})
    Optional<Order> findById(Long id);

    boolean existsByClientId(Long clientId);
}
