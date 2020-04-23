package com.tte.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class Accueil extends HttpServlet
{
    static int count = 0;
    public void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        count++;
        response.setContentType("text/html");
        response.setCharacterEncoding( "UTF-8" );
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset=\"utf-8\" />");
        out.println("<title>Test</title>");
        out.println("</head>");
        out.println("<body>");
        if (count== 1)
            out.println("<p>1ère connexion</p>");
        else
            out.println("<p>" + count + "ème connexion</p>");
        out.println("</body>");
        out.println("</html>");
    }
}
