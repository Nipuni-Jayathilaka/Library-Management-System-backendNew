package lk.ijse.dep9.api;

import jakarta.annotation.Resource;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lk.ijse.dep9.api.exception.ValidationException;
import lk.ijse.dep9.api.util.HttpServlet2;
import lk.ijse.dep9.dto.BookDTO;
import lk.ijse.dep9.dto.util.Groups;
import lk.ijse.dep9.exception.ResponseStatusException;
import lk.ijse.dep9.service.ServiceFactory;
import lk.ijse.dep9.service.ServiceTypes;
import lk.ijse.dep9.service.custom.BookService;
import lk.ijse.dep9.util.ConnectionUtil;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "BookServlet", value = "/books/*")
public class BookServlet extends HttpServlet2 {
    @Resource(lookup = "java:/comp/env/jdbc/lms")
    private DataSource pool;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo()==null || request.getPathInfo().equals("/") ) {
            String query=request.getParameter("q");
            String size=request.getParameter("size");
            String page=request.getParameter("page");
            System.out.println(query+size+page);
            if (query!=null && size!=null && page!=null){
                if (!size.matches("\\d+") || !page.matches("\\d+")){
                    throw new ResponseStatusException(400,"Wrong size");
                }else {
                    searchPaginatedBooks(response,query, Integer.parseInt(page), Integer.parseInt(size));
                }
            }else  if(query!=null){
//              searchMember(response,query);
            }else if (size!=null & page!=null){
                if (!size.matches("\\d+") || !page.matches("\\d+")){
                    throw new ResponseStatusException(400,"Wrong size");
                }else {
//                  paginatedAlBooks(response,Integer.parseInt(size),Integer.parseInt(page));
                }

            }else {
//              loadAllbooks(response);
            }
        }else {
            Matcher matcher = Pattern.compile("^/(\\d{3}-\\d-\\d{6}-\\d{2}-\\d)$").matcher(request.getPathInfo());
            if (matcher.matches()){
                getBookDetails(response,matcher.group(1));
            }else {
                throw new ResponseStatusException(404,"Expected valid id");

            }
        }

    }
    private void searchPaginatedBooks(HttpServletResponse response, String query, int page, int size) throws IOException {
        try (Connection connection = pool.getConnection()) {
            ConnectionUtil.setConnection(connection);
            BookService bookService = ServiceFactory.getInstance().getService(ServiceTypes.BOOK);
            List<BookDTO> books = bookService.findBooks(query, size, page);
            response.setIntHeader("X-Total-Count",books.size());
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(books,response.getWriter());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void getBookDetails(HttpServletResponse response, String isbn) throws IOException {

            try (Connection connection = pool.getConnection()) {
                ConnectionUtil.setConnection(connection);
                BookService service = ServiceFactory.getInstance().getService(ServiceTypes.BOOK);
                BookDTO bookDetails = service.getBookDetails(isbn);
                Jsonb jsonb = JsonbBuilder.create();
                response.setContentType("application/json");
                jsonb.toJson(bookDetails,response.getWriter());

            } catch (SQLException e) {
               throw new RuntimeException(e) ;
            }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo()==null || request.getPathInfo().equals("/")) {
            if (request.getContentType()==null || !request.getContentType().startsWith("application/json")){
                throw new ResponseStatusException(400);

            }
            BookDTO book = JsonbBuilder.create().fromJson(request.getReader(), BookDTO.class);
            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
            Set<ConstraintViolation<BookDTO>> violations = validator.validate(book);
            violations.stream().findAny().ifPresent(violate->{
                throw new ValidationException(violate.getMessage());
            });
            try (Connection connection = pool.getConnection()) {
                ConnectionUtil.setConnection(connection);
                BookService service = ServiceFactory.getInstance().getService(ServiceTypes.BOOK);
                service.addNewBook(book);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else {
            throw new ResponseStatusException(500);
        }

    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        if (req.getPathInfo()!=null || !req.getPathInfo().equals("/")) {
            if (req.getContentType() == null || !req.getContentType().startsWith("application/json")) {
                throw new ResponseStatusException(501);
            }
            Matcher matcher = Pattern.compile("\\d{3}-\\d-\\d{6}-\\d{2}-\\d").matcher(req.getPathInfo());
            if (matcher.matches()){
                updateBook(matcher.group(1),req,resp);
            }else {
                throw new ValidationException("Invalid path");
            }

        }else {
            throw new ResponseStatusException(501);
        }
    }
    private void updateBook(String isbn,HttpServletRequest request, HttpServletResponse response) throws IOException {
        try{
            if (request.getContentType()==null || !request.getContentType().startsWith("application/json")) {
                throw new ValidationException("Invalid JSON");
            }
            BookDTO book = JsonbBuilder.create().fromJson(request.getReader(), BookDTO.class);
            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
            Set<ConstraintViolation<BookDTO>> violations = validator.validate(book, Groups.update.class);

            violations.stream().findAny().ifPresent(violate->{
                throw new ValidationException(violate.getMessage());
            });

            if (!book.getIsbn().equals(isbn)) throw new ValidationException("Books id are mismatching");

            try (Connection connection = pool.getConnection()) {

                BookService bookService = ServiceFactory.getInstance().getService(ServiceTypes.BOOK);
                bookService.updateBookDetails(book);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }catch (JsonbException e){
            throw new ValidationException(e.getMessage());
        }

    }



}
