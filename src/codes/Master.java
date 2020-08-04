package codes;

/**
 * 메인 화면
 * 달력 -> 날짜 클릭 하면 해당 날짜로 이동
 * 지출 수입 확인 -> 추가 누르면 새로운 창에서 수입, 지출 입력 가능
 * 메모 -> 날짜별 메모 기
 * JTabbedPane을 이용해 그래프 형식으로 보기
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

public class Master extends JFrame {

    /* 이미지 */
    private BufferedImage colorBackground = null;

    /* 달력 */
    private static final int CAL_WIDTH = 7; // 캘린더의 너비
    private static final int CAL_HEIGHT = 6; // 캘린더의 높이
    private final int[][] calDates = new int[CAL_HEIGHT][CAL_WIDTH]; // 캘린더를 담을 배열
    private int calYear; // 년을 담을 변수
    private int calMonth; // 월을 담을 변수
    private int calDayOfMon; // 일을 담을 변수
    private final int[] calLastDateofMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}; // 1월 ~ 12월 일의 수를 담은 배열
    private final Calendar today = Calendar.getInstance(); // 추상 클래스 Calendar의 날짜 값을 가져오기 위한 메소드
    private Calendar cal; // 캘린더 지정하기 위한 변수
    private JButton todayBtn; // 오늘 날짜로 이동할 버튼
    private JButton lastYearBtn; // 저번 년도로 이동 버튼
    private JButton lastMonthBtn; // 저번 달로 이동 버튼
    private JLabel currMMYYYYLbl; // 표시 되고 있는 년/월 표시
    private JButton nextMonBtn; // 다음 년도로 이동 버튼
    private JButton nextYearBtn; // 다음 달로 이동 버튼
    ListenForCalOpButtons lForCalOpButtons = new ListenForCalOpButtons(); // 달력의 날짜를 입력 했을 때 액션 함수
    private final JButton[][] dateBtns = new JButton[6][7]; // 날짜를 담을 배열 (6 x 7)
    listenforDateBtns lforDateBtns = new listenforDateBtns(); // 날짜 이동 버튼을 입력 했을 때 액션 함수
    private final String[] WEEK_DAY_NAME = {"SUN", "MON", "TUE", "WED", "THR", "FRI", "SAT"}; // 요일 이름 담은 배열
    static String getDate; // 날짜를 표시 하기 위한 레이블
    private String getEndDate;
    private String getFirstDate;

    /* 탭 패널 */
    private JPanel jTabbedPane1_daily; // 수입과 지출을 보여줄 탭 패널
    private JPanel jTabbedPane1_report_circle; // 원형 그래프로 보여줄 탭 패널
    private JPanel jTabbedPane1_report_stick; // 막대 그래프로 보여줄 탭 패널

    /* 수입 & 지출 & 합계 */
    private final JButton addBtn = new JButton("추가"); // 수입 혹은 지출을 추가할 버튼
    private final JButton delBtn = new JButton("삭제"); // 수입 혹은 지출을 삭제할 버튼
    static JLabel getDailyDateLbl = new JLabel(); // 수입과 지출 패널에서 선택한 날짜를 보여줄 레이블
    private final Object[][] rowData = new Object[0][4]; // 테이블을 위한 배열
    private final String[] columnTitle = {"시간", "항목 이름", "결제 수단", "항목", "금액"}; // 테이블 칼럼의 제목
    static Vector incomeTable_Model_Vector; // 수입 테이블을 위한 벡터
    static DefaultTableModel incomeTable_Model; // 수입 테이블을 위한 DefaultTableModel (추가 및 삭제를 위해)
    static JTable incomeTable; // 수입 테이블
    static Vector expenseTable_Model_Vector; // 지출 테이블을 위한 벡터
    static DefaultTableModel expenseTable_Model; // 지출 테이블을 위한 DefaultTableModel (추가 및 삭제를 위해)
    static JTable expenseTable; // 지출 테이블
    private final JLabel incomeLbl = new JLabel("수입 :"); // 수입 합계를 위한 레이블
    private final JLabel expenseLbl = new JLabel("지출 :"); // 지출 합계를 위한 레이블
    private final JLabel totalLbl = new JLabel("전체 :"); // 전체 합계를 위한 레이블
    static JLabel incomeSum = new JLabel("0"); // 수입 합계를 보여 주는 레이블, 0 으로 초기화
    static JLabel expenseSum = new JLabel("0"); // 지출 합계를 보여 주는 레이블, 0 으로 초기화
    static JLabel totalSum = new JLabel("0"); // 합계 합계를 보여 주는 레이블, 0 으로 초기화
    static int totalIncomeSum = 0; // 디비에서 수입의 합을 구하기 위한 변수
    static String stringIncomeSum; // 디비에서 수입의 합을 구하기 위한 변수
    static int intIncomeSum; // 디비에서 수입의 함을 구하기 위한 변수
    static int totalExpenseSum = 0; // 디비에서 지출의 합을 구하기 위한 변수
    static String stringExpenseSum; // 디비에서 지출의 합을 구하기 위한 변수
    static int intExpenseSum; // 디비에서 지출의 합을 구하기 위한 변수
    static String stringTotalSum; // 디비에서 지출의 합을 구하기 위한 변수
    static int intTotalSum; // 디비에서 지출의 합을 구하기 위한 변수
    static int totalAmountSum = 0; // 디비에서 지출의 합을 구하기 위한 변수

    // 원형 그래프
    private final JButton refreshCircle = new JButton("새로고침");
    private final JLabel getCircleDateLbl = new JLabel(); // 원형 그래프 패널에서 선택한 날짜를 보여줄 레이블
    int arc1, arc2, arc3, arc4;
    String stringTransportSum, stringPhoneSum, stringFoodSum, stringPersonalSum;
    int monthlyExpenseTotal = 0, intTransportSum = 0, totalTransportSum = 0, intPhoneSum = 0, totalPhoneSum = 0, intFoodSum = 0, totalFoodSum = 0, intPersonalSum = 0, totalPersonalSum = 0;
    int arc1Percentage, arc2Percentage, arc3Percentage, arc4Percentage;
    DrawCircle drawCircle = new DrawCircle();

    // 막대 그래프
    private final JButton refreshStick = new JButton("새로고침");
    private final JLabel getStickDateLbl = new JLabel(); // 막대 그래프 패널에서 선택한 날짜를 보여줄 레이블
    DrawStick drawStick = new DrawStick();

    /* 메모 패널 */
    private final JTextArea memoArea = new JTextArea(""); // 메모를 위한 TextArea, ""로 초기화
    private final JLabel getMemoDateLbl = new JLabel(); // 메모의 날짜를 표시 하기 위한 레이블
    private final JLabel memoLbl = new JLabel("메모"); // 메모 레이블
    private final JButton saveMemoBtn = new JButton("저장"); // 메모 저장을 위한 버튼 (디비와 연동)
    private final JButton delMemoBtn = new JButton("삭제"); // 메모를 삭제하기 위한 버튼 (디비와 연동)
    private final JButton editMemoBtn = new JButton("수정"); // 메모릉 수정하기 위한 버튼
    private String memoContents = ""; // 메모가 빈칸 일 때

    /* DB */
    private Connection con = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private Statement stmt = null;

    /* 생성자 */
    public Master() {
        setTitle("꿀꿀이");
        setSize(Main.BIG_SCREEN_WIDTH, Main.BIG_SCREEN_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, Main.BIG_SCREEN_WIDTH, Main.BIG_SCREEN_HEIGHT);
        layeredPane.setLayout(null);

        try {
            colorBackground = ImageIO.read(new File("images/background.png"));
        } catch (IOException e) {
            System.out.println("ERROR");
            //System.exit(0);
        }

        MyPanel panel = new MyPanel();
        panel.setBounds(0, 0, Main.BIG_SCREEN_WIDTH, Main.BIG_SCREEN_HEIGHT);

        // 프로필 패널
        JPanel profilePanel = new JPanel();
        profilePanel.setBounds(0, 0, 1280, 40);
        profilePanel.setOpaque(false);
        profilePanel.setLayout(null);
        layeredPane.add(profilePanel);

        // 프로필 레이블
        JLabel showProfileLbl = new JLabel(Start.getname + "(" + Start.getalias + ")님의 계정");
        showProfileLbl.setBounds(60, 15, 200, 30);
        showProfileLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 16));
        profilePanel.add(showProfileLbl);

        // 로그아웃 버튼
        JButton logoutBtn = new JButton("로그아웃");
        logoutBtn.setBounds(1150, 15, 90, 30);
        logoutBtn.setFont(new Font("DX빨간우체통B", Font.PLAIN, 12));
        logoutBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new Start();
            }
        });
        profilePanel.add(logoutBtn);

        /* 함수 시작 */
        makeCalendar(); // 캘린더 만들기
        setToday(); // 오늘 지정
        showCal(); // 달력을 표시
        focusToday(); // 오늘 포커스
        daily(); // 수입과 지출 표시

        /* 디비에서 데이터 불러오기 */
        getDate = (today.get(Calendar.YEAR) + "/" + (today.get(Calendar.MONTH) + 1) + "/" + today.get(Calendar.DAY_OF_MONTH));

        getIncomeData(Start.getname, getDate);
        getExpenseData(Start.getname, getDate);

        getIncomeSum(Start.getname, getDate);
        getExpenseSum(Start.getname, getDate);
        getTotalSum(Start.getname, getDate);

        readMemo(Start.getname, getDate);

        getFirstDate = (today.get(Calendar.YEAR) + "/" + (today.get(Calendar.MONTH) + 1) + "/" + "01");
        getEndDate = (today.get(Calendar.YEAR) + "/" + (today.get(Calendar.MONTH) + 1) + "/" + today.getActualMaximum(Calendar.DAY_OF_MONTH));

        getMonthlyExpense(Start.getname, getFirstDate, getEndDate);
        drawCircle.repaint();
        drawStick.repaint();

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

    // 달력 만드는 함수
    public void makeCalendar() {
        JPanel calTopPanel = new JPanel();
        calTopPanel.setOpaque(false);

        todayBtn = new JButton("Today");
        todayBtn.setFont(new Font("DX빨간우체통B", Font.PLAIN, 13));
        todayBtn.setToolTipText("오늘");
        todayBtn.addActionListener(lForCalOpButtons);

        JLabel todayLbl = new JLabel(today.get(Calendar.MONTH) + 1 + "/" + today.get(Calendar.DAY_OF_MONTH) + "/" + today.get(Calendar.YEAR));
        todayLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 12));

        lastYearBtn = new JButton("<<");
        lastYearBtn.setToolTipText("이전 년도");
        lastYearBtn.addActionListener(lForCalOpButtons);

        lastMonthBtn = new JButton("<");
        lastMonthBtn.setToolTipText("이전 달");
        lastMonthBtn.addActionListener(lForCalOpButtons);

        currMMYYYYLbl = new JLabel((today.get(Calendar.MONTH) + 1) + " / " + today.get(Calendar.YEAR));
        currMMYYYYLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 20));

        nextMonBtn = new JButton(">");
        nextMonBtn.setToolTipText("다음 달");
        nextMonBtn.addActionListener(lForCalOpButtons);

        nextYearBtn = new JButton(">>");
        nextYearBtn.setToolTipText("다음 년도");
        nextYearBtn.addActionListener(lForCalOpButtons);

        calTopPanel.setLayout(new GridBagLayout());
        GridBagConstraints calOpGC = new GridBagConstraints();
        calOpGC.gridx = 1;
        calOpGC.gridy = 1;
        calOpGC.gridwidth = 2;
        calOpGC.gridheight = 1;
        calOpGC.weightx = 1;
        calOpGC.weighty = 1;
        calOpGC.insets = new Insets(5, 5, 0, 0);
        calOpGC.anchor = GridBagConstraints.WEST;
        calOpGC.fill = GridBagConstraints.NONE;
        calTopPanel.add(todayBtn, calOpGC);
        calOpGC.gridwidth = 3;
        calOpGC.gridx = 2;
        calOpGC.gridy = 1;
        calTopPanel.add(todayLbl, calOpGC);
        calOpGC.anchor = GridBagConstraints.CENTER;
        calOpGC.gridwidth = 1;
        calOpGC.gridx = 1;
        calOpGC.gridy = 2;
        calTopPanel.add(lastYearBtn, calOpGC);
        calOpGC.gridwidth = 1;
        calOpGC.gridx = 2;
        calOpGC.gridy = 2;
        calTopPanel.add(lastMonthBtn, calOpGC);
        calOpGC.gridwidth = 2;
        calOpGC.gridx = 3;
        calOpGC.gridy = 2;
        calTopPanel.add(currMMYYYYLbl, calOpGC);
        calOpGC.gridwidth = 1;
        calOpGC.gridx = 5;
        calOpGC.gridy = 2;
        calTopPanel.add(nextMonBtn, calOpGC);
        calOpGC.gridwidth = 1;
        calOpGC.gridx = 6;
        calOpGC.gridy = 2;
        calTopPanel.add(nextYearBtn, calOpGC);

        JPanel calPanel = new JPanel();
        calPanel.setOpaque(false);
        JButton[] weekDaysName = new JButton[7];
        for (int i = 0; i < CAL_WIDTH; i++) {
            weekDaysName[i] = new JButton(WEEK_DAY_NAME[i]);
            weekDaysName[i].setBorderPainted(false);
            weekDaysName[i].setContentAreaFilled(false);
            weekDaysName[i].setForeground(Color.WHITE);

            if (i == 0) weekDaysName[i].setBackground(new Color(200, 50, 50));
            else if (i == 6) weekDaysName[i].setBackground(new Color(50, 100, 200));
            else weekDaysName[i].setBackground(new Color(170, 170, 170));

            weekDaysName[i].setOpaque(true);
            weekDaysName[i].setFocusPainted(false);
            weekDaysName[i].setFont(new Font("DX빨간우체통B", Font.PLAIN, 14));
            calPanel.add(weekDaysName[i]);
        }
        for (int i = 0; i < CAL_HEIGHT; i++) {
            for (int j = 0; j < CAL_WIDTH; j++) {
                dateBtns[i][j] = new JButton();
                //dateBtns[i][j].setBorderPainted(false);
                dateBtns[i][j].setContentAreaFilled(false);
                dateBtns[i][j].setBackground(Color.WHITE);
                dateBtns[i][j].setOpaque(true);
                dateBtns[i][j].setFont(new Font("DX빨간우체통B", Font.PLAIN, 14));
                dateBtns[i][j].addActionListener(lforDateBtns);
                calPanel.add(dateBtns[i][j]);
            }
        }
        calPanel.setLayout(new GridLayout(0, 7, 2, 2));
        calPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JPanel calendarPanel = new JPanel();
        calendarPanel.setBounds(50, 50, 550, 400);
        calendarPanel.setOpaque(false);
        Dimension calTopPanelSize = calTopPanel.getPreferredSize();
        calTopPanelSize.height = 100;
        calTopPanel.setPreferredSize(calTopPanelSize);
        calendarPanel.setLayout(new BorderLayout());
        calendarPanel.add(calTopPanel, BorderLayout.NORTH);
        calendarPanel.add(calPanel, BorderLayout.CENTER);
        add(calendarPanel);
    }

    // 오늘 날짜로 지정하는 함수
    public void setToday() {
        calYear = today.get(Calendar.YEAR);
        calMonth = today.get(Calendar.MONTH);
        calDayOfMon = today.get(Calendar.DAY_OF_MONTH);

        makeCalData(today);
    }

    // 달력 데이터 만드는 함수
    public void makeCalData(Calendar cal) {
        int calStartingPos = (cal.get(Calendar.DAY_OF_WEEK) + 7 - (cal.get(Calendar.DAY_OF_MONTH)) % 7) % 7;
        int calLastDate;
        if (calMonth == 1) {
            calLastDate = calLastDateofMonth[calMonth] + leapCheck(calYear);
        } else {
            calLastDate = calLastDateofMonth[calMonth];
        }
        for (int i = 0; i < CAL_HEIGHT; i++) {
            for (int j = 0; j < CAL_WIDTH; j++) {
                calDates[i][j] = 0;
            }
        }
        for (int i = 0, num = 1, k = 0; i < CAL_HEIGHT; i++) {
            if (i == 0) k = calStartingPos;
            else k = 0;
            for (int j = k; j < CAL_WIDTH; j++) {
                if (num <= calLastDate) calDates[i][j] = num++;
            }
        }
    }

    // 윤년 구하는 함수
    private int leapCheck(int year) {
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) return 1;
        else return 0;
    }

    // 월별 혹은 연도별로 움직이는 함수
    public void moveMonth(int mon) {
        calMonth += mon;
        if (calMonth > 11) {
            while (calMonth > 11) {
                calYear++;
                calMonth -= 12;
            }
        } else if (calMonth < 0) {
            while (calMonth < 0) {
                calYear--;
                calMonth += 12;
            }
        }
        cal = new GregorianCalendar(calYear, calMonth, calDayOfMon);
        makeCalData(cal);
    }

    // 오늘을 포커스 하는 함수
    private void focusToday() {
        if (today.get(Calendar.DAY_OF_WEEK) == 1) {
            dateBtns[today.get(Calendar.WEEK_OF_MONTH)][today.get(Calendar.DAY_OF_WEEK) - 1].requestFocusInWindow();
            dateBtns[today.get(Calendar.WEEK_OF_MONTH)][today.get(Calendar.DAY_OF_WEEK) - 1].setBackground(new Color(164, 151, 254));
        } else {
            dateBtns[today.get(Calendar.WEEK_OF_MONTH) - 1][today.get(Calendar.DAY_OF_WEEK) - 1].requestFocusInWindow();
            dateBtns[today.get(Calendar.WEEK_OF_MONTH) - 1][today.get(Calendar.DAY_OF_WEEK) - 1].setBackground(new Color(164, 151, 254));
        }
    }

    // 달력 숫자 채우고 "오늘"을 표시 해주는 함수
    private void showCal() {
        for (int i = 0; i < CAL_HEIGHT; i++) {
            for (int j = 0; j < CAL_WIDTH; j++) {
                if (j == 0) {
                    dateBtns[i][j].setForeground(Color.RED);
                    dateBtns[i][j].setFont(new Font("DX빨간우체통B", Font.PLAIN, 13));
                    dateBtns[i][j].setText(Integer.toString(calDates[i][j]));
                } else if (j == 6) {
                    dateBtns[i][j].setForeground(Color.BLUE);
                    dateBtns[i][j].setFont(new Font("DX빨간우체통B", Font.PLAIN, 13));
                    dateBtns[i][j].setText(Integer.toString(calDates[i][j]));
                } else {
                    dateBtns[i][j].setForeground(Color.BLACK);
                    dateBtns[i][j].setFont(new Font("DX빨간우체통B", Font.PLAIN, 13));
                    dateBtns[i][j].setText(Integer.toString(calDates[i][j]));
                }

                // 오늘 날짜 표시하기
                if (calMonth == today.get(Calendar.MONTH) &&
                        calYear == today.get(Calendar.YEAR) &&
                        calDates[i][j] == today.get(Calendar.DAY_OF_MONTH)) {
                    dateBtns[i][j].setBorder(BorderFactory.createLineBorder(new Color(164, 151, 254), 2, true)); // 테두리 색 입히기
                } else {
                    dateBtns[i][j].setBorder(BorderFactory.createEmptyBorder()); // 빈 테두리 만들기
                }

                if (calDates[i][j] == 0) dateBtns[i][j].setVisible(false);
                else dateBtns[i][j].setVisible(true);

            }
        }

    }

    // 캘린더 버튼을 누를 때 발생하는 이벤트를 다루는 함수
    private class ListenForCalOpButtons implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == todayBtn) {
                setToday();
                lforDateBtns.actionPerformed(e);
                focusToday();
            } else if (e.getSource() == lastYearBtn) {
                moveMonth(-12);
            } else if (e.getSource() == lastMonthBtn) moveMonth(-1);
            else if (e.getSource() == nextMonBtn) moveMonth(1);
            else if (e.getSource() == nextYearBtn) moveMonth(12);

            getEndDate = calYear + "/" + (calMonth + 1) + "/" + cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            getFirstDate = (calYear + "/" + (calMonth + 1) + "/" + "01");

            for (int i = 0; i < CAL_HEIGHT; i++) {
                for (int j = 0; j < CAL_WIDTH; j++) {
                    dateBtns[i][j].setBackground(Color.WHITE);
                }
            }

            // 수입과 지출 화면을 위한 선택한 날짜 표시하기
            currMMYYYYLbl.setText((calMonth + 1) + " / " + calYear);
            showCal();

            getCircleDateLbl.setText((calMonth + 1) + " 월");

            getMonthlyExpense(Start.getname, getFirstDate, getEndDate);
            drawCircle.repaint();
            drawStick.repaint();
        }
    }

    // 날짜를 누를 때 발생하는 이벤트를 다루는 함수
    private class listenforDateBtns implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int k = 0, l = 0;
            for (int i = 0; i < CAL_HEIGHT; i++) {
                for (int j = 0; j < CAL_WIDTH; j++) {
                    dateBtns[i][j].setBackground(Color.WHITE);
                    if (e.getSource() == dateBtns[i][j]) {
                        k = i;
                        l = j;

                        if (dateBtns[i][j] == dateBtns[k][l]) {
                            dateBtns[k][l].setBackground(new Color(164, 151, 254));
                        }
                    }
                }
            }

            if (!(k == 0 && l == 0)) {
                calDayOfMon = calDates[k][l];
            }

            cal = new GregorianCalendar(calYear, calMonth, calDayOfMon);

            getDailyDateLbl.setText((calMonth + 1) + "/" + calDayOfMon + "/" + calYear);
            getMemoDateLbl.setText((calMonth + 1) + "/" + calDayOfMon + "/" + calYear);
            getDate = (calYear + "/" + (calMonth + 1) + "/" + calDayOfMon);

            getIncomeData(Start.getname, getDate);
            getExpenseData(Start.getname, getDate);

            getIncomeSum(Start.getname, getDate);
            getExpenseSum(Start.getname, getDate);

            readMemo(Start.getname, getDate);

            getTotalSum(Start.getname, getDate);
        }
    }

    // 가계부를 위한 탭 메뉴 정의하는 함수
    public void daily() {
        JTabbedPane jTabbedPane1 = new JTabbedPane(JTabbedPane.BOTTOM);

        jTabbedPane1_daily = new JPanel();
        jTabbedPane1_daily.setLayout(null);
        jTabbedPane1_daily.setBackground(jTabbedPane1.getBackground());

        jTabbedPane1_report_circle = new JPanel();
        jTabbedPane1_report_circle.setLayout(null);
        jTabbedPane1_report_circle.setBackground(jTabbedPane1.getBackground());

        jTabbedPane1_report_stick = new JPanel();
        jTabbedPane1_report_stick.setLayout(null);
        jTabbedPane1_report_stick.setBackground(jTabbedPane1.getBackground());

        jTabbedPane1.add("수입 / 지출", jTabbedPane1_daily);
        jTabbedPane1.add("원형 그래프(월)", jTabbedPane1_report_circle);
        jTabbedPane1.add("막대 그래프(월)", jTabbedPane1_report_stick);

        jTabbedPane1.setBounds(650, 50, 600, 600);
        add(jTabbedPane1);

        daily_date();
        daily_income();
        daily_expense();
        daily_total();
        daily_memo();
    }

    // 날짜 표시 및 추가 버튼을 위한 함수
    public void daily_date() {
        JPanel dailyPanel = new JPanel();
        dailyPanel.setLayout(null);
        dailyPanel.setBorder(new EtchedBorder());
        dailyPanel.setBounds(1, 0, 579, 80);
        dailyPanel.setBackground(new Color(204, 204, 255));

        JPanel circlePanel = new JPanel();
        circlePanel.setLayout(null);
        circlePanel.setBorder(new EtchedBorder());
        circlePanel.setBounds(1, 0, 579, 80);
        circlePanel.setBackground(new Color(204, 204, 255));

        drawCircle.setBounds(1, 80, 579, 475);
        drawCircle.setLayout(null);
        drawCircle.setBorder(new EtchedBorder());
        jTabbedPane1_report_circle.add(drawCircle);

        // 막대 그래프 탭에서 날짜를 위한 패널
        JPanel stickPanel = new JPanel();
        stickPanel.setLayout(null);
        stickPanel.setBorder(new EtchedBorder());
        stickPanel.setBounds(1, 0, 579, 80);
        stickPanel.setBackground(new Color(204, 204, 255));

        drawStick.setBounds(1, 80, 579, 475);
        drawStick.setLayout(null);
        drawStick.setBorder(new EtchedBorder(0));
        jTabbedPane1_report_stick.add(drawStick);

        jTabbedPane1_daily.add(dailyPanel);
        jTabbedPane1_report_circle.add(circlePanel);
        jTabbedPane1_report_stick.add(stickPanel);

        getDailyDateLbl.setText((today.get(Calendar.MONTH) + 1) + "/" + today.get(Calendar.DAY_OF_MONTH) + "/" + today.get(Calendar.YEAR));
        getDailyDateLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 18));
        getDailyDateLbl.setBounds(250, 20, 150, 35);
        dailyPanel.add(getDailyDateLbl);

        getCircleDateLbl.setText((today.get(Calendar.MONTH) + 1) + " 월");
        getCircleDateLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 20));
        getCircleDateLbl.setBounds(270, 10, 150, 70);
        circlePanel.add(getCircleDateLbl);

        getStickDateLbl.setText((today.get(Calendar.MONTH) + 1) + " 월");
        getStickDateLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 20));
        getStickDateLbl.setBounds(270, 10, 150, 70);
        stickPanel.add(getStickDateLbl);

        addBtn.setFont(new Font("DX빨간우체통B", Font.BOLD, 12));
        addBtn.setBounds(500, 20, 70, 35);
        addBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new addExpenseIncome();
            }
        });
        dailyPanel.add(addBtn);

        delBtn.setFont(new Font("DX빨간우체통B", Font.BOLD, 12));
        delBtn.setBounds(10, 20, 70, 35);
        delBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int incomeTableSelected = incomeTable.getSelectedRow();
                int expenseTableSelected = expenseTable.getSelectedRow();
                if (incomeTableSelected >= 0) {
                    Object incomeTime = incomeTable.getModel().getValueAt(incomeTableSelected, 0);
                    String incomeTimeString = incomeTime.toString();
                    String[] yesnoBtn = {"예", "아니요"};
                    int deleteResult = JOptionPane.showOptionDialog(null, "삭제하면 복구가 불가능합니다. 정말 삭제하시겠습니까?", "CONFIRM", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                            null, yesnoBtn, "아니요");
                    if (deleteResult == JOptionPane.YES_OPTION) {
                        incomeTable_Model.removeRow(incomeTableSelected);
                        try {
                            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
                            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook_project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false", "root", "dhgusgh8520"); // 2. 드라이버 연결
                        } catch (Exception f) {
                            f.printStackTrace();
                        }
                        String deleteTableRowFromDB = "DELETE FROM accountbook WHERE inputtime = '" + incomeTimeString + "' AND username = '" + Start.getname + "'";
                        updateDB(deleteTableRowFromDB);
                    }
                } else if (expenseTableSelected >= 0) {
                    Object expenseTime = expenseTable.getModel().getValueAt(expenseTableSelected, 0);
                    String expenseTimeString = expenseTime.toString();
                    String[] yesnoBtn = {"예", "아니요"};
                    int deleteResult = JOptionPane.showOptionDialog(null, "삭제하면 복구가 불가능합니다. 정말 삭제하시겠습니까?", "CONFIRM", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                            null, yesnoBtn, "아니요");
                    if (deleteResult == JOptionPane.YES_OPTION) {
                        expenseTable_Model.removeRow(expenseTableSelected);
                        try {
                            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
                            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook_project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false", "root", "dhgusgh8520"); // 2. 드라이버 연결
                        } catch (Exception f) {
                            f.printStackTrace();
                        }
                        String deleteTableRowFromDB = "DELETE FROM accountbook WHERE inputtime = '" + expenseTimeString + "' AND username = '" + Start.getname + "'";
                        updateDB(deleteTableRowFromDB);
                    }
                }

            }
        });
        dailyPanel.add(delBtn);

        refreshCircle.setFont(new Font("DX빨간우체통B", Font.BOLD, 12));
        refreshCircle.setBounds(500, 20, 70, 35);
        refreshCircle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getExpenseData(Start.getname, getDate);
                getMonthlyExpense(Start.getname, getFirstDate, getEndDate);
                drawCircle.repaint();
            }
        });
        circlePanel.add(refreshCircle);

        refreshStick.setFont(new Font("DX빨간우체통B", Font.BOLD, 12));
        refreshStick.setBounds(500, 20, 70, 35);
        refreshStick.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getExpenseData(Start.getname, getDate);
                getMonthlyExpense(Start.getname, getFirstDate, getEndDate);
                drawStick.repaint();
            }
        });
        stickPanel.add(refreshStick);
    }

    // 수입 테이블을 만드는 함수
    public void daily_income() {
        JPanel incomePanel = new JPanel();
        incomePanel.setLayout(null);
        incomePanel.setBorder(new EtchedBorder());
        incomePanel.setBackground((new Color(204, 204, 255)));
        incomePanel.setBounds(2, 80, 598, 198);
        jTabbedPane1_daily.add(incomePanel);

        JPanel incomeLblPanel = new JPanel();
        incomeLblPanel.setLayout(null);
        incomeLblPanel.setBackground((new Color(204, 204, 255)));
        JLabel incomeLbl1 = new JLabel("수");
        JLabel incomeLbl2 = new JLabel("입");
        incomeLbl1.setFont(new Font("DX빨간우체통B", Font.BOLD, 14));
        incomeLbl1.setBounds(7, 70, 14, 14);
        incomeLbl2.setFont(new Font("DX빨간우체통B", Font.BOLD, 14));
        incomeLbl2.setBounds(7, 100, 14, 14);
        incomeLblPanel.add(incomeLbl1);
        incomeLblPanel.add(incomeLbl2);
        incomeLblPanel.setBorder(new EtchedBorder());
        incomeLblPanel.setBounds(0, 0, 27, 198);
        incomePanel.add(incomeLblPanel);

        incomeTable_Model = new DefaultTableModel(rowData, columnTitle) {
            public boolean isCellEditable(int row, int column) {
                if (column >= 0) {
                    return false;
                } else {
                    return true;
                }
            }
        };
        incomeTable = new JTable(incomeTable_Model);
        JScrollPane incomePane = new JScrollPane(incomeTable);
        incomePane.setBounds(27, 0, 549, 197);
        incomePanel.add(incomePane);

    }

    // 지출 테이블을 만드는 함수
    public void daily_expense() {
        JPanel expensePanel = new JPanel();
        expensePanel.setLayout(null);
        expensePanel.setBorder(new EtchedBorder());
        expensePanel.setBackground((new Color(204, 204, 255)));
        expensePanel.setBounds(2, 279, 598, 198);
        jTabbedPane1_daily.add(expensePanel);

        JPanel expenseLblPanel = new JPanel();
        expenseLblPanel.setLayout(null);
        expenseLblPanel.setBackground((new Color(204, 204, 255)));
        JLabel expenseLbl1 = new JLabel("지");
        JLabel expenseLbl2 = new JLabel("출");
        expenseLbl1.setFont(new Font("DX빨간우체통B", Font.BOLD, 14));
        expenseLbl1.setBounds(7, 70, 14, 14);
        expenseLbl2.setFont(new Font("DX빨간우체통B", Font.BOLD, 14));
        expenseLbl2.setBounds(7, 100, 14, 14);
        expenseLblPanel.add(expenseLbl1);
        expenseLblPanel.add(expenseLbl2);
        expenseLblPanel.setBorder(new EtchedBorder());
        expenseLblPanel.setBounds(0, 0, 27, 198);
        expensePanel.add(expenseLblPanel);

        expenseTable_Model = new DefaultTableModel(rowData, columnTitle) {
            public boolean isCellEditable(int row, int column) {
                if (column >= 0) {
                    return false;
                } else {
                    return true;
                }
            }
        };
        expenseTable = new JTable(expenseTable_Model);
        JScrollPane expensePane = new JScrollPane(expenseTable);
        expensePane.setBounds(27, 0, 549, 197);
        expensePanel.add(expensePane);
    }

    // 수입의 합, 지출의 합, 전체 합을 위한 함수
    public void daily_total() {
        JPanel sumPanel = new JPanel();
        sumPanel.setLayout(null);
        sumPanel.setBorder(new EtchedBorder());
        sumPanel.setBackground((new Color(204, 204, 255)));
        sumPanel.setBounds(2, 478, 598, 100);
        jTabbedPane1_daily.add(sumPanel);

        JPanel sumLblPanel = new JPanel();
        sumLblPanel.setLayout(null);
        sumLblPanel.setBackground((new Color(204, 204, 255)));
        JLabel sumLbl1 = new JLabel("합");
        JLabel sumLbl2 = new JLabel("계");
        sumLbl1.setFont(new Font("DX빨간우체통B", Font.BOLD, 14));
        sumLbl1.setBounds(7, 15, 14, 14);
        sumLbl2.setFont(new Font("DX빨간우체통B", Font.BOLD, 14));
        sumLbl2.setBounds(7, 40, 14, 14);
        sumLblPanel.add(sumLbl1);
        sumLblPanel.add(sumLbl2);
        sumLblPanel.setBorder(new EtchedBorder());
        sumLblPanel.setBounds(0, 0, 27, 75);
        sumPanel.add(sumLblPanel);

        incomeLbl.setBounds(60, 20, 50, 30);
        incomeLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        sumPanel.add(incomeLbl);

        incomeSum.setBounds(110, 20, 100, 30);
        incomeSum.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        sumPanel.add(incomeSum);

        expenseLbl.setBounds(240, 20, 50, 30);
        expenseLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        sumPanel.add(expenseLbl);

        expenseSum.setBounds(290, 20, 100, 30);
        expenseSum.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        sumPanel.add(expenseSum);

        totalLbl.setBounds(410, 20, 50, 30);
        totalLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        sumPanel.add(totalLbl);

        totalSum.setBounds(460, 20, 100, 30);
        totalSum.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
        sumPanel.add(totalSum);

        if (Integer.parseInt(totalSum.getText()) < 0) {
            totalSum.setForeground(Color.RED);
        }
    }

    // 메모를 위한 함수
    public void daily_memo() {
        JPanel memoPanel = new JPanel();
        memoPanel.setLayout(null);
        memoPanel.setBorder(new EtchedBorder());
        memoPanel.setBounds(50, 460, 550, 160);
        memoPanel.setBackground(new Color(204, 204, 255));
        add(memoPanel);

        memoArea.setLineWrap(true);
        memoArea.setWrapStyleWord(true);
        memoArea.setEditable(false);
        memoArea.setBackground(new Color(230, 230, 230));
        JScrollPane memoScroll = new JScrollPane(memoArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        memoScroll.setBounds(10, 40, 530, 110);
        memoPanel.add(memoScroll);

        getMemoDateLbl.setText((today.get(Calendar.MONTH) + 1) + "/" + today.get(Calendar.DAY_OF_MONTH) + "/" + today.get(Calendar.YEAR));
        getMemoDateLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 14));
        getMemoDateLbl.setBounds(20, 6, 150, 35);
        memoPanel.add(getMemoDateLbl);

        memoLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 14));
        memoLbl.setBounds(250, 6, 150, 35);
        memoPanel.add(memoLbl);

        // 저장 버튼
        saveMemoBtn.setEnabled(false);
        saveMemoBtn.setFont(new Font("DX빨간우체통B", Font.BOLD, 12));
        saveMemoBtn.setBounds(420, 7, 60, 30);
        saveMemoBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 저장 이벤트
                if (checkMemo(Start.getname, getDate).equals("")) {
                    if (memoArea.getText().equals("")) {
                        return;
                    } else {
                        String addMemoToDB = "INSERT INTO memos(username,inputdate,content)" +
                                "VALUES('" + Start.getname + "','" + getDate + "','" + memoArea.getText() + "')";
                        updateDB(addMemoToDB);
                    }
                } else {
                    String updateMemo = "UPDATE memos set content = '" + memoArea.getText() + "' WHERE username= '" + Start.getname + "'  AND inputdate= '" + getDate + "' ";
                    updateDB(updateMemo);
                }
                memoArea.setBackground(new Color(230, 230, 230));
                memoArea.setEditable(false);
                editMemoBtn.setEnabled(true);
                saveMemoBtn.setEnabled(false);
                delMemoBtn.setEnabled(false);
            }
        });
        memoPanel.add(saveMemoBtn);

        // 삭제 버튼
        delMemoBtn.setEnabled(false);
        delMemoBtn.setFont(new Font("DX빨간우체통B", Font.BOLD, 12));
        delMemoBtn.setBounds(480, 7, 60, 30);
        delMemoBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 삭제 이벤트
                String[] yesnoBtn = {"예", "아니요"};
                JOptionPane.showOptionDialog(null, "삭제하면 복구가 불가능합니다. 정말 삭제하시겠습니까?", "CONFIRM", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                        null, yesnoBtn, "아니요");
                String deleteMemoFromDB = "DELETE FROM memos WHERE inputdate = '" + getDate + "'";
                updateDB(deleteMemoFromDB);
                memoArea.setText("");
            }
        });
        memoPanel.add(delMemoBtn);

        //수정 버튼
        editMemoBtn.setFont(new Font("DX빨간우체통B", Font.BOLD, 12));
        editMemoBtn.setBounds(360, 7, 60, 30);
        editMemoBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 삭제 이벤트
                if (memoArea.getText().equals("")) {
                    delMemoBtn.setEnabled(false);
                    saveMemoBtn.setEnabled(true);
                } else {
                    saveMemoBtn.setEnabled(true);
                    delMemoBtn.setEnabled(true);
                }
                memoArea.setBackground(Color.WHITE);
                memoArea.setEditable(true);
                editMemoBtn.setEnabled(false);
            }
        });
        memoPanel.add(editMemoBtn);


    }

    // 지출에 대한 원형 그래프
    private class DrawCircle extends JPanel {

        public void paintComponent(Graphics g) {

            g.clearRect(0, 0, getWidth(), getHeight());

            g.setColor(new Color(255, 101, 30));
            g.fillArc(140, 70, 300, 300, 0, arc1);
            g.setColor(new Color(66, 164, 82));
            g.fillArc(140, 70, 300, 300, arc1, arc2);
            g.setColor(new Color(24, 95, 255));
            g.fillArc(140, 70, 300, 300, arc1 + arc2, arc3);
            g.setColor(new Color(187, 6, 255));
            g.fillArc(140, 70, 300, 300, arc1 + arc2 + arc3, 360 - (arc1 + arc2 + arc3));


            g.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
            g.setColor(new Color(255, 101, 30));
            g.drawString("교통비 : " + Integer.toString(arc1Percentage) + "%", 80, 450);
            g.setColor(new Color(66, 164, 82));
            g.drawString("통신비 : " + Integer.toString(arc2Percentage) + "%", 190, 450);
            g.setColor(new Color(24, 95, 255));
            g.drawString("식  비 : " + Integer.toString(arc3Percentage) + "%", 310, 450);
            g.setColor(new Color(187, 6, 255));
            g.drawString("개  인 : " + Integer.toString(arc4Percentage) + "%", 420, 450);
        }
    }

    // 지출에 대한 막대 그래프
    private class DrawStick extends JPanel {

        public void paintComponent(Graphics g) {

            g.clearRect(0, 0, getWidth(), getHeight());

            g.drawLine(100, 350, 500, 350); // 가로

            for (int i = 1; i < 6; i++) {
                g.setColor(Color.BLACK);
                g.drawString(i * 20 + "", 60, 355 - 50 * i);
                g.setColor(Color.LIGHT_GRAY);
                g.drawLine(100, 350 - 50 * i, 500, 350 - 50 * i);
            }

            g.setColor(Color.BLACK);
            g.drawLine(100, 100, 100, 350); // 세로

            g.setColor(new Color(255, 101, 30));
            g.fillRect(130, 350 - arc1Percentage * 5 / 2, 50, arc1Percentage * 5 / 2);
            g.setColor(new Color(66, 164, 82));
            g.fillRect(230, 350 - arc2Percentage * 5 / 2, 50, arc2Percentage * 5 / 2);
            g.setColor(new Color(24, 95, 255));
            g.fillRect(330, 350 - arc3Percentage * 5 / 2, 50, arc3Percentage * 5 / 2);
            g.setColor(new Color(187, 6, 255));
            g.fillRect(430, 350 - arc4Percentage * 5 / 2, 50, arc4Percentage * 5 / 2);

            g.setFont(new Font("DX빨간우체통B", Font.BOLD, 15));
            g.setColor(new Color(255, 101, 30));
            g.drawString("교통비", 135, 370);
            g.setColor(new Color(66, 164, 82));
            g.drawString("통신비", 235, 370);
            g.setColor(new Color(24, 95, 255));
            g.drawString("식  비", 335, 370);
            g.setColor(new Color(187, 6, 255));
            g.drawString("개  인", 435, 370);
        }
    }

    // 선택 달의 지출의 전체 합 구하기 및 각 항목별 합 (디비)
    public void getMonthlyExpense(String name, String startDate, String lastDate) {
        // DB 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook_project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false&autoReconnection=true", "root", "dhgusgh8520"); // 2. 드라이버 연결
        } catch (Exception e) {
            e.printStackTrace();
        }

        monthlyExpenseTotal = 0;
        totalTransportSum = 0;
        totalPhoneSum = 0;
        totalFoodSum = 0;
        totalPersonalSum = 0;
        arc1 = 0;
        arc2 = 0;
        arc3 = 0;
        arc4 = 0;
        arc1Percentage = 0;
        arc2Percentage = 0;
        arc3Percentage = 0;
        arc4Percentage = 0;

        String SQL1 = "SELECT * FROM accountbook WHERE username=? AND incomeexpense = '지출' AND date_format(NOW(), ?) <= DATE(inputdate) AND DATE(inputdate) <= date_format(NOW(), ?) AND itemtype = '교통'";
        try {
            ps = con.prepareStatement(SQL1);
            ps.setString(1, name);
            ps.setString(2, startDate);
            ps.setString(3, lastDate);
            rs = ps.executeQuery();
            while (rs.next()) {
                stringTransportSum = rs.getString("amount");
                intTransportSum = Integer.parseInt(stringTransportSum);
                totalTransportSum += intTransportSum;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String SQL2 = "SELECT * FROM accountbook WHERE username=? AND incomeexpense = '지출' AND date_format(NOW(), ?) <= DATE(inputdate) AND DATE(inputdate) <= date_format(NOW(), ?) AND itemtype = '통신'";
        try {
            ps = con.prepareStatement(SQL2);
            ps.setString(1, name);
            ps.setString(2, startDate);
            ps.setString(3, lastDate);
            rs = ps.executeQuery();
            while (rs.next()) {
                stringPhoneSum = rs.getString("amount");
                intPhoneSum = Integer.parseInt(stringPhoneSum);
                totalPhoneSum += intPhoneSum;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String SQL3 = "SELECT * FROM accountbook WHERE username=? AND incomeexpense = '지출' AND date_format(NOW(), ?) <= DATE(inputdate) AND DATE(inputdate) <= date_format(NOW(), ?) AND itemtype = '식비'";
        try {
            ps = con.prepareStatement(SQL3);
            ps.setString(1, name);
            ps.setString(2, startDate);
            ps.setString(3, lastDate);
            rs = ps.executeQuery();
            while (rs.next()) {
                stringFoodSum = rs.getString("amount");
                intFoodSum = Integer.parseInt(stringFoodSum);
                totalFoodSum += intFoodSum;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String SQL4 = "SELECT * FROM accountbook WHERE username=? AND incomeexpense = '지출' AND date_format(NOW(), ?) <= DATE(inputdate) AND DATE(inputdate) <= date_format(NOW(), ?) AND itemtype = '개인'";
        try {
            ps = con.prepareStatement(SQL4);
            ps.setString(1, name);
            ps.setString(2, startDate);
            ps.setString(3, lastDate);
            rs = ps.executeQuery();
            while (rs.next()) {
                stringPersonalSum = rs.getString("amount");
                intPersonalSum = Integer.parseInt(stringPersonalSum);
                totalPersonalSum += intPersonalSum;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        monthlyExpenseTotal = totalTransportSum + totalPhoneSum + totalFoodSum + totalPersonalSum;
        if (monthlyExpenseTotal == 0) {
            return;
        }

        arc1 = (int) 360.0 * totalTransportSum / monthlyExpenseTotal;
        arc2 = (int) 360.0 * totalPhoneSum / monthlyExpenseTotal;
        arc3 = (int) 360.0 * totalFoodSum / monthlyExpenseTotal;
        arc4 = (int) 360.0 * totalPersonalSum / monthlyExpenseTotal;

        arc1Percentage = 100 * totalTransportSum / monthlyExpenseTotal;
        arc2Percentage = 100 * totalPhoneSum / monthlyExpenseTotal;
        arc3Percentage = 100 * totalFoodSum / monthlyExpenseTotal;
        arc4Percentage = 100 * totalPersonalSum / monthlyExpenseTotal;
    }

    // 수입 테이블 데이터를 불러오는 함수 (디비)
    public void getIncomeData(String name, String date) {
        // DB 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook_project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false&autoReconnection=true", "root", "dhgusgh8520"); // 2. 드라이버 연결
        } catch (Exception e) {
            e.printStackTrace();
        }

        String SQL = "SELECT * FROM accountbook WHERE username=? AND incomeexpense = '수입' AND inputdate=?";
        try {
            incomeTable_Model.setNumRows(0);
            ps = con.prepareStatement(SQL);
            ps.setString(1, name);
            ps.setString(2, date);
            rs = ps.executeQuery();
            while (rs.next()) {
                String inputtime = rs.getString("inputtime");
                String itemname = rs.getString("itemname");
                String cardcash = rs.getString("cardcash");
                String itemtype = rs.getString("itemtype");
                String amount = rs.getString("amount");

                incomeTable.setAutoCreateRowSorter(true);
                TableRowSorter sorter = new TableRowSorter(incomeTable.getModel());
                incomeTable.setRowSorter(sorter);

                incomeTable_Model_Vector = new Vector();
                incomeTable_Model_Vector.add(inputtime);
                incomeTable_Model_Vector.add(itemname);
                incomeTable_Model_Vector.add(cardcash);
                incomeTable_Model_Vector.add(itemtype);
                incomeTable_Model_Vector.add(amount);
                incomeTable_Model.addRow(incomeTable_Model_Vector);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 지출 테이블 데이터를 불러오는 함수 (디비)
    public void getExpenseData(String name, String date) {
        // DB 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook_project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false&autoReconnection=true", "root", "dhgusgh8520"); // 2. 드라이버 연결
        } catch (Exception e) {
            e.printStackTrace();
        }

        String SQL = "SELECT * FROM accountbook WHERE username=? AND inputdate = ? AND incomeexpense = '지출'";
        try {
            expenseTable_Model.setNumRows(0);
            ps = con.prepareStatement(SQL);
            ps.setString(1, name);
            ps.setString(2, date);
            rs = ps.executeQuery();
            while (rs.next()) {
                String inputtime = rs.getString("inputtime");
                String itemname = rs.getString("itemname");
                String cardcash = rs.getString("cardcash");
                String itemtype = rs.getString("itemtype");
                String amount = rs.getString("amount");

                expenseTable.setAutoCreateRowSorter(true);
                TableRowSorter sorter = new TableRowSorter(expenseTable.getModel());
                expenseTable.setRowSorter(sorter);

                expenseTable_Model_Vector = new Vector();
                expenseTable_Model_Vector.add(inputtime);
                expenseTable_Model_Vector.add(itemname);
                expenseTable_Model_Vector.add(cardcash);
                expenseTable_Model_Vector.add(itemtype);
                expenseTable_Model_Vector.add(amount);
                expenseTable_Model.addRow(expenseTable_Model_Vector);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 수입의 합 데이터를 불러오는 함수 (디비)
    public void getIncomeSum(String name, String date) {
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
            if (incomeTable_Model.getRowCount() == 0) {
                incomeSum.setText("0");
            }
            while (rs.next()) {
                stringIncomeSum = rs.getString("amount");
                intIncomeSum = Integer.parseInt(stringIncomeSum);
                totalIncomeSum += intIncomeSum;
                if (totalIncomeSum == 0) {
                    incomeSum.setText(Integer.toString(totalIncomeSum));
                } else {
                    incomeSum.setText(Integer.toString(totalIncomeSum) + "원");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        totalIncomeSum = 0;
    }

    // 지출의 합 데이터를 불러오는 함수 (디비)
    public void getExpenseSum(String name, String date) {
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
            if (expenseTable_Model.getRowCount() == 0) {
                expenseSum.setText("0");
            }
            while (rs.next()) {
                stringExpenseSum = rs.getString("amount");
                intExpenseSum = Integer.parseInt(stringExpenseSum);
                totalExpenseSum += intExpenseSum;
                if (totalExpenseSum == 0) {
                    expenseSum.setText(Integer.toString(totalExpenseSum));
                } else {
                    expenseSum.setText(Integer.toString(totalExpenseSum) + "원");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        totalExpenseSum = 0;
    }

    // 전체의 합 데이터를 불러오는 함수 (디비)
    public void getTotalSum(String name, String date) {
        // 디비 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook_project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false&autoReconnection=true", "root", "dhgusgh8520"); // 2. 드라이버 연결
        } catch (Exception e) {
            e.printStackTrace();
        }
        String SQL = "SELECT * FROM accountbook WHERE username=? AND inputdate=?";
        try {
            ps = con.prepareStatement(SQL);
            ps.setString(1, name);
            ps.setString(2, date);
            rs = ps.executeQuery();
            if (expenseTable_Model.getRowCount() == 0 && incomeTable_Model.getRowCount() == 0) {
                totalSum.setText("0");
            }
            while (rs.next()) {
                stringTotalSum = rs.getString("amount");
                intTotalSum = Integer.parseInt(stringTotalSum);
                totalAmountSum += intTotalSum;
                if (totalAmountSum == 0) {
                    totalSum.setText(Integer.toString(totalAmountSum));
                } else {
                    totalSum.setText(Integer.toString(totalAmountSum) + "원");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        totalAmountSum = 0;
    }

    // 메모를 불러오는 함수 (디비)
    public void readMemo(String name, String date) {
        // DB 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook_project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false&autoReconnection=true", "root", "dhgusgh8520"); // 2. 드라이버 연결
        } catch (Exception e) {
            e.printStackTrace();
        }

        String SQL = "SELECT * FROM memos WHERE username=? AND inputdate=?";
        try {
            memoArea.setText("");
            ps = con.prepareStatement(SQL);
            ps.setString(1, name);
            ps.setString(2, date);
            rs = ps.executeQuery();

            while (rs.next()) {
                String memoContents = rs.getString("content");
                memoArea.setText(memoContents);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 메모를 저장할 때 디비에 추가해야하는지 수정해야하는지 확인하는 함수
    public String checkMemo(String name, String date) {
        // DB 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook_project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false&autoReconnection=true", "root", "dhgusgh8520"); // 2. 드라이버 연결
        } catch (Exception e) {
            e.printStackTrace();
        }

        String SQL = "SELECT * FROM memos WHERE username=? AND inputdate=?";

        try {
            //memoArea.setText("");
            ps = con.prepareStatement(SQL);
            ps.setString(1, name);
            ps.setString(2, date);
            rs = ps.executeQuery();

            while (rs.next()) {
                memoContents = rs.getString("content");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return memoContents;
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

}






