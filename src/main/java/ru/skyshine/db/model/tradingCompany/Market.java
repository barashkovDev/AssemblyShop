package ru.skyshine.db.model.tradingCompany;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skyshine.db.model.assemplyShop.Detail;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "market", schema = "trading_company")
public class Market {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",
            nullable = false)
    protected Integer id;

    @Column(name = "name",
            unique = true,
            nullable = false)
    protected String name;

    @Column(name = "owner",
            nullable = false)
    protected String owner;

    @Column(name = "adress",
            nullable = false)
    protected String adress;

    @Column(name = "email")
    protected String email;

    @Column(name = "phone_number")
    protected String phoneNumber;
}
