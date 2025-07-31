package com.main.repository;

import com.main.dto.RevenueByAreaDTO;
import com.main.dto.RevenueByCategoryDTO;
import com.main.dto.RevenueByTimeDTO;
import com.main.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    //Repository for dashboard
    @Query("""
        SELECT YEAR(ts.transactionDate)
            , COUNT(o.orderID)
            , SUM(ts.amount)
        FROM Transaction ts
        JOIN ts.order o
        WHERE ts.transactionType =true
        GROUP BY YEAR(ts.transactionDate)
        ORDER BY YEAR(ts.transactionDate) ASC
        """)
    List<RevenueByTimeDTO> getRevenueByYear();

    @Query("""
    SELECT YEAR(ts.transactionDate),
           COUNT(o.orderID),
           SUM(ts.amount)
    FROM Transaction ts
    JOIN ts.order o
    JOIN ts.facility f
    JOIN f.parent p
    WHERE ts.transactionType = true
      AND p.manager.staffID = :managerId
      AND p.type = 'Z'
    GROUP BY YEAR(ts.transactionDate)
    ORDER BY YEAR(ts.transactionDate) ASC
    """)
    List<RevenueByTimeDTO> getRevenueByYear(@Param("managerId") String managerId);

    @Query("""
        SELECT MONTH(ts.transactionDate)
            , COUNT(o.orderID)
            , SUM(ts.amount)
        FROM Transaction ts
        JOIN ts.order o
        WHERE YEAR(ts.transactionDate) = :year AND ts.transactionType =true
        GROUP BY  MONTH(ts.transactionDate)
        ORDER BY MONTH(ts.transactionDate) ASC
        """)
    List<RevenueByTimeDTO> getRevenueByMonth(@Param("year") int year);

    @Query("""
        SELECT MONTH(ts.transactionDate)
            , COUNT(o.orderID)
            , SUM(ts.amount)
        FROM Transaction ts
        JOIN ts.order o
        JOIN ts.facility f
        JOIN f.parent p
        WHERE YEAR(ts.transactionDate) = :year
                AND ts.transactionType =true
                AND p.manager.staffID = :managerID
                AND p.type = 'Z'
        GROUP BY  MONTH(ts.transactionDate)
        ORDER BY MONTH(ts.transactionDate) ASC
        """)
    List<RevenueByTimeDTO> getRevenueByMonth(@Param("year") int year, @Param("managerID") String managerId);



    @Query(value = """
    SELECT YEAR(ts.transactionDate)
    FROM Transaction ts
    WHERE ts.transactionType =true
    GROUP BY YEAR(ts.transactionDate)
    ORDER BY YEAR(ts.transactionDate) DESC
    """)
    List<Integer> getAvailableYear();

    @Query(value = """
            SELECT YEAR(ts.transactionDate)
            FROM Transaction ts
            JOIN ts.facility f
            JOIN f.parent p
            WHERE ts.transactionType = true AND p.type = 'Z'
            AND p.manager.staffID = :managerID
            GROUP BY YEAR(ts.transactionDate)
            ORDER BY YEAR(ts.transactionDate) DESC
            """)
    List<Integer> getAvailableYear(@Param("managerID") String managerID);







    //----------ByAREA-----------------------------------------
    @Query("""
        SELECT MONTH(ts.transactionDate)
            ,fp.facilityName
            ,sum(ts.amount)
        FROM Transaction ts
        JOIN ts.facility f
        JOIN f.parent fp
        WHERE ts.transactionType = true AND YEAR(ts.transactionDate) = :year AND fp.type = 'Z'
        GROUP BY MONTH(ts.transactionDate), fp.facilityName
        ORDER BY MONTH(ts.transactionDate) ASC
        """)
    List<RevenueByAreaDTO> getRevenueByArea(@Param("year") int year);

    @Query("""
        SELECT YEAR(ts.transactionDate)
            ,fp.facilityName
            ,sum(ts.amount)
        FROM Transaction ts
        JOIN ts.facility f
        JOIN f.parent fp
        WHERE ts.transactionType = true AND fp.type = 'Z'
        GROUP BY YEAR(ts.transactionDate), fp.facilityName
        ORDER BY YEAR(ts.transactionDate) ASC
        """)
    List<RevenueByAreaDTO> getRevenueByArea();

    @Query("""
        SELECT c.categoryName,
               COALESCE(SUM(od.unitPrice * od.quantity), 0)
        FROM Category c
        LEFT JOIN c.products p
        LEFT JOIN p.variants v
        LEFT JOIN v.items i
        LEFT JOIN i.orderDetails od
        LEFT JOIN od.order o
        WHERE c.parent IS NOT NULL
          AND (o.orderDate IS NULL OR FUNCTION('YEAR', o.orderDate) = :year)
        GROUP BY c.categoryName
        """)
    List<RevenueByCategoryDTO> getRevenueBySubCategoryAndYear(@Param("year") int year);


    @Query("""
    SELECT DISTINCT YEAR(o.orderDate)
        FROM Order o
        WHERE o.orderDate IS NOT NULL
        ORDER BY YEAR(o.orderDate) DESC
    """)
    List<Integer> getAvailableYearCategory();

    Transaction findByOrder_OrderID(Integer orderId);

}
