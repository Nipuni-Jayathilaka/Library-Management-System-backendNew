package lk.ijse.dep9.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book implements SuperEntity {
    private String isbn;
    private String bookName;
    private String author;
    private int copies;

}
