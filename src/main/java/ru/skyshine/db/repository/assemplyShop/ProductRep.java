package ru.skyshine.db.repository.assemplyShop;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.skyshine.db.model.assemplyShop.Product;
import ru.skyshine.db.repository.customRep.SharedRepository;

import java.util.List;

public interface ProductRep extends CrudRepository<Product, Integer>, SharedRepository<Product, Integer> {
    /**
     * @return Изделие с общим количеством на складах
     */
    @Query(value = "SELECT P.code, P.name, P.weight, P.color, P.price, P.description, sum(PW.availability_amount) AS total " +
            "FROM assembly_shop.product AS P left join assembly_shop.product_warehouse AS PW " +
            "ON P.code = PW.code_product " +
            "GROUP BY P.code", nativeQuery = true)
    List<Object[]> productsInfo();
}
