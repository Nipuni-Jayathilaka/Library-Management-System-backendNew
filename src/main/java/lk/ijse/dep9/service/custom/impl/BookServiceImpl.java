package lk.ijse.dep9.service.custom.impl;

import lk.ijse.dep9.dao.DAOFactory;
import lk.ijse.dep9.dao.DAOTypes;
import lk.ijse.dep9.dao.SuperDAO;
import lk.ijse.dep9.dao.custom.BookDAO;
import lk.ijse.dep9.dto.BookDTO;
import lk.ijse.dep9.entity.Book;
import lk.ijse.dep9.service.custom.BookService;
import lk.ijse.dep9.service.exception.DuplicateException;
import lk.ijse.dep9.service.exception.NotFoundException;
import lk.ijse.dep9.service.util.Converter;
import lk.ijse.dep9.util.ConnectionUtil;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookServiceImpl implements BookService {

    private BookDAO bookDAO;
    private final Converter converter=new Converter();

    public BookServiceImpl() {
        this.bookDAO = DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.BOOK);
    }

    @Override
    public void addNewBook(BookDTO bookDTO) throws DuplicateException {
        if (bookDAO.existsById(bookDTO.getIsbn())){
            throw new DuplicateException("Book with the isbn already exist");
        }
//        Book bookEntity = new Book(bookDTO.getIsbn(), bookDTO.getBookName(), bookDTO.getAuthor(), bookDTO.getCopies());
        bookDAO.save(converter.toBookEntity(bookDTO));

    }

    @Override
    public void updateBookDetails(BookDTO bookDTO) throws NotFoundException {
        if (!bookDAO.existsById(bookDTO.getIsbn())){
            throw new NotFoundException("Book with the isbn does not exist");
        }
//        Book bookEntity = new Book(bookDTO.getIsbn(), bookDTO.getBookName(), bookDTO.getAuthor(), bookDTO.getCopies());
        bookDAO.update(converter.toBookEntity(bookDTO));
    }

    @Override
    public BookDTO getBookDetails(String isbn) throws NotFoundException {
        return bookDAO.findById(isbn).map(book -> converter.fromBookEntity(book))//when use map if there
                // is null it will not convert that to the bookdto if not it will throw an option
                .orElseThrow(()->new NotFoundException("Book doesn't exists"));

    }

    @Override
    public List<BookDTO> findBooks(String query, int size, int page) {
        List<Book> bookEntityList = bookDAO.findBooksByQuery(query, size, page);
        return bookEntityList.stream().map(converter::fromBookEntity).collect(Collectors.toList());
    }
}

