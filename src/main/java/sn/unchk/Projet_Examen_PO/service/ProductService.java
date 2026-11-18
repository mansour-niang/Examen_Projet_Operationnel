package sn.unchk.Projet_Examen_PO.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sn.unchk.Projet_Examen_PO.dto.ProductRequestDTO;
import sn.unchk.Projet_Examen_PO.dto.ProductResponseDTO;
import sn.unchk.Projet_Examen_PO.entity.Product;
import sn.unchk.Projet_Examen_PO.exception.ConflictException;
import sn.unchk.Projet_Examen_PO.exception.ResourceNotFoundException;
import sn.unchk.Projet_Examen_PO.repository.OrderDetailsRepository;
import sn.unchk.Projet_Examen_PO.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderDetailsRepository orderDetailsRepository;

    @Transactional
    public ProductResponseDTO addProduct(ProductRequestDTO dto) {
        Product product = new Product();
        applyFields(product, dto);
        return ProductResponseDTO.from(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> listProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id) {
        return ProductResponseDTO.from(getProduct(id));
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
        Product product = getProduct(id);
        applyFields(product, dto);
        return ProductResponseDTO.from(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProduct(id);
        if (orderDetailsRepository.existsByProductId(id)) {
            throw new ConflictException("Ce produit est utilisé dans des commandes et ne peut pas être supprimé");
        }
        productRepository.delete(product);
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable : " + id));
    }

    private void applyFields(Product product, ProductRequestDTO dto) {
        product.setName(dto.name());
        product.setPrice(dto.price());
    }
}
