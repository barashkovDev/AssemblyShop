package ru.skyshine.db.repository.assemplyShop;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.skyshine.db.model.assemplyShop.CompositeKeys.KeyManufacturing;
import ru.skyshine.db.model.assemplyShop.Manufacturing;
import ru.skyshine.db.model.assemplyShop.Product;
import ru.skyshine.db.repository.customRep.SharedRepository;

import java.util.List;

public interface ManufacturingRep extends CrudRepository<Manufacturing, KeyManufacturing>, SharedRepository<Manufacturing, KeyManufacturing> {
    /**
     * Вернуть по изделию схему из таблицы "Изготовление"
     *
     * @param codeProduct номер изделия
     */
    @Query("SELECT t FROM #{#entityName} t " +
            "WHERE t.codeProduct = :codeProduct")
    List<Manufacturing> getAllByProduct(@Param("codeProduct") Product codeProduct);

    /**
     * Вернуть номер и наименование деталей, которые не используются в схеме переданного изделия
     *
     * @param codeProduct номер изделия
     */
    @Query(value = "SELECT D.code, D.name FROM assembly_shop.detail AS D " +
            "WHERE D.code NOT IN " +
            "(SELECT M.code_detail " +
            "FROM assembly_shop.manufacturing AS M " +
            "WHERE M.code_product = ?1) " +
            "ORDER BY D.code ASC", nativeQuery = true)
    List<Object[]> getAvailableAddDetail(Integer codeProduct);

    /**
     * Вернуть код, наименование, текущее и максимальное количество изделия
     */
    @Query(value = "SELECT P.code, P.name, SUM(PW.availability_amount) AS current, SUM(PW.maximum_amount) AS max, " +
            "SUM(PW.maximum_amount) - SUM(PW.availability_amount) AS remainder FROM assembly_shop.product AS P " +
            "JOIN assembly_shop.product_warehouse AS PW ON P.code = PW.code_product " +
            "WHERE P.code IN (SELECT M.code_product FROM assembly_shop.manufacturing AS M) " +
            "GROUP BY P.code, P.name " +
            "ORDER BY P.code ASC", nativeQuery = true)
    List<Object[]> getProduction();

    /**
     * Вернуть список необходимых деталей
     * код детали, наименование детали, необходимое количество, текущее количество (нет на складе - 0)
     * по переданной схеме изготовления
     */
    @Query(value = "SELECT D.code, D.name, M.need_amount AS need, COALESCE(SUM(CW.availability_amount), 0) AS inStock " +
            "FROM assembly_shop.manufacturing AS M " +
            "JOIN assembly_shop.detail AS D ON M.code_detail = D.code " +
            "LEFT JOIN assembly_shop.components_warehouse AS CW ON D.code = CW.code_detail " +
            "WHERE M.code_product = ?1 " +
            "GROUP BY D.code, D.name, M.need_amount " +
            "ORDER BY D.code ASC", nativeQuery = true)
    List<Object[]> getProduceInfoByProductId(Integer scheme);

    /**
     * Вернуть список необходимых деталей и их количество для переданного изделия
     */
    @Query(value = "SELECT M.code_detail, M.need_amount FROM assembly_shop.manufacturing AS M " +
            "WHERE M.code_product = ?1", nativeQuery = true)
    List<Object[]> getDetailAndAmountForProductId(Integer codeProduct);
}
