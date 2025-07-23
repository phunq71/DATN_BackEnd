package com.main.repository;

import com.main.dto.OrderItemDTO;
import com.main.dto.ReturnItemDTO;
import com.main.dto.ReturnRequestDTO;
import com.main.entity.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Integer > {
    @Query("""
            SELECT r.returnRequestID
            , r.requestDate
            , r.status
            , o.orderID
            FROM ReturnRequest r
            JOIN r.order o
            JOIN o.customer c
            WHERE c.customerId=:customerID AND YEAR(r.requestDate)= :year
            """)
    List<ReturnRequestDTO> getReturnRequestByCustomerID(@Param("customerID") String customerID, int year);

    @Query("""
            SELECT r.returnRequestID
            , r.requestDate
            , r.status
            , o.orderID
            FROM ReturnRequest r
            JOIN r.order o
            JOIN o.customer c
            WHERE r.returnRequestID =:returnRequestID
            AND c.customerId=:customerID
            """)
    ReturnRequestDTO getReturnRequestByID(@Param("returnRequestID") Integer returnRequestID, @Param("customerID") String customerID);

    @Query("""
        SELECT od.orderDetailID
        , img.imageUrl
        , p.productName
        , i.size.code
        , v.color
        , od.unitPrice
        , pp.discountPercent
        , od.quantity
        , ri.reason
        , ri.returnItemId
        FROM OrderDetail od
        JOIN od.promotionProduct pp
        JOIN od.item i
        JOIN i.variant v
        JOIN v.product p
        JOIN Image img ON img.variant = v AND img.isMainImage=true
        JOIN od.returnItem ri
        JOIN ri.returnRequest rr
        WHERE rr.returnRequestID=:returnRequestID
        """)
    List<ReturnItemDTO> getReturnItemsByReturnRequestID(@Param("returnRequestID") int returnRequestID);

    @Query("""
        SELECT rimg.imageUrl
        FROM ReviewImage rimg
        WHERE rimg.returnItem.returnItemId=:returnItemID
        """)
    List<String> getImagesByReturnItemsId(@Param("returnItemID") int returnItemID);

    boolean existsByOrder_OrderID(int order_OrderID);
}
