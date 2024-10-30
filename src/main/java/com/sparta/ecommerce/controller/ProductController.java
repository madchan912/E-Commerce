package com.sparta.ecommerce.controller;

import com.sparta.ecommerce.entity.Product;
import com.sparta.ecommerce.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 상품을 등록합니다.
     *
     * @param product 등록할 상품 정보
     * @return 등록된 상품
     */
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    /**
     * 모든 상품을 조회합니다.
     *
     * @return 상품 목록
     */
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    /**
     * ID로 특정 상품을 조회합니다.
     *
     * @param id 조회할 상품의 ID
     * @return 조회된 상품 정보
     */
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id).orElse(null);
    }

    /**
     * 특정 상품의 정보를 업데이트합니다.
     *
     * @param id 업데이트할 상품의 ID
     * @param productDetails 업데이트할 상품 정보
     * @return 업데이트된 상품 정보
     */
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        return productService.updateProduct(id, productDetails);
    }

    /**
     * 특정 상품을 삭제합니다.
     *
     * @param id 삭제할 상품의 ID
     */
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
