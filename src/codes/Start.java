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

public class Start extends JFrame {

    private BufferedImage loginImg = null;

    private JLabel loginLbl = new JLabel(" 아  이  디");
    private JLabel passLbl = new JLabel("비 밀 번 호");
    private JTextField loginTxt = new JTextField(15);
    private JPasswordField passTxt = new JPasswordField(15);
    private JButton loginBtn = new JButton("로그인");    // 이미지로 대체
    private JLabel createAccountLbl = new JLabel("회원가입  하기");

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

        // 로그인 레이블
        loginLbl.setBounds(500, 400, 80, 30);
        loginLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 16));
        layeredPane.add(loginLbl);

        // 로그인 텍스트 필드
        loginTxt.setBounds(600, 400, 150, 30);
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
        passLbl.setBounds(500, 460, 80, 30);
        passLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 16));
        layeredPane.add(passLbl);

        // 비밀번호 패스워드 필드
        passTxt.setBounds(600, 460, 150, 30);
        passTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder()); // 텍스트필드의 경계선 제거
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

        loginBtn.setBounds(600,530,80,40);
        layeredPane.add(loginBtn);

        createAccountLbl.setBounds(600,590,100,40);
        createAccountLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 14));
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

}
