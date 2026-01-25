package com.sparta.productservice.service;

import com.sparta.productservice.dto.ProductResponse;
import com.sparta.productservice.dto.ProductSaveRequestDto;
import com.sparta.productservice.entity.Product;
import com.sparta.productservice.entity.ProductDetail;
import com.sparta.productservice.repository.ProductDetailRepository;
import com.sparta.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;

    // 1. 상품 등록 (DTO 사용, 상태는 STOP 자동 저장)
    public ProductResponse createProduct(ProductSaveRequestDto requestDto) {
        Product product = requestDto.toEntity();
        Product savedProduct = productRepository.save(product);
        return new ProductResponse(savedProduct);
    }

    // 2. 전체 상품 조회
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
    }

    // 3. 단건 상품 조회
    public ProductResponse getProductById(Long id) {
        Product product = findProduct(id);
        return new ProductResponse(product);
    }

    // 4. 상품 정보 수정 (Transactional 사용)
    @Transactional
    public ProductResponse updateProduct(Long id, Product requestProduct) { // requestProduct는 DTO에서 변환된 임시 엔티티
        Product product = findProduct(id);

        String newDescription = null;
        if (requestProduct.getProductDetail() != null) {
            newDescription = requestProduct.getProductDetail().getDetailedDescription();
        }

        product.update(
                requestProduct.getName(),
                requestProduct.getPrice(),
                requestProduct.getDescription(),
                newDescription
        );

        return new ProductResponse(product);
    }

    // 5. 상품 삭제
    public void deleteProduct(Long id) {
        Product product = findProduct(id);
        productRepository.delete(product);
    }

    // 6. 상품 상세 정보 조회
    public ProductDetail getProductDetail(Long productId) {
        return productDetailRepository.findByProductId(productId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // 7. 상품과 상세 정보 동시 저장
    public void saveProductWithDetail(Product product, ProductDetail productDetail) {
        product.addDetail(productDetail);
        productRepository.save(product);
    }

    // 공통 메서드: 상품 찾기 (없으면 404 예외 발생)
    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 상품을 찾을 수 없습니다."));
    }
}