package sn.unchk.Projet_Examen_PO.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_details")
@NoArgsConstructor
@Getter
@Setter
public class OrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal prix;

    @Column(nullable = false)
    private Integer quantite;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_order")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_product")
    private Product product;

    /** Builds a line for the given product, copying the product's current price. */
    public static OrderDetails of(Product product, int quantite) {
        OrderDetails detail = new OrderDetails();
        detail.setProduct(product);
        detail.setPrix(product.getPrice());
        detail.setQuantite(quantite);
        return detail;
    }
}
