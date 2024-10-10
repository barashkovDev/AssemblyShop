package ru.skyshine.db.repository.customRep;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Общий репозиторий
 * #{#entityName} - сам подставляет соотв. модель
 * @param <T>  model
 * @param <ID> primary key
 * '@Modifying' - аннотация для изменения состояния таблицы
 * Examples:
 * 1) Native SQL (прописываем куда и что сами, может подставлять ?<число> - параметр, переданный в функцию)
 *      '@Query(value = "SELECT * FROM goods WHERE name = ?1", nativeQuery = true)'
 *      List<Goods> findByName(String name);
 * 2) JPQL (подстановки присутствуют, t - это синоним, будем его использовать)
 *      '@Query(value = "SELECT t.number FROM #{#entityName} t WHERE t.name IS NULL")'
 *      Iterable<String> getColumnsFromDB();
 * 3) есть 3 способ - HQL, но там заморока, если что, дополним
 */
@NoRepositoryBean
public interface SharedRepository<T, ID> extends JpaRepository<T, ID> {

    /**
     * @param table имя таблицы в БД
     *              Example: Goods.NAME_TABLE
     * @return все колонки переданной таблицы
     */
    @Query(value = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE " +
            "`TABLE_SCHEMA`='labs' AND `TABLE_NAME`= :table ORDER BY `TABLE_NAME`,ORDINAL_POSITION", nativeQuery = true)
    List<String> getColumns(@Param("table") String table);
}
