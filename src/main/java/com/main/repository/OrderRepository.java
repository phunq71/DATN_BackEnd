package com.main.repository;

import com.main.dto.OrderDTO;
import com.main.dto.OrderDetailDTO;
import com.main.dto.OrderItemDTO;
import com.main.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("""
    SELECT o.orderID
        , o.orderDate
        , o.status
        , o.costShip
    FROM Order o
    WHERE o.customer.customerId=:customerId
    AND (:status='ALL' OR o.status=:status)
    ORDER BY o.orderDate DESC
    """
    )
    List<OrderDTO> getOrdersByCustomerIdAndStatus(@Param("customerId") String customerId, @Param("status") String status);

    @Query("""
    SELECT o.orderID
        , o.orderDate
        , o.status
        , o.shippingAddress
        , o.customer.fullName
        , o.customer.phone
        , o.costShip
        , o.transaction.paymentMethod
        , o.transaction.transactionDate
    FROM Order o
    WHERE o.orderID=:orderId
    """)
    OrderDetailDTO getOrderDetailByOrderId(@Param("orderId") Integer orderId);

    @Query(

    )
    List<OrderItemDTO> getOrderItemsByOrderID(Integer orderID);


    @Query(value = """
        SELECT 
            o.OrderID,
            SUM(od.unitPrice * od.quantity) AS TotalPrice,
            SUM(
                CASE 
                    WHEN pp.PPID IS NOT NULL THEN od.unitPrice * od.quantity * (pp.discountPercent / 100.0)
                    ELSE 0
                END
            ) AS ProductDiscount,
            CASE 
                WHEN v.discountType = 1 THEN
                    (SUM(od.unitPrice * od.quantity) - 
                     SUM(CASE WHEN pp.PPID IS NOT NULL THEN od.unitPrice * od.quantity * (pp.discountPercent / 100.0) ELSE 0 END)
                    ) * (v.discountValue / 100.0)
                WHEN v.discountType = 0 THEN
                    v.discountValue
                ELSE 0
            END AS VoucherDiscount,
            SUM(od.unitPrice * od.quantity) 
                - SUM(CASE WHEN pp.PPID IS NOT NULL THEN od.unitPrice * od.quantity * (pp.discountPercent / 100.0) ELSE 0 END)
                - CASE 
                    WHEN v.discountType = 1 THEN
                        (SUM(od.unitPrice * od.quantity) - 
                         SUM(CASE WHEN pp.PPID IS NOT NULL THEN od.unitPrice * od.quantity * (pp.discountPercent / 100.0) ELSE 0 END)
                        ) * (v.discountValue / 100.0)
                    WHEN v.discountType = 0 THEN
                        v.discountValue
                    ELSE 0
                  END AS FinalPrice
        FROM Orders o
        JOIN OrderDetails od ON o.OrderID = od.OrderID
        LEFT JOIN PromotionProducts pp ON od.PPID = pp.PPID
        LEFT JOIN Vouchers v ON o.VoucherID = v.VoucherID
        WHERE o.orderID=:orderId
        GROUP BY o.OrderID, v.discountType, v.discountValue
    """, nativeQuery = true)
    Object[] getOrderPrices(@Param("orderId") Integer orderId);

    @Query(value = """
    SELECT 
        o.OrderID,
        SUM(od.unitPrice * od.quantity) AS TotalPrice,
        SUM(
            CASE 
                WHEN pp.PPID IS NOT NULL THEN od.unitPrice * od.quantity * (pp.discountPercent / 100.0)
                ELSE 0
            END
        ) AS ProductDiscount,
        CASE 
            WHEN v.discountType = 1 THEN
                (SUM(od.unitPrice * od.quantity) - 
                 SUM(CASE WHEN pp.PPID IS NOT NULL THEN od.unitPrice * od.quantity * (pp.discountPercent / 100.0) ELSE 0 END)
                ) * (v.discountValue / 100.0)
            WHEN v.discountType = 0 THEN
                v.discountValue
            ELSE 0
        END AS VoucherDiscount,
        SUM(od.unitPrice * od.quantity) 
            - SUM(CASE WHEN pp.PPID IS NOT NULL THEN od.unitPrice * od.quantity * (pp.discountPercent / 100.0) ELSE 0 END)
            - CASE 
                WHEN v.discountType = 1 THEN
                    (SUM(od.unitPrice * od.quantity) - 
                     SUM(CASE WHEN pp.PPID IS NOT NULL THEN od.unitPrice * od.quantity * (pp.discountPercent / 100.0) ELSE 0 END)
                    ) * (v.discountValue / 100.0)
                WHEN v.discountType = 0 THEN
                    v.discountValue
                ELSE 0
              END AS FinalPrice
    FROM Orders o
    JOIN OrderDetails od ON o.OrderID = od.OrderID
    LEFT JOIN PromotionProducts pp ON od.PPID = pp.PPID
    LEFT JOIN Vouchers v ON o.VoucherID = v.VoucherID
    WHERE o.CustomerID = :customerId
    GROUP BY o.OrderID, v.discountType, v.discountValue
""", nativeQuery = true)
    List<Object[]> getOrderPricesByCustomer(@Param("customerId") String customerId);

    @Query("""
    SELECT od.item.itemId,
    od.item.variant.product.productName,
    (SELECT img.imageUrl AS image FROM Image img WHERE img.variant = od.item.variant AND img.isMainImage = true),
    od.item.variant.color,
    od.item.size.code,
    od.item.variant.price,
    od.promotionProduct.discountPercent,
    od.quantity
    FROM OrderDetail od
    WHERE od.order.orderID = :orderId
    """)
    List<OrderItemDTO> getOrderItemsByOrderId(@Param("orderId") Integer orderId);

    boolean existsByOrderIDAndCustomer_CustomerId(Integer orderID, String customerCustomerId);

    boolean existsByCustomer_CustomerIdAndStatusIn(String customerId, List<String> statuses);
}


