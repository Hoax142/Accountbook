package codes;

/**
 * 시작 화면
 * 로그인 할 수 있음
 * 로그인 할 떄 DB에 접근 후 존재하는 아이디에 한해서만 로그인 가능
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

    /* 이미지 */
    private BufferedImage loginImg = null; // 이미지 저장 하기 위한 객체

    /* JLabel */
    private JLabel loginLbl = new JLabel(" 아  이  디"); // "아이디" 레이블
    private JLabel passLbl = new JLabel("비 밀 번 호"); //  "비밀번호" 레이블
    private JLabel createAccountLbl = new JLabel("회원가입  하기"); // "회원가입 하기" 레이블
    public static String getname;
    public static String getalias;

    /* JTextField & JPasswordField*/
    private JTextField loginTxt = new JTextField(15); // 아이디 입력 텍스트 필드
    private JPasswordField passTxt = new JPasswordField(15); // 비밀번호 입력 텍스트 필드

    /* JButton*/
    private JButton loginBtn = new JButton("로  그  인");    // 이미지로 대체

    /* DB */
    private Connection con = null;
    private PreparedStatement ps = null;
    private ResultSet rs;

    // 생성자
    public Start() {
        setTitle("꿀꿀이");
        setSize(Main.BIG_SCREEN_WIDTH, Main.BIG_SCREEN_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, Main.BIG_SCREEN_WIDTH, Main.BIG_SCREEN_HEIGHT);
        layeredPane.setLayout(null);

        //이미지 배경화면 불러오기
        try {
            loginImg = ImageIO.read(new File("images/startBackground2.png"));
        } catch (IOException e) {
            System.out.println("Image Error");
            System.exit(0);
        }

        MyPanel panel = new MyPanel();
        panel.setBounds(0, 0, Main.BIG_SCREEN_WIDTH, Main.BIG_SCREEN_HEIGHT);

        // 로그인 레이블
        loginLbl.setBounds(520, 400, 80, 30);
        loginLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 16));
        layeredPane.add(loginLbl);

        // 로그인 텍스트 필드
        loginTxt.setBounds(610, 400, 150, 30);
        loginTxt.setHorizontalAlignment(JTextField.CENTER);
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
        passLbl.setBounds(520, 460, 80, 30);
        passLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 16));
        layeredPane.add(passLbl);

        // 비밀번호 패스워드 필드
        passTxt.setBounds(610, 460, 150, 30);
        passTxt.setHorizontalAlignment(JTextField.CENTER);
        passTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        PlainDocument document = (PlainDocument) passTxt.getDocument();
        // 비밀번호 길이 15자로 제한
        document.setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String string = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
                if (string.length() <= 15) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        passTxt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int enterkey = e.getKeyChar();
                if(enterkey == KeyEvent.VK_ENTER) {
                    char[] pass = passTxt.getPassword();
                    String passString = new String(pass);
                    int result = checkLogin(loginTxt.getText(), passString);
                    // 로그인 성공
                    if (result == 1) {
                        JOptionPane.showMessageDialog(null, "로그인 성공", "MESSAGE", JOptionPane.PLAIN_MESSAGE);
                        getName(loginTxt.getText());
                        getAlias(loginTxt.getText());
                        // 가계부 메인
                        dispose();
                        new Master();
                    }
                    // 비밀번호 불일치
                    else if (result == 0) {
                        JOptionPane.showMessageDialog(null, "비밀번호를 확인해주세요", "MESSAGE", JOptionPane.ERROR_MESSAGE);
                        loginTxt.setText("");
                        passTxt.setText("");
                    }
                    // 없는 아이디
                    else if (result == -1) {
                        JOptionPane.showMessageDialog(null, "아이디를 확인해주세요", "MESSAGE", JOptionPane.ERROR_MESSAGE);
                        loginTxt.setText("");
                        passTxt.setText("");
                    }
                    // DB 오류
                    else if (result == -2) {
                        JOptionPane.showMessageDialog(null, "DB 오류가 발생했습니다.", "MESSAGE", JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    }
                }
            }
        });
        layeredPane.add(passTxt);

        loginBtn.setBounds(600, 540, 100, 40);
        loginBtn.addMouseListener(new MouseAdapter() {
            @Override
            // 로그인 시도
            public void mouseClicked(MouseEvent e) {
                char[] pass = passTxt.getPassword();
                String passString = new String(pass);
                int result = checkLogin(loginTxt.getText(), passString);
                // 로그인 성공
                if (result == 1) {
                    JOptionPane.showMessageDialog(null, "로그인 성공", "MESSAGE", JOptionPane.PLAIN_MESSAGE);
                    getName(loginTxt.getText());
                    getAlias(loginTxt.getText());
                    // 가계부 메인
                    dispose();
                    new Master();
                }
                // 비밀번호 불일치
                else if (result == 0) {
                    JOptionPane.showMessageDialog(null, "비밀번호를 확인해주세요", "MESSAGE", JOptionPane.ERROR_MESSAGE);
                    loginTxt.setText("");
                    passTxt.setText("");
                }
                // 없는 아이디
                else if (result == -1) {
                    JOptionPane.showMessageDialog(null, "아이디를 확인해주세요", "MESSAGE", JOptionPane.ERROR_MESSAGE);
                    loginTxt.setText("");
                    passTxt.setText("");
                }
                // DB 오류
                else if (result == -2) {
                    JOptionPane.showMessageDialog(null, "DB 오류가 발생했습니다.", "MESSAGE", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            }
        });
        layeredPane.add(loginBtn);

        createAccountLbl.setBounds(610, 590, 100, 40);
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

    // 로그인시 아이디 및 비밀번호 확인
    public int checkLogin(String id, String pswd) {
        // DB 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Term_Project?serverTimezone=Asia/Seoul&useSSL=false", "root", "dhgusgh8520"); // 2. 드라이버 연결
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    public String getName(String id) {
        // DB 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Term_Project?serverTimezone=Asia/Seoul&useSSL=false", "root", "dhgusgh8520"); // 2. 드라이버 연결
        } catch (Exception e) {
            e.printStackTrace();
        }

        String SQL = "SELECT name FROM members WHERE id=?";
        try {
            ps = con.prepareStatement(SQL);
            ps.setString(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                getname = rs.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getname;
    }

    public String getAlias(String id) {
        // DB 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Term_Project?serverTimezone=Asia/Seoul&useSSL=false", "root", "dhgusgh8520"); // 2. 드라이버 연결
        } catch (Exception e) {
            e.printStackTrace();
        }

        String SQL = "SELECT nickname FROM members WHERE id=?";
        try {
            ps = con.prepareStatement(SQL);
            ps.setString(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                getalias = rs.getString("nickname");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getalias;
    }
}


