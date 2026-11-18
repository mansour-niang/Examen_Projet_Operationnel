package sn.unchk.Projet_Examen_PO.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.unchk.Projet_Examen_PO.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
