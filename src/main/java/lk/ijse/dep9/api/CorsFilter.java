package lk.ijse.dep9.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

//@WebFilter(filterName = "CorsFilter", urlPatterns = {"/members/*","/books/*"})
public class CorsFilter extends HttpFilter {
    private String[] origins;
    @Override
    public void init() throws ServletException {
        String origins1 = getFilterConfig().getInitParameter("origins");
        origins=origins1.split(", ");

    }

//    private final String[] origins ={"http://localhost","http://34.93.50.37:8080","http://127.0.0.1"};
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String requestedOrigin= req.getHeader("Origin");
        if (requestedOrigin!=null){
            for (String origin: origins){
                if (requestedOrigin.startsWith(origin.trim())){
                    res.setHeader("Access-Control-Allow-Origin", requestedOrigin);
                    break;
                }
            }
        }


        if (req.getMethod().equalsIgnoreCase("OPTIONS")){
            res.setHeader("Access-Control-Allow-Methods","POST,GET,PATCH,DELETE,HEAD,OPTIONS,PUT");

            String reqHeaders=req.getHeader("Access-Control-Request-Headers");//this work if we send data inside the body of the request. for the delete there is no anything in the body so this will be null
            String reqMethod=req.getHeader("Access-Control-Request-Method");
            if (reqMethod.equalsIgnoreCase("POST") || reqMethod.equalsIgnoreCase("PATCH") && reqHeaders.toLowerCase().contains("content-type")){
                res.setHeader("Access-Control-Allow-Headers", "content-type");
            }
        }else {
            if (req.getMethod().equalsIgnoreCase("GET") || req.getMethod().equalsIgnoreCase("HEAD")){
                res.setHeader("Access-Control-Expose-Headers","X-Total-Count");
            }
        }
        chain.doFilter(req,res);

    }
}
