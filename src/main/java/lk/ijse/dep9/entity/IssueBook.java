package lk.ijse.dep9.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueBook implements SuperEntity {
    private IssueBookPK issueBookPK;

    public IssueBook(int issueId,  String isbn) {
        this.issueBookPK = new IssueBookPK(issueId,isbn);
    }
}
