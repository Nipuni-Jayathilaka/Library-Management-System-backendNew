package lk.ijse.dep9.dao.custom.impl;

import lk.ijse.dep9.dao.custom.ReturnDAO;
import lk.ijse.dep9.dao.custom.exception.ConstraintViolationException;
import lk.ijse.dep9.entity.Return;
import lk.ijse.dep9.entity.ReturnPK;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReturnDAOImpl implements ReturnDAO {
    private Connection connection;

    public ReturnDAOImpl(Connection connection){
        this.connection=connection;
    }
    @Override
    public long count(){
        try {
            PreparedStatement stm=connection.prepareStatement("SELECT COUNT(issueBook_id) FROM `return`");
            ResultSet rst = stm.executeQuery();
            rst.next();
            return rst.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }
    @Override
    public void deleteById(ReturnPK pk){
        try {
            PreparedStatement stm=connection.prepareStatement("DELETE FROM `return` WHERE issueBook_id=? OR book_isbn=?");
            stm.setInt(1,pk.getIssueBookId());
            stm.setString(2,pk.getBookIsbn());
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public boolean existsById(ReturnPK pk){
        try {
            PreparedStatement stm=connection.prepareStatement("SELECT * FROM `return` WHERE book_isbn=? OR issueBook_id=?");
            stm.setString(1,pk.getBookIsbn());
            stm.setInt(2,pk.getIssueBookId());
            return stm.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public List<Return> findAll(){
        try {
            PreparedStatement stm=connection.prepareStatement("SELECT * FROM `return`");
            ResultSet rst = stm.executeQuery();
            List<Return> returnList = new ArrayList<>();
            while (rst.next()){
                Date date = rst.getDate("date");
                int issueBookId= rst.getInt("issueBook_id");
                String bookIsbn = rst.getString("book_isbn");

                returnList.add(new Return(date,issueBookId,bookIsbn));
            }
            return returnList;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Optional<Return> findById(ReturnPK returnPK){
        try {
            PreparedStatement stm=connection.prepareStatement("SELECT * FROM `return` WHERE issueBook_id=? OR book_isbn=?");
            stm.setInt(1,returnPK.getIssueBookId());
            stm.setString(2,returnPK.getBookIsbn());
            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()){
                Date date = resultSet.getDate("date");
                int issueBookId = resultSet.getInt("issueBook_id");
               String bookIsbn= resultSet.getString("book_isbn");

               return Optional.of(new Return(date,issueBookId,bookIsbn));
            }else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Return save(Return ret){
        try {
            PreparedStatement stm=connection.prepareStatement("INSERT INTO `return` (date, issueBook_id, book_isbn) VALUES (?,?,?)");
            stm.setDate(1,ret.getDate());
            stm.setInt(2,ret.getReturnPK().getIssueBookId());
            stm.setString(3,ret.getReturnPK().getBookIsbn());
            if (stm.executeUpdate()==1){
                return ret;
            }else {
                throw new SQLException("Failed to save the return");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object update(Return entity) throws ConstraintViolationException {
        return null;
    }
}
