package lk.ijse.dep9.service.util;

import lk.ijse.dep9.dao.custom.IssueNoteDAO;
import lk.ijse.dep9.dto.BookDTO;
import lk.ijse.dep9.dto.IssueNoteDTO;
import lk.ijse.dep9.dto.MemberDTO;
import lk.ijse.dep9.dto.ReturnBookDTO;
import lk.ijse.dep9.entity.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.internal.bytebuddy.description.method.MethodDescription;

import java.lang.reflect.Type;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Converter {

    private ModelMapper mapper;
    public Converter(){
        this.mapper=new ModelMapper();
        mapper.typeMap(LocalDate.class, Date.class).setConverter(mc->Date.valueOf(mc.getSource()));
    }
    public BookDTO fromBookEntity(Book bookEntity){
        return mapper.map(bookEntity,BookDTO.class);
    }
    public Book toBookEntity(BookDTO bookDTO){
        return mapper.map(bookDTO,Book.class);
    }

    public MemberDTO fromMemberEntity(Member member){
        return mapper.map(member, MemberDTO.class);
    }
    public Member toMemberEntity(MemberDTO memberDTO){
        return mapper.map(memberDTO,Member.class);
    }
    public IssueNote toIssueNoteEntity(IssueNoteDTO issueNoteDTO){
        return mapper.map(issueNoteDTO,IssueNote.class);
    }
    public List<IssueBook> toIssueBookEntity(IssueNoteDTO issueNoteDTO){
        Type type = new TypeToken<List<IssueBook>>() {
        }.getType();
        mapper.typeMap(IssueNoteDTO.class,List.class)
                .setConverter(mc->{
            IssueNoteDTO source = mc.getSource();

            return source.getBooks().stream()
                    .map(isbn->new IssueBook(source.getId(),isbn)).collect(Collectors.toList());
        });
        return mapper.map(issueNoteDTO,type);
    }
    public Return toReturn(ReturnBookDTO returnBookDTO){
        mapper.typeMap(ReturnBookDTO.class,Return.class).setConverter(mappingContext ->
            new Return(null,mappingContext.getSource().getIssueNoteId(), mappingContext.getSource().getIsbn()));
        return mapper.map(returnBookDTO,Return.class);
    }


}
