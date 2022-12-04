package lk.ijse.dep9.dao.custom.impl;

import lk.ijse.dep9.dao.custom.IssueBookDAO;
import lk.ijse.dep9.dao.custom.exception.ConstraintViolationException;
import lk.ijse.dep9.entity.IssueBook;
import lk.ijse.dep9.entity.IssueBookPK;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IssueBookDAOImpl implements IssueBookDAO {
    private Connection connection;

    public IssueBookDAOImpl(Connection connection) {
        this.connection=connection;
    }
    @Override
    public long count(){
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT COUNT(issueBook_id) FROM issueBook");
            ResultSet resultSet = stm.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public void deleteById(IssueBookPK pk) throws ConstraintViolationException {
        try {
            PreparedStatement stm = connection.prepareStatement("DELETE FROM issueBook WHERE issueBook_id=? OR book_isbn=?");
            stm.setInt(1, pk.getIssueBookId());
            stm.setString(2,pk.getBookIsbn());
            stm.executeUpdate();

        } catch (SQLException e) {
            if (existsById(pk)) throw new ConstraintViolationException("Issue book primary key still exists within other tables",e);
            throw new RuntimeException(e);
        }


    }
    @Override
    public boolean existsById(IssueBookPK pk){
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM issueBook WHERE issueBook_id=? AND book_isbn=?");
            stm.setInt(1,pk.getIssueBookId());
            stm.setString(2,pk.getBookIsbn());
            return stm.executeQuery().next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public List<IssueBook> findAll(){
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM issueBook");
            ResultSet resultSet = stm.executeQuery();
            List<IssueBook> issueBookList = new ArrayList<>();
            while (resultSet.next()){
                String bookIsbn = resultSet.getString("book_isbn");
                int issueBookId = resultSet.getInt("issueBook_id");
                issueBookList.add(new IssueBook(issueBookId,bookIsbn));
            }
            return issueBookList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public Optional<IssueBook> findById(IssueBookPK pk){
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM issueBook WHERE book_isbn=? AND issueBook_id=?");
            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()){
                String bookIsbn = resultSet.getString("book_isbn");
                int issueBookId = resultSet.getInt("issueBook_id");
                return Optional.of(new IssueBook(issueBookId,bookIsbn));
            }else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public IssueBook save(IssueBook issueBook){
        try {
            PreparedStatement stm = connection.prepareStatement("INSERT INTO issueBook (issueBook_id, book_isbn) VALUES (?,?)");
            stm.setInt(1,issueBook.getIssueBookPK().getIssueBookId());
            stm.setString(2,issueBook.getIssueBookPK().getBookIsbn());
            if(stm.executeUpdate()==1){
                return issueBook;
            }else {
                throw new SQLException("Failed to save the issue item");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Object update(IssueBook entity) throws ConstraintViolationException {
        return null;
    }


}
