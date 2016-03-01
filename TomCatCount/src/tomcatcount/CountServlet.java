package tomcatcount;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class CountServlet
 */
@WebServlet("/CountServlet")
public class CountServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CountServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		Integer counter = new Integer(0);
		HttpSession session = request.getSession();

	      if (session.getAttribute("counter") == null){
	         session.setAttribute("counter", counter);
	      } else {
	         counter = (Integer)session.getAttribute("counter");
	         counter++;
	      }
	      session.setAttribute("counter",  counter);
		
		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();

		writer.println("<h1>CatCount: " + session.getAttribute("counter") + "</h1>");
		writer.println("<p>Served at: " + request.getContextPath() + "</br>");
		writer.println("Host: " + request.getLocalName() + ":");
		writer.println(request.getLocalPort() + "</p>");
		writer.close();
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
