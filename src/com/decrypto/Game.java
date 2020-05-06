package com.decrypto;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Game extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String sessionId = request.getSession().getId();
        System.out.println(sessionId);
        request.setAttribute("ownId", sessionId);
        this.getServletContext().getRequestDispatcher("/WEB-INF/decrypto.jsp").forward(request, response);
    }
}
