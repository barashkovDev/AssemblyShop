package ru.skyshine.db.model.assemplyShop.CompositeKeys;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.skyshine.db.model.assemplyShop.Detail;
import ru.skyshine.db.model.assemplyShop.Product;

@NoArgsConstructor
@AllArgsConstructor
public class KeyManufacturing {
    private Product codeProduct;
    private Detail codeDetail;
}
