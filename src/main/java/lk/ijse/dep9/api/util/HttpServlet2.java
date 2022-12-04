package lk.ijse.dep9.api.util;

import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.dep9.api.exception.ValidationException;
import lk.ijse.dep9.dto.ResponseStatusDTO;
import lk.ijse.dep9.exception.ResponseStatusException;
import lk.ijse.dep9.service.exception.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;

@Slf4j
public class HttpServlet2 extends HttpServlet {

    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            if (req.getMethod().equalsIgnoreCase("PATCH")){
                doPatch(req,res);
            }else {
                super.service(req,res);
            }
        }catch (Throwable t){
            ResponseStatusException r= t instanceof ResponseStatusException ? (ResponseStatusException) t :null;
            if (r ==null || r.getStatus()>=500){//if this is not an exception that we have made or it is an internel server error
                log.error(t.getMessage());
            }
            ResponseStatusDTO statusDTO = new ResponseStatusDTO(r == null? 500 : r.getStatus(),
                    t.getMessage(),
                    req.getRequestURI(),
                    new Date().getTime());
            if (t instanceof ValidationException || t instanceof LimitExceedException
            || t instanceof AlreadyReturnedException || t instanceof AlreadyIssuedException){
                statusDTO.setStatus(400);
            }else if (t instanceof NotFoundException){
                statusDTO.setStatus(404);
            }else if (t instanceof InUseException || t instanceof DuplicateException){
                statusDTO.setStatus(409);
            }
            res.setContentType("application/json");
            res.setStatus(statusDTO.getStatus());
            JsonbBuilder.create().toJson(statusDTO,res.getWriter());
        }

    }
}
