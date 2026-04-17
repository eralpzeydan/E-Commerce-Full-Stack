package com.eralp.ecommerce.service;

import com.eralp.ecommerce.dto.cart.AddCartItemRequest;
import com.eralp.ecommerce.dto.cart.CartResponse;
import com.eralp.ecommerce.dto.cart.UpdateCartItemQuantityRequest;
import com.eralp.ecommerce.entity.Cart;
import com.eralp.ecommerce.entity.CartItem;
import com.eralp.ecommerce.entity.Product;
import com.eralp.ecommerce.entity.User;
import com.eralp.ecommerce.exception.ResourceNotFoundException;
import com.eralp.ecommerce.repository.CartItemRepository;
import com.eralp.ecommerce.repository.CartRepository;
import com.eralp.ecommerce.repository.ProductRepository;
import com.eralp.ecommerce.repository.UserRepository;
import com.eralp.ecommerce.service.impl.CartServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    void shouldIncreaseQuantityWhenProductAlreadyExistsInCart() {
        User user = buildUser(1L);
        Cart cart = buildCart(10L, user);
        Product product = buildProduct(100L, "Mouse", new BigDecimal("50.00"));

        CartItem existingCartItem = new CartItem();
        existingCartItem.setId(1000L);
        existingCartItem.setCart(cart);
        existingCartItem.setProduct(product);
        existingCartItem.setQuantity(1);
        existingCartItem.setUnitPrice(product.getPrice());

        AddCartItemRequest request = new AddCartItemRequest();
        request.setProductId(product.getId());
        request.setQuantity(2);

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
                .thenReturn(Optional.of(existingCartItem));
        when(cartItemRepository.findAllByCartId(cart.getId())).thenReturn(List.of(existingCartItem));

        CartResponse response = cartService.addItemToCart(user.getId(), request);

        assertThat(existingCartItem.getQuantity()).isEqualTo(3);
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getTotal()).isEqualByComparingTo("150.00");
    }

    @Test
    void shouldRemoveCartItemWhenUpdatedQuantityIsZero() {
        User user = buildUser(1L);
        Cart cart = buildCart(11L, user);
        Product product = buildProduct(101L, "Keyboard", new BigDecimal("120.00"));

        CartItem cartItem = new CartItem();
        cartItem.setId(2000L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        cartItem.setUnitPrice(product.getPrice());

        UpdateCartItemQuantityRequest request = new UpdateCartItemQuantityRequest();
        request.setQuantity(0);

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByIdAndCartId(cartItem.getId(), cart.getId())).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.findAllByCartId(cart.getId())).thenReturn(List.of());

        CartResponse response = cartService.updateCartItemQuantity(user.getId(), cartItem.getId(), request);

        verify(cartItemRepository).delete(cartItem);
        verify(cartItemRepository).flush();
        assertThat(response.getItems()).isEmpty();
        assertThat(response.getTotal()).isEqualByComparingTo("0");
    }

    @Test
    void shouldThrowWhenProductDoesNotExist() {
        User user = buildUser(1L);
        Cart cart = buildCart(12L, user);

        AddCartItemRequest request = new AddCartItemRequest();
        request.setProductId(999L);
        request.setQuantity(1);

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addItemToCart(user.getId(), request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product not found with id: 999");
    }

    private User buildUser(Long userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }

    private Cart buildCart(Long cartId, User user) {
        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setUser(user);
        return cart;
    }

    private Product buildProduct(Long productId, String name, BigDecimal price) {
        Product product = new Product();
        product.setId(productId);
        product.setName(name);
        product.setPrice(price);
        return product;
    }
}
