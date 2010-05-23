package jp.gr.java_conf.ussiy.propedit;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Convert extends HttpServlet {

	private static final long serialVersionUID = -4266910369514629762L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String src1 = request.getParameter("src1");
		String src2 = request.getParameter("src2");
		String cvt1 = EncodeChanger.unicode2UnicodeEsc(src1);
		String cvt2 = EncodeChanger.unicodeEsc2Unicode(src2);
		request.setAttribute("cvt1", cvt1);
		request.setAttribute("src1", src1);
		request.setAttribute("cvt2", cvt2);
		request.setAttribute("src2", src2);
		RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
		dispatcher.forward(request, response);
	}

}
