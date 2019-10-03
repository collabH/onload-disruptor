package dev.disruptor.client.actual.multi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Order {
    private String id;
    private String name;
    private double price;
}
