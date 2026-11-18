package sn.unchk.Projet_Examen_PO.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.unchk.Projet_Examen_PO.entity.OrderDetails;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {

    boolean existsByProductId(Long productId);
}
