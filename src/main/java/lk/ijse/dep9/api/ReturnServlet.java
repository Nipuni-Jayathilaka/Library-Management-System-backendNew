package lk.ijse.dep9.api;

import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import lk.ijse.dep9.api.util.HttpServlet2;

import java.io.IOException;

@WebServlet(name = "ReturnNoteServlet", value = "/return-notes/*")
public class ReturnNoteServlet extends HttpServlet2 {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo()==null || request.getPathInfo().equals("/")){
            if (request.getContentType()==null || !request.getContentType().startsWith("application/json")){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Invali json");
                return;
            }

            JsonbBuilder.create().fromJson(request.getReader(),Ret)
        }else {
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
    }
}
