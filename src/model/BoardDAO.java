package model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;
 
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
 
public class BoardDAO {
 
    Connection con;
    PreparedStatement pstmt;
    ResultSet rs;
    
    
    // ������ ���̽��� ���� �޼ҵ�
    public void getCon() {
        try {
            Context initctx = new InitialContext();
            Context envctx = (Context) initctx.lookup("java:comp/env");
            // Ÿ���� ������ �ҽ��̹Ƿ� �����ͼҽ��� (Ÿ�Ժ�ȯ�ؼ�) �޴´�.
            DataSource ds = (DataSource) envctx.lookup("jdbc/pool");
            // ���� �����ͼҽ��� ����� �����Ѵ�.
            con = ds.getConnection(); // Ŀ�ؼ� ���� ���ִ� �޼ҵ�
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    // ��ü �Խñ��� ������ �����ϴ� �޼ҵ�
    public int getAllCount() {
        getCon();
        // ���ñ��� ������ �����ϱ� ������ ī��Ʈ ������ �߰��ϰ� �ʱⰪ�� �����Ѵ�.
        int count = 0;
 
        try {
            // sql ���� �غ���
            String sql = "select count(*) from board";
            pstmt = con.prepareStatement(sql);
            // ?ǥ�� �����Ƿ� �ٷ� ��������� ���Ͻ����ָ� �ȴ�.
            rs = pstmt.executeQuery();
            // ��ü�Խñ��� ��ĭ���� �ۿ� ����� ���� �����Ƿ� 1ĭ�� ������ �ȴ�. �ݺ��� ���� if������ ����Ѵ�.
            if (rs.next()) { // �����Ͱ� �ִٸ� ī��Ʈ�� �ִ´�.
                // ��ü �Խñ� ��
                count = rs.getInt(1);
            }
            con.close();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
 
    // ȭ�鿡 ������ �����͸� 10���� �����ؼ� �����ϴ� �޼ҵ�
    public Vector<BoardBean> getAllBoard(int startRow, int endRow) {
        // �����Ұ�ü ����
        getCon();
        Vector<BoardBean> v = new Vector<>();
        try {
            // ���� �ۼ�
            String sql = "select * from (select A.*, Rownum Rnum from (select * from board order by ref desc, re_step asc)A)"
                    + "where Rnum >= ? and Rnum <= ?";
            // ���� ������ ��ü�� ����
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, startRow);
            pstmt.setInt(2, endRow);
            // ���������� ��� ����
            rs = pstmt.executeQuery();
            // ������ ������ ����� �𸣱⿡ �ݺ����� �̿��Ͽ� �����͸� ����
            while (rs.next()) {
                // �����͸� ��Ű¡ ( BoardBean Ŭ������ �̿�) ����
                BoardBean bean = new BoardBean();
                bean.setNum(rs.getInt(1));
                bean.setWriter(rs.getString(2));
                bean.setEmail(rs.getString(3));
                bean.setSubject(rs.getString(4));
                bean.setPassword(rs.getString(5));
                bean.setReg_date(rs.getDate(6).toString());
                bean.setRef(rs.getInt(7));
                bean.setRe_step(rs.getInt(8));
                bean.setRe_level(rs.getInt(9));
                bean.setReadcount(rs.getInt(10));
                bean.setContent(rs.getString(11));
                // ��Ű¡�� �����͸� ���Ϳ� ����
                v.add(bean);
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }
 
    // �ϳ��� �Խñ��� �����ϴ� �޼ҵ� ȣ��
    public void insertBoard(BoardBean bean) {
 
        getCon();
        // �ʱⰪ ����
        int ref = 0;
        int re_step = 1;// �����̱⿡
        int re_level = 1;// �����̱⿡
 
        try {
            // ���� �ۼ�
            // �� sql���� 1�� ���ϸ� �Ǳ⶧���� ����ū���� �˻�
            String refsql = "select max(ref) from board";
            pstmt = con.prepareStatement(refsql);
            // ���� ������ ����� ����
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ref = rs.getInt(1) + 1; // ref���� ū ���� 1�� ������
                // �ֽű��� �۹�ȣ�� ���� ũ�� ������ ���� �ִ� �ۿ��� 1�� ������
 
            }
            // �����͸� �����ϴ� ����
            String sql = "insert into board values(board_seq.NEXTVAL,?,?,?,?,sysdate,?,?,?,0,?)";
            pstmt = con.prepareStatement(sql);
            // ?�� ���� �����Ѵ�.
            pstmt.setString(1, bean.getWriter());
            pstmt.setString(2, bean.getEmail());
            pstmt.setString(3, bean.getSubject());
            pstmt.setString(4, bean.getPassword());
            pstmt.setInt(5, ref);
            pstmt.setInt(6, re_step);
            pstmt.setInt(7, re_level);
            pstmt.setString(8, bean.getContent());
 
            pstmt.executeUpdate();
            con.close();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
 
    // �ϳ��� �Խñ��� �о���̴� �޼ҵ� �ۼ�
    // �Խñ��� �о��ٴ� ���� ��ȸ���� 1�����ȴٴ� ��
    // �Խñ��� ������ ��ȸ���� �ö󰡾���
    public BoardBean getOneBoard(int num) {
        getCon();
        // �ʱⰪ�̴ϱ� null�� �ش�.
        BoardBean bean = null;
        try {
            // �ϳ��� �Խñ��� �о����� ��ȸ�� ����
            String countsql = "update board set readcount = readcount+1 where num=?";
            pstmt = con.prepareStatement(countsql);
            pstmt.setInt(1, num);
            // ������ ����
            pstmt.executeUpdate();
 
            // �� �Խñۿ� ���� ������ �������ִ� ������ �ۼ�
            String sql = "select * from board where num=?";
            pstmt = con.prepareStatement(sql);
            // ?�� ���� ����ֱ�
            pstmt.setInt(1, num);
            // ���������� ����� ����
            rs = pstmt.executeQuery();
            if (rs.next()) {
                bean = new BoardBean();
                bean.setNum(rs.getInt(1));
                bean.setWriter(rs.getString(2));
                bean.setEmail(rs.getString(3));
                bean.setSubject(rs.getString(4));
                bean.setPassword(rs.getString(5));
                bean.setReg_date(rs.getDate(6).toString());
                bean.setRef(rs.getInt(7));
                bean.setRe_step(rs.getInt(8));
                bean.setRe_level(rs.getInt(9));
                bean.setReadcount(rs.getInt(10));
                bean.setContent(rs.getString(11));
 
            }
 
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
 
    }
 
    // �亯���� �����ϴ� �޼ҵ�
    public void reInsertBoard(BoardBean bean) {
        getCon();
        // �ʱⰪ ����
        int ref = bean.getRef();
        int re_step = bean.getRe_step();
        int re_level = bean.getRe_level();
 
        try {
            // ���� �ۼ�
            // �� sql���� 1�� ���ϸ� �Ǳ⶧���� ����ū���� �˻�
            // �亯�� �Խ����� Ư¡�� ���� ���� ���������� ���� ���� �ö󰡾� �ϱ� ������ �ٸ��۵��� �� ������ �������Ѵ�.
            String relevelsql = "update board set re_level=re_level+1 where ref=? and re_level >?";
            pstmt = con.prepareStatement(relevelsql);
            pstmt.setInt(1, ref);
            pstmt.setInt(2, re_level);
 
            // ���� ������ ����� ����
            pstmt.executeLargeUpdate(); 
            // �����͸� �����ϴ� ����
            String sql = "insert into board values(board_seq.NEXTVAL,?,?,?,?,sysdate,?,?,?,0,?)";
            pstmt = con.prepareStatement(sql);
            // ?�� ���� �����Ѵ�.
            pstmt.setString(1, bean.getWriter());
            pstmt.setString(2, bean.getEmail());
            pstmt.setString(3, bean.getSubject());
            pstmt.setString(4, bean.getPassword());
            pstmt.setInt(5, ref);
            pstmt.setInt(6, re_step + 1); // ���� �θ�ۿ� ���ܺ��� 1�� �������Ѿ� �Ѵ�.
            pstmt.setInt(7, re_level + 1); // ���� �θ�ۿ� ���ܺ��� 1�� �������Ѿ� �Ѵ�.
            pstmt.setString(8, bean.getContent());
 
            pstmt.executeUpdate();
            con.close();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    // ��ȸ���� �������� �ʴ� �ϳ��� �Խñ��� �����ϴ� �޼ҵ�
    // �Խñ� Ȯ���� Ȯ���ϴ� �޼ҵ忡�� ��ȸ�� ���� ������ �����Ѵ�.
    public BoardBean getOneUpdateBoard(int num) {
        getCon();
        // �ʱⰪ�̴ϱ� null�� �ش�.
        BoardBean bean = null;
        try {
 
            // �� �Խñۿ� ���� ������ �������ִ� ������ �ۼ�
            String sql = "select * from board where num=?";
            pstmt = con.prepareStatement(sql);
            // ?�� ���� ����ֱ�
            pstmt.setInt(1, num);
            // ���������� ����� ����
            rs = pstmt.executeQuery();
            if (rs.next()) {
                bean = new BoardBean();
                bean.setNum(rs.getInt(1));
                bean.setWriter(rs.getString(2));
                bean.setEmail(rs.getString(3));
                bean.setSubject(rs.getString(4));
                bean.setPassword(rs.getString(5));
                bean.setReg_date(rs.getDate(6).toString());
                bean.setRef(rs.getInt(7));
                bean.setRe_step(rs.getInt(8));
                bean.setRe_level(rs.getInt(9));
                bean.setReadcount(rs.getInt(10));
                bean.setContent(rs.getString(11));
 
            }
 
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }
 
    // �ϳ��� �Խñ��� �����ϴ� �޼ҵ�
    public void updateBoard(int num, String subject, String content) {
 
        // �����ͺ��̽� ����
        getCon();
        try {
            String sql = "update board set subject=?,content=? where num=?";
            pstmt = con.prepareStatement(sql);
            // ?�� ���� �����Ѵ�.
            pstmt.setString(1, subject);
            pstmt.setString(2, content);
            pstmt.setInt(3, num);
 
            // 4. ������ �����Ѵ�.
            pstmt.executeLargeUpdate();
            // insert, delete, update ���� ������ executeUpdate�� ����Ѵ�.
            // �� DML(���������۾�)�� ����� ���� executeUpdate()�� ����Ѵ�.
            // 5. �ڿ� �ݳ�
            con.close();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
    public void deleteBoard(int num) {
   	 
        getCon();
 
        try {
            String sql = "delete from board where num=?";
            pstmt = con.prepareStatement(sql);
            // ?�� ���� �־��ش�.
            pstmt.setInt(1, num);
            pstmt.executeUpdate();
            con.close();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
    


}