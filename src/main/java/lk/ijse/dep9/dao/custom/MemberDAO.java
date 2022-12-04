package lk.ijse.dep9.dao.custom;

import lk.ijse.dep9.dao.CrudDAO;
import lk.ijse.dep9.entity.Member;

import java.util.List;

public interface MemberDAO extends CrudDAO<Member,String > {
    boolean existByContact(String contact);
    public List<Member> findMembersByQuery(String query);
    public List<Member> findMembersByQuery(String query, int page, int size);
    public List<Member> findAllMembers(int page, int size);
}
