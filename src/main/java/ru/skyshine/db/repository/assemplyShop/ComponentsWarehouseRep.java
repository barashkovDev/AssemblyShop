package ru.skyshine.db.repository.assemplyShop;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skyshine.db.model.assemplyShop.ComponentsWarehouse;
import ru.skyshine.db.repository.customRep.SharedRepository;

import java.util.List;

@Repository
public interface ComponentsWarehouseRep extends CrudRepository<ComponentsWarehouse, Integer>, SharedRepository<ComponentsWarehouse, Integer> {

    /**
     * Выдать все ячейки на складе для переданной детали по наим. заполненности
     *
     * @param codeDetail code детали
     * @return
     */
    @Query(value = "SELECT t FROM #{#entityName} t " +
            "WHERE t.codeDetail.code = :codeDetail " +
            "ORDER BY t.availability ASC ")
    List<ComponentsWarehouse> getComponentsWarehouseForDetail(@Param("codeDetail") Integer codeDetail);
}

