package codes;

/**
 * 회원가입 화면
 * 이름, 아이디, 비밀번호, 별명, 생년월일
 * 비밀번호 일치 확인
 * 데이터베이스 연동
 * 아이디 중복 확인
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
import java.util.Arrays;

public class Login extends JFrame {

    /* JLabel X 값 */
    private static final int FIRST_X = 30;

    /* TextField X 값 */
    private static final int SECOND_X = 140;

    /* 이미지 */
    private BufferedImage colorBackground = null;

    /* 이름 */
    private JLabel nameLbl = new JLabel("이름"); // 이름
    private JTextField nameTxt = new JTextField(null); // 이름 입력 필드

    /* 아이디 */
    private JLabel idLbl = new JLabel("아이디"); // 아이디
    private JLabel id15 = new JLabel("15자 이내로 입력해주세요"); // 아이디 주의사항
    private JTextField idTxt = new JTextField(); // 아이디 입력 필드
    private JButton checkIdBtn = new JButton("중복확인"); // 아이디 중복 확인 버튼

    /* 비밀번호 */
    private JLabel passLbl = new JLabel("비밀번호"); // 비밀번호
    private JLabel passLbl_Check = new JLabel("비밀번호 확인"); // 비밀번호 확인
    private JLabel password15 = new JLabel("15자 이내로 입력해주세요"); // 비밀번호 주의사항
    private JLabel password_check = new JLabel(""); // 비밀번호 일치 확인
    private JPasswordField passTxt = new JPasswordField(); // 비밀번호 입력 필드
    private JPasswordField passTxt_Check = new JPasswordField(); // 비밀번호 재입력 입력 필드
    private JButton checkPassBtn = new JButton("일치확인"); // 비밀번호 일치 확인 버튼

    /* 별명 */
    private JLabel nicknameLbl = new JLabel("별명"); // 별명
    private JLabel nickname15 = new JLabel("15자 이내로 입력해주세요"); // 별명 주의사항
    private JTextField nicknameTxt = new JTextField(); // 별명 입력 필드

    /* 생년월일 */
    private JLabel birthdateLbl = new JLabel("생년월일"); // 생년월일
    private JLabel dateLbl = new JLabel("ex) 1994 06 06"); // 생년월일 입력 예시
    private JTextField yearTxt = new JTextField(); // 태어난 년도 입력 필드
    private JTextField monthTxt = new JTextField(); // 태어난 월 입력 필드
    private JTextField dayTxt = new JTextField(); // 태어난 일 입력 필드

    /* 가입, 취소 버튼 */
    private JButton signupBtn = new JButton("가입"); // 가입 버튼
    private JButton exitBtn = new JButton("취소"); // 취소 버튼

    /* 변수 */
    private int idCount = 0; // 가입할 때 아이디 중복 체크 했는지 확인 하기 위한 변수
    private int passCount = 0; // 가입할 때 비밀번호 일치 했는지 확인 하기 위한 변수

    /* DB */
    private Connection con = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private Statement stmt = null;

    // 생성자
    public Login() {
        setTitle("회원가입 하기");
        setSize(Main.SMALL_SCREEN_WIDTH, Main.SMALL_SCREEN_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, Main.SMALL_SCREEN_WIDTH, Main.SMALL_SCREEN_HEIGHT);
        layeredPane.setLayout(null);

        try {
            colorBackground = ImageIO.read(new File("images/background.png"));
        } catch (IOException e) {
            System.out.println("ERROR");
            System.exit(0);
        }

        MyPanel panel = new MyPanel();
        panel.setBounds(0, 0, Main.SMALL_SCREEN_WIDTH, Main.SMALL_SCREEN_HEIGHT);

        // 이름 레이블
        nameLbl.setBounds(FIRST_X, 50, 80, 30);
        nameLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(nameLbl);

        // 이름 텍스트 필드
        nameTxt.setBounds(SECOND_X, 50, 150, 30);
        nameTxt.setHorizontalAlignment(JTextField.CENTER);
        nameTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        layeredPane.add(nameTxt);


        // 아이디 레이블
        idLbl.setBounds(FIRST_X, 120, 80, 30);
        idLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(idLbl);

        // 아이디 15자 안내 레이블
        id15.setBounds(SECOND_X, 150, 150, 30);
        id15.setFont(new Font("DX빨간우체통B", Font.ITALIC, 13));
        id15.setForeground(Color.DARK_GRAY);
        layeredPane.add(id15);

        // 아이디 텍스트 필드
        idTxt.setBounds(SECOND_X, 120, 150, 30);
        idTxt.setHorizontalAlignment(JTextField.CENTER);
        idTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        idTxt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                // 아이디 15자로 제한
                JTextField idCheck = (JTextField) e.getSource();
                if (idCheck.getText().length() > 15) {
                    JOptionPane.showMessageDialog(null, "15자 이내로 입력해주세요", "MESSAGE", JOptionPane.ERROR_MESSAGE);
                    idTxt.setText("");
                }
            }
        });
        layeredPane.add(idTxt);

        // 아이디 중복 확인 버튼
        checkIdBtn.setBounds(310, 117, 70, 35);
        checkIdBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 아이디 중복 확인 이벤트
                int result = checkID(idTxt.getText());
                // 중복된 아이디
                if (result == 1) {
                    JOptionPane.showMessageDialog(null, "중복된 아이디입니다", "SYSTEM MESSAGE", JOptionPane.ERROR_MESSAGE);
                }
                // 사용할 수 있는 아이디
                else if (result == 0) {
                    JOptionPane.showMessageDialog(null, "사용할 수 있는 아이디입니다", "SYSTEM MESSAGE", JOptionPane.DEFAULT_OPTION);
                    idCount++;
                }
                //
                else if (result == -1) {
                    JOptionPane.showMessageDialog(null, "result = -1", "SYSTEM MESSAGE", JOptionPane.ERROR_MESSAGE);
                }
                // 데이터베이스 오류
                else if (result == -2) {
                    JOptionPane.showMessageDialog(null, "DB 오류가 발생했습니다.", "SYSTEM MESSAGE", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            }
        });
        layeredPane.add(checkIdBtn);

        // 비밀번호 레이블
        passLbl.setBounds(FIRST_X, 190, 80, 30);
        passLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(passLbl);

        // 비밀번호 15자 이내 레이블
        password15.setBounds(SECOND_X, 220, 150, 30);
        password15.setFont(new Font("DX빨간우체통B", Font.ITALIC, 13));
        password15.setForeground(Color.DARK_GRAY);
        layeredPane.add(password15);

        // 비밀번호 패스워드 필드
        passTxt.setBounds(SECOND_X, 190, 150, 30);
        passTxt.setHorizontalAlignment(JPasswordField.CENTER);
        passTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        PlainDocument document = (PlainDocument) passTxt.getDocument();
        // 비밀번호 길이 15로 제한
        document.setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String string = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
                if (string.length() <= 15) {
                    super.replace(fb, offset, length, text, attrs);
                } else {
                    JOptionPane.showMessageDialog(null, "15자 내외로 작성해주세요", "MESSAGE", JOptionPane.ERROR_MESSAGE);
                    passTxt.setText("");
                }
            }
        });
        layeredPane.add(passTxt);

        // 비밀번호 재확인 레이블
        passLbl_Check.setBounds(FIRST_X, 260, 100, 30);
        passLbl_Check.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(passLbl_Check);

        // 비밀번호 재확인 패스워드 필드
        passTxt_Check.setBounds(SECOND_X, 260, 150, 30);
        passTxt_Check.setHorizontalAlignment(JPasswordField.CENTER);
        passTxt_Check.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        layeredPane.add(passTxt_Check);

        // 비밀번호 일치/불일치 레이블
        password_check.setBounds(SECOND_X, 290, 180, 30);
        password_check.setFont(new Font("DX빨간우체통B", Font.ITALIC, 13));
        layeredPane.add(password_check);

        // 비밀번호 확인 버튼
        checkPassBtn.setBounds(310, 257, 70, 35);
        checkPassBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 비밀번호 일치 확인 이벤트
                if (Arrays.equals(passTxt.getPassword(), passTxt_Check.getPassword())) {
                    password_check.setForeground(Color.DARK_GRAY);
                    password_check.setText("비밀번호가 일치합니다");
                    passCount++;
                } else {
                    password_check.setForeground(Color.RED);
                    password_check.setText("비밀번호가 일치하지 않습니다");
                }
            }
        });
        layeredPane.add(checkPassBtn);


        // 생일 레이블
        birthdateLbl.setBounds(FIRST_X, 330, 80, 30);
        birthdateLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(birthdateLbl);

        // 출생 연도 텍스트 필드
        yearTxt.setBounds(SECOND_X, 330, 50, 30);
        yearTxt.setHorizontalAlignment(JTextField.CENTER);
        yearTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        yearTxt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                // 숫자만 입력
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                    return;
                }
                // 숫자 4개만 입
                JTextField yearCheck = (JTextField) e.getSource();
                if (yearCheck.getText().length() >= 4) {
                    e.consume();
                }
            }
        });
        layeredPane.add(yearTxt);

        // 출생 월 텍스트 필드
        monthTxt.setBounds(200, 330, 30, 30);
        monthTxt.setHorizontalAlignment(JTextField.CENTER);
        monthTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        monthTxt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                // 숫자만 입력
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                    return;
                }
                // 숫자 2개만 입력
                JTextField monthCheck = (JTextField) e.getSource();
                if (monthCheck.getText().length() >= 2) {
                    e.consume();
                }
            }
        });
        layeredPane.add(monthTxt);

        // 출생 일 텍스트 필드
        dayTxt.setBounds(240, 330, 30, 30);
        dayTxt.setHorizontalAlignment(JTextField.CENTER);
        dayTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        dayTxt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                // 숫자만 입력
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                    return;
                }
                // 숫자 2개만 입력
                JTextField dayCheck = (JTextField) e.getSource();
                if (dayCheck.getText().length() >= 2) {
                    e.consume();
                }
            }
        });
        layeredPane.add(dayTxt);

        // 생일 입력 예시 레이블
        dateLbl.setBounds(290, 330, 100, 30);
        dateLbl.setFont(new Font("DX빨간우체통B", Font.PLAIN, 13));
        dateLbl.setForeground(Color.DARK_GRAY);
        layeredPane.add(dateLbl);

        // 별명 레이블
        nicknameLbl.setBounds(FIRST_X, 400, 80, 30);
        nicknameLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(nicknameLbl);

        // 별명 15자 이내 레이블
        nickname15.setBounds(SECOND_X, 430, 150, 30);
        nickname15.setFont(new Font("DX빨간우체통B", Font.ITALIC, 13));
        nickname15.setForeground(Color.DARK_GRAY);
        layeredPane.add(nickname15);

        // 별명 텍스트 필드
        nicknameTxt.setBounds(SECOND_X, 400, 150, 30);
        nicknameTxt.setHorizontalAlignment(JTextField.CENTER);
        nicknameTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        nicknameTxt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                // 별명 15자로 제한
                JTextField nickNameCheck = (JTextField) e.getSource();
                if (nickNameCheck.getText().length() > 15) {
                    JOptionPane.showMessageDialog(null, "15자 이내로 입력해주세요", "MESSAGE", JOptionPane.ERROR_MESSAGE);
                    nicknameTxt.setText("");
                }
            }
        });
        layeredPane.add(nicknameTxt);


        // 가입 버튼
        signupBtn.setBounds(80, 500, 70, 35);
        signupBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 하나라도 빈칸일 시 팝업 창
                char[] pass = passTxt.getPassword();
                char[] pass_check = passTxt_Check.getPassword();
                String passString = new String(pass);
                String passString_check = new String(pass_check);
                String birthday = (yearTxt.getText() + "-" + monthTxt.getText() + "-" + dayTxt.getText());
                if (nameTxt.getText().equals("") || idTxt.getText().equals("")
                        || passString.equals("") || passString_check.equals("")
                        || yearTxt.getText().equals("") || monthTxt.getText().equals("")
                        || dayTxt.getText().equals("") || nicknameTxt.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "빈칸 없이 입력해주세요", "MESSAGE", JOptionPane.WARNING_MESSAGE);
                }
                // 비밀번호 체크 안했을 때 팝업
                else if (passCount < 1) {
                    JOptionPane.showMessageDialog(null, "비밀번호가 일치하지 않습니다", "MESSAGE", JOptionPane.WARNING_MESSAGE);
                }
                // 아이디 중복 확인 안했을 때 팝업
                else if (idCount < 1) {
                    JOptionPane.showMessageDialog(null, "아이디 중복 확인을 해주세요", "MESSAGE", JOptionPane.WARNING_MESSAGE);
                }
                // 가입 성공
                else {
                    JOptionPane.showMessageDialog(null, "회원가입을 축하드립니다!", "MESSAGE", JOptionPane.DEFAULT_OPTION);
                    String addToDB = "INSERT INTO members(name,id,password,birthday,nickname)" +
                            "VALUES('" + nameTxt.getText() + "','" + idTxt.getText() + "','" + passString + "','" + birthday + "', '" + nicknameTxt.getText() + "')";
                    updateDB(addToDB);
                    passCount = 0;
                    idCount = 0;
                    dispose();
                }
            }
        });
        layeredPane.add(signupBtn);

        // 취소 버튼
        exitBtn.setBounds(250, 500, 70, 35);
        exitBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        });
        layeredPane.add(exitBtn);

        layeredPane.add(panel);
        add(layeredPane);
        setVisible(true);
    }

    // 이미지를 위한 그래픽
    class MyPanel extends JPanel {
        public void paint(Graphics g) {
            g.drawImage(colorBackground, 0, 0, null);
        }
    }

    // 아이디 확인을 위한 함수 (디비)
    public int checkID(String id) {
        // DB 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Term_Project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false", "root", "dhgusgh8520"); // 2. 드라이버 연결
        } catch (Exception e) {
            e.printStackTrace();
        }
        String SQL = "SELECT id FROM members where id = ?";
        try {
            ps = con.prepareStatement(SQL);
            ps.setString(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                if (rs.getString(1).equals(id)) {
                    return 1; // 아이디 중복
                } else {
                    return 0; // 없는 아이디
                }
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -2; // 데이터베이스 오류
    }

    // 디비 업데이트 (디비)
    public void updateDB(String addToDB) {
        // DB 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Term_Project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false", "root", "dhgusgh8520"); // 2. 드라이버 연결
            stmt = con.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            stmt.executeUpdate(addToDB);
        } catch (Exception e) {
            System.out.println("update error : " + e);
        }
    }
}
