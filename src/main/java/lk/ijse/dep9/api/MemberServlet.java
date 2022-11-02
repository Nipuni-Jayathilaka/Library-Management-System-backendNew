package lk.ijse.dep9.api;

import com.mysql.cj.jdbc.Driver;
import jakarta.annotation.Resource;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import lk.ijse.dep9.api.util.HttpServlet2;
import lk.ijse.dep9.db.ConnectionPool;
import lk.ijse.dep9.dto.MemberDTO;
import org.apache.commons.dbcp2.BasicDataSource;


import javax.lang.model.util.ElementScanner6;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.*;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "MemberServlet", value = "/members/*",loadOnStartup = 0)
public class MemberServlet extends HttpServlet2 {

    @Resource(lookup = "java:/comp/env/jdbc/lms")//for tom cat if for glassfish jdbc/lms
    private DataSource pool;

//    @Override
//    public void init(){
//        try {
//            InitialContext context = new InitialContext();
//            pool = (DataSource) context.lookup("jdbc/lms");//lookup
//            System.out.println(pool);
//        } catch (NamingException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      if (request.getPathInfo()==null || request.getPathInfo().equals("/")){
          String query=request.getParameter("q");
          String size=request.getParameter("size");
          String page=request.getParameter("page");
          if (query!=null && size!=null && page!=null){
              if (!size.matches("\\d+") || !page.matches("\\d+")){
                  response.sendError(HttpServletResponse.SC_BAD_REQUEST,"wrong size");
              }else {
                  searchPaginatedMembers(response, Integer.parseInt(size), Integer.parseInt(page),query);
              }
          }else  if(query!=null){
              searchMember(response,query);
          }else if (size!=null & page!=null){
              if (!size.matches("\\d+") || !page.matches("\\d+")){
                  response.sendError(HttpServletResponse.SC_BAD_REQUEST,"wrong size");
              }else {
                  paginatedAllMembers(response,Integer.parseInt(size),Integer.parseInt(page));
              }

          }else {
              loadAllMembers(response);
          }

      }else {
          Matcher matcher = Pattern.compile("^/([a-zA-Z0-9]{8}(-[a-zA-Z0-9]{4}){3}-[a-zA-Z0-9]{12}/?)$").matcher(request.getPathInfo());
          if (matcher.matches()){
              getMemberDetails(response,matcher.group(1));
          }else {
              response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,"expected valid id");
          }
      }
    }
    private void loadAllMembers(HttpServletResponse response) throws IOException {
        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            try (Connection connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/dep9_lms","root","root")){
//            ConnectionPool pool= (ConnectionPool) getServletContext().getAttribute("pool");
//            BasicDataSource pool= (BasicDataSource) getServletContext().getAttribute("pool");
            Connection connection=pool.getConnection();//do not use try with resource if use the connection will close so others cannot use
            Statement stm = connection.createStatement();
            ResultSet rst=stm.executeQuery("SELECT * FROM member");

            //making json objects using jsonb

            ArrayList<MemberDTO> members = new ArrayList<>();
            while (rst.next()) {
                String id = rst.getString("id");
                String name = rst.getString("name");
                String address = rst.getString("address");
                String contact = rst.getString("contact");

                MemberDTO dto = new MemberDTO(id, name, address, contact);
                members.add(dto);
            }
            // pool.releaseConnection(connection);
            connection.close();//to release the connection made from customize connectionpool
            Jsonb jsonb= JsonbBuilder.create();
            response.addHeader("Access-Control-Allow-Origin","*");//this is the header that
            // should be send with the response. when use wildcard charcter anyone can come .
            // if put http://127.0.0.1:5501 this only this one is accepted then
            response.setContentType("application/json");
            //easy way to write json obj in response
            jsonb.toJson(members,response.getWriter());

//                String json=jsonb.toJson(members);
//                response.getWriter().println(json);

            //normal way difficult way to convert to json mannually

//                StringBuilder sb=new StringBuilder();
//                sb.append("[");
//                while (rst.next()){
//                    String id=rst.getString("id");
//                    String name=rst.getString("name");
//                    String address=rst.getString("address");
//                    String contact=rst.getString("contact");
//
//                    String jsonobj="{\n" +
//                            "  \"id\": \""+id+"\",\n" +
//                            "  \"name\": \""+name+"\",\n" +
//                            "  \"address\": \""+address+"\",\n" +
//                            "  \"contact\": \""+contact+"\"\n" +
//                            "}";
//
//                    sb.append(jsonobj).append(",");
//                }
//                sb.deleteCharAt(sb.length()-1).append("]");
//                response.setContentType("application/json");
//                response.getWriter().println(sb);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    private void searchMember(HttpServletResponse response,String query) throws IOException {
        try (Connection connection = pool.getConnection();){

            PreparedStatement stm = connection.prepareStatement("SELECT * FROM member WHERE id LIKE ? OR name LIKE ? OR address LIKE ? OR contact LIKE ?");
            query ="%" +query+"%";
            stm.setString(1,query);
            stm.setString(2,query);
            stm.setString(3,query);
            stm.setString(4,query);

            ResultSet rst = stm.executeQuery();

            ArrayList<MemberDTO> dto=new ArrayList<>();
            while (rst.next()){
                String id = rst.getString("id");
                String name = rst.getString("name");
                String address = rst.getString("address");
                String contact = rst.getString("contact");

                dto.add(new MemberDTO(id,name,address,contact));
            }

            Jsonb jsonb = JsonbBuilder.create();
            response.setContentType("application/json");
            jsonb.toJson(dto,response.getWriter());

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Failed to find in the database");
        }


    }
    private void paginatedAllMembers(HttpServletResponse response,int size,int page) throws IOException {
        try (Connection connection = pool.getConnection();){
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT COUNT(id) AS count FROM member");
            rst.next();
            int totalMembers = rst.getInt("count");
            response.setIntHeader("X-Total-Count",totalMembers);

            PreparedStatement stm2 = connection.prepareStatement("SELECT * FROM member LIMIT ? OFFSET ?");
            stm2.setInt(1,size);
            stm2.setInt(2,(page-1)*size);

            ResultSet rst2 = stm2.executeQuery();
            ArrayList<MemberDTO> dtos =new ArrayList<>();
            while (rst2.next()){
                String id = rst2.getString("id");
                String name = rst2.getString("name");
                String address = rst2.getString("address");
                String contact = rst2.getString("contact");

                dtos.add(new MemberDTO(id,name,address,contact));
            }

            Jsonb jsonb = JsonbBuilder.create();
            response.addHeader("Access-Control-Allow-Origin","*");
            response.addHeader("Access-Control-Allow-Headers","X-Total-Count");
            response.addHeader("Access-Control-Expose-Headers","X-Total-Count");//to expose the xtotal to the browser
            response.setContentType("application/json");
            jsonb.toJson(dtos,response.getWriter());


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    private void getMemberDetails(HttpServletResponse response,String id) throws IOException {
        try (Connection connection=pool.getConnection()){
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM member WHERE id LIKE ?");
            stm.setString(1,id);
            ResultSet rst = stm.executeQuery();
            if(rst.next()){
                String idn = rst.getString("id");
                String name = rst.getString("name");
                String address = rst.getString("address");
                String contact = rst.getString("contact");

                Jsonb jsonb = JsonbBuilder.create();
                response.setContentType("application/json");
                jsonb.toJson(new MemberDTO(idn,name,address,contact),response.getWriter());
            }else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,"invalid member id");

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    private void searchPaginatedMembers(HttpServletResponse response,int size,int page, String query) throws IOException {
        try (Connection connection = pool.getConnection();){

            PreparedStatement stm = connection.prepareStatement("SELECT COUNT(id) AS count FROM member WHERE id LIKE ? OR name LIKE ? OR address LIKE ? OR contact LIKE ?");
            query="%"+query+"%";
            stm.setString(1,query);
            stm.setString(2,query);
            stm.setString(3,query);
            stm.setString(4,query);
            ResultSet rst = stm.executeQuery();
            rst.next();
            int totalMembers = rst.getInt("count");
            response.setIntHeader("X-Total-Count",totalMembers);

            PreparedStatement stm2 = connection.prepareStatement("SELECT * FROM member WHERE id LIKE ? OR name LIKE ? OR address LIKE ? OR contact LIKE ? LIMIT ? OFFSET ?");

            stm2.setString(1,query);
            stm2.setString(2,query);
            stm2.setString(3,query);
            stm2.setString(4,query);
            stm2.setInt(5,size);
            stm2.setInt(6,(page-1)*size);

            ResultSet rst2 = stm2.executeQuery();
            ArrayList<MemberDTO> dtos =new ArrayList<>();
            while (rst2.next()){
                String id = rst2.getString("id");
                String name = rst2.getString("name");
                String address = rst2.getString("address");
                String contact = rst2.getString("contact");

                dtos.add(new MemberDTO(id,name,address,contact));
            }

            Jsonb jsonb = JsonbBuilder.create();
            response.addHeader("Access-Control-Allow-Origin","*");
            response.addHeader("Access-Control-Allow-Headers","X-Total-Count");
            response.addHeader("Access-Control-Expose-Headers","X-Total-Count");
            response.setContentType("application/json");
            jsonb.toJson(dtos,response.getWriter());


        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"failed to fetch data");
        }
    }

    //@Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        if (req.getPathInfo()==null || req.getPathInfo().equals("/")){
//            try {
//                if (req.getPathInfo()==null || !req.getContentType().startsWith("application/json")) {
//                    throw new JsonbException("Invalid JSON");
//                }
//                MemberDTO member = JsonbBuilder.create().fromJson(req.getReader(), MemberDTO.class);
//                if (member.getName()==null  ||
//                    !member.getName().matches("[A-Za-z ]+")){
//
//                    throw new JsonbException("invalid name");
//                }else if (member.getAddress()==null  ||
//                        !member.getAddress().matches("[A-Za-z0-9,.;:/\\\\-]+")){
//
//                    throw new JsonbException("invalid address");
//                }else if (member.getContact()==null  ||
//                        !member.getContact().matches("\\\\d{3}-\\\\d{7}")){
//
//                    throw new JsonbException("invalid contact");
//                }
//                try (Connection connection = pool.getConnection()) {
//                    member.setId(UUID.randomUUID().toString());
//                    PreparedStatement stm = connection.prepareStatement("INSERT INTO member (id, name, address, contact) VALUES (?,?,?,?)");
//                    stm.setString(1,member.getId());
//                    stm.setString(2,member.getName());
//                    stm.setString(3,member.getAddress());
//                    stm.setString(4,member.getContact());
//                    int i = stm.executeUpdate();
//
//                    if (i==1){
//                        resp.setStatus(HttpServletResponse.SC_CREATED);
//                        resp.setContentType("application/json");
//                        JsonbBuilder.create().toJson(member,resp.getWriter());
//                    }else {
//                        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//                    }
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
//            }catch (JsonbException e){
//              resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"invalid json");
//            }
//        }else {
//            resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED,"expected valid id");
//        }
//
//    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getPathInfo() == null || request.getPathInfo().equals("/")){
            try{
                if (request.getContentType() == null || !request.getContentType().startsWith("application/json")){
                    throw new JsonbException("Invalid JSON");
                }
                MemberDTO member = JsonbBuilder.create().fromJson(request.getReader(), MemberDTO.class);
                if (member.getName() == null || !member.getName().matches("[A-Za-z ]+")){
                    throw new  JsonbException("Name is empty or invalid");
                } else if (member.getContact() == null || !member.getContact().matches("\\d{3}-\\d{7}")) {
                    throw new JsonbException("Contact is empty or invalid");
                } else if (member.getAddress() == null || !member.getAddress().matches("[A-Za-z0-9,:;/\\- ]+")) {
                    throw new JsonbException("Address is empty or invalid");
                }
                try (Connection connection = pool.getConnection()){
                    member.setId(UUID.randomUUID().toString());
                    PreparedStatement stm = connection.prepareStatement("INSERT INTO member (id, name, address, contact) VALUES (?, ?, ?, ?)");
                    stm.setString(1, member.getId());
                    stm.setString(2, member.getName());
                    stm.setString(3, member.getAddress());
                    stm.setString(4, member.getContact());
                    int affectedRows = stm.executeUpdate();

                    if(affectedRows == 1){
                        response.setStatus(HttpServletResponse.SC_CREATED);
                        response.setContentType("application/json");
                        JsonbBuilder.create().toJson(member, response.getWriter());
                    }else{
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }catch (JsonbException e){
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getPathInfo()==null || req.getPathInfo().equals("/")){
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,"expected valid id");
            return;
        }
        Matcher matcher = Pattern.compile("^/([a-zA-Z0-9]{8}(-[a-zA-Z0-9]{4}){3}-[a-zA-Z0-9]{12}/?)$").matcher(req.getPathInfo());
        if (matcher.matches()){
            deleteMember(matcher.group(1),resp );
        }else {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,"expected valid id");
        }


    }
    private void deleteMember(String memid, HttpServletResponse resp) throws IOException {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("DELETE FROM member WHERE id=?");
            stm.setString(1,memid);
            if(stm.executeUpdate()==0){
                resp.sendError(HttpServletResponse.SC_NOT_FOUND,"Invalid id number");
            }else {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }

        } catch (SQLException | IOException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Unable to load the data ");
        }
    }
    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        if (req.getPathInfo()==null || req.getPathInfo().equals("/")){
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,"expected valid id");
            return;
        }
        Matcher matcher = Pattern.compile("^/([a-zA-Z0-9]{8}(-[a-zA-Z0-9]{4}){3}-[a-zA-Z0-9]{12}/?)$").matcher(req.getPathInfo());
        if (matcher.matches()){
            updateMember(matcher.group(1),req,resp );
        }else {
            resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED,"expected valid id");
        }
    }
    private void updateMember(String memberId, HttpServletRequest request, HttpServletResponse response) throws IOException {

            try {
                if (request.getContentType()==null || !request.getContentType().startsWith("application/json")) {
                    throw new JsonbException("Invalid JSON");
                }
                MemberDTO member = JsonbBuilder.create().fromJson(request.getReader(), MemberDTO.class);//convert what is in the request body to json object
                if (member.getId()==null || !memberId.equalsIgnoreCase(member.getId())){
                    throw new JsonbException("Id is empty or Invalid");
                }
                else if (member.getName() == null || !member.getName().matches("[A-Za-z ]+")){
                    throw new  JsonbException("Name is empty or invalid");
                } else if (member.getContact() == null || !member.getContact().matches("\\d{3}-\\d{7}")) {
                    throw new JsonbException("Contact is empty or invalid");
                } else if (member.getAddress() == null || !member.getAddress().matches("[A-Za-z0-9,:;/\\- ]+")) {
                    throw new JsonbException("Address is empty or invalid");
                }

                try (Connection connection = pool.getConnection()) {
                    PreparedStatement stm = connection.prepareStatement("UPDATE member SET name=?, address=?, contact=? WHERE id=?");
                    stm.setString(1,member.getName());
                    stm.setString(2,member.getAddress());
                    stm.setString(3,member.getContact());
                    stm.setString(4,memberId);
                    int i = stm.executeUpdate();
                    if (i==1){
                        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    }else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND,"Member does not exist");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Failed to update the member");
                }

            } catch (JsonbException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,e.getMessage());
            }

    }

}
