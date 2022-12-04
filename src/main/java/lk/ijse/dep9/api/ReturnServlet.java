package lk.ijse.dep9.api;

import jakarta.annotation.Resource;
import jakarta.json.JsonException;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lk.ijse.dep9.api.exception.ValidationException;
import lk.ijse.dep9.api.util.HttpServlet2;
import lk.ijse.dep9.dto.ReturnDTO;
import lk.ijse.dep9.service.ServiceFactory;
import lk.ijse.dep9.service.ServiceTypes;
import lk.ijse.dep9.service.custom.ReturnService;
import lk.ijse.dep9.util.ConnectionUtil;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

@WebServlet(name = "ReturnNoteServlet", value = "/returns/*")
public class ReturnServlet extends HttpServlet2 {

    @Resource(lookup = "java:/comp/env/jdbc/lms")
    private DataSource pool;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo()==null || request.getPathInfo().equals("/")){
            try {
                if (request.getContentType()==null || !request.getContentType().startsWith("application/json")){
                    throw new JsonbException("Invalid Json");
                }

                ReturnDTO returnDTO = JsonbBuilder.create().fromJson(request.getReader(), ReturnDTO.class);
                createReturnNote(returnDTO,response);
            }catch (JsonException e){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,e.getMessage());
            }

        }else {
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
    }

    private void createReturnNote(ReturnDTO returnDTO, HttpServletResponse response) throws IOException {
        /*Data validation part*/
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<ReturnDTO>> violations = validator.validate(returnDTO);
        violations.stream().findAny().ifPresent(violate->{
            throw new ValidationException(violate.getMessage());
        });

        /*Business validation*/
        try (Connection connection = pool.getConnection()) {
            ConnectionUtil.setConnection(connection);
            ReturnService returnService = ServiceFactory.getInstance().getService(ServiceTypes.RETURN);
            returnService.updateReturnStatus(returnDTO);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            JsonbBuilder.create().toJson(returnDTO,response.getWriter());

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Failed to return items");
        }


    }
}
