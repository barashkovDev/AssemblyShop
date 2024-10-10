package ru.skyshine.db.model.assemplyShop;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import ru.skyshine.db.model.assemplyShop.CompositeKeys.KeyManufacturing;

/**
 * Таблица "Изготовление"
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(KeyManufacturing.class)
@Table(name = Manufacturing.NAME_TABLE, schema = "assembly_shop")
public class Manufacturing {

    public static final String NAME_TABLE = "manufacturing";

    /**
     * Код изделия
     */
    @Id
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE})
    @JoinColumn(name = "code_product",
            nullable = false,
            foreignKey = @ForeignKey(name = "CODE_PRODUCT"))
    private Product codeProduct;

    /**
     * Код детали
     */
    @Id
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE})
    @JoinColumn(name = "code_detail",
            nullable = false,
            foreignKey = @ForeignKey(name = "CODE_DETAIL"))
    private Detail codeDetail;

    /**
     * Количество
     */
    @Column(name = "need_amount",
            nullable = false)
    @Check(constraints = "need_amount > 0")
    private Integer need;

}
