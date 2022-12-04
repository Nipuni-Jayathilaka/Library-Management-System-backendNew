package lk.ijse.dep9.dao.custom;

import lk.ijse.dep9.dao.CrudDAO;
import lk.ijse.dep9.entity.Book;

import java.util.List;

public interface BookDAO extends CrudDAO<Book,String > {

    public List<Book> findBooksByQuery(String query);
    public List<Book> findBooksByQuery(String query, int size, int page);
    public List<Book> findAllBooks(int size, int page);

}
