package controller;
import java.io.IOException;
 
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import model.BoardBean;
import model.BoardDAO;
 
/**
 * Servlet implementation class BoardDeleteCon
 */
@WebServlet("/BoardDeleteCon.do")
public class BoardDeleteCon extends HttpServlet {
 
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        reqPro(request, response);
    }
 
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        reqPro(request, response);
    }
 
    protected void reqPro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
 
        // �ۻ����̱� ������ �� ��ȣ ���� �Է¹޴´�.
        int num = Integer.parseInt(request.getParameter("num"));
 
        // �����ͺ��̽��� �����Ͽ� �ϳ��� �Խñ��� �����ϴ� �޼ҵ�
        BoardDAO bdao = new BoardDAO();
 
        // �� Ŭ���� Ÿ������ ����
        BoardBean bean = bdao.getOneUpdateBoard(num); // ��ȸ���� ������Ű�� �ʴ� �޼ҵ带 �����
 
        request.setAttribute("bean", bean);
 
        RequestDispatcher dis = request.getRequestDispatcher("BoardDeleteForm.jsp");
        dis.forward(request, response);
 
    }
}
 