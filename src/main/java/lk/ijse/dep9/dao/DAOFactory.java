package lk.ijse.dep9.dao;

import lk.ijse.dep9.dao.custom.*;
import lk.ijse.dep9.dao.custom.impl.*;

import java.sql.Connection;

public class DAOFactory {

    private static DAOFactory daoFactory;

    private DAOFactory(){

    }
    public static DAOFactory getInstance(){
        return (daoFactory==null) ? daoFactory=new DAOFactory():daoFactory;
    }

    public <T extends SuperDAO> T getDAO(Connection connection, DAOTypes dao){
        switch (dao){
            case BOOK:
                return (T) new BookDAOImpl(connection);
            case MEMBER:
                return (T)  new MemberDAOImpl(connection);
            case ISSUE_BOOK:
                return (T)  new IssueBookDAOImpl(connection);
            case ISSUE_NOTE:
                return (T)  new IssueNoteDAOImpl(connection);
            case RETURN:
                return (T)  new ReturnDAOImpl(connection);
            case QUERY:
                return (T)  new QueryDAOImpl(connection);
            default:
                return null;
        }
    }

//    public MemberDAO getMemberDAO(Connection connection){
//        return new MemberDAOImpl(connection);
//    }
//    public BookDAO getBookDAO(Connection connection){
//        return new BookDAOImpl(connection);
//    }
//    public IssueNoteDAO getIssueNoteDAO(Connection connection){
//        return new IssueNoteDAOImpl(connection) ;
//    }
//    public IssueBookDAO getIssueBookDAO(Connection connection){
//        return new IssueBookDAOImpl(connection) ;
//    }
//    public ReturnDAO getReturnDAO(Connection connection){
//        return new ReturnDAOImpl(connection) ;
//    }
//    public QueryDAO getQueryDAO(Connection connection){
//        return new QueryDAOImpl(connection);
//    }

}
