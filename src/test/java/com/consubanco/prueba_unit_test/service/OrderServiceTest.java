package com.consubanco.prueba_unit_test.service;

import com.consubanco.prueba_unit_test.entity.Order;
import com.consubanco.prueba_unit_test.repository.OrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private OrderService orderService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPlaceOrder_Success() {
        Order order = new Order();
        order.setAmount(100.0);
        when(paymentService.processPayment(order.getAmount())).thenReturn(true);

        boolean result = orderService.placeOrder(order);

        assertTrue(result);
        verify(orderRepository).save(order);
    }

    @Test
    public void testPlaceOrder_Failure() {
        Order order = new Order();
        order.setAmount(100.0);
        when(paymentService.processPayment(order.getAmount())).thenReturn(false);

        boolean result = orderService.placeOrder(order);

        assertFalse(result);
        verify(orderRepository, never()).save(order);
    }

    @Test
    public void testGetOrderById_Found() {
        Order order = new Order();
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(1);

        assertNotNull(result);
        assertEquals(order, result);
    }

    @Test
    public void testGetOrderById_NotFound() {
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        Order result = orderService.getOrderById(1);

        assertNull(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCancelOrder_NotFound() {
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        orderService.cancelOrder(1);
    }

    @Test
    public void testCancelOrder_Found() {
        Order order = new Order();
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1);

        verify(orderRepository).delete(order);
    }

    @Test
    public void testListAllOrders() {
        Order order1 = new Order();
        Order order2 = new Order();
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));

        List<Order> orders = orderService.listAllOrders();

        assertEquals(2, orders.size());
        assertTrue(orders.contains(order1));
        assertTrue(orders.contains(order2));
    }
}

