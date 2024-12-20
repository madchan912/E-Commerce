package com.sparta.productservice.controller;

import com.sparta.productservice.entity.Product;
import com.sparta.productservice.entity.ProductDetail;
import com.sparta.productservice.service.ProductService;
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
     */
    @PostMapping
    public void createProduct(@RequestBody Product product) {
        ProductDetail productDetail = product.getProductDetail();
        productDetail.setProduct(product);
        productService.saveProductWithDetail(product, productDetail);
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

    /**
     * 상품 상세 정보 조회
     *
     * @param productId 상품 ID
     * @return 상품 상세 정보
     */
    @GetMapping("/{productId}/details")
    public ProductDetail getProductDetail(@PathVariable Long productId) {
        return productService.getProductDetail(productId);
    }
}
