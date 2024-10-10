package ru.skyshine.db.repository.assemplyShop;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.skyshine.db.model.assemplyShop.MonthlyPlan;
import ru.skyshine.db.repository.customRep.SharedRepository;

import java.util.List;

public interface MonthlyPlanRep extends CrudRepository<MonthlyPlan, Integer>, SharedRepository<MonthlyPlan, Integer> {

    /**
     * Выдать все месяцы, на которые есть план
     */
    @Query(value = "SELECT DISTINCT MP.month FROM assembly_shop.monthly_plan AS MP " +
            "ORDER BY MP.month ASC", nativeQuery = true)
    List<Object> getMonthsSincePlans();

    /**
     * Выдать все изделия, которые должны быть сделаны в переданный месяц
     */
    @Query(value = "SELECT t FROM #{#entityName} t " +
            "WHERE t.month = :month " +
            "ORDER BY t.codeProduct.code ASC")
    List<MonthlyPlan> getProductsInPlan(@Param("month") Integer month);

    /**
     * Выдать все изделия, не входящий в переданный месячный план
     */
    @Query(value = "SELECT P.code, P.name FROM assembly_shop.product AS P " +
            "WHERE P.code NOT IN (SELECT MP.code_product FROM assembly_shop.monthly_plan AS MP " +
            "WHERE MP.month = ?1)", nativeQuery = true)
    List<Object[]> getNotProductsInPlan(Integer month);

    /**
     * Удалить план
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM #{#entityName} t " +
            "WHERE t.month = :month")
    void deletePlan(@Param("month") Integer month);

    /**
     * Выдать информацию по изделию из плана по месяцу и id изделию
     */
    @Query(value = "SELECT t FROM #{#entityName} t " +
            "WHERE t.month = :month AND t.codeProduct.code = :codeProduct")
    MonthlyPlan getProductInPlan(@Param("month") Integer month, @Param("codeProduct") Integer codeProduct);
}
