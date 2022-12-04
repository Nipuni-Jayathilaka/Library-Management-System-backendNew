package lk.ijse.dep9.service.custom.impl;

import lk.ijse.dep9.dao.DAOFactory;
import lk.ijse.dep9.dao.DAOTypes;
import lk.ijse.dep9.dao.custom.*;
import lk.ijse.dep9.dto.IssueNoteDTO;
import lk.ijse.dep9.entity.IssueBook;
import lk.ijse.dep9.entity.IssueNote;
import lk.ijse.dep9.service.custom.IssueService;
import lk.ijse.dep9.service.exception.AlreadyIssuedException;
import lk.ijse.dep9.service.exception.LimitExceedException;
import lk.ijse.dep9.service.exception.NotAvailableException;
import lk.ijse.dep9.service.exception.NotFoundException;
import lk.ijse.dep9.service.util.Converter;
import lk.ijse.dep9.service.util.Executor;
import lk.ijse.dep9.util.ConnectionUtil;

import java.util.List;

public class IssueServiceImpl implements IssueService {
    private IssueNoteDAO issueNoteDAO;
    private IssueBookDAO issueBookDAO;
    private MemberDAO memberDAO;
    private BookDAO bookDAO;
    private Converter converter;
    private final QueryDAO queryDAO;

    public IssueServiceImpl() {
        issueNoteDAO= DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.ISSUE_NOTE);
        issueBookDAO= DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.ISSUE_BOOK);
        memberDAO= DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.MEMBER);
        bookDAO= DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.BOOK);
        queryDAO = DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.QUERY);
        converter=new Converter();
    }

    @Override
    public void placeNewIssueNote(IssueNoteDTO issueNoteDTO) throws NotFoundException, NotAvailableException, LimitExceedException, AlreadyIssuedException {
        //check whether the member exists
        if (!memberDAO.existsById(issueNoteDTO.getMemberId())){
            throw new NotFoundException("Member id does not exists");
        }
        //book existance and availability
        //check how many books can be issued for the member(maximum=3)
        for (String  isbn: issueNoteDTO.getBooks()){
            int avialableCopies= queryDAO.getAvailableCopies(isbn).
                    orElseThrow(()->new NotAvailableException("Book"+ isbn+ " does not exists"));
            if (avialableCopies ==0) throw new NotAvailableException("Book "+ isbn+ " not available at the moment");
            if (queryDAO.alreadyIssued(isbn,issueNoteDTO.getMemberId())){
                throw new AlreadyIssuedException("Book: "+isbn+" has been already issued to the same member");
            }

        }
        //check whether the given book has been issued to the same person before
        Integer avaialbleLimit = queryDAO.availableBookLimit(issueNoteDTO.getMemberId()).get();
        if (avaialbleLimit<issueNoteDTO.getBooks().size())
            throw new LimitExceedException("Members' book limit exceeds");

        try {
            ConnectionUtil.getConnection().setAutoCommit(false);
            IssueNote issueNote = converter.toIssueNoteEntity(issueNoteDTO);
            List<IssueBook> issueBooks = converter.toIssueBookEntity(issueNoteDTO);

            IssueNote save = (IssueNote) issueNoteDAO.save(issueNote);
            int issueId = save.getIssueId();
            issueBooks.forEach(book->{
                issueBooks.forEach(issueBookDAO::save);
            });
//            for (IssueBook issueBook:issueBooks){

//                issueBookDAO.save(issueBook);
//            }


            ConnectionUtil.getConnection().commit();

        }catch (Throwable t){
//            try {
//                ConnectionUtil.getConnection().rollback();
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }

            Executor.execute(ConnectionUtil.getConnection()::rollback);

        }finally {
//            try {
//                ConnectionUtil.getConnection().setAutoCommit(true);
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
            Executor.execute(()->ConnectionUtil.getConnection().setAutoCommit(true));

        }

    }
}
