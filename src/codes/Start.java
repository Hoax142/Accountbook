package codes;

/**
 * 시작 화면
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class Start extends JFrame {

    private BufferedImage loginImg = null;

    private JLabel loginLbl = new JLabel(" 아  이  디");
    private JLabel passLbl = new JLabel("비 밀 번 호");
    private JTextField loginTxt = new JTextField(15);
    private JPasswordField passTxt = new JPasswordField(15);
    private JButton loginBtn = new JButton("로  그  인");    // 이미지로 대체
    private JLabel createAccountLbl = new JLabel("회원가입  하기");

    private Connection con = null;
    private PreparedStatement ps = null;
    private ResultSet rs;
    private Statement st;

    // 생성자
    public Start() {
        setTitle("꿀꿀이");
        setSize(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
        layeredPane.setLayout(null);

        //이미지 배경화면 불러오
        try {
            loginImg = ImageIO.read(new File("images/startBackground2.png"));
        } catch (IOException e) {
            System.out.println("Image Error");
            System.exit(0);
        }

        MyPanel panel = new MyPanel();
        panel.setBounds(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);

        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Term_Project?serverTimezone=Asia/Seoul&useSSL=false", "root", "dhgusgh8520"); // 2. 드라이버 연결
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 로그인 레이블
        loginLbl.setBounds(480, 400, 80, 30);
        loginLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 16));
        layeredPane.add(loginLbl);

        // 로그인 텍스트 필드
        loginTxt.setBounds(570, 400, 150, 30);
        loginTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder()); // 텍스트필드의 경계선 제거
        // 아이디 길이 15로 제한
        loginTxt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                JTextField logCheck = (JTextField) e.getSource();
                if (logCheck.getText().length() >= 15) {
                    e.consume();
                }
            }
        });
        layeredPane.add(loginTxt);

        // 비밀번호 레이블
        passLbl.setBounds(480, 460, 80, 30);
        passLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 16));
        layeredPane.add(passLbl);

        // 비밀번호 패스워드 필드
        passTxt.setBounds(570, 460, 150, 30);
        passTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        PlainDocument document = (PlainDocument) passTxt.getDocument();
        // 비밀번호 길이 15로 제한
        document.setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String string = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
                if (string.length() <= 15) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        layeredPane.add(passTxt);

        loginBtn.setBounds(745, 395, 90, 100);
        loginBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                char[] pass = passTxt.getPassword();
                String passString = new String(pass);
                int result = checkLogin(loginTxt.getText(), passString);
                // 로그인 성공
                if (result == 1) {
                    JOptionPane.showMessageDialog(null, "로그인 성공", "MESSAGE", JOptionPane.PLAIN_MESSAGE);
                    loginTxt.setText("");
                    passTxt.setText("");
                    // 가계부 메인
                }
                // 비밀번호 불일치
                else if (result == 0) {
                    JOptionPane.showMessageDialog(null, "비밀번호를 확인해주세요", "MESSAGE", JOptionPane.ERROR_MESSAGE);
                    loginTxt.setText("");
                    passTxt.setText("");
                }
                // 없는 아이디
                else if (result == -1) {
                    JOptionPane.showMessageDialog(null, "없는 아이디입니다", "MESSAGE", JOptionPane.ERROR_MESSAGE);
                    loginTxt.setText("");
                    passTxt.setText("");
                }
                // DB 오류
                else if (result == -2) {
                    JOptionPane.showMessageDialog(null, "디비 오류가 발생했습니다.", "MESSAGE", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            }
        });
        layeredPane.add(loginBtn);

        createAccountLbl.setBounds(600, 590, 100, 40);
        createAccountLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 14));
        createAccountLbl.setForeground(Color.GRAY);
        createAccountLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 회원가입 하러 가기
                new Login();
            }
        });
        layeredPane.add(createAccountLbl);

        layeredPane.add(panel);
        add(layeredPane);
        setVisible(true);
    }

    class MyPanel extends JPanel {
        public void paint(Graphics g) {
            g.drawImage(loginImg, 0, 0, null);
        }
    }

    public int checkLogin(String id, String pswd) {
        String SQL = "SELECT password FROM members WHERE id=?";
        try {
            ps = con.prepareStatement(SQL);
            ps.setString(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                if (rs.getString(1).equals(pswd)) {
                    return 1; // 로그인 성공
                } else {
                    return 0; // 비밀번호 불일치
                }
            }
            return -1; // 아이디 없음
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -2; // 데이터베이스 오류
    }

}

