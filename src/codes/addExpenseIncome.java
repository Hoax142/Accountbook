package codes;

/**
 * 수입 혹은 지출 추가 화면
 * 날짜, 시간, 항목 이름, 수입 / 지출, 구분 등 상세하게 추가 가능
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class addExpenseIncome extends JFrame {

    /* 이미지 */
    private BufferedImage colorBackground = null;

    /* JLabel X 값 */
    private static final int FIRST_X = 30;

    /* TextField X 값 */
    private static final int SECOND_X = 140;

    /* 시간 */
    private final JLabel useTimeLbl = new JLabel("현재 시간 사용"); // 현재 시간 사용 레이블
    private final JLabel useCustomTimeLbl = new JLabel("직접 시간 입력"); // 직접 시간 입력 레이블
    private final JTextField hourTxt = new JTextField(); // 시 텍스트 필드
    private final JTextField minTxt = new JTextField(); // 분 텍스트 필드
    private final JTextField secTxt = new JTextField(); // 분 텍스트 필드
    Date now = new Date(); // 시간을 불러오기 위한 함수
    SimpleDateFormat formatH, formatM, formatS; // 시간 저장 포맷 지정

    /* 구분 */
    private final JRadioButton incomeRadioBtn = new JRadioButton("수입"); // 수입 라디오 버튼
    private final JRadioButton expenseRadioBtn = new JRadioButton("지출"); // 지출 라디오 버튼

    /* 항목 이름*/
    private final JTextField itemNameTxt = new JTextField(); // 항목 이름 텍스트 필드

    /* 항목 선택 */
    String[] items = {"선택", "<지출>", "교통", "통신", "식비", "개인", "---", "<수입>", "용돈", "월급", "기타"}; // 항목 선택 사항
    private final JComboBox itemCombo = new JComboBox(items); // 항목을 위한 itemCombo

    /* 결제 수단 */
    private final JRadioButton cardRadioBtn = new JRadioButton("카드"); // 카드 라디오 버튼

    /* 금액 */
    private final JTextField amountTxt = new JTextField(); // 금액 텍스트 필드

    /* 변수 */
    private String isCashCard; // 현금인지 카드인지 저장하는 변수
    private String isIncomeExpense; // 지출인지 수입인지 저장하는 변수
    private String getComboBoxItem; // 아이템 콤보의 텍스트 읽어 오는 변수
    String getHrs, getMin, getSec; // 최종적으로 정한 시간 저장하기 위한 변수
    static String getTime; // 최종적으로 정한 시간 하나로 모은 변수

    /* DB */
    private Connection con = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private Statement stmt = null;

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
            //System.exit(0);
        }

        MyPanel panel = new MyPanel();
        panel.setBounds(0, 0, Main.SMALL_SCREEN_WIDTH, Main.SMALL_SCREEN_HEIGHT);

        // 날짜 레이블
        JLabel dateLbl = new JLabel("날    짜");
        dateLbl.setBounds(FIRST_X, 50, 80, 30);
        dateLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(dateLbl);

        // 날짜 가져오는 레이블
        JLabel getDateLbl = new JLabel(Master.getDailyDateLbl.getText());
        getDateLbl.setBounds(SECOND_X + 50, 50, 150, 30);
        getDateLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(getDateLbl);

        // 시간 레이블
        JLabel timeLbl = new JLabel("시    간");
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
        JCheckBox useTime = new JCheckBox();
        useTime.setBounds(SECOND_X, 100, 30, 30);
        useTime.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    useTimeLbl.setVisible(true);
                    useCustomTimeLbl.setVisible(false);
                    hourTxt.setEditable(true);
                    minTxt.setEditable(true);
                    secTxt.setEditable(true);
                    hourTxt.setBackground(Color.WHITE);
                    minTxt.setBackground(Color.WHITE);
                    secTxt.setBackground(Color.WHITE);
                    hourTxt.setText("");
                    minTxt.setText("");
                    secTxt.setText("");
                    hourTxt.requestFocus();
                } else {
                    useTimeLbl.setVisible(false);
                    useCustomTimeLbl.setVisible(true);
                    hourTxt.setEditable(false);
                    minTxt.setEditable(false);
                    secTxt.setEditable(false);
                    hourTxt.setBackground(Color.LIGHT_GRAY);
                    minTxt.setBackground(Color.LIGHT_GRAY);
                    secTxt.setBackground(Color.LIGHT_GRAY);
                    hourTxt.setText(formatH.format(now)); // 추가 누른 시간의 시
                    minTxt.setText(formatM.format(now)); // 추가 누른 시간의 분
                    secTxt.setText(formatS.format(now)); // 추가 누른 시간의 초
                }
            }
        });
        layeredPane.add(useTime);

        // 시간 선택
        formatH = new SimpleDateFormat("HH"); // 시 구하기
        formatM = new SimpleDateFormat("mm"); // 분 구하기
        formatS = new SimpleDateFormat("ss"); // 초 구하기

        // 시 텍스트
        hourTxt.setBounds(SECOND_X + 40, 100, 30, 30);
        hourTxt.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        hourTxt.setText(formatH.format(now));
        hourTxt.setEditable(false);
        hourTxt.setBackground(Color.LIGHT_GRAY);
        hourTxt.setHorizontalAlignment(JTextField.CENTER);
        hourTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        hourTxt.addKeyListener(new KeyAdapter() {
            @Override
            //숫자만 입력 가능
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });
        PlainDocument document1 = (PlainDocument) hourTxt.getDocument();
        // 입력 가능한 숫자 2개로 제한
        document1.setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String string = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
                if (string.length() <= 2) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        layeredPane.add(hourTxt);

        // 시 레이블
        JLabel hourLbl = new JLabel("시");
        hourLbl.setBounds(SECOND_X + 75, 100, 30, 30);
        hourLbl.setFont(new Font("DX빨간우체통B", Font.PLAIN, 15));
        layeredPane.add(hourLbl);

        // 분 텍스트
        minTxt.setBounds(SECOND_X + 100, 100, 30, 30);
        minTxt.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        minTxt.setText(formatM.format(now));
        minTxt.setEditable(false);
        minTxt.setBackground(Color.LIGHT_GRAY);
        minTxt.setHorizontalAlignment(JTextField.CENTER);
        minTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        minTxt.addKeyListener(new KeyAdapter() {
            @Override
            // 숫자만 입력 가능
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });
        PlainDocument document2 = (PlainDocument) minTxt.getDocument();
        // 입력 가능한 숫자 2개로 제한
        document2.setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String string = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
                if (string.length() <= 2) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        layeredPane.add(minTxt);

        // 분 레이블
        JLabel minuteLbl = new JLabel("분");
        minuteLbl.setBounds(SECOND_X + 135, 100, 30, 30);
        minuteLbl.setFont(new Font("DX빨간우체통B", Font.PLAIN, 15));
        layeredPane.add(minuteLbl);

        // 초 텍스트
        secTxt.setBounds(SECOND_X + 160, 100, 30, 30);
        secTxt.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        secTxt.setText(formatS.format(now));
        secTxt.setEditable(false);
        secTxt.setBackground(Color.LIGHT_GRAY);
        secTxt.setHorizontalAlignment(JTextField.CENTER);
        secTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        secTxt.addKeyListener(new KeyAdapter() {
            @Override
            // 숫자만 입력 가능
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });
        PlainDocument document3 = (PlainDocument) secTxt.getDocument();
        // 입력 가능한 숫자 2개로 제한
        document3.setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String string = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
                if (string.length() <= 2) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        layeredPane.add(secTxt);

        // 초 레이블
        JLabel secondLbl = new JLabel("초");
        secondLbl.setBounds(SECOND_X + 195, 100, 30, 30);
        secondLbl.setFont(new Font("DX빨간우체통B", Font.PLAIN, 15));
        layeredPane.add(secondLbl);

        // 수입, 지출 레이블
        JLabel incomeexpenseLbl = new JLabel("구    분");
        incomeexpenseLbl.setBounds(FIRST_X, 150, 80, 30);
        incomeexpenseLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(incomeexpenseLbl);

        // 수입 지출 라디오 버튼
        incomeRadioBtn.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        incomeRadioBtn.setBounds(SECOND_X, 150, 100, 30);
        expenseRadioBtn.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        expenseRadioBtn.setBounds(230, 150, 100, 30);
        ButtonGroup incomexpenseTypeGroup = new ButtonGroup();
        incomexpenseTypeGroup.add(incomeRadioBtn);
        incomexpenseTypeGroup.add(expenseRadioBtn);
        layeredPane.add(incomeRadioBtn);
        layeredPane.add(expenseRadioBtn);

        // 항목 이름 레이블
        JLabel itemNameLbl = new JLabel("항목 이름");
        itemNameLbl.setBounds(FIRST_X, 200, 80, 30);
        itemNameLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(itemNameLbl);

        // 항목 이름 텍스트필드
        itemNameTxt.setBounds(SECOND_X, 200, 150, 30);
        itemNameTxt.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        itemNameTxt.setHorizontalAlignment(JTextField.CENTER);
        itemNameTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        layeredPane.add(itemNameTxt);

        // 카드, 현금 레이블
        JLabel cardcashTypeLbl = new JLabel("결제 수단");
        cardcashTypeLbl.setBounds(FIRST_X, 250, 80, 30);
        cardcashTypeLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(cardcashTypeLbl);

        // 카드, 현금 라디오 버튼
        cardRadioBtn.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        cardRadioBtn.setBounds(SECOND_X, 250, 100, 30);
        JRadioButton cashRadioBtn = new JRadioButton("현금");
        cashRadioBtn.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        cashRadioBtn.setBounds(230, 250, 100, 30);
        ButtonGroup cardcashTypeGroup = new ButtonGroup();
        cardcashTypeGroup.add(cardRadioBtn);
        cardcashTypeGroup.add(cashRadioBtn);
        layeredPane.add(cardRadioBtn);
        layeredPane.add(cashRadioBtn);

        // 항목 선택 레이블
        JLabel itemLbl = new JLabel("항    목");
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
        // 금액 레이블
        JLabel amountLbl = new JLabel("금    액");
        amountLbl.setBounds(FIRST_X, 350, 80, 30);
        amountLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        layeredPane.add(amountLbl);

        // 금액 텍스트 필드
        amountTxt.setBounds(SECOND_X, 350, 150, 30);
        amountTxt.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        amountTxt.setHorizontalAlignment(JTextField.CENTER);
        amountTxt.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        amountTxt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });
        layeredPane.add(amountTxt);

        // 추가 버튼
        // 추가 버튼
        JButton addBtn = new JButton("추가");
        addBtn.setBounds(80, 500, 70, 35);
        addBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 시간 값 저장
                getHrs = hourTxt.getText();
                getMin = minTxt.getText();
                getSec = secTxt.getText();
                getTime = (getHrs + ":" + getMin + ":" + getSec); // 시:분:토 형식

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
                        "VALUES('" + Start.getname + "', '" + isIncomeExpense + "','" + Master.getDate + "','" + itemNameTxt.getText() + "','" + isCashCard + "', '" + getComboBoxItem + "', '" + amountTxt.getText() + "','" + getTime + "')";
                updateDB(addToDB);
                if (isIncomeExpense.equals("수입")) {
                    Master.incomeTable_Model_Vector = new Vector();
                    Master.incomeTable_Model_Vector.add(getTime);
                    Master.incomeTable_Model_Vector.add(itemNameTxt.getText());
                    Master.incomeTable_Model_Vector.add(isCashCard);
                    Master.incomeTable_Model_Vector.add(getComboBoxItem);
                    Master.incomeTable_Model_Vector.add(amountTxt.getText());
                    Master.incomeTable_Model.addRow(Master.incomeTable_Model_Vector);

                    Master.incomeTable.setAutoCreateRowSorter(true);
                    TableRowSorter sorter = new TableRowSorter(Master.incomeTable.getModel());
                    Master.incomeTable.setRowSorter(sorter);

                    updateIncome(Start.getname, Master.getDate);
                } else {
                    Master.expenseTable_Model_Vector = new Vector();
                    Master.expenseTable_Model_Vector.add(getTime);
                    Master.expenseTable_Model_Vector.add(itemNameTxt.getText());
                    Master.expenseTable_Model_Vector.add(isCashCard);
                    Master.expenseTable_Model_Vector.add(getComboBoxItem);
                    Master.expenseTable_Model_Vector.add(amountTxt.getText());
                    Master.expenseTable_Model.addRow(Master.expenseTable_Model_Vector);

                    Master.expenseTable.setAutoCreateRowSorter(true);
                    TableRowSorter sorter = new TableRowSorter(Master.expenseTable.getModel());
                    Master.expenseTable.setRowSorter(sorter);

                    updateExpense(Start.getname, Master.getDate);
                }
                dispose();
            }
        });
        layeredPane.add(addBtn);

        // 취소 버튼
        // 취소 버튼
        JButton closeBtn = new JButton("취소");
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

    // 이미지를 위한 그래픽
    class MyPanel extends JPanel {
        public void paint(Graphics g) {
            g.drawImage(colorBackground, 0, 0, null);
        }
    }

    // 디비 업데이트 (디비)
    public void updateDB(String addToDB) {
        // DB 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook_project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false&autoReconnection=true", "root", "dhgusgh8520"); // 2. 드라이버 연결
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

    // 수입 데이터 업데이트 (디비)
    public void updateIncome(String name, String date) {
        // DB 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook_project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false&autoReconnection=true", "root", "dhgusgh8520"); // 2. 드라이버 연결
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

    // 지출 데이터 업데이트 (디비)
    public void updateExpense(String name, String date) {
        // DB 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook_project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false&autoReconnection=true", "root", "dhgusgh8520"); // 2. 드라이버 연결
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
