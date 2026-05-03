package com.java.springmvc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.java.springmvc.domain.Cart;
import com.java.springmvc.domain.CartDetail;
import com.java.springmvc.domain.Product;
import com.java.springmvc.domain.User;
import com.java.springmvc.repository.CartDetailRepository;
import com.java.springmvc.repository.CartRepository;
import com.java.springmvc.repository.ProductRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UploadFileService uploadFileService;
    private final UserService userService;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;

    public ProductService(ProductRepository productRepository,
            UploadFileService uploadFileService,
            UserService userService,
            CartRepository cartRepository,
            CartDetailRepository cartDetailRepository) {
        this.productRepository = productRepository;
        this.uploadFileService = uploadFileService;
        this.userService = userService;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
    }

    public List<Product> handleGetAllProduct() {
        return this.productRepository.findAll();
    }

    public Product handleCreateProduct(Product product) {
        return this.productRepository.save(product);
    }

    public Product handleGetProductById(Long id) {
        Optional<Product> optionalProduct = this.productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            return optionalProduct.get();
        }
        return null;
    }

    public Product handleUpdateProductById(Long id, Product productUpdate, MultipartFile file) {
        Product updateProductById = this.handleGetProductById(id);
        updateProductById.setName(productUpdate.getName());
        updateProductById.setPrice(productUpdate.getPrice());
        updateProductById.setDetailDesc(productUpdate.getDetailDesc());
        updateProductById.setShortDesc(productUpdate.getShortDesc());
        updateProductById.setQuantity(productUpdate.getQuantity());
        updateProductById.setFactory(productUpdate.getFactory());
        updateProductById.setTarget(productUpdate.getTarget());

        String fileName = file.getOriginalFilename();
        if (fileName != null && !fileName.equals("")) {
            String updateFileName = this.uploadFileService.handleStorefile(file, "product");
            if (!updateFileName.equals("")) {
                updateProductById.setImage(updateFileName);
            }
        }
        return this.productRepository.save(updateProductById);
    }

    public void handleDeleteProductById(Long id) {
        Product deleteProductById = this.handleGetProductById(id);
        if (deleteProductById != null) {
            this.productRepository.deleteById(id);
        }
    }

    public void handleAddProductToCart(String email, Long productId, HttpSession session) {
        User user = this.userService.getUserByUserEmail(email);

        if (user != null) {
            // nếu người dùng tồn tại thì tạo cart
            Cart cart = this.cartRepository.findByUser(user);
            // nếu người dùng chưa có cart thì tạo mới
            if (cart == null) {
                Cart newCart = new Cart();
                newCart.setUser(user);
                newCart.setSum(0L);
                cart = this.cartRepository.save(newCart);
            }

            // Nếu đã có cart tiến hành lưu
            Product product = this.handleGetProductById(productId);
            CartDetail cartDetail = this.cartDetailRepository.findByCartAndProduct(cart, product);

            if (cartDetail == null) {
                CartDetail newCartDetail = new CartDetail();
                newCartDetail.setCart(cart);
                newCartDetail.setProduct(product);
                newCartDetail.setPrice(product.getPrice());
                newCartDetail.setQuantity(1L);
                this.cartDetailRepository.save(newCartDetail);

                long sum = cart.getSum() + 1;
                cart.setSum(sum);
                this.cartRepository.save(cart);
                session.setAttribute("sum", sum);
            } else {
                cartDetail.setQuantity(cartDetail.getQuantity() + 1);
                this.cartDetailRepository.save(cartDetail);
            }
        }
    }
}
