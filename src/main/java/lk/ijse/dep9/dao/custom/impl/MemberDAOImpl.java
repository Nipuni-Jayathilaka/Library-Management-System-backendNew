package lk.ijse.dep9.dao.custom.impl;

import lk.ijse.dep9.dao.custom.MemberDAO;
import lk.ijse.dep9.dao.custom.exception.ConstraintViolationException;
import lk.ijse.dep9.entity.Member;
import lk.ijse.dep9.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberDAOImpl implements MemberDAO {

    private Connection connection;
    public MemberDAOImpl(Connection connection){
        this.connection=connection;
    }
    @Override
    public long count(){
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT COUNT(id) FROM member");
            ResultSet resultSet = stm.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public void deleteById(String id) throws ConstraintViolationException {
        try {
            PreparedStatement stm = connection.prepareStatement("DELETE FROM member WHERE id=?");
            stm.setString(1,id);
            stm.executeUpdate();
        } catch (SQLException e) {
            if (existsById(id)) throw new ConstraintViolationException("Member already existing",e);
                throw new RuntimeException(e);
        }

    }
    @Override
    public boolean existsById(String id){
        try {
            PreparedStatement stm=connection.prepareStatement("SELECT * FROM member WHERE id=?");
            stm.setString(1,id);
            return stm.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public List<Member> findAll(){
        try {
            PreparedStatement stm=connection.prepareStatement("SELECT * FROM member");
            ResultSet resultSet = stm.executeQuery();
            List<Member> memberList=new ArrayList<>();
            while (resultSet.next()){
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String contact = resultSet.getString("contact");
                memberList.add(new Member(id,name,address,contact));
            }
            return memberList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Optional<Member> findById(String id){
        try {
            System.out.println(connection);
            PreparedStatement stm=connection.prepareStatement("SELECT * FROM member WHERE id=?");
            stm.setString(1,id);
            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()){
                String id1 = resultSet.getString("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String contact = resultSet.getString("contact");
                return Optional.of(new Member(id1,name,address,contact));
            }else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public Member save(Member member){
        try {
            PreparedStatement stm=connection.prepareStatement("INSERT INTO member (id, name, address, contact) VALUES (?,?,?,?)");
            stm.setString(1,member.getId());
            stm.setString(2,member.getName());
            stm.setString(3,member.getAddress());
            stm.setString(4,member.getContact());
            if(stm.executeUpdate()==1){
                return member;
            }else {
                throw new SQLException("Failed to save the member");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public Member update(Member member){
        System.out.println("memberdaoimpl");
        try {

            PreparedStatement stm=connection.prepareStatement("UPDATE member SET name=?, address=?, contact=? WHERE id=?");
            stm.setString(1,member.getName());
            stm.setString(2,member.getAddress());
            stm.setString(3,member.getContact());
            stm.setString(4,member.getId());
            System.out.println(stm.executeUpdate());
            if(stm.executeUpdate()==1){
                return member;
            }else {
                throw new SQLException("Faied to update the member");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean existByContact(String contact) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM member WHERE contact=?");
            stm.setString(1,contact);
            ResultSet rest = stm.executeQuery();
            if (rest.next()){
                return true;
            }
            return false;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Member> findMembersByQuery(String query){
        try {
            PreparedStatement stm=connection.prepareStatement("SELECT * FROM member WHERE id LIKE ? OR name LIKE ? OR address LIKE ? ");
            query="%"+query+"%";
            stm.setString(1,query);
            stm.setString(2,query);
            stm.setString(3,query);

            ResultSet resultSet = stm.executeQuery();
            List<Member> memberList=new ArrayList<>();
            while (resultSet.next()){
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String contact = resultSet.getString("contact");

                memberList.add(new Member(id,name,address,contact));
            }
            return memberList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public List<Member> findMembersByQuery(String query, int page, int size){
        try {
            PreparedStatement stm=connection.prepareStatement("SELECT * FROM member WHERE id LIKE ? OR name LIKE ? OR address LIKE ? LIMIT ? OFFSET ?");
            query="%"+query+"%";
            stm.setString(1,query);
            stm.setString(2,query);
            stm.setString(3,query);
            stm.setInt(4,size);
            stm.setInt(5,(page-1)*size);

            ResultSet resultSet = stm.executeQuery();
            List<Member> memberList=new ArrayList<>();
            while (resultSet.next()){
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String contact = resultSet.getString("contact");

                memberList.add(new Member(id,name,address,contact));
                System.out.println(id);
            }
            return memberList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public List<Member> findAllMembers(int page, int size){
        try {
            PreparedStatement stm=connection.prepareStatement("SELECT * FROM member LIMIT ? OFFSET ?");
            stm.setInt(1,size);
            stm.setInt(2,(page-1)*size);

            ResultSet resultSet = stm.executeQuery();
            List<Member> memberList=new ArrayList<>();
            while (resultSet.next()){
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String contact = resultSet.getString("contact");

                memberList.add(new Member(id,name,address,contact));
            }
            return memberList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

