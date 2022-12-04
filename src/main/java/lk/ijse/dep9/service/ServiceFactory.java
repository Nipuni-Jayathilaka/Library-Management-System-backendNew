package lk.ijse.dep9.service;

import lk.ijse.dep9.service.custom.impl.BookServiceImpl;
import lk.ijse.dep9.service.custom.impl.IssueServiceImpl;
import lk.ijse.dep9.service.custom.impl.MemberServiceImpl;
import lk.ijse.dep9.service.custom.impl.ReturnServiceImpl;

public class ServiceFactory {

    private static ServiceFactory serviceFactory;

    private ServiceFactory(){

    }
    public static ServiceFactory getInstance(){
        return (serviceFactory ==null)? (serviceFactory=new ServiceFactory()):serviceFactory;
    }

    public <T extends SuperService> T getService(ServiceTypes serviceTypes){
        SuperService s;
        switch (serviceTypes){
            case BOOK:
                s=new BookServiceImpl();
                break;
            case ISSUE:
                s=new IssueServiceImpl();
                break;
            case MEMBER:
                s=new MemberServiceImpl();
                break;
            case RETURN:
                s=new ReturnServiceImpl();
                break;
            default:
                s=null;
        }

        return (T) s;
    }
}
