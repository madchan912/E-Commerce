package com.sparta.productservice.controller;

import com.sparta.productservice.dto.ProductDetailResponse;
import com.sparta.productservice.dto.ProductResponse;
import com.sparta.productservice.dto.ProductSaveRequestDto;
import com.sparta.productservice.entity.ProductDetail;
import com.sparta.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 등록
     */
    @PostMapping
    public ProductResponse createProduct(@RequestBody @Valid ProductSaveRequestDto requestDto) {
        return productService.createProduct(requestDto);
    }

    /**
     * 전체 상품 조회
     */
    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    /**
     * 단건 상품 조회
     */
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    /**
     * 상품 상세 정보 조회
     */
    @GetMapping("/{productId}/details")
    public ProductDetailResponse getProductDetail(@PathVariable Long productId) {
        ProductDetail detail = productService.getProductDetail(productId);
        return new ProductDetailResponse(detail);
    }

    /**
     * 상품 정보 수정
     */
    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable Long id, @RequestBody ProductSaveRequestDto requestDto) {
        return productService.updateProduct(id, requestDto.toEntity());
    }

    /**
     * 상품 삭제
     */
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
