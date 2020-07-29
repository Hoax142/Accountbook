package codes;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class addExpenseIncome extends JFrame {

    /* 이미지 */
    private BufferedImage colorBackground = null;

    /* JLabel X 값 */
    private static final int FIRST_X = 30;

    /* TextField X 값 */
    private static final int SECOND_X = 140;

    /* DB */
    private Connection con = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private Statement stmt = null;

    /* JLabel */
    private JLabel dateLbl = new JLabel("날    짜");
    private JLabel timeLbl = new JLabel("시    간");
    private JLabel useTimeLbl = new JLabel("현재 시간 사용");
    private JLabel useCustomTimeLbl = new JLabel("직접 시간 입력");
    private JLabel hourLbl = new JLabel("시");
    private JLabel minuteLbl = new JLabel("분");
    private JLabel secondLbl = new JLabel("초");
    private JLabel getDateLbl = new JLabel(Master.getDailyDateLbl.getText());
    private JLabel nameLbl = new JLabel("항목 이름");
    private JLabel itemLbl = new JLabel("항    목");
    private JLabel cardcashTypeLbl = new JLabel("결제 수단");
    private JLabel amountLbl = new JLabel("금    액");
    private JLabel incomeexpenseLbl = new JLabel("구    분");

    /* JTextfield */
    private JTextField nameTxt = new JTextField();
    private JTextField amountTxt = new JTextField();
    private JTextField hourTxt = new JTextField();
    private JTextField minTxt = new JTextField();
    private JTextField secTxt = new JTextField();

    /* JCheckBox */
    private JCheckBox useTime = new JCheckBox();

    /* JComboBox */
    String items[] = {"선택", "<지출>", "교통", "통신", "식비", "개인", "---", "<수입>", "용돈", "월급", "기타"};
    private JComboBox itemCombo = new JComboBox(items);

    /* JRadioButton */
    private JRadioButton cardRadioBtn = new JRadioButton("카드");
    private JRadioButton cashRadioBtn = new JRadioButton("현금");
    private ButtonGroup cardcashTypeGroup = new ButtonGroup();
    private JRadioButton incomeRadioBtn = new JRadioButton("수입");
    private JRadioButton expenseRadioBtn = new JRadioButton("지출");
    private ButtonGroup incomexpenseTypeGroup = new ButtonGroup();

    /* JButton */
    private JButton addBtn = new JButton("추가");
    private JButton closeBtn = new JButton("취소");

    /* String */
    private String isCashCard;
    private String isIncomeExpense;
    private String getComboBoxItem;

    Date now = new Date();
    SimpleDateFormat formatH, formatM, formatS;
    String getHrs, getMin, getSec;
    static String getTime;

    private Calendar today = Calendar.getInstance();

    /* 생성자 */
    public addExpenseIncome() {
        setTitle("수입 / 지출 추가");
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

        // 날짜 레이블
        dateLbl.setBounds(FIRST_X, 50, 80, 30);
        dateLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(dateLbl);

        // 날짜 가져오는 레이블
        getDateLbl.setBounds(SECOND_X + 50, 50, 150, 30);
        getDateLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(getDateLbl);

        // 시간 레이블
        timeLbl.setBounds(FIRST_X, 100, 80, 30);
        timeLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(timeLbl);

        useTimeLbl.setVisible(false);
        useTimeLbl.setBounds(SECOND_X + 5, 130, 100, 25);
        useTimeLbl.setFont(new Font("DX빨간우체통B", Font.PLAIN, 12));
        useTimeLbl.setForeground(Color.gray);
        layeredPane.add(useTimeLbl);

        useCustomTimeLbl.setBounds(SECOND_X + 5, 130, 100, 25);
        useCustomTimeLbl.setFont(new Font("DX빨간우체통B", Font.PLAIN, 12));
        useCustomTimeLbl.setForeground(Color.gray);
        layeredPane.add(useCustomTimeLbl);

        // 시간 체크박스
        useTime.setBounds(SECOND_X, 100, 30, 30);
        useTime.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    useTimeLbl.setVisible(true);
                    useCustomTimeLbl.setVisible(false);
                    hourTxt.setEditable(true);
                    hourTxt.setBackground(Color.WHITE);
                    minTxt.setEditable(true);
                    minTxt.setBackground(Color.WHITE);
                    secTxt.setEditable(true);
                    secTxt.setBackground(Color.WHITE);
                } else {
                    useTimeLbl.setVisible(false);
                    useCustomTimeLbl.setVisible(true);
                    hourTxt.setEditable(false);
                    hourTxt.setBackground(Color.LIGHT_GRAY);
                    minTxt.setEditable(true);
                    minTxt.setBackground(Color.LIGHT_GRAY);
                    secTxt.setEditable(true);
                    secTxt.setBackground(Color.LIGHT_GRAY);
                }
            }
        });
        layeredPane.add(useTime);

        // 시간 선택
        formatH = new SimpleDateFormat("HH"); // 시 구하기
        formatM = new SimpleDateFormat("mm"); // 분 구하기
        formatS = new SimpleDateFormat("ss"); // 초 구하기

        hourTxt.setBounds(SECOND_X + 40, 100, 30, 30);
        hourTxt.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        hourTxt.setText(formatH.format(now));
        hourTxt.setEditable(false);
        hourTxt.setBackground(Color.LIGHT_GRAY);
        hourTxt.setHorizontalAlignment(JTextField.CENTER);
        hourTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        layeredPane.add(hourTxt);

        hourLbl.setBounds(SECOND_X + 75, 100, 30, 30);
        hourLbl.setFont(new Font("DX빨간우체통B", Font.PLAIN, 15));
        layeredPane.add(hourLbl);

        minTxt.setBounds(SECOND_X + 100, 100, 30, 30);
        minTxt.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        minTxt.setText(formatM.format(now));
        minTxt.setEditable(false);
        minTxt.setBackground(Color.LIGHT_GRAY);
        minTxt.setHorizontalAlignment(JTextField.CENTER);
        minTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        layeredPane.add(minTxt);

        minuteLbl.setBounds(SECOND_X + 135, 100, 30, 30);
        minuteLbl.setFont(new Font("DX빨간우체통B", Font.PLAIN, 15));
        layeredPane.add(minuteLbl);

        secTxt.setBounds(SECOND_X + 160, 100, 30, 30);
        secTxt.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        secTxt.setText(formatS.format(now));
        secTxt.setEditable(false);
        secTxt.setBackground(Color.LIGHT_GRAY);
        secTxt.setHorizontalAlignment(JTextField.CENTER);
        secTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        layeredPane.add(secTxt);

        secondLbl.setBounds(SECOND_X + 195, 100, 30, 30);
        secondLbl.setFont(new Font("DX빨간우체통B", Font.PLAIN, 15));
        layeredPane.add(secondLbl);

        // 수입, 지출 레이블
        incomeexpenseLbl.setBounds(FIRST_X, 150, 80, 30);
        incomeexpenseLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(incomeexpenseLbl);

        // 수입 지출 라디오 버튼
        incomeRadioBtn.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        incomeRadioBtn.setBounds(SECOND_X, 150, 100, 30);
        expenseRadioBtn.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        expenseRadioBtn.setBounds(230, 150, 100, 30);
        incomexpenseTypeGroup.add(incomeRadioBtn);
        incomexpenseTypeGroup.add(expenseRadioBtn);
        layeredPane.add(incomeRadioBtn);
        layeredPane.add(expenseRadioBtn);

        // 이름 레이블
        nameLbl.setBounds(FIRST_X, 200, 80, 30);
        nameLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(nameLbl);

        // 이름 텍스트필드
        nameTxt.setBounds(SECOND_X, 200, 150, 30);
        nameTxt.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        nameTxt.setHorizontalAlignment(JTextField.CENTER);
        nameTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        layeredPane.add(nameTxt);

        // 카드, 현금 레이블
        cardcashTypeLbl.setBounds(FIRST_X, 250, 80, 30);
        cardcashTypeLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(cardcashTypeLbl);

        // 카드, 현금 라디오 버튼
        cardRadioBtn.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        cardRadioBtn.setBounds(SECOND_X, 250, 100, 30);
        cashRadioBtn.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        cashRadioBtn.setBounds(230, 250, 100, 30);
        cardcashTypeGroup.add(cardRadioBtn);
        cardcashTypeGroup.add(cashRadioBtn);
        layeredPane.add(cardRadioBtn);
        layeredPane.add(cashRadioBtn);

        // 항목 선택 레이블
        itemLbl.setBounds(FIRST_X, 300, 80, 30);
        itemLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(itemLbl);

        // 항목 콤보박스
        itemCombo.setBounds(SECOND_X, 300, 90, 30);
        itemCombo.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        itemCombo.addActionListener(new ActionListener() {
            @Override
            // (선택, 지출 수입) 선택 시 오류 메시지 띄우기
            public void actionPerformed(ActionEvent e) {
                if (incomeRadioBtn.isSelected()) {
                    if (itemCombo.getSelectedIndex() == 0 || itemCombo.getSelectedIndex() == 1 || itemCombo.getSelectedIndex() == 6 || itemCombo.getSelectedIndex() == 7) {
                        JOptionPane.showMessageDialog(null, "선택 불가능합니다. 다시 선택해주세요", "ERROR!", JOptionPane.ERROR_MESSAGE);
                        itemCombo.setSelectedIndex(0);
                    } else if (itemCombo.getSelectedIndex() == 2 || itemCombo.getSelectedIndex() == 3 || itemCombo.getSelectedIndex() == 4 || itemCombo.getSelectedIndex() == 5) {
                        JOptionPane.showMessageDialog(null, "수입 항목 중에서 골라주세요.", "ERROR!", JOptionPane.ERROR_MESSAGE);
                        itemCombo.setSelectedIndex(0);
                    }
                } else if (expenseRadioBtn.isSelected()) {
                    if (itemCombo.getSelectedIndex() == 0 || itemCombo.getSelectedIndex() == 1 || itemCombo.getSelectedIndex() == 6 || itemCombo.getSelectedIndex() == 7) {
                        JOptionPane.showMessageDialog(null, "선택 불가능합니다. 다시 선택해주세요", "ERROR!", JOptionPane.ERROR_MESSAGE);
                        itemCombo.setSelectedIndex(0);
                    } else if (itemCombo.getSelectedIndex() == 8 || itemCombo.getSelectedIndex() == 9 || itemCombo.getSelectedIndex() == 10) {
                        JOptionPane.showMessageDialog(null, "지출 항목 중에서 골라주세요.", "ERROR!", JOptionPane.ERROR_MESSAGE);
                        itemCombo.setSelectedIndex(0);
                    }
                }
            }
        });
        layeredPane.add(itemCombo);

        // 금액 레이블
        amountLbl.setBounds(FIRST_X, 350, 80, 30);
        amountLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(amountLbl);

        // 금액 텍스트필드
        amountTxt.setBounds(SECOND_X, 350, 150, 30);
        amountTxt.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        amountTxt.setHorizontalAlignment(JTextField.CENTER);
        amountTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        layeredPane.add(amountTxt);

        // 추가 버튼
        addBtn.setBounds(80, 500, 70, 35);
        addBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 시간 값 저장
                getHrs = hourTxt.getText();
                getMin = minTxt.getText();
                getSec = secTxt.getText();
                getTime = (getHrs + ":" + getMin + ":" + getSec); // 시:분:토 형식
                // -> 위 세개 하나로 묵고 아래 디비 저장값에 집어 넣

                // 수입/지출 인지 값 저장
                if (cardRadioBtn.isSelected() == true) {
                    isCashCard = "카드";
                } else {
                    isCashCard = "현금";
                }
                // 카드/현금 인지 값 저장
                if (incomeRadioBtn.isSelected() == true) {
                    isIncomeExpense = "수입";
                } else {
                    isIncomeExpense = "지출";
                }
                // 콤보박스 아이템 가져오기
                getComboBoxItem = itemCombo.getSelectedItem().toString();
                JOptionPane.showMessageDialog(null, "추가되었습니다.", "MESSAGE", JOptionPane.ERROR_MESSAGE);
                String addToDB = "INSERT INTO accountbook(username,incomeexpense,inputdate,itemname,cardcash,itemtype,amount,inputtime)" +
                        "VALUES('" + Start.getname + "', '" + isIncomeExpense + "','" + Master.getDate + "','" + nameTxt.getText() + "','" + isCashCard + "', '" + getComboBoxItem + "', '" + amountTxt.getText() + "','" + getTime + "')";
                updateDB(addToDB);
                if (isIncomeExpense.equals("수입")) {
                    Master.incomeTable_Model_Vector = new Vector();
                    Master.incomeTable_Model_Vector.add(getTime);
                    Master.incomeTable_Model_Vector.add(nameTxt.getText());
                    Master.incomeTable_Model_Vector.add(isCashCard);
                    Master.incomeTable_Model_Vector.add(getComboBoxItem);
                    Master.incomeTable_Model_Vector.add(amountTxt.getText());
                    Master.incomeTable_Model.addRow(Master.incomeTable_Model_Vector);
                    updateIncomeSum(Start.getname, Master.getDate);
                } else {
                    Master.expenseTable_Model_Vector = new Vector();
                    Master.expenseTable_Model_Vector.add(getTime);
                    Master.expenseTable_Model_Vector.add(nameTxt.getText());
                    Master.expenseTable_Model_Vector.add(isCashCard);
                    Master.expenseTable_Model_Vector.add(getComboBoxItem);
                    Master.expenseTable_Model_Vector.add(amountTxt.getText());
                    Master.expenseTable_Model.addRow(Master.expenseTable_Model_Vector);
                    getExpenseSum(Start.getname, Master.getDate);
                }
                dispose();
            }
        });
        layeredPane.add(addBtn);

        // 취소 버튼
        closeBtn.setBounds(250, 500, 70, 35);
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

    public void updateIncomeSum(String name, String date) {
        // DB 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Term_Project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false", "root", "dhgusgh8520"); // 2. 드라이버 연결
        } catch (Exception e) {
            e.printStackTrace();
        }

        String SQL = "SELECT * FROM accountbook WHERE username=? AND incomeexpense = '수입' AND inputdate=?";
        try {
            ps = con.prepareStatement(SQL);
            ps.setString(1, name);
            ps.setString(2, date);
            rs = ps.executeQuery();
            if (Master.incomeTable_Model.getRowCount() == 0) {
                Master.incomeSum.setText("0");
            }
            while (rs.next()) {
                Master.stringIncomeSum = rs.getString("amount");
                Master.intIncomeSum = Integer.parseInt(Master.stringIncomeSum);
                Master.totalIncomeSum += Master.intIncomeSum;
                Master.incomeSum.setText(Integer.toString(Master.totalIncomeSum));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Master.totalIncomeSum = 0;
    }

    public void getExpenseSum(String name, String date) {
        // DB 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Term_Project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false", "root", "dhgusgh8520"); // 2. 드라이버 연결
        } catch (Exception e) {
            e.printStackTrace();
        }

        String SQL = "SELECT * FROM accountbook WHERE username=? AND incomeexpense = '지출' AND inputdate=?";
        try {
            ps = con.prepareStatement(SQL);
            ps.setString(1, name);
            ps.setString(2, date);
            rs = ps.executeQuery();
            if (Master.expenseTable_Model.getRowCount() == 0) {
                Master.expenseSum.setText("0");
            }
            while (rs.next()) {
                Master.stringExpenseSum = rs.getString("amount");
                Master.intExpenseSum = Integer.parseInt(Master.stringExpenseSum);
                Master.totalExpenseSum += Master.intExpenseSum;
                Master.expenseSum.setText(Integer.toString(Master.totalExpenseSum));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Master.totalExpenseSum = 0;
    }

}
