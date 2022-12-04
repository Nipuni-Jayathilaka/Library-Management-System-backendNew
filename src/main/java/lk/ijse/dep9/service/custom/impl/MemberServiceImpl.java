package lk.ijse.dep9.service.custom.impl;

import lk.ijse.dep9.dao.DAOFactory;
import lk.ijse.dep9.dao.DAOTypes;
import lk.ijse.dep9.dao.custom.MemberDAO;
import lk.ijse.dep9.dao.custom.exception.ConstraintViolationException;
import lk.ijse.dep9.dto.MemberDTO;
import lk.ijse.dep9.entity.Member;
import lk.ijse.dep9.service.custom.MemberService;
import lk.ijse.dep9.service.exception.DuplicateException;
import lk.ijse.dep9.service.exception.InUseException;
import lk.ijse.dep9.service.exception.NotFoundException;
import lk.ijse.dep9.service.util.Converter;
import lk.ijse.dep9.util.ConnectionUtil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class MemberServiceImpl implements MemberService {
    private MemberDAO memberDAO;
    private final Converter converter=new Converter();

    public MemberServiceImpl(){
        this.memberDAO=DAOFactory.getInstance().getDAO(ConnectionUtil.getConnection(), DAOTypes.MEMBER);
    }

    @Override
    public void signupMember(MemberDTO memberDTO) throws DuplicateException {
        if (memberDAO.existByContact(memberDTO.getContact())){
            throw new DuplicateException("Member already exists");
        }
        memberDTO.setId(UUID.randomUUID().toString());
        memberDAO.save(converter.toMemberEntity(memberDTO));

    }

    @Override
    public void updateMember(MemberDTO memberDTO) throws NotFoundException {
        if (!memberDAO.existsById(memberDTO.getId())){
            throw new NotFoundException("Member from this id does not exists");
        }
        System.out.println("memberserviceimpl");
         memberDAO.update(converter.toMemberEntity(memberDTO));
    }

    @Override
    public void removeMemberAccount(String memberId) throws NotFoundException, InUseException {
        if (!memberDAO.existsById(memberId)){
            throw new NotFoundException("Member from this id does not exists");
        }
        try {
            memberDAO.deleteById(memberId);
        }catch (ConstraintViolationException e){
            throw new InUseException("Member details already used",e);
        }

    }

    @Override
    public MemberDTO getMemberDetails(String memberId) throws NotFoundException {
       return memberDAO.findById(memberId).map(converter::fromMemberEntity).orElseThrow(()->new NotFoundException("Member does not exists"));

    }

    @Override
    public List<MemberDTO> findMembers(String query, int size, int page) {
        return memberDAO.findMembersByQuery(query,page,size).stream().map(converter::fromMemberEntity).collect(Collectors.toList());

    }
}
