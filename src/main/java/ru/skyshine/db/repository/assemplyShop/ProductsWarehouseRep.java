package ru.skyshine.db.repository.assemplyShop;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.skyshine.db.model.assemplyShop.ProductsWarehouse;
import ru.skyshine.db.repository.customRep.SharedRepository;

import java.util.List;

public interface ProductsWarehouseRep extends CrudRepository<ProductsWarehouse, Integer>, SharedRepository<ProductsWarehouse, Integer> {

    /**
     * Вернуть количество свободных ячеек на складе для изделия
     */
    @Query(value = "SELECT sum(maximum_amount) - sum(availability_amount) as maximumProduction FROM assembly_shop.product_warehouse " +
            "WHERE code_product = ?1 " +
            "GROUP BY code_product", nativeQuery = true)
    Object getMaximumWarehouseVolumeProductId(Integer product);

    /**
     * Вернуть все свободные ячейки для изделия на складе в порядке уменьшения заполненности
     */
    @Query(value = "SELECT t FROM #{#entityName} t " +
            "WHERE t.codeProduct.code = :codeProduct " +
            "ORDER BY t.availability DESC")
    List<ProductsWarehouse> getFreeWarehouseCellsByCodeProduct(@Param("codeProduct") Integer codeProduct);
}
