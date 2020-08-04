package codes;

/**
 * 시작 화면
 * 로그인 기능
 * 회원가입 기능
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Start extends JFrame {

    /* 이미지 */
    private BufferedImage loginImg = null; // 이미지 저장 하기 위한 객체

    /* 아이디 */
    private final JTextField idTxt = new JTextField(15); // 아이디 입력 텍스트 필드

    /* 비밀번호 */
    private final JPasswordField passTxt = new JPasswordField(15); // 비밀번호 입력 텍스트 필드

    /* 변수 */
    public static String getname; // 다른 클래스에서 사용 할 수 있도록 이름 저장
    public static String getalias; // 다른 클래스에서 사용 할 수 있도록 별명 저장

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
            //System.exit(0);
        }

        MyPanel panel = new MyPanel();
        panel.setBounds(0, 0, Main.BIG_SCREEN_WIDTH, Main.BIG_SCREEN_HEIGHT);

        // 아이디 레이블
        JLabel idLbl = new JLabel(" 아  이  디");
        idLbl.setBounds(520, 400, 80, 30);
        idLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 16));
        layeredPane.add(idLbl);

        // 아이디 텍스트 필드
        idTxt.setBounds(610, 400, 150, 30);
        idTxt.setHorizontalAlignment(JTextField.CENTER);
        idTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder()); // 텍스트필드의 경계선 제거
        idTxt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                // 아이디 길이 15자로 제한
                JTextField logCheck = (JTextField) e.getSource();
                if (logCheck.getText().length() >= 15) {
                    e.consume();
                }
            }
        });
        layeredPane.add(idTxt);

        // 비밀번호 레이블
        JLabel passLbl = new JLabel("비 밀 번 호");
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
                // 패스워드 택스트 필드에서 엔터키로 로그인
                int enterkey = e.getKeyChar();
                if (enterkey == KeyEvent.VK_ENTER) {
                    char[] pass = passTxt.getPassword();
                    String passString = new String(pass);
                    int result = checkLogin(idTxt.getText(), passString);
                    // 로그인 성공
                    if (result == 1) {
                        JOptionPane.showMessageDialog(null, "로그인 성공", "SYSTEM MESSAGE", JOptionPane.DEFAULT_OPTION);
                        getName(idTxt.getText()); // 로그인 한 사람의 이름 가져오기
                        getAlias(idTxt.getText()); // 로그인 한 사람의 별명 가져오기
                        dispose();
                        new Master();
                    }
                    // 비밀번호 불일치
                    else if (result == 0) {
                        JOptionPane.showMessageDialog(null, "비밀번호를 확인해주세요", "SYSTEM MESSAGE", JOptionPane.ERROR_MESSAGE);
                        passTxt.setText("");
                    }
                    // 없는 아이디
                    else if (result == -1) {
                        JOptionPane.showMessageDialog(null, "아이디를 확인해주세요", "SYSTEM MESSAGE", JOptionPane.ERROR_MESSAGE);
                        idTxt.setText("");
                        passTxt.setText("");
                        idTxt.requestFocus();
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

        // 로그인 버튼
        JButton loginBtn = new JButton("로  그  인");
        loginBtn.setBounds(600, 540, 100, 40);
        loginBtn.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                int enterkey = e.getKeyChar();
                if (enterkey == KeyEvent.VK_ENTER) {
                    char[] pass = passTxt.getPassword();
                    String passString = new String(pass);
                    int result = checkLogin(idTxt.getText(), passString);
                    // 로그인 성공
                    if (result == 1) {
                        JOptionPane.showMessageDialog(null, "로그인 성공", "SYSTEM MESSAGE", JOptionPane.DEFAULT_OPTION);
                        getName(idTxt.getText()); // 로그인 한 사람의 이름 가져오기
                        getAlias(idTxt.getText()); // 로그인 한 사람의 별명 가져오기
                        dispose();
                        new Master();
                    }
                    // 비밀번호 불일치
                    else if (result == 0) {
                        JOptionPane.showMessageDialog(null, "비밀번호를 확인해주세요", "SYSTEM MESSAGE", JOptionPane.ERROR_MESSAGE);
                        passTxt.setText("");
                    }
                    // 없는 아이디
                    else if (result == -1) {
                        JOptionPane.showMessageDialog(null, "아이디를 확인해주세요", "SYSTEM MESSAGE", JOptionPane.ERROR_MESSAGE);
                        idTxt.setText("");
                        passTxt.setText("");
                        idTxt.requestFocus();
                    }
                    // DB 오류
                    else if (result == -2) {
                        JOptionPane.showMessageDialog(null, "DB 오류가 발생했습니다.", "MESSAGE", JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    }
                }
            }
        });
        loginBtn.addMouseListener(new MouseAdapter() {
            @Override
            // 로그인 시도
            public void mouseClicked(MouseEvent e) {
                char[] pass = passTxt.getPassword();
                String passString = new String(pass);
                int result = checkLogin(idTxt.getText(), passString);
                // 로그인 성공
                if (result == 1) {
                    JOptionPane.showMessageDialog(null, "로그인 성공", "SYSTEM MESSAGE", JOptionPane.DEFAULT_OPTION);
                    getName(idTxt.getText()); // 로그인 한 사람의 이름 가져오기
                    getAlias(idTxt.getText()); // 로그인 한 사람의 별명 가져오기
                    dispose();
                    new Master();
                }
                // 비밀번호 불일치
                else if (result == 0) {
                    JOptionPane.showMessageDialog(null, "비밀번호를 확인해주세요", "SYSTEM MESSAGE", JOptionPane.ERROR_MESSAGE);
                    passTxt.setText("");
                }
                // 없는 아이디
                else if (result == -1) {
                    JOptionPane.showMessageDialog(null, "아이디를 확인해주세요", "SYSTEM MESSAGE", JOptionPane.ERROR_MESSAGE);
                    idTxt.setText("");
                    passTxt.setText("");
                    idTxt.requestFocus();
                }
                // DB 오류
                else if (result == -2) {
                    JOptionPane.showMessageDialog(null, "DB 오류가 발생했습니다.", "SYSTEM MESSAGE", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            }
        });
        layeredPane.add(loginBtn);

        // 회원가입 레이블
        JLabel createAccountLbl = new JLabel("회원가입  하기");
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

    // 이미지를 위한 그래픽
    class MyPanel extends JPanel {
        public void paint(Graphics g) {
            g.drawImage(loginImg, 0, 0, null);
        }
    }

    // 로그인시 아이디 및 비밀번호 확인 (디비 접속)
    public int checkLogin(String id, String pswd) {
        // DB 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook_project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false&autoReconnection=true", "root", "dhgusgh8520"); // 2. 드라이버 연결
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

    // 로그인 시 이름 가져오는 함수 (디비 접속)
    public void getName(String id) {
        // DB 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook_project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false&autoReconnection=true", "root", "dhgusgh8520"); // 2. 드라이버 연결
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
    }

    // 로그인 시 별명 가져오는 함수 (디비 접속)
    public void getAlias(String id) {
        // DB 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook_project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false&autoReconnection=true", "root", "dhgusgh8520"); // 2. 드라이버 연결
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
    }
}

