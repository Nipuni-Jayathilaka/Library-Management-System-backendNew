package lk.ijse.dep9.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueNote implements SuperEntity {
    private int issueId;
    private Date date;
    private String memberId;

}
