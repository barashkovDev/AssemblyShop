package ru.skyshine.db.repository.assemplyShop;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.skyshine.db.model.tradingCompany.Market;
import ru.skyshine.db.repository.customRep.SharedRepository;

@Repository
public interface MarketRep extends CrudRepository<Market, Integer>, SharedRepository<Market, Integer> {
}
