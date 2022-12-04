package lk.ijse.dep9.api;

import jakarta.annotation.Resource;
import jakarta.json.JsonException;
import jakarta.json.JsonMergePatch;
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
import lk.ijse.dep9.dto.IssueNoteDTO;
import lk.ijse.dep9.service.ServiceFactory;
import lk.ijse.dep9.service.ServiceTypes;
import lk.ijse.dep9.service.SuperService;
import lk.ijse.dep9.service.custom.IssueService;
import lk.ijse.dep9.util.ConnectionUtil;
import org.eclipse.yasson.internal.JsonBinding;
import org.eclipse.yasson.internal.serializer.BooleanArrayDeserializer;

import javax.management.remote.JMXServerErrorException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.Serializable;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@WebServlet(name = "IssueNoteServlet", value = "/issue-notes/*")
public class IssueNoteServlet extends HttpServlet2 {

    @Resource(lookup = "java:/comp/env/jdbc/lms")
    private DataSource pool;


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo()==null || request.getPathInfo().equals("/")){
            try {
                if (request.getContentType()==null || !request.getContentType().startsWith("application/json")){
                    throw new JsonbException("Invalid Json");
                }
                IssueNoteDTO issueNoteDTO = JsonbBuilder.create().fromJson(request.getReader(), IssueNoteDTO.class);
                createIssueNote(issueNoteDTO,response);
            }catch (JsonException e){
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }

        }else {
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
    }

    private void createIssueNote(IssueNoteDTO issueNoteDTO, HttpServletResponse response) throws IOException {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<IssueNoteDTO>> violations = validator.validate(issueNoteDTO);
        violations.stream().findAny().ifPresent(violate->{
            throw new ValidationException(violate.getMessage());
        });
        if (issueNoteDTO.getBooks().stream().collect(Collectors.toSet()).size()!= issueNoteDTO.getBooks().size()){
            throw new JsonbException("Duplicate books are available in the list");
        }
        try (Connection connection=pool.getConnection()){
            ConnectionUtil.setConnection(connection);
            IssueService service = ServiceFactory.getInstance().getService(ServiceTypes.ISSUE);
            service.placeNewIssueNote(issueNoteDTO);
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_CREATED);
            JsonbBuilder.create().toJson(issueNoteDTO,response.getWriter());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
