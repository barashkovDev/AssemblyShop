package ru.skyshine.db.repository.assemplyShop;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.skyshine.db.model.assemplyShop.Detail;
import ru.skyshine.db.repository.customRep.SharedRepository;

import java.util.List;

@Repository
public interface DetailRep extends CrudRepository<Detail, Integer>, SharedRepository<Detail, Integer> {

    /**
     * @return Деталь с общим количеством на складах
     */
    @Query(value = "SELECT D.code, D.name, D.material, D.weight, D.color, D.price, D.description, sum(CW.availability_amount) AS total " +
            "FROM assembly_shop.detail AS D left join assembly_shop.components_warehouse AS CW " +
            "ON D.code = CW.code_detail " +
            "GROUP BY D.code", nativeQuery = true)
    List<Object[]> detailsInfo();
}
