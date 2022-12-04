package lk.ijse.dep9.service.custom.impl;

import lk.ijse.dep9.dao.DAOFactory;
import lk.ijse.dep9.dao.DAOTypes;
import lk.ijse.dep9.dao.custom.MemberDAO;
import lk.ijse.dep9.dao.custom.QueryDAO;
import lk.ijse.dep9.dao.custom.ReturnDAO;
import lk.ijse.dep9.dto.ReturnBookDTO;
import lk.ijse.dep9.dto.ReturnDTO;
import lk.ijse.dep9.entity.Return;
import lk.ijse.dep9.entity.ReturnPK;
import lk.ijse.dep9.service.custom.ReturnService;
import lk.ijse.dep9.service.exception.AlreadyReturnedException;
import lk.ijse.dep9.service.exception.NotFoundException;
import lk.ijse.dep9.service.util.Converter;
import lk.ijse.dep9.service.util.Executor;
import lk.ijse.dep9.util.ConnectionUtil;

import javax.xml.crypto.Data;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;

public class ReturnServiceImpl implements ReturnService {

    private ReturnDAO returnDAO;
    private MemberDAO memberDAO;
    private final Converter converter;
    private final QueryDAO queryDAO;

    public ReturnServiceImpl() {
        queryDAO = DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.QUERY);
        converter=new Converter();
    }

    @Override
    public void updateReturnStatus(ReturnDTO returnDTO) throws NotFoundException , AlreadyReturnedException {
        HashSet<ReturnBookDTO> returnBooks = new HashSet<>(returnDTO.getBooks());//to remove duplicates in the returndto
        try {

            ConnectionUtil.getConnection().setAutoCommit(false);
            returnBooks.forEach(
                    returnBook -> {
                        if (!queryDAO.isValidIssue(returnDTO.getMemberId(), returnBook.getIssueNoteId(), returnBook.getIsbn())){
                            throw new NotFoundException("Invalid return");}
                        if (returnDAO.existsById(new ReturnPK(returnBook.getIssueNoteId(), returnBook.getIsbn()))){
                            throw new AlreadyReturnedException("Item has been returned already");
                        };
                        Return returnEntity = converter.toReturn(returnBook);
                        returnEntity.setDate(Date.valueOf(LocalDate.now()));
                        returnDAO.save(returnEntity);
                    });
        }catch (Throwable t){
            Executor.execute(ConnectionUtil.getConnection()::rollback);
        }finally {
            Executor.execute(()->ConnectionUtil.getConnection().setAutoCommit(true));
        }
    }
}
