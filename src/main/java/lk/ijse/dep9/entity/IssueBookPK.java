package lk.ijse.dep9.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueBookPK implements Serializable {
    private int issueBookId;
    private String bookIsbn;

}
