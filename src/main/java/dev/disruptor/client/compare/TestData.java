package dev.disruptor.client.compare;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
class TestData {
    private long count;
    private String name;
}