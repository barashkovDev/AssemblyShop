package ru.skyshine.db.model.tradingCompany;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "storage", schema = "trading_company")
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",
            nullable = false)
    protected Integer id;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE})
    @JoinColumn(name = "id_goods",
            nullable = false,
            foreignKey = @ForeignKey(name = "id_goods"))
    protected Goods idGoods;

    @Column(name = "quantity",
            nullable = false)
    @Check(constraints = "quantity >= 0 AND quantity <= max_quantity")
    protected Integer quantity;

    @Column(name = "max_quantity",
            nullable = false)
    @Check(constraints = "max_quantity >= 0")
    protected Integer maxQuantity;
}
