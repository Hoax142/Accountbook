package codes;

/**
 * 달력 -> 날짜 클릭 하면 해당 날짜로 이동
 * 지출 수입 확인 -> 추가 누르면 새로운 창에서 수입, 지출 입력 가능
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

    static String getDate;

    /* DB */
    private Connection con = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private Statement stmt = null;

    /* 프로파일 */
    private JPanel profilePanel = new JPanel();
    private JLabel showProfileLbl = new JLabel(Start.getname + "(" + Start.getalias + ")님의 계정");
    private JButton logoutBtn = new JButton("로그아웃");

    /* 달력 */
    private static final int CAL_WIDTH = 7; // 캘린더의 너비
    private static final int CAL_HEIGHT = 6; // 캘린더의 높이
    private int calDates[][] = new int[CAL_HEIGHT][CAL_WIDTH]; // 캘린더를 담을 배열
    private int calYear;
    private int calMonth;
    private int calDayOfMon;
    private final int calLastDateofMonth[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private int calLastDate;
    private Calendar today = Calendar.getInstance();
    private Calendar cal;

    private JPanel calTopPanel;
    private JButton todayBtn;
    private JLabel todayLbl;
    private JButton lYearBtn;
    private JButton lMonthBtn;
    private JLabel currMMYYYYLbl;
    private JButton nMonBtn;
    private JButton nYearBtn;
    ListenForCalOpButtons lForCalOpButtons = new ListenForCalOpButtons();
    private JPanel calPanel;
    private JButton weekDaysName[];
    private JButton dateBtns[][] = new JButton[6][7];
    listenforDateBtns lforDateBtns = new listenforDateBtns();
    private final String WEEK_DAY_NAME[] = {"SUN", "MON", "TUE", "WED", "THR", "FRI", "SAT"};

    /* 수입 & 지출 */
    JTabbedPane jTabbedPane1;
    JPanel jTabbedPane1_daily;
    JPanel jTabbedPane1_report_circle;
    JPanel jTabbedPane1_report_stick;

    JPanel dailyPanel;
    JPanel circlePanel;
    JPanel stickPanel;
    JButton addBtn = new JButton("추가");
    JButton delBtn = new JButton("삭제");
    JButton refreshCircle = new JButton("새로고침");
    JButton refreshStick = new JButton("새로고침");
    static JLabel getDailyDateLbl = new JLabel();
    JLabel getCircleDateLbl = new JLabel();
    JLabel getStickDateLbl = new JLabel();

    JPanel incomePanel;
    JPanel incomeLblPanel;
    static Vector incomeTable_Model_Vector;
    static DefaultTableModel incomeTable_Model;
    JTable incomeTable;
    JScrollPane incomePane;

    Object[][] rowData = new Object[0][4];
    String[] columnTitle = {"시간", "항목 이름", "결제 수단", "항목", "금액"};

    JPanel expensePanel;
    JPanel expenseLblPanel;
    static Vector expenseTable_Model_Vector;
    static DefaultTableModel expenseTable_Model;
    JTable expenseTable;
    JScrollPane expensePane;

    JPanel sumPanel;
    JPanel sumLblPanel;
    JLabel incomeLbl = new JLabel("수입 :");
    JLabel expenseLbl = new JLabel("지출 :");
    JLabel totalLbl = new JLabel("전체 :");
    static JLabel incomeSum = new JLabel("0");
    static JLabel expenseSum = new JLabel("0");
    static JLabel totalSum = new JLabel("0");

    static int totalIncomeSum = 0;
    static String stringIncomeSum;
    static int intIncomeSum;

    static int totalExpenseSum = 0;
    static String stringExpenseSum;
    static int intExpenseSum;

    /* 메모 패널 */
    JPanel memoPanel;
    JTextArea memoArea = new JTextArea("");
    JScrollPane memoScroll = new JScrollPane();
    JLabel getMemoDateLbl = new JLabel();
    JLabel memoLbl = new JLabel("메모");
    JButton saveMemoBtn = new JButton("저장");
    JButton delMemoBtn = new JButton("삭제");
    JButton editMemoBtn = new JButton("수정");

    String memoContents = "";

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
            System.exit(0);
        }

        MyPanel panel = new MyPanel();
        panel.setBounds(0, 0, Main.BIG_SCREEN_WIDTH, Main.BIG_SCREEN_HEIGHT);

        showProfileLbl.setBounds(60, 15, 200, 30);
        showProfileLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 16));
        profilePanel.add(showProfileLbl);

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

        profilePanel.setBounds(0, 0, 1280, 40);
        profilePanel.setOpaque(false);
        profilePanel.setLayout(null);
        layeredPane.add(profilePanel);

        makeCalendar();
        setToday();
        showCal(); // 달력을 표시
        focusToday();
        daily(); // 수입 / 지출 표시

        getIncomeData(Start.getname, getDate);
        getExpenseData(Start.getname, getDate);
        getIncomeSum(Start.getname, getDate);
        getExpenseSum(Start.getname, getDate);
        readMemo(Start.getname, getDate);
        totalSum.setText(Integer.toString(Integer.parseInt(incomeSum.getText()) - Integer.parseInt(expenseSum.getText())));

        layeredPane.add(panel);
        add(layeredPane);
        setVisible(true);
    }

    class MyPanel extends JPanel {
        public void paint(Graphics g) {
            g.drawImage(colorBackground, 0, 0, null);
        }
    }

    public void makeCalendar() {
        calTopPanel = new JPanel();
        calTopPanel.setOpaque(false);

        todayBtn = new JButton("Today");
        todayBtn.setFont(new Font("DX빨간우체통B", Font.PLAIN, 13));
        todayBtn.setToolTipText("Today");
        todayBtn.addActionListener(lForCalOpButtons);

        todayLbl = new JLabel(today.get(Calendar.MONTH) + 1 + "/" + today.get(Calendar.DAY_OF_MONTH) + "/" + today.get(Calendar.YEAR));
        todayLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 12));

        lYearBtn = new JButton("<<");
        lYearBtn.setToolTipText("Previous Year");
        lYearBtn.addActionListener(lForCalOpButtons);

        lMonthBtn = new JButton("<");
        lMonthBtn.setToolTipText("Previous Month");
        lMonthBtn.addActionListener(lForCalOpButtons);

        currMMYYYYLbl = new JLabel("<html><table width=100><tr><th><font size=5>" + (today.get(Calendar.MONTH) + 1) + " / " + today.get(Calendar.YEAR) + "</th></tr></table></html>");
        currMMYYYYLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 20));

        nMonBtn = new JButton(">");
        nMonBtn.setToolTipText("Next Month");
        nMonBtn.addActionListener(lForCalOpButtons);

        nYearBtn = new JButton(">>");
        nYearBtn.setToolTipText("Next Year");
        nYearBtn.addActionListener(lForCalOpButtons);

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
        calTopPanel.add(lYearBtn, calOpGC);
        calOpGC.gridwidth = 1;
        calOpGC.gridx = 2;
        calOpGC.gridy = 2;
        calTopPanel.add(lMonthBtn, calOpGC);
        calOpGC.gridwidth = 2;
        calOpGC.gridx = 3;
        calOpGC.gridy = 2;
        calTopPanel.add(currMMYYYYLbl, calOpGC);
        calOpGC.gridwidth = 1;
        calOpGC.gridx = 5;
        calOpGC.gridy = 2;
        calTopPanel.add(nMonBtn, calOpGC);
        calOpGC.gridwidth = 1;
        calOpGC.gridx = 6;
        calOpGC.gridy = 2;
        calTopPanel.add(nYearBtn, calOpGC);

        calPanel = new JPanel();
        calPanel.setOpaque(false);
        weekDaysName = new JButton[7];
        for (int i = 0; i < CAL_WIDTH; i++) {
            weekDaysName[i] = new JButton(WEEK_DAY_NAME[i]);
            weekDaysName[i].setBorderPainted(false);
            weekDaysName[i].setContentAreaFilled(false);
            weekDaysName[i].setForeground(Color.WHITE);
            if (i == 0) weekDaysName[i].setBackground(new Color(200, 50, 50));
            else if (i == 6) weekDaysName[i].setBackground(new Color(50, 100, 200));
            else weekDaysName[i].setBackground(new Color(150, 150, 150));
            weekDaysName[i].setOpaque(true);
            weekDaysName[i].setFocusPainted(false);
            weekDaysName[i].setFont(new Font("DX빨간우체통B", Font.PLAIN, 14));
            calPanel.add(weekDaysName[i]);
        }
        for (int i = 0; i < CAL_HEIGHT; i++) {
            for (int j = 0; j < CAL_WIDTH; j++) {
                dateBtns[i][j] = new JButton();
                dateBtns[i][j].setBorderPainted(false);
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

    public void setToday() {
        calYear = today.get(Calendar.YEAR);
        calMonth = today.get(Calendar.MONTH);
        calDayOfMon = today.get(Calendar.DAY_OF_MONTH);
        makeCalData(today);
    }

    public void makeCalData(Calendar cal) {
        int calStartingPos = (cal.get(Calendar.DAY_OF_WEEK) + 7 - (cal.get(Calendar.DAY_OF_MONTH)) % 7) % 7;
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

    private int leapCheck(int year) {
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) return 1;
        else return 0;
    }

    public void moveMonth(int mon) {
        calMonth += mon;
        if (calMonth > 11) while (calMonth > 11) {
            calYear++;
            calMonth -= 12;
        }
        else if (calMonth < 0) while (calMonth < 0) {
            calYear--;
            calMonth += 12;
        }
        cal = new GregorianCalendar(calYear, calMonth, calDayOfMon);
        makeCalData(cal);
    }

    private void focusToday() {
        if (today.get(Calendar.DAY_OF_WEEK) == 1)
            dateBtns[today.get(Calendar.WEEK_OF_MONTH)][today.get(Calendar.DAY_OF_WEEK) - 1].requestFocusInWindow();
        else
            dateBtns[today.get(Calendar.WEEK_OF_MONTH) - 1][today.get(Calendar.DAY_OF_WEEK) - 1].requestFocusInWindow();
    }

    private void showCal() {
        for (int i = 0; i < CAL_HEIGHT; i++) {
            for (int j = 0; j < CAL_WIDTH; j++) {
                String fontColor = "black";
                if (j == 0) fontColor = "red";
                else if (j == 6) fontColor = "blue";

                File f = new File("MemoData/" + calYear + ((calMonth + 1) < 10 ? "0" : "") + (calMonth + 1) + (calDates[i][j] < 10 ? "0" : "") + calDates[i][j] + ".txt");
                if (f.exists()) {
                    dateBtns[i][j].setText("<html><b><font color=" + fontColor + ">" + calDates[i][j] + "</font></b></html>");
                } else
                    dateBtns[i][j].setText("<html><font color=" + fontColor + ">" + calDates[i][j] + "</font></html>");

                JLabel todayMark = new JLabel("<html><font color=green>*</html>");
                dateBtns[i][j].removeAll();
                if (calMonth == today.get(Calendar.MONTH) &&
                        calYear == today.get(Calendar.YEAR) &&
                        calDates[i][j] == today.get(Calendar.DAY_OF_MONTH)) {
                    dateBtns[i][j].add(todayMark);
                    dateBtns[i][j].setToolTipText("Today");
                }

                if (calDates[i][j] == 0) dateBtns[i][j].setVisible(false);
                else dateBtns[i][j].setVisible(true);
            }
        }
    }

    private class ListenForCalOpButtons implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == todayBtn) {
                setToday();
                lforDateBtns.actionPerformed(e);
                focusToday();
            } else if (e.getSource() == lYearBtn) moveMonth(-12);
            else if (e.getSource() == lMonthBtn) moveMonth(-1);
            else if (e.getSource() == nMonBtn) moveMonth(1);
            else if (e.getSource() == nYearBtn) moveMonth(12);

            currMMYYYYLbl.setText("<html><table width=100><tr><th><font size=5>" + ((calMonth + 1) < 10 ? "&nbsp;" : "") + (calMonth + 1) + " / " + calYear + "</th></tr></table></html>");
            showCal();
            getCircleDateLbl.setText((calMonth + 1) + "/" + calYear);
            getStickDateLbl.setText((calMonth + 1) + "/" + calYear);
            getMemoDateLbl.setText((calMonth + 1) + "/" + calDayOfMon + "/" + calYear);
            getDailyDateLbl.setText((calMonth + 1) + "/" + calDayOfMon + "/" + calYear);
            getDate = (calYear + "/" + (calMonth + 1) + "/" + calDayOfMon);

            getIncomeData(Start.getname, getDate);
            getExpenseData(Start.getname, getDate);

            getIncomeSum(Start.getname, getDate);
            getExpenseSum(Start.getname, getDate);

            readMemo(Start.getname, getDate);

            totalSum.setText(Integer.toString(Integer.parseInt(incomeSum.getText()) - Integer.parseInt(expenseSum.getText())));
        }
    }

    private class listenforDateBtns implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int k = 0, l = 0;
            for (int i = 0; i < CAL_HEIGHT; i++) {
                for (int j = 0; j < CAL_WIDTH; j++) {
                    if (e.getSource() == dateBtns[i][j]) {
                        k = i;
                        l = j;
                    }
                }
            }

            if (!(k == 0 && l == 0)) calDayOfMon = calDates[k][l];

            cal = new GregorianCalendar(calYear, calMonth, calDayOfMon);
            getDailyDateLbl.setText((calMonth + 1) + "/" + calDayOfMon + "/" + calYear);
            getMemoDateLbl.setText((calMonth + 1) + "/" + calDayOfMon + "/" + calYear);
            getDate = (calYear + "/" + (calMonth + 1) + "/" + calDayOfMon);

            getIncomeData(Start.getname, getDate);
            getExpenseData(Start.getname, getDate);

            getIncomeSum(Start.getname, getDate);
            getExpenseSum(Start.getname, getDate);

            readMemo(Start.getname, getDate);

            totalSum.setText(Integer.toString(Integer.parseInt(incomeSum.getText()) - Integer.parseInt(expenseSum.getText())));
        }
    }

    /*
        private class ThreadConrol extends Thread{
            public void run(){
                boolean msgCntFlag = false;
                int num = 0;
                String curStr = new String();
                while(true){
                    try{
                        today = Calendar.getInstance();
                        String amPm = (today.get(Calendar.AM_PM)==0?"AM":"PM");
                        String hour;
                        if(today.get(Calendar.HOUR) == 0) hour = "12";
                        else if(today.get(Calendar.HOUR) == 12) hour = " 0";
                        else hour = (today.get(Calendar.HOUR)<10?" ":"")+today.get(Calendar.HOUR);
                        String min = (today.get(Calendar.MINUTE)<10?"0":"")+today.get(Calendar.MINUTE);
                        String sec = (today.get(Calendar.SECOND)<10?"0":"")+today.get(Calendar.SECOND);
                        infoClock.setText(amPm+" "+hour+":"+min+":"+sec);

                        sleep(1000);
                        String infoStr = bottomInfo.getText();

                        if(infoStr != " " && (msgCntFlag == false || curStr != infoStr)){
                            num = 5;
                            msgCntFlag = true;
                            curStr = infoStr;
                        }
                        else if(infoStr != " " && msgCntFlag == true){
                            if(num > 0) num--;
                            else{
                                msgCntFlag = false;
                                bottomInfo.setText(" ");
                            }
                        }
                    }
                    catch(InterruptedException e){
                        System.out.println("Thread:Error");
                    }
                }
            }
        }*/

    // 가계부를 위한 탭 메뉴 정의
    public void daily() {
        jTabbedPane1 = new JTabbedPane(JTabbedPane.BOTTOM);
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

    // 날짜 표시 및 추가 버튼을 위한 패널
    public void daily_date() {
        dailyPanel = new JPanel();
        dailyPanel.setLayout(null);
        dailyPanel.setBorder(new EtchedBorder());
        dailyPanel.setBounds(0, 0, 600, 80);
        dailyPanel.setBackground(new Color(204, 204, 255));

        circlePanel = new JPanel();
        circlePanel.setLayout(null);
        circlePanel.setBorder(new EtchedBorder());
        circlePanel.setBounds(0, 0, 600, 80);
        circlePanel.setBackground(new Color(204, 204, 255));

        stickPanel = new JPanel();
        stickPanel.setLayout(null);
        stickPanel.setBorder(new EtchedBorder());
        stickPanel.setBounds(0, 0, 600, 80);
        stickPanel.setBackground(new Color(204, 204, 255));

        jTabbedPane1_daily.add(dailyPanel);
        jTabbedPane1_report_circle.add(circlePanel);
        jTabbedPane1_report_stick.add(stickPanel);

        getDailyDateLbl.setText((today.get(Calendar.MONTH) + 1) + "/" + today.get(Calendar.DAY_OF_MONTH) + "/" + today.get(Calendar.YEAR));
        getDailyDateLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 18));
        getDailyDateLbl.setBounds(250, 20, 150, 35);
        dailyPanel.add(getDailyDateLbl);

        getDate = (today.get(Calendar.YEAR) + "/" + (today.get(Calendar.MONTH) + 1) + "/" + today.get(Calendar.DAY_OF_MONTH));

        getCircleDateLbl.setText((today.get(Calendar.MONTH) + 1) + "/" + today.get(Calendar.YEAR));
        getCircleDateLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 18));
        getCircleDateLbl.setBounds(270, 20, 150, 35);
        circlePanel.add(getCircleDateLbl);

        getStickDateLbl.setText((today.get(Calendar.MONTH) + 1) + "/" + today.get(Calendar.YEAR));
        getStickDateLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 18));
        getStickDateLbl.setBounds(270, 20, 150, 35);
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
//                for (int i = 0; i < incomeTable.getColumnCount(); i++) {
//                    System.out.print(incomeTable.getModel().getValueAt(incomeTableSelected, i) + "\t");
//                }
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
                            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Term_Project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false", "root", "dhgusgh8520"); // 2. 드라이버 연결
                        } catch (Exception f) {
                            f.printStackTrace();
                        }
                        String deleteTableRowFromDB = "DELETE FROM accountbook WHERE inputtime = '" + incomeTimeString + "' AND username = '" + Start.getname + "'";
                        updateDB(deleteTableRowFromDB);
                    } else {
                        return;
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
                            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Term_Project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false", "root", "dhgusgh8520"); // 2. 드라이버 연결
                        } catch (Exception f) {
                            f.printStackTrace();
                        }
                        String deleteTableRowFromDB = "DELETE FROM accountbook WHERE inputtime = '" + expenseTimeString + "' AND username = '" + Start.getname + "'";
                        updateDB(deleteTableRowFromDB);
                    } else {
                        return;
                    }
                }

            }
        });

        dailyPanel.add(delBtn);

        refreshCircle.setFont(new Font("DX빨간우체통B", Font.BOLD, 12));
        refreshCircle.setBounds(500, 20, 70, 35);
        circlePanel.add(refreshCircle);

        refreshStick.setFont(new Font("DX빨간우체통B", Font.BOLD, 12));
        refreshStick.setBounds(500, 20, 70, 35);
        stickPanel.add(refreshStick);
    }

    // 수입을 위한 패널
    public void daily_income() {
        incomePanel = new JPanel();
        incomePanel.setLayout(null);
        incomePanel.setBorder(new EtchedBorder());
        incomePanel.setBackground((new Color(204, 204, 255)));
        incomePanel.setBounds(2, 80, 598, 198);
        jTabbedPane1_daily.add(incomePanel);

        incomeLblPanel = new JPanel();
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
        incomePane = new JScrollPane(incomeTable);
        incomePane.setBounds(27, 0, 549, 197);
        incomePanel.add(incomePane);

    }

    // 지출을 위한 패널
    public void daily_expense() {
        expensePanel = new JPanel();
        expensePanel.setLayout(null);
        expensePanel.setBorder(new EtchedBorder());
        expensePanel.setBackground((new Color(204, 204, 255)));
        expensePanel.setBounds(2, 279, 598, 198);
        jTabbedPane1_daily.add(expensePanel);

        expenseLblPanel = new JPanel();
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
        expensePane = new JScrollPane(expenseTable);
        expensePane.setBounds(27, 0, 549, 197);
        expensePanel.add(expensePane);
    }

    // 수입합, 지출합, 전체합 보여주는 패널
    public void daily_total() {
        sumPanel = new JPanel();
        sumPanel.setLayout(null);
        sumPanel.setBorder(new EtchedBorder());
        sumPanel.setBackground((new Color(204, 204, 255)));
        sumPanel.setBounds(2, 478, 598, 100);
        jTabbedPane1_daily.add(sumPanel);

        sumLblPanel = new JPanel();
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
    }

    // 메모를 따로 볼 수 있게 해주는 패널
    public void daily_memo() {
        memoPanel = new JPanel();
        memoPanel.setLayout(null);
        memoPanel.setBorder(new EtchedBorder());
        memoPanel.setBounds(50, 460, 550, 160);
        memoPanel.setBackground(new Color(204, 204, 255));
        add(memoPanel);

        memoArea.setLineWrap(true);
        memoArea.setWrapStyleWord(true);
        memoArea.setEditable(false);
        memoArea.setBackground(new Color(230, 230, 230));
        memoScroll = new JScrollPane(memoArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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

    public void getIncomeData(String name, String date) {
        // DB 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Term_Project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false", "root", "dhgusgh8520"); // 2. 드라이버 연결
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

                incomeTable_Model_Vector = new Vector();
                incomeTable_Model_Vector.add(inputtime);
                incomeTable_Model_Vector.add(itemname);
                incomeTable_Model_Vector.add(cardcash);
                incomeTable_Model_Vector.add(itemtype);
                incomeTable_Model_Vector.add(amount);
                incomeTable_Model.addRow(incomeTable_Model_Vector);

                incomeTable.setAutoCreateRowSorter(true);
                TableRowSorter sorter = new TableRowSorter(incomeTable.getModel());
                incomeTable.setRowSorter(sorter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getExpenseData(String name, String date) {
        // DB 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Term_Project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false", "root", "dhgusgh8520"); // 2. 드라이버 연결
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

                expenseTable_Model_Vector = new Vector();
                expenseTable_Model_Vector.add(inputtime);
                expenseTable_Model_Vector.add(itemname);
                expenseTable_Model_Vector.add(cardcash);
                expenseTable_Model_Vector.add(itemtype);
                expenseTable_Model_Vector.add(amount);
                expenseTable_Model.addRow(expenseTable_Model_Vector);

                expenseTable.setAutoCreateRowSorter(true);
                TableRowSorter sorter = new TableRowSorter(expenseTable.getModel());
                expenseTable.setRowSorter(sorter);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getIncomeSum(String name, String date) {
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
            if (incomeTable_Model.getRowCount() == 0) {
                incomeSum.setText("0");
            }
            while (rs.next()) {
                stringIncomeSum = rs.getString("amount");
                intIncomeSum = Integer.parseInt(stringIncomeSum);
                totalIncomeSum += intIncomeSum;
                incomeSum.setText(Integer.toString(totalIncomeSum));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        totalIncomeSum = 0;
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
            if (expenseTable_Model.getRowCount() == 0) {
                expenseSum.setText("0");
            }
            while (rs.next()) {
                stringExpenseSum = rs.getString("amount");
                intExpenseSum = Integer.parseInt(stringExpenseSum);
                totalExpenseSum += intExpenseSum;
                expenseSum.setText(Integer.toString(totalExpenseSum));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        totalExpenseSum = 0;
    }

    public void readMemo(String name, String date) {
        // DB 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Term_Project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false", "root", "dhgusgh8520"); // 2. 드라이버 연결
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

    public String checkMemo(String name, String date) {
        // DB 연결 시도
        try {
            Class.forName("com.mysql.jdbc.Driver"); // 1. 드라이버 로딩
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Term_Project?serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false", "root", "dhgusgh8520"); // 2. 드라이버 연결
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






