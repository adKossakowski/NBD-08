package pl.edu.pja.nbd08;

import com.basho.riak.client.api.commands.kv.UpdateValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person  implements Serializable {

    private String name;
    private String surname;
    private Integer age;
    private Double height;
}
