package codes;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class addExpenseIncome extends JFrame {

    /* 수입&지출 사이즈 */
    private static final int HEIGHT = 600;
    private static final int WIDTH = 400;

    /* 이미지 */
    private BufferedImage colorBackground = null;

    /* JLabel X 값 */
    private static final int FIRST_X = 30;

    /* TextField X 값 */
    private static final int SECOND_X = 140;


    private JLabel dateLbl = new JLabel("날    짜");
    private JLabel getDateLbl = new JLabel("날짜 가져오기");
    private JLabel nameLbl = new JLabel("항목 이름");
    private JTextField nameTxt = new JTextField();
    private JLabel itemLbl = new JLabel("항    목");
    String items[] = {"선택", "<지출>", "교통", "통신", "식비", "개인", "---", "<수입>", "용돈", "월급", "기타"};
    private JComboBox itemCombo = new JComboBox(items);
    private JLabel typeLbl = new JLabel("구    분");
    private JRadioButton cardRadioBtn = new JRadioButton("카드");
    private JRadioButton cashRadioBtn = new JRadioButton("현금");
    private ButtonGroup typeGroup = new ButtonGroup();
    private JLabel amountLbl = new JLabel("금    액");
    private JTextField amountTxt = new JTextField();
    private JLabel memoLbl = new JLabel("메    모");
    private JTextArea memoTxt = new JTextArea(5,10);

    private JButton addBtn = new JButton("추가");
    private JButton closeBtn = new JButton("취소");

    boolean isSelected;

    public addExpenseIncome() {
        setTitle("수입 / 지출 추가");
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

        // 날짜 레이블
        dateLbl.setBounds(FIRST_X, 50, 80, 30);
        dateLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(dateLbl);

        // 날짜 가져오는 레이블

        // 이름 레이블
        nameLbl.setBounds(FIRST_X, 100, 80, 30);
        nameLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(nameLbl);

        // 이름 텍스트필드
        nameTxt.setBounds(SECOND_X, 100, 150, 30);
        nameTxt.setHorizontalAlignment(JTextField.CENTER);
        nameTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        layeredPane.add(nameTxt);

        // 수입, 지출 구분 레이블
        typeLbl.setBounds(FIRST_X, 150, 80, 30);
        typeLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(typeLbl);

        // 수입, 지출 라디오 버튼
        cardRadioBtn.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        cardRadioBtn.setBounds(140, 150, 100, 30);
        cashRadioBtn.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        cashRadioBtn.setBounds(240, 150, 100, 30);
        typeGroup.add(cardRadioBtn);
        typeGroup.add(cashRadioBtn);
        layeredPane.add(cardRadioBtn);
        layeredPane.add(cashRadioBtn);

        // 항목 선택 레이블
        itemLbl.setBounds(FIRST_X, 200, 80, 30);
        itemLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(itemLbl);

        // 항목 콤보박스
        itemCombo.setBounds(SECOND_X, 200, 80, 30);
        itemCombo.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(itemCombo);

        // 금액 레이블
        amountLbl.setBounds(FIRST_X, 250, 80, 30);
        amountLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(amountLbl);

        // 금액 텍스트필드
        amountTxt.setBounds(SECOND_X, 250, 150, 30);
        amountTxt.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        amountTxt.setHorizontalAlignment(JTextField.CENTER);
        amountTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        layeredPane.add(amountTxt);

        // 메모 레이블
        memoLbl.setBounds(FIRST_X, 300, 80, 30);
        memoLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(memoLbl);

        // 메모 텍스트 에리어
        memoTxt.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        JScrollPane memoScroll = new JScrollPane(memoTxt);
        memoScroll.setBounds(SECOND_X,300,150,120);
        layeredPane.add(memoScroll);

        // 추가 버튼
        addBtn.setBounds(80,500,70,35);
        addBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, "추가되었습니다.", "MESSAGE", JOptionPane.ERROR_MESSAGE);
            }
        });
        layeredPane.add(addBtn);

        // 취소 버튼
        closeBtn.setBounds(250,500,70,35);
        closeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        });
        layeredPane.add(closeBtn);

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
