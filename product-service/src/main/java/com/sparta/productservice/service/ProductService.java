package com.sparta.productservice.service;

import com.sparta.productservice.entity.Product;
import com.sparta.productservice.entity.ProductDetail;
import com.sparta.productservice.repository.ProductDetailRepository;
import com.sparta.productservice.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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

    // 모든 상품을 조회
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ID로 특정 상품을 조회
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }


    // 특정 상품의 정보를 업데이트
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());
        product.setDescription(productDetails.getDescription());

        return productRepository.save(product);
    }

    // 특정 상품을 삭제
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        productRepository.delete(product);
    }

    // 상품 ID로 상세 정보 조회
    public ProductDetail getProductDetail(Long productId) {
        return productDetailRepository.findByProductId(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product detail not found"));
    }

    // 상품과 상세 정보 저장
    public void saveProductWithDetail(Product product, ProductDetail productDetail) {
        productDetail.setProduct(product);
        product.setProductDetail(productDetail);

        productRepository.save(product); // Cascade로 ProductDetail도 저장됨
    }
}