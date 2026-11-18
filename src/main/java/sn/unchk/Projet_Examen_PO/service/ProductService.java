package sn.unchk.Projet_Examen_PO.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sn.unchk.Projet_Examen_PO.dto.ProductRequestDTO;
import sn.unchk.Projet_Examen_PO.dto.ProductResponseDTO;
import sn.unchk.Projet_Examen_PO.entity.Product;
import sn.unchk.Projet_Examen_PO.exception.ResourceNotFoundException;
import sn.unchk.Projet_Examen_PO.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

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
        productRepository.delete(getProduct(id));
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
