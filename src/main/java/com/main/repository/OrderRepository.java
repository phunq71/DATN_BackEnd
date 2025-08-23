package com.main.repository;

import com.main.dto.*;
import com.main.entity.Facility;
import com.main.entity.Order;
import com.main.entity.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("""
    SELECT o.orderID
        , o.orderDate
        , o.status
        , o.costShip
        , o.shippingCode
    FROM Order o
    WHERE o.customer.customerId=:customerId
    AND (:status='ALL' OR o.status=:status)
    AND YEAR(o.orderDate) =:year
    ORDER BY o.orderDate DESC
    """
    )
    List<OrderDTO> getOrdersByCustomerIdAndStatus(@Param("customerId") String customerId, @Param("status") String status, @Param("year") Integer year);

    @Query("""
    SELECT DISTINCT o.orderID
        , o.orderDate
        , o.status
        , o.costShip
    FROM Order o
    LEFT JOIN o.orderDetails od
    LEFT JOIN od.item i
    LEFT JOIN i.variant v
    LEFT JOIN v.product p
    WHERE o.customer.customerId = :customerId
    AND (:keyword IS NULL OR :keyword = ''
         OR p.productName LIKE %:keyword%)
    ORDER BY o.orderDate DESC
    """
    )
    List<OrderDTO> getOrdersByCustomerIdAndKeywords(@Param("customerId") String customerId, @Param("keyword") String keyword);

    @Query("""
    SELECT DISTINCT o.orderID
        , o.orderDate
        , o.status
        , o.costShip
    FROM Order o
    LEFT JOIN o.orderDetails od
    LEFT JOIN od.item i
    LEFT JOIN i.variant v
    LEFT JOIN v.product p
    WHERE o.customer.customerId = :customerId
    AND (:keyword IS NULL OR :keyword = ''
         OR CAST(o.orderID AS string) LIKE %:keyword%
         )
    ORDER BY o.orderDate DESC
    """
    )
    List<OrderDTO> getOrdersByCustomerIdAndOrderID(@Param("customerId") String customerId, @Param("keyword") String keyword);

    @Query("""
    SELECT DISTINCT YEAR(o.orderDate)
    FROM Order o
    WHERE o.customer.customerId = :customerId
    ORDER BY YEAR(o.orderDate) DESC
    """)
    List<Integer> getOrderYearByCustomerId(@Param("customerId") String customerId);

    @Query("""
    SELECT o.orderID
        , o.orderDate
        , o.status
        , o.shippingAddress
        , o.customer.fullName
        , o.customer.phone
        , o.costShip
        , o.discountCost
        , o.transaction.paymentMethod
        , t.transactionDate
        , o.updateStatusAt
        , o.shippingCode
        , o.delivery
    FROM Order o
        LEFT JOIN o.transaction t ON t.status = 'DaThanhToan'
    WHERE o.orderID=:orderId
    """)
    OrderDetailDTO getOrderDetailByOrderId(@Param("orderId") Integer orderId);


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
                WHEN v.discountType = 'Percent' THEN
                    (SUM(od.unitPrice * od.quantity) - 
                     SUM(CASE WHEN pp.PPID IS NOT NULL THEN od.unitPrice * od.quantity * (pp.discountPercent / 100.0) ELSE 0 END)
                    ) * (v.discountValue / 100.0)
                WHEN v.discountType = 'Amount' THEN
                    v.discountValue
                ELSE 0
            END AS VoucherDiscount,
            SUM(od.unitPrice * od.quantity) 
                - SUM(CASE WHEN pp.PPID IS NOT NULL THEN od.unitPrice * od.quantity * (pp.discountPercent / 100.0) ELSE 0 END)
                - CASE 
                    WHEN v.discountType = 'Percent' THEN
                        (SUM(od.unitPrice * od.quantity) - 
                         SUM(CASE WHEN pp.PPID IS NOT NULL THEN od.unitPrice * od.quantity * (pp.discountPercent / 100.0) ELSE 0 END)
                        ) * (v.discountValue / 100.0)
                    WHEN v.discountType = 'Amount' THEN
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
            WHEN v.discountType = 'Percent' THEN
                (SUM(od.unitPrice * od.quantity) - 
                 SUM(CASE WHEN pp.PPID IS NOT NULL THEN od.unitPrice * od.quantity * (pp.discountPercent / 100.0) ELSE 0 END)
                ) * (v.discountValue / 100.0)
            WHEN v.discountType = 'Amount' THEN
                v.discountValue
            ELSE 0
        END AS VoucherDiscount,
        SUM(od.unitPrice * od.quantity) 
            - SUM(CASE WHEN pp.PPID IS NOT NULL THEN od.unitPrice * od.quantity * (pp.discountPercent / 100.0) ELSE 0 END)
            - CASE 
                WHEN v.discountType = 'Percent' THEN
                    (SUM(od.unitPrice * od.quantity) - 
                     SUM(CASE WHEN pp.PPID IS NOT NULL THEN od.unitPrice * od.quantity * (pp.discountPercent / 100.0) ELSE 0 END)
                    ) * (v.discountValue / 100.0)
                WHEN v.discountType = 'Amount' THEN
                    v.discountValue
                ELSE 0
              END AS FinalPrice
    FROM Orders o
    JOIN OrderDetails od ON o.OrderID = od.OrderID
    LEFT JOIN PromotionProducts pp ON od.PPID = pp.PPID
    LEFT JOIN Vouchers v ON o.VoucherID = v.VoucherID
    WHERE o.CustomerID = :customerId
    AND (:status = 'ALL' OR :status = o.status)
    AND YEAR(o.OrderDate) = :year
    GROUP BY o.OrderID, v.discountType, v.discountValue
""", nativeQuery = true)
    List<Object[]> getOrderPricesByCustomer(@Param("customerId") String customerId, @Param("status") String status, @Param("year") Integer year);

    @Query("""
    SELECT od.item.itemId,
           od.item.variant.product.productName,
           (SELECT img.imageUrl FROM Image img WHERE img.variant = od.item.variant AND img.isMainImage = true),
           od.item.variant.color,
           od.item.size.code,
           od.unitPrice,
           COALESCE(pp.discountPercent, 0),
           od.quantity,
           CASE WHEN EXISTS (SELECT 1 FROM Review r WHERE r.orderDetail = od) THEN true ELSE false END,
           od.orderDetailID
    FROM OrderDetail od
    LEFT JOIN od.promotionProduct pp
    WHERE od.order.orderID = :orderId
""")
    List<OrderItemDTO> getOrderItemsByOrderId(@Param("orderId") Integer orderId);


    boolean existsByOrderIDAndCustomer_CustomerId(Integer orderID, String customerCustomerId);

    Optional<Order> findByOrderIDAndCustomer_CustomerId(Integer orderID, String customerCustomerId);

    boolean existsByCustomer_CustomerIdAndStatusIn(String customerId, List<String> statuses);


    List<String> getShippingAddressByCustomer_CustomerId(String customerId);

    List<String> getAddressIdGHNByCustomer_CustomerId(String customerId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status IN ('ChoXacNhan', 'ChuanBiDon', 'SanSangGiao') and o.facility.facilityId = :facilityId")
    long countProcessingOrders(String facilityId);

    Order findByOrderID(Integer orderID);
    @Modifying
    @Query("UPDATE Order o SET o.status = :status WHERE o.orderID = :orderID")
    void updateOrderStatus(@Param("status   ") String status, @Param("orderID") int orderID);

    @Query("""
    SELECT o.orderID,
           o.orderDate,
           o.status,
           o.updateStatusAt,
           o.shippingAddress,
           o.note,
           o.isOnline,
           o.shipMethod,
           c.fullName,
           s.fullname,
           f.facilityName,
           t.transactionDate,
           t.amount
         FROM Order o
            LEFT JOIN o.customer c
            LEFT JOIN o.staff s
            LEFT JOIN o.facility f
            LEFT JOIN o.transaction t
            WHERE o.orderDate = :orderDate
            AND o.status = :status
    """)
    public Page<OrdManagement_OrderDTO> getOrdersWithOrderDate(Pageable pageable
            , @Param("orderDate") LocalDateTime orderDate
            , @Param("status") String status);

    @Query("""
    SELECT  o.orderID, o.orderDate, o.status, o.updateStatusAt, o.shippingAddress,
           o.note, o.isOnline, o.shipMethod, o.addressIdGHN, c.fullName, s.fullname, f.facilityName,
           CASE WHEN t.status = 'DaThanhToan' THEN t.transactionDate ELSE NULL END, t.amount, t.paymentMethod, t.paymentCode, c.phone, pr.addressIdGHN, o.shippingCode, o.delivery
    FROM Order o
    LEFT JOIN o.customer c
    LEFT JOIN o.staff s
    LEFT JOIN o.facility f
    LEFT JOIN o.transaction t
    LEFT JOIN f.parent pr
    LEFT JOIN pr.parent pr2
    WHERE (:startDate IS NULL OR o.orderDate >= :startDate)
      AND (:endDate IS NULL OR o.orderDate < :endDate)
      AND (:status IS NULL OR o.status = :status)
   
      AND (
           (:facilityId IS NOT NULL AND f.facilityId = :facilityId OR f.parent.facilityId = :facilityId)
        OR (:facilityId IS NULL AND :parentId IS NOT NULL AND
            (pr.facilityId = :parentId OR pr2.facilityId = :parentId))
        OR (:facilityId IS NULL AND :parentId IS NULL)
      )
      AND (:orderId IS NULL OR o.orderID = :orderId)
          ORDER BY o.orderDate DESC
    """)
    Page<OrdManagement_OrderDTO> getOrders(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") String status,
            @Param("facilityId") String facilityId,
            @Param("parentId") String parentId,
            @Param("orderId") Integer orderId,
            Pageable pageable
    );


    @Query("""
    SELECT p.productName, img.imageUrl, pp.discountPercent,
           odt.unitPrice, odt.quantity, o.costShip, o.discountCost,
           vc.discountType, vc.discountValue
    FROM Item i
        JOIN i.variant v
        JOIN v.product p
        JOIN i.orderDetails odt
        LEFT JOIN odt.promotionProduct pp
        JOIN odt.order o
        LEFT JOIN o.voucher vc
        JOIN Image img on img.variant = v AND img.isMainImage = true
    WHERE o.orderID = :orderID
    """)
    public List<OrdManagement_ProductDTO> getProductsByOrderID(@Param("orderID") Integer orderID);

    @Query("""
    SELECT new com.main.dto.OrderDetailDTO(o.orderID, o.shippingCode, o.status)
    FROM Order o
    WHERE o.shippingCode IS NOT NULL
      AND o.status <> 'DaGiao'
""")
    List<OrderDetailDTO> getAllOrderIdShippingCodes();


    @Query(""" 
    SELECT o.orderID, o.shippingCode, o.status
        FROM Order o
            WHERE o.shippingCode = :shippingCode
    """)
    public OrderDetailDTO getOrderByShippingCodes(@Param("shippingCode") String shippingCode);

    boolean existsByFacilityAndStatusNot(Facility facility, String status);

    @Query("""
    SELECT o.orderID
        , o.orderDate
        , o.status
        , o.shippingAddress
        , o.shippingCode
        , o.isOnline
        , o.staff.staffID
        , o.voucher.voucherID
        , o.facility.facilityId
        , o.transaction.amount
    FROM Order o
    WHERE o.customer.customerId=:customerId
    ORDER BY o.orderDate DESC
    """
    )
    public Page<CusManagement_orderDTO> getOrdersByCustomerId(@Param("customerId") String customerId, Pageable pageable);

    @Query("""
    SELECT o
    FROM Order o
    WHERE o.customer.customerId=:customerId
    """)
    List<Order> getOrdersByCusID(@Param("customerId") String customerId);

}


