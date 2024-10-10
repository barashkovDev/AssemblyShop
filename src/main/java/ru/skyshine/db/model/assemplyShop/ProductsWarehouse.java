package ru.skyshine.db.model.assemplyShop;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.ColumnDefault;

/**
 * Таблица "Склад изделий"
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = ProductsWarehouse.NAME_TABLE, schema = "assembly_shop")
public class ProductsWarehouse {

    public static final String NAME_TABLE = "product_warehouse";

    /**
     * Код ячейки
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code",
            nullable = false)
    private Integer code;

    /**
     * Код изделия
     */
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE})
    @JoinColumn(name = "code_product",
            nullable = false,
            foreignKey = @ForeignKey(name = "CODE_PRODUCT"))
    private Product codeProduct;

    /**
     * Текущее количество
     */
    @Column(name = "availability_amount")
    @ColumnDefault("0")
    @Check(constraints = "0 <= availability_amount AND availability_amount <= maximum_amount")
    private Integer availability;

    /**
     * Максимальное наличие
     */
    @Column(name = "maximum_amount",
            nullable = false)
    @Check(constraints = "maximum_amount >= 0")
    private Integer maximum;

}
