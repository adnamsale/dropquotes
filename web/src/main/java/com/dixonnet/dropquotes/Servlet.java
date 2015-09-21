package com.dixonnet.dropquotes;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by mark on 9/20/15.
 */
public class Servlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Dictionary dict = new Dictionary(Dictionary.DictSize.m);
        DropQuotesSolver dqs = new DropQuotesSolver();
        JSONObject ret = new JSONObject();
        try {
            JSONObject postdata = (JSONObject)(new JSONParser().parse(req.getReader()));
            String src = (String)postdata.get("src");
            String answer = dqs.solve(dict, new ByteArrayInputStream(src.getBytes("UTF-8")));
            ret.put("answer", answer);
        }
        catch (Throwable t) {
            ret.put("error", "Error " + t.getMessage());
        }
        resp.getWriter().write(ret.toJSONString());
        resp.setContentType("application/json");
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.setStatus(200);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setHeader("Access-Control-Max-Age", "86400");
        resp.setHeader("Allow", "GET, HEAD, POST, TRACE, OPTIONS");
    }
}
