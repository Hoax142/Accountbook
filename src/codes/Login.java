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
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Login extends JFrame {

    private static final int HEIGHT = 600;
    private static final int WIDTH = 400;
    private static final int FIRST_X = 30;
    private static final int SECOND_X = 140;

    private BufferedImage colorBackground = null;

    private JLabel nameLbl = new JLabel("이름"); // 이름
    private JLabel idLbl = new JLabel("아이디"); // 아이디
    private JLabel id15 = new JLabel("15자 이내로 입력해주세요"); // 아이디 주의사항
    private JLabel nicknameLbl = new JLabel("별명"); // 별명
    private JLabel nickname15 = new JLabel("15자 이내로 입력해주세요"); // 별명 주의사항
    private JLabel passLbl = new JLabel("비밀번호"); // 비밀번호
    private JLabel passLbl_Check = new JLabel("비밀번호 확인"); // 비밀번호 확인
    private JLabel birthdateLbl = new JLabel("생년월일"); // 생년월일
    private JLabel password15 = new JLabel("15자 이내로 입력해주세요"); // 비밀번호 주의사항
    private JLabel password_check = new JLabel(""); // 비밀번호 일치 확인
    private JLabel dateLbl = new JLabel("ex) 1994 06 06"); // 생년월일 입력 예시

    private JTextField nameTxt = new JTextField(null); // 이름 입력 필드
    private JTextField idTxt = new JTextField(); // 아이디 입력 필드
    private JTextField nicknameTxt = new JTextField(); // 별명 입력 필드
    private JPasswordField passTxt = new JPasswordField(); // 비밀번호 입력 필드
    private JPasswordField passTxt_Check = new JPasswordField(); // 비밀번호 재입력 입력 필드
    private JTextField yearTxt = new JTextField(); // 태어난 년도 입력 필드
    private JTextField monthTxt = new JTextField(); // 태어난 월 입력 필드
    private JTextField dayTxt = new JTextField(); // 태어난 일 입력 필드

    private JButton checkIdBtn = new JButton("중복확인"); // 아이디 중복 확인 버튼
    private JButton checkPassBtn = new JButton("일치확인"); // 비밀번호 일치 확인 버튼
    private JButton signupBtn = new JButton("가입"); // 가입 버튼
    private JButton exitBtn = new JButton("취소"); // 취소 버튼

    private int passCount = 0;

    public Login() {
        setTitle("회원가입 하기");
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, WIDTH, HEIGHT);
        layeredPane.setLayout(null);

        try {
            colorBackground = ImageIO.read(new File("images/background.png"));
        } catch (IOException e) {
            System.out.println("ERROR");
            System.exit(0);
        }

        MyPanel panel = new MyPanel();
        panel.setBounds(0, 0, WIDTH, HEIGHT);


        // 이름
        nameLbl.setBounds(FIRST_X, 50, 80, 30);
        nameLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(nameLbl);

        nameTxt.setBounds(SECOND_X, 50, 150, 30);
        nameTxt.setHorizontalAlignment(JTextField.CENTER);
        nameTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        layeredPane.add(nameTxt);


        // 아이디 -> 15자 이내
        idLbl.setBounds(FIRST_X, 120, 80, 30);
        idLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(idLbl);

        id15.setBounds(SECOND_X, 150, 150, 30);
        id15.setFont(new Font("DX빨간우체통B", Font.ITALIC, 13));
        id15.setForeground(Color.DARK_GRAY);
        layeredPane.add(id15);

        idTxt.setBounds(SECOND_X, 120, 150, 30);
        idTxt.setHorizontalAlignment(JTextField.CENTER);
        idTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        idTxt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                JTextField idCheck = (JTextField) e.getSource();
                if (idCheck.getText().length() > 15) {
                    JOptionPane.showMessageDialog(null, "15자 이내로 입력해주세요", "MESSAGE", JOptionPane.ERROR_MESSAGE);
                    idTxt.setText("");
                }
            }
        });
        layeredPane.add(idTxt);

        checkIdBtn.setBounds(310, 117, 70, 35);
        checkIdBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 아이디 중복 확인 이벤트
            }
        });
        layeredPane.add(checkIdBtn);


        // 비밀번호 -> 영어 대소문자, 숫자만 가능, 특수 문자 불가! -> 15자 이내
        passLbl.setBounds(FIRST_X, 190, 80, 30);
        passLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(passLbl);

        password15.setBounds(SECOND_X, 220, 150, 30);
        password15.setFont(new Font("DX빨간우체통B", Font.ITALIC, 13));
        password15.setForeground(Color.DARK_GRAY);
        layeredPane.add(password15);

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


        // 비밀번호 확인
        passLbl_Check.setBounds(FIRST_X, 260, 100, 30);
        passLbl_Check.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(passLbl_Check);

        passTxt_Check.setBounds(SECOND_X, 260, 150, 30);
        passTxt_Check.setHorizontalAlignment(JPasswordField.CENTER);
        passTxt_Check.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        layeredPane.add(passTxt_Check);

        password_check.setBounds(SECOND_X, 290, 180, 30);
        password_check.setFont(new Font("DX빨간우체통B", Font.ITALIC, 13));
        layeredPane.add(password_check);

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


        // 생일 -> 숫자만 입력
        birthdateLbl.setBounds(FIRST_X, 330, 80, 30);
        birthdateLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(birthdateLbl);

        yearTxt.setBounds(SECOND_X, 330, 50, 30);
        yearTxt.setHorizontalAlignment(JTextField.CENTER);
        yearTxt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                    return;
                }
                JTextField yearCheck = (JTextField) e.getSource();
                if (yearCheck.getText().length() >= 4) {
                    e.consume();
                }

            }
        });
        yearTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        layeredPane.add(yearTxt);

        monthTxt.setBounds(200, 330, 30, 30);
        monthTxt.setHorizontalAlignment(JTextField.CENTER);
        monthTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        monthTxt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                    return;
                }
                JTextField monthCheck = (JTextField) e.getSource();
                if (monthCheck.getText().length() >= 2) {
                    e.consume();
                }
            }
        });
        layeredPane.add(monthTxt);

        dayTxt.setBounds(240, 330, 30, 30);
        dayTxt.setHorizontalAlignment(JTextField.CENTER);
        dayTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        dayTxt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                    return;
                }
                JTextField dayCheck = (JTextField) e.getSource();
                if (dayCheck.getText().length() >= 2) {
                    e.consume();
                }
            }
        });
        layeredPane.add(dayTxt);

        dateLbl.setBounds(290, 330, 100, 30);
        dateLbl.setFont(new Font("DX빨간우체통B", Font.PLAIN, 13));
        dateLbl.setForeground(Color.DARK_GRAY);
        layeredPane.add(dateLbl);


        // 별명
        nicknameLbl.setBounds(FIRST_X, 400, 80, 30);
        nicknameLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(nicknameLbl);

        nickname15.setBounds(SECOND_X, 430, 150, 30);
        nickname15.setFont(new Font("DX빨간우체통B", Font.ITALIC, 13));
        nickname15.setForeground(Color.DARK_GRAY);
        layeredPane.add(nickname15);

        nicknameTxt.setBounds(SECOND_X, 400, 150, 30);
        nicknameTxt.setHorizontalAlignment(JTextField.CENTER);
        nicknameTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        nicknameTxt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                JTextField nickNameCheck = (JTextField) e.getSource();
                if (nickNameCheck.getText().length() > 15) {
                    JOptionPane.showMessageDialog(null, "15자 이내로 입력해주세요", "MESSAGE", JOptionPane.ERROR_MESSAGE);
                    nicknameTxt.setText("");
                }
            }
        });
        layeredPane.add(nicknameTxt);


        // 가입 버튼 -> 빈칸 있을시 팝업
        signupBtn.setBounds(80, 500, 70, 35);
        signupBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (nameTxt.getText().equals("") || idTxt.getText().equals("")
                        || passTxt.getText().equals("") || passTxt_Check.getText().equals("")
                        || yearTxt.getText().equals("") || monthTxt.getText().equals("")
                        || dayTxt.getText().equals("") || nicknameTxt.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "빈칸 없이 입력해주세요", "MESSAGE", JOptionPane.WARNING_MESSAGE);
                } else if (passCount < 1) {
                    JOptionPane.showMessageDialog(null, "비밀번호가 일치하지 않습니다", "MESSAGE", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "회원가입을 축하드립니다!", "MESSAGE", JOptionPane.INFORMATION_MESSAGE);
                    nameTxt.setText("");
                    idTxt.setText("");
                    passTxt.setText("");
                    passTxt_Check.setText("");
                    yearTxt.setText("");
                    monthTxt.setText("");
                    dayTxt.setText("");
                    nicknameTxt.setText("");
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

    class MyPanel extends JPanel {
        public void paint(Graphics g) {
            g.drawImage(colorBackground, 0, 0, null);
        }
    }
}
