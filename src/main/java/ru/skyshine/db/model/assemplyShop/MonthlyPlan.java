package ru.skyshine.db.model.assemplyShop;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.ColumnDefault;

/**
 * Таблица "Месячный план"
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = MonthlyPlan.NAME_TABLE, schema = "assembly_shop")
public class MonthlyPlan {

    public static final String NAME_TABLE = "monthly_plan";

    /**
     * Код
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code",
            nullable = false)
    private Integer code;

    /**
     * Месяц для выполнения плана
     */
    @Column(name = "month",
            nullable = false)
    @Check(constraints = "1 <= month AND month <= 12")
    private Integer month;

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
    @Check(constraints = "availability_amount >= 0")
    private Integer availability;

    /**
     * Нужное количество (по плану)
     */
    @Column(name = "need_amount",
            nullable = false)
    @Check(constraints = "need_amount >= 1")
    private Integer need;

}
