package lk.ijse.dep9.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lk.ijse.dep9.db.ConnectionPool;
import org.apache.commons.dbcp2.BasicDataSource;

//@WebListener //t0 remove the listener now this is not needed
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {//to get the time of deploying the app
//        this is our connection pool
//        ConnectionPool dbPool=new ConnectionPool(3);
//        sce.getServletContext().setAttribute("pool",dbPool);//store the pool inside the servlet context

        //get connection pool from dbcp
        BasicDataSource dbPool = new BasicDataSource();
        dbPool.setUrl("jdbc:mysql://localhost/3306/dep9_lms");
        dbPool.setUsername("root");
        dbPool.setPassword("root");
        dbPool.setDriverClassName("com.mysql.cj.jdbc.Driver");

        dbPool.setInitialSize(10);
        dbPool.setMaxTotal(20);

        sce.getServletContext().setAttribute("pool",dbPool);

    }
}
