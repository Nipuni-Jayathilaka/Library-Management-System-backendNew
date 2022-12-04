package lk.ijse.dep9.api;

import jakarta.annotation.Resource;
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
import lk.ijse.dep9.db.ConnectionPool;
import lk.ijse.dep9.dto.MemberDTO;
import lk.ijse.dep9.dto.util.Groups;
import lk.ijse.dep9.exception.ResponseStatusException;
import lk.ijse.dep9.service.ServiceFactory;
import lk.ijse.dep9.service.ServiceTypes;
import lk.ijse.dep9.service.custom.MemberService;
import lk.ijse.dep9.util.ConnectionUtil;


import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "MemberServlet", value = "/members/*",loadOnStartup = 0)
public class MemberServlet extends HttpServlet2 {

    @Resource(lookup = "java:/comp/env/jdbc/lms")
    private DataSource pool;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      if (request.getPathInfo()==null || request.getPathInfo().equals("/")){
          String query=request.getParameter("q");
          String size=request.getParameter("size");
          String page=request.getParameter("page");
          if (query!=null && size!=null && page!=null){
              if (!size.matches("\\d+") || !page.matches("\\d+")){
                  throw new ResponseStatusException(400,"Wrong size");
//                  response.sendError(HttpServletResponse.SC_BAD_REQUEST,"wrong size");
              }else {
                  searchPaginatedMembers(response, Integer.parseInt(size), Integer.parseInt(page),query);
              }
          }else  if(query!=null){
//              searchMember(response,query);
          }else if (size!=null & page!=null){
              if (!size.matches("\\d+") || !page.matches("\\d+")){
                  throw new ResponseStatusException(400,"Wrong size");
//                  response.sendError(HttpServletResponse.SC_BAD_REQUEST,"wrong size");
              }else {
//                  paginatedAllMembers(response,Integer.parseInt(size),Integer.parseInt(page));
              }

          }else {
//              loadAllMembers(response);
          }

      }else {
          Matcher matcher = Pattern.compile("^/([a-zA-Z0-9]{8}(-[a-zA-Z0-9]{4}){3}-[a-zA-Z0-9]{12}/?)$").matcher(request.getPathInfo());
          if (matcher.matches()){
              getMemberDetails(response,matcher.group(1));
          }else {
              throw new ResponseStatusException(404,"Expected valid id");
          }
      }
    }

    private void getMemberDetails(HttpServletResponse response,String id) throws IOException {
        try (Connection connection=pool.getConnection()){
            ConnectionUtil.setConnection(connection);//associate with the thread
            MemberService service = ServiceFactory.getInstance().getService(ServiceTypes.MEMBER);
            MemberDTO memberDetails = service.getMemberDetails(id);
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(memberDetails,response.getWriter());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    private void searchPaginatedMembers(HttpServletResponse response,int size,int page, String query) throws IOException {
        try (Connection connection = pool.getConnection();){
            ConnectionUtil.setConnection(connection);
            MemberService service = ServiceFactory.getInstance().getService(ServiceTypes.MEMBER);
            List<MemberDTO> members = service.findMembers(query, size, page);
            response.setIntHeader("X-Total-Count",members.size());
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(members,response.getWriter());

        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getPathInfo() == null || request.getPathInfo().equals("/")){
            try{
                if (request.getContentType() == null || !request.getContentType().startsWith("application/json")){
                    throw new JsonbException("Invalid JSON");
                }
                MemberDTO member = JsonbBuilder.create().fromJson(request.getReader(), MemberDTO.class);

                Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
                Set<ConstraintViolation<MemberDTO>> violations = validator.validate(member);

                if (!violations.isEmpty()){
                    throw new ValidationException(violations.stream().findAny().get().getMessage());
                }

                try (Connection connection = pool.getConnection()){
                    ConnectionUtil.setConnection(connection);
                    MemberService memberService = ServiceFactory.getInstance().getService(ServiceTypes.MEMBER);
                    memberService.signupMember(member);
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    response.setContentType("application/json");
                    JsonbBuilder.create().toJson(member);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }catch (JsonbException e){
                throw new ResponseStatusException(400,e.getMessage(),e);
            }
        }else{
            throw new ResponseStatusException(401);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getPathInfo()==null || req.getPathInfo().equals("/")){
            throw new ValidationException("Invalid id");
        }
        Matcher matcher = Pattern.compile("^/([a-zA-Z0-9]{8}(-[a-zA-Z0-9]{4}){3}-[a-zA-Z0-9]{12}/?)$").matcher(req.getPathInfo());
        if (matcher.matches()){
            deleteMember(matcher.group(1),resp );
        }else {
            throw new ResponseStatusException(400);
        }


    }
    private void deleteMember(String memid, HttpServletResponse resp) {
        try (Connection connection = pool.getConnection()) {
            ConnectionUtil.setConnection(connection);
            MemberService memberService = ServiceFactory.getInstance().getService(ServiceTypes.MEMBER);
            memberService.removeMemberAccount(memid);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        if (req.getPathInfo()==null || req.getPathInfo().equals("/")){
            throw new ValidationException("Invalid path");
        }
        Matcher matcher = Pattern.compile("^/([a-zA-Z0-9]{8}(-[a-zA-Z0-9]{4}){3}-[a-zA-Z0-9]{12}/?)$").matcher(req.getPathInfo());
        if (matcher.matches()){
            updateMember(matcher.group(1),req,resp );
        }else {
            throw new ValidationException("Invalid path");
        }
    }
    private void updateMember(String memberId, HttpServletRequest request, HttpServletResponse response) throws IOException {
            try {
                if (request.getContentType()==null || !request.getContentType().startsWith("application/json")) {
                    throw new JsonbException("Invalid JSON");
                }
                MemberDTO member = JsonbBuilder.create().fromJson(request.getReader(), MemberDTO.class);//convert what is in the request body to json object

                Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
                Set<ConstraintViolation<MemberDTO>> violations = validator.validate(member, Groups.update.class);
                if (!violations.isEmpty()){
                    violations.stream().findAny().ifPresent(violate->{throw new ValidationException(violate.getMessage());
                    });
                }
                if (!memberId.equals(member.getId())) throw new ValidationException("Member ids are mismatching");

                try (Connection connection = pool.getConnection()) {
                    ConnectionUtil.setConnection(connection);
                    MemberService memberService = ServiceFactory.getInstance().getService(ServiceTypes.MEMBER);
                    memberService.updateMember(member);
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            } catch (JsonbException e) {
                throw new ResponseStatusException(400,e.getMessage(),e);
            }

    }
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.setHeader("Access-Control-Allow-Origin","*");
//        resp.setHeader("Access-Control-Allow-Methods","POST,GET,PATCH,DELETE,HEAD,OPTIONS,PUT");
//
//        String headers=req.getHeader("Access-Control-Request-Headers");
//        if (headers!=null){
//            resp.setHeader("Access-Control-Allow-Headers",headers);
//            resp.setHeader("Access-Control-Expose-Headers",headers);
       // }
    }
}
