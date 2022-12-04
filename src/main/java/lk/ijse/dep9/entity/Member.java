package lk.ijse.dep9.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member implements SuperEntity {
    private String id;
    private String name;
    private String address;
    private String contact;


}
