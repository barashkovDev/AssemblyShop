package ru.skyshine.db.model.assemplyShop;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Таблица "Изделие"
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = Product.NAME_TABLE, schema = "assembly_shop")
public class Product {

    public static final String NAME_TABLE = "product";

    /**
     * Код
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code",
            nullable = false)
    private Integer code;

    /**
     * Наименование
     */
    @Column(name = "name",
            unique = true,
            nullable = false)
    private String name;

    /**
     * Вес
     */
    @Column(name = "weight",
            columnDefinition = "DECIMAL(9,2)")
    private Double weight;

    /**
     * Цвет
     */
    @Column(name = "color")
    private String color;

    /**
     * Цена
     */
    @Column(name = "price",
            nullable = false,
            columnDefinition = "DECIMAL(9,2)")
    private Double price;

    /**
     * Описание
     */
    @Column(name = "description")
    private String description;

}
