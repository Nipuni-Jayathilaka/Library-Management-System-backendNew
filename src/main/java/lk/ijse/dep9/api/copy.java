//package lk.ijse.dep9.api;
//
//import jakarta.json.bind.Jsonb;
//import jakarta.json.bind.JsonbBuilder;
//import jakarta.servlet.http.HttpServletResponse;
//import lk.ijse.dep9.dto.MemberDTO;
//
//import java.io.IOException;
//import java.sql.*;
//import java.util.ArrayList;
//
//public class copy {
//    private void loadAllMembers(HttpServletResponse response) throws IOException {
//        try {
////            Class.forName("com.mysql.cj.jdbc.Driver");
////            try (Connection connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/dep9_lms","root","root")){
////            ConnectionPool pool= (ConnectionPool) getServletContext().getAttribute("pool");
////            BasicDataSource pool= (BasicDataSource) getServletContext().getAttribute("pool");
//            Connection connection=pool.getConnection();//do not use try with resource if use the connection will close so others cannot use
//            Statement stm = connection.createStatement();
//            ResultSet rst=stm.executeQuery("SELECT * FROM member");
//
//            //making json objects using jsonb
//
//            ArrayList<MemberDTO> members = new ArrayList<>();
//            while (rst.next()) {
//                String id = rst.getString("id");
//                String name = rst.getString("name");
//                String address = rst.getString("address");
//                String contact = rst.getString("contact");
//
//                MemberDTO dto = new MemberDTO(id, name, address, contact);
//                members.add(dto);
//            }
//            // pool.releaseConnection(connection);
//            connection.close();//to release the connection made from customize connectionpool
//            Jsonb jsonb= JsonbBuilder.create();
//            response.setContentType("application/json");
//            //easy way to write json obj in response
//            jsonb.toJson(members,response.getWriter());
//
////                String json=jsonb.toJson(members);
////                response.getWriter().println(json);
//
//            //normal way difficult way to convert to json mannually
//
////                StringBuilder sb=new StringBuilder();
////                sb.append("[");
////                while (rst.next()){
////                    String id=rst.getString("id");
////                    String name=rst.getString("name");
////                    String address=rst.getString("address");
////                    String contact=rst.getString("contact");
////
////                    String jsonobj="{\n" +
////                            "  \"id\": \""+id+"\",\n" +
////                            "  \"name\": \""+name+"\",\n" +
////                            "  \"address\": \""+address+"\",\n" +
////                            "  \"contact\": \""+contact+"\"\n" +
////                            "}";
////
////                    sb.append(jsonobj).append(",");
////                }
////                sb.deleteCharAt(sb.length()-1).append("]");
////                response.setContentType("application/json");
////                response.getWriter().println(sb);
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
//    private void searchMember(HttpServletResponse response,String query) throws IOException {
//        try (Connection connection = pool.getConnection();){
//
//            PreparedStatement stm = connection.prepareStatement("SELECT * FROM member WHERE id LIKE ? OR name LIKE ? OR address LIKE ? OR contact LIKE ?");
//            query ="%" +query+"%";
//            stm.setString(1,query);
//            stm.setString(2,query);
//            stm.setString(3,query);
//            stm.setString(4,query);
//
//            ResultSet rst = stm.executeQuery();
//
//            ArrayList<MemberDTO> dto=new ArrayList<>();
//            while (rst.next()){
//                String id = rst.getString("id");
//                String name = rst.getString("name");
//                String address = rst.getString("address");
//                String contact = rst.getString("contact");
//
//                dto.add(new MemberDTO(id,name,address,contact));
//            }
//
//            Jsonb jsonb = JsonbBuilder.create();
//            response.setContentType("application/json");
//            jsonb.toJson(dto,response.getWriter());
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Failed to find in the database");
//        }
//
//
//    }
//    private void paginatedAllMembers(HttpServletResponse response,String query,int size,int page) throws IOException {
//        try (Connection connection = pool.getConnection();){
//            Statement stm = connection.createStatement();
//            ResultSet rst = stm.executeQuery("SELECT COUNT(id) AS count FROM member");
//            rst.next();
//            int totalMembers = rst.getInt("count");
//            response.setIntHeader("X-Total-Count",totalMembers);
//
//            PreparedStatement stm2 = connection.prepareStatement("SELECT * FROM member LIMIT ? OFFSET ?");
//            stm2.setInt(1,size);
//            stm2.setInt(2,(page-1)*size);
//
//            ResultSet rst2 = stm2.executeQuery();
//            ArrayList<MemberDTO> dtos =new ArrayList<>();
//            while (rst2.next()){
//                String id = rst2.getString("id");
//                String name = rst2.getString("name");
//                String address = rst2.getString("address");
//                String contact = rst2.getString("contact");
//
//                dtos.add(new MemberDTO(id,name,address,contact));
//            }
//
//            Jsonb jsonb = JsonbBuilder.create();
//            response.setContentType("application/json");
//            jsonb.toJson(dtos,response.getWriter());
//
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//
//    }
//
//    private void getMemberDetails(HttpServletResponse response,String id) throws IOException {
//        try (Connection connection=pool.getConnection()){
//            PreparedStatement stm = connection.prepareStatement("SELECT * FROM member WHERE id LIKE ?");
//            stm.setString(1,id);
//            ResultSet rst = stm.executeQuery();
//            if(rst.next()){
//                String idn = rst.getString("id");
//                String name = rst.getString("name");
//                String address = rst.getString("address");
//                String contact = rst.getString("contact");
//
//                Jsonb jsonb = JsonbBuilder.create();
//                response.setContentType("application/json");
//                jsonb.toJson(new MemberDTO(idn,name,address,contact),response.getWriter());
//            }else {
//                response.sendError(HttpServletResponse.SC_NOT_FOUND,"invalid member id");
//
//            }
//
//
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
//    private void searchPaginatedMembers(HttpServletResponse response,int size,int page, String query) throws IOException {
//        try (Connection connection = pool.getConnection();){
//
//            PreparedStatement stm = connection.prepareStatement("SELECT COUNT(id) AS count FROM member WHERE id LIKE ? OR name LIKE ? OR address LIKE ? OR contact LIKE ?");
//            stm.setString(1,query);
//            stm.setString(2,query);
//            stm.setString(3,query);
//            stm.setString(4,query);
//            ResultSet rst = stm.executeQuery();
//            rst.next();
//            int totalMembers = rst.getInt("count");
//            response.setIntHeader("X-Total-Count",totalMembers);
//
//            PreparedStatement stm2 = connection.prepareStatement("SELECT * FROM member WHERE id LIKE ? OR name LIKE ? OR address LIKE ? OR contact LIKE ? LIMIT ? OFFSET ?");
//            query="%"+query+"%";
//            stm2.setString(1,query);
//            stm2.setString(2,query);
//            stm2.setString(3,query);
//            stm2.setString(4,query);
//            stm2.setInt(5,size);
//            stm2.setInt(6,(page-1)*size);
//
//            ResultSet rst2 = stm2.executeQuery();
//            ArrayList<MemberDTO> dtos =new ArrayList<>();
//            while (rst2.next()){
//                String id = rst2.getString("id");
//                String name = rst2.getString("name");
//                String address = rst2.getString("address");
//                String contact = rst2.getString("contact");
//
//                dtos.add(new MemberDTO(id,name,address,contact));
//            }
//
//            Jsonb jsonb = JsonbBuilder.create();
//            response.setContentType("application/json");
//            jsonb.toJson(dtos,response.getWriter());
//
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"failed to fetch data");
//        }
//    }
//
//}



