package ru.skyshine.db.model.tradingCompany;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "goods", schema = "trading_company")
public class Goods {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",
            nullable = false)
    private Integer id;

    @Column(name = "name",
            nullable = false)
    protected String name;

    @Column(name = "cost",
            columnDefinition = "DECIMAL(9,2)",
            nullable = false)
    private Double cost;

    @Column(name = "description")
    private String description;
}
