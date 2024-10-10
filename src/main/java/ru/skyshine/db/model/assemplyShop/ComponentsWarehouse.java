package ru.skyshine.db.model.assemplyShop;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.ColumnDefault;

/**
 * Таблица "Склад комплектующих"
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = ComponentsWarehouse.NAME_TABLE, schema = "assembly_shop")
public class ComponentsWarehouse {

    public static final String NAME_TABLE = "components_warehouse";

    /**
     * Код ячейки
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code",
            nullable = false)
    private Integer code;

    /**
     * Код детали
     */
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE})
    @JoinColumn(name = "code_detail",
            nullable = false,
            foreignKey = @ForeignKey(name = "CODE_DETAIL"))
    private Detail codeDetail;

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