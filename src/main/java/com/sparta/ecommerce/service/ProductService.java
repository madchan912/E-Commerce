package com.sparta.ecommerce.service;

import com.sparta.ecommerce.entity.Product;
import com.sparta.ecommerce.entity.ProductDetail;
import com.sparta.ecommerce.repository.ProductDetailRepository;
import com.sparta.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;

    public ProductService(ProductRepository productRepository, ProductDetailRepository productDetailRepository) {
        this.productRepository = productRepository;
        this.productDetailRepository = productDetailRepository;
    }

    // 새로운 상품을 추가
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // 모든 상품을 조회
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ID로 특정 상품을 조회
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // 특정 상품의 정보를 업데이트
    public Product updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(productDetails.getName());
                    product.setPrice(productDetails.getPrice());
                    product.setDescription(productDetails.getDescription());
                    return productRepository.save(product);
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    // 특정 상품을 삭제
    public void deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
    }

    // 상품 ID로 상세 정보 조회
    public ProductDetail getProductDetail(Long productId) {
        return productDetailRepository.findByProductId(productId);
    }

    // 상품과 상세 정보 저장
    public void saveProductWithDetail(Product product, ProductDetail productDetail) {
        productDetail.setProduct(product);
        product.setProductDetail(productDetail);

        productRepository.save(product); // Cascade로 ProductDetail도 저장됨
    }
}
