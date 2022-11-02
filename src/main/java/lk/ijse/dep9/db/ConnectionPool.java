package lk.ijse.dep9.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class ConnectionPool {

    private final ArrayList<Connection> pool=new ArrayList<>();
    private final ArrayList<Connection> consumerPool=new ArrayList<>();
    private final int poolSize;

    public ConnectionPool(int poolSize){
        this.poolSize=poolSize;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            for (int i=0; i<poolSize; i++){
                Connection connection= DriverManager.getConnection("jdbc:mysql://localhost:3306/dep9_lms","root","root");
                pool.add(connection);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized Connection getConnection(){//synchronized is added to use with wait
        while (pool.isEmpty()){
            //if all are gone in the pool should say the next one to wait
            try {
                wait();//here can be done a spurious wakeup by jvm so it should be prevent.
                // when wakeup should check what the work done so change if to while in the loop
                //so if the work is done it can exit.
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        Connection connection=pool.get(0);
        consumerPool.add(connection);
        pool.remove(connection);
        return connection;
    }
    public synchronized void releaseConnection(Connection connection){
        consumerPool.remove(connection);
        pool.add(connection);
        //need to notify to the queue to get this connection
        notify();
    }
    public synchronized void releaseAllConnection(){
        pool.addAll(consumerPool);
        consumerPool.clear();
        //need to notify to the queue to get this connection
        notifyAll();

    }
}
