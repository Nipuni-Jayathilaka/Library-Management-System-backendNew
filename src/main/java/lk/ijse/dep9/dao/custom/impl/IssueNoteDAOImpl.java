package lk.ijse.dep9.dao.custom.impl;

import lk.ijse.dep9.dao.custom.IssueNoteDAO;
import lk.ijse.dep9.dao.custom.exception.ConstraintViolationException;
import lk.ijse.dep9.entity.IssueNote;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IssueNoteDAOImpl implements IssueNoteDAO {
    private Connection connection;

    public IssueNoteDAOImpl(Connection connection){
        this.connection=connection;
    }
    @Override
    public long count(){
        try {
            PreparedStatement stm=connection.prepareStatement("SELECT COUNT(issue_id) FROM issueNote");
            ResultSet resultSet = stm.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void deleteById(Integer issueId) throws ConstraintViolationException {
        try {
            PreparedStatement stm=connection.prepareStatement("DELETE FROM issueNote WHERE issue_id=?");
            stm.setInt(1,issueId);
            stm.executeUpdate();
        } catch (SQLException e) {
            if (existsById(issueId)) throw new ConstraintViolationException("issueid is already used by another table",e);
            throw new RuntimeException(e);
        }
    }
    @Override
    public boolean existsById(Integer issueId){
        try {
            PreparedStatement stm=connection.prepareStatement("SELECT COUNT(issue_id) FROM issueNote WHERE issue_id=?");
            stm.setInt(1,issueId);
            ResultSet resultSet = stm.executeQuery();
            return resultSet.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public List<IssueNote> findAll(){
        try {
            PreparedStatement stm=connection.prepareStatement("SELECT * FROM issueNote");
            ResultSet rst=stm.executeQuery();
            List<IssueNote> issueNoteList =new ArrayList<>();
            while (rst.next()){
                int issue_id = rst.getInt("issue_id");
                Date date = rst.getDate("date");
                String memberId= rst.getString("member_id");

                issueNoteList.add(new IssueNote(issue_id,date ,memberId));
            }
            return issueNoteList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Optional<IssueNote> findById(Integer issueId){
        try {
            PreparedStatement stm=connection.prepareStatement("SELECT * FROM issueNote WHERE issue_id=?");
            stm.setInt(1,issueId);
            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()){
                Date date = resultSet.getDate("date");
                String memberId = resultSet.getString("member_id");

                return Optional.of(new IssueNote(issueId,date,memberId));
            }else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public IssueNote save(IssueNote issueNote){
        try {
            PreparedStatement stm=connection.prepareStatement("INSERT INTO issueNote (issue_id, date, member_id) VALUES (?,?,?)");
            stm.setInt(1,issueNote.getIssueId());
            stm.setDate(2,issueNote.getDate());
            stm.setString(3, issueNote.getMemberId());
            if (stm.executeUpdate()==1){
                return issueNote;
            }else {
                throw new SQLException("Failed to save the issue note");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public IssueNote update(IssueNote issueNote){
        try {
            PreparedStatement stm=connection.prepareStatement("UPDATE issueNote SET date=? , member_id=? WHERE issue_id=?");
            stm.setDate(1,issueNote.getDate());
            stm.setString(2,issueNote.getMemberId());
            stm.setInt(3,issueNote.getIssueId());
            if (stm.executeUpdate()==1){
                return issueNote;
            }else {
                throw new SQLException("Failed to update the issue note");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
