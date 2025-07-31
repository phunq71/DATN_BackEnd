package com.main.repository;

import com.main.dto.RevenueByAreaDTO;
import com.main.dto.RevenueByCategoryDTO;
import com.main.dto.RevenueByShopDTO;
import com.main.dto.RevenueByTimeDTO;
import com.main.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    //Repository for dashboard
    @Query("""
        SELECT YEAR(o.orderDate)
            , COUNT(o.orderID)
            , SUM(ts.amount)
        FROM Transaction ts
        JOIN ts.order o
        WHERE ts.transactionType =true
        GROUP BY YEAR(o.orderDate)
        ORDER BY YEAR(o.orderDate) ASC
        """)
    List<RevenueByTimeDTO> getRevenueByYear();

    @Query("""
    SELECT YEAR(o.orderDate),
           COUNT(o.orderID),
           SUM(ts.amount)
    FROM Transaction ts
    JOIN ts.order o
    JOIN ts.facility f
    LEFT JOIN f.parent parentOfF
    LEFT JOIN parentOfF.parent parentOfParent
    WHERE ts.transactionType = true
      AND (
          (f.type = 'S' AND parentOfF.type = 'Z' AND parentOfF.manager.staffID = :managerId)
          OR
          (f.type = 'K' AND parentOfF.type = 'S' AND parentOfParent.type = 'Z' AND parentOfParent.manager.staffID = :managerId)
      )
    GROUP BY YEAR(o.orderDate)
    ORDER BY YEAR(o.orderDate) ASC
    """)
    List<RevenueByTimeDTO> getRevenueByYear(@Param("managerId") String managerId);

    @Query("""
        SELECT MONTH(o.orderDate)
            , COUNT(o.orderID)
            , SUM(ts.amount)
        FROM Transaction ts
        JOIN ts.order o
        WHERE YEAR(o.orderDate) = :year AND ts.transactionType =true
        GROUP BY  MONTH(o.orderDate)
        ORDER BY MONTH(o.orderDate) ASC
        """)
    List<RevenueByTimeDTO> getRevenueByMonth(@Param("year") int year);

    @Query("""
        SELECT MONTH(o.orderDate)
            , COUNT(o.orderID)
            , SUM(ts.amount)
        FROM Transaction ts
        JOIN ts.order o
        JOIN ts.facility f
        JOIN f.parent p
        LEFT JOIN f.parent parentOfF
        LEFT JOIN parentOfF.parent parentOfParent
            WHERE ts.transactionType = true
                AND (
                        (f.type = 'S' AND parentOfF.type = 'Z' AND parentOfF.manager.staffID = :managerId)
                        OR
                        (f.type = 'K' AND parentOfF.type = 'S' AND parentOfParent.type = 'Z' AND parentOfParent.manager.staffID = :managerId)
                    )
        GROUP BY  MONTH(o.orderDate)
        ORDER BY MONTH(o.orderDate) ASC
        """)
    List<RevenueByTimeDTO> getRevenueByMonth(@Param("year") int year, @Param("managerId") String managerId);



    @Query(value = """
    SELECT YEAR(o.orderDate)
    FROM Transaction ts
    JOIN ts.order o
    WHERE ts.transactionType =true
    GROUP BY YEAR(o.orderDate)
    ORDER BY YEAR(o.orderDate) DESC
    """)
    List<Integer> getAvailableYear();

    @Query(value = """
            SELECT YEAR(o.orderDate)
            FROM Transaction ts
            JOIN ts.order o
            JOIN ts.facility f
            JOIN f.parent p
            LEFT JOIN f.parent parentOfF
            LEFT JOIN parentOfF.parent parentOfParent
            WHERE ts.transactionType = true
                AND (
                    (f.type = 'S' AND parentOfF.type = 'Z' AND parentOfF.manager.staffID = :managerId)
                    OR
                    (f.type = 'K' AND parentOfF.type = 'S' AND parentOfParent.type = 'Z' AND parentOfParent.manager.staffID = :managerId)
                    )
            GROUP BY YEAR(o.orderDate)
            ORDER BY YEAR(o.orderDate) DESC
    """)
    List<Integer> getAvailableYear(@Param("managerId") String managerId);







    //----------ByAREA-----------------------------------------
    @Query("""
        SELECT MONTH(o.orderDate)
            ,z.facilityName
            ,sum(ts.amount)
        FROM Transaction ts
        JOIN ts.order o
        JOIN ts.facility f
        JOIN Facility z ON (
            (f.type = 'S' AND f.parent = z) OR
            (f.type = 'K' AND f.parent.parent = z)
        )
        WHERE ts.transactionType = true 
        AND YEAR(o.orderDate) = :year 
        AND z.type = 'Z'
        GROUP BY MONTH(o.orderDate), z.facilityName
        ORDER BY MONTH(o.orderDate) ASC
        """)
    List<RevenueByAreaDTO> getRevenueByArea(@Param("year") int year);

    @Query("""
    SELECT YEAR(o.orderDate),
           z.facilityName,
           SUM(ts.amount)
    FROM Transaction ts
    JOIN ts.order o
    JOIN ts.facility f
    JOIN Facility z ON (
        (f.type = 'S' AND f.parent = z) OR
        (f.type = 'K' AND f.parent.parent = z)
    )
    WHERE ts.transactionType = true
      AND z.type = 'Z'
    GROUP BY YEAR(o.orderDate), z.facilityName
    ORDER BY YEAR(o.orderDate) ASC
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
          AND (o.orderDate IS NULL OR YEAR(o.orderDate) = :year)
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


    //-----------------------ByShop---------------------------------





    Transaction findByOrder_OrderID(Integer orderId);



    @Query("""
    SELECT
        z.facilityName,
        CASE 
            WHEN f.type = 'K' THEN fp.facilityName
            ELSE f.facilityName
        END,
        SUM(ts.amount)
    FROM Transaction ts
    JOIN ts.order o
    JOIN ts.facility f
    LEFT JOIN f.parent fp
    JOIN Facility z ON (
        (f.type = 'S' AND f.parent = z) OR
        (f.type = 'K' AND fp.parent = z)
    )
    WHERE ts.transactionType = true
      AND z.type = 'Z'
      AND YEAR(o.orderDate) = :year
      AND (:month IS NULL OR MONTH(o.orderDate) = :month)
    GROUP BY 
        z.facilityName,
        CASE 
            WHEN f.type = 'K' THEN fp.facilityName
            ELSE f.facilityName
        END
""")
    List<RevenueByShopDTO> getRevenueByShop(@Param("year") int year,
                                            @Param("month") Integer month);

}
