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
@Table(name = "content", schema = "trading_company")
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",
            nullable = false)
    private Integer id;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE})
    @JoinColumn(name = "id_market",
            nullable = false,
            foreignKey = @ForeignKey(name = "id_market"))
    Market idMarket;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE})
    @JoinColumn(name = "id_goods",
            nullable = false,
            foreignKey = @ForeignKey(name = "id_goods"))
    Goods idGoods;

    @Column(name = "quantity_goods",
            nullable = false)
    @Check(constraints = "quantity_goods >= 0 AND quantity_goods >= min_quantity")
    protected Integer quantityGoods;

    @Column(name = "min_quantity",
            nullable = false)
    @Check(constraints = "min_quantity >= 0")
    protected Integer minQuantity;

}
