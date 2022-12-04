package lk.ijse.dep9.dao.custom;

import lk.ijse.dep9.dao.CrudDAO;
import lk.ijse.dep9.dao.SuperDAO;

import java.util.Optional;

public interface QueryDAO extends SuperDAO {
    Optional<Integer> getAvailableCopies(String isbn);

    boolean alreadyIssued(String isbn, String memberId);

    Optional<Integer> availableBookLimit(String memberId);

    boolean isValidIssue(String memberId,int issueId,String isbn);
}
