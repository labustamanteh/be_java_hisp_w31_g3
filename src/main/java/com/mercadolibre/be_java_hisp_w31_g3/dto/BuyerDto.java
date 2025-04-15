package com.mercadolibre.be_java_hisp_w31_g3.dto;

import com.mercadolibre.be_java_hisp_w31_g3.model.Seller;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyerDto extends UserDto{
    List<Seller> sellerList;
}
