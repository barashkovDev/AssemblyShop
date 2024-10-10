package ru.skyshine.db.repository.assemplyShop;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.skyshine.db.model.tradingCompany.Goods;
import ru.skyshine.db.repository.customRep.SharedRepository;

import java.util.List;

@Repository
public interface GoodsRep extends CrudRepository<Goods, Integer>, SharedRepository<Goods, Integer> {

    @Query(value = "SELECT G.id, G.name, G.cost, G.description, sum(S.quantity) AS total " +
            "FROM trading_company.goods AS G left join trading_company.storage AS S " +
            "ON G.id = S.id_goods " +
            "GROUP BY G.id", nativeQuery = true)
    List<Object[]> goodsInfo();

    @Query(value = "SELECT G.id, G.name, G.cost, G.description, sum(S.max_quantity) AS total " +
            "FROM trading_company.goods AS G left join trading_company.storage AS S " +
            "ON G.id = S.id_goods " +
            "GROUP BY G.id", nativeQuery = true)
    List<Object[]> goodsMaxCapacity();
}
