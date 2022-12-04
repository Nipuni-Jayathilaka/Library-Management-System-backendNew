package lk.ijse.dep9.dao.custom.impl;

import lk.ijse.dep9.dao.custom.QueryDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class QueryDAOImpl implements QueryDAO {
    private final Connection connection;

    public QueryDAOImpl (Connection connection){
        System.out.println(connection);
        this.connection=connection;
    }

    @Override
    public Optional<Integer> getAvailableCopies(String isbn) {
        try {
            System.out.println(connection);
            PreparedStatement stm = connection.prepareStatement("SELECT b.copies-COUNT(issueBook.book_isbn)+ COUNT(r.book_isbn) AS available_copies FROM issueBook\n" +
                    "    LEFT OUTER JOIN `return` r on issueBook.issueBook_id = r.issueBook_id and issueBook.book_isbn = r.book_isbn\n" +
                    "    RIGHT OUTER JOIN book b on issueBook.book_isbn = b.isbn WHERE b.isbn=? GROUP BY b.isbn");
            stm.setString(1,isbn);
            ResultSet rst = stm.executeQuery();
            if(!rst.next()) return Optional.empty();
            return Optional.of(rst.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean alreadyIssued(String isbn, String memberId) {
        PreparedStatement stmIssuedBefore = null;
        try {
            stmIssuedBefore = connection.prepareStatement("SELECT * FROM member m LEFT OUTER JOIN issueNote `iN` on m.id = `iN`.member_id\n" +
                    "                     LEFT OUTER JOIN `return` r ON r.issueBook_id=`iN`.issue_id\n" +
                    "                    LEFT OUTER JOIN issueBook iB on `iN`.issue_id = iB.issueBook_id\n" +
                    "                    WHERE r.date IS NULL AND m.id=? AND iB.book_isbn=?");
            stmIssuedBefore.setString(1,memberId);
            stmIssuedBefore.setString(2,isbn);
            return stmIssuedBefore.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Integer> availableBookLimit(String memberId) {
        PreparedStatement stmCanIssued = null;
        try {
            stmCanIssued = connection.prepareStatement("SELECT m.id,(3-COUNT(`iN`.member_id)) AS can_be_issued FROM member m LEFT OUTER JOIN issueNote `iN` on m.id = `iN`.member_id LEFT OUTER JOIN `return` r on `iN`.issue_id = r.issueBook_id\n" +
                    "    WHERE r.date IS NULL AND m.id=? GROUP BY m.id");
            stmCanIssued.setString(1,memberId);
            ResultSet rstCanIssue = stmCanIssued.executeQuery();
            if (!rstCanIssue.next()) return Optional.empty();
            return Optional.of(rstCanIssue.getInt("can_be_issued"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean isValidIssue(String memberId, int issueId, String isbn) {
        try {

            PreparedStatement stm1 = connection.prepareStatement("SELECT * FROM issueBook iB INNER JOIN issueNote `iN` on iB.issueBook_id = `iN`.issue_id  \n" +
                    "    WHERE member_id=? AND issueBook_id=?  AND iB.book_isbn=?");
            stm1.setString(1, memberId);
            stm1.setInt(2, issueId);
            stm1.setString(3, isbn);
            return stm1.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
