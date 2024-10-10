package ru.skyshine.db.repository.assemplyShop;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.skyshine.db.model.tradingCompany.Content;
import ru.skyshine.db.repository.customRep.SharedRepository;

@Repository
public interface ContentRep extends CrudRepository<Content, Integer>, SharedRepository<Content, Integer> {

}
