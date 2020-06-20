package codes;

/**
 * 달력 -> 날짜 클릭 하면 해당 날짜로 이동
 * 지출 수입 확인 -> 추가 누르면 새로운 창에서 수입, 지출 입력 가능
 * JTabbedPane을 이용해 그래프 형식으로 보기
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class Master extends JFrame {

    /* 이미지 */
    private BufferedImage colorBackground = null;

    private JTabbedPane menuPane;

    /* 달력 */
    private static final int CAL_WIDTH = 7;
    private static final int CAL_HEIGHT = 6;
    private int calDates[][] = new int[CAL_HEIGHT][CAL_WIDTH];
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
    private JPanel infoPanel;
    private JLabel infoClock;
    private JLabel selectedDate;
    private final String WEEK_DAY_NAME[] = {"SUN", "MON", "TUE", "WED", "THR", "FRI", "SAT"};

    /* 수입 & 지출 */
    JPanel tab1Panel;
    JPanel addPanel;
    JButton addBtn = new JButton("추가");
    JLabel getDateLbl = new JLabel("선택 날짜 받아오기");

    JScrollPane incomePane;
    JPanel expensePanel;
    JTable incomeTable;
    String incomeHeader[] = {"이름", "분류", "메모", "결재 수단", "금액"};
    JTable expenseTable;


    /* 생성자 */
    public Master() {
        setTitle("꿀꿀이");
        setSize(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
        layeredPane.setLayout(null);

        try {
            colorBackground = ImageIO.read(new File("images/background.png"));
        } catch (IOException e) {
            System.out.println("ERROR");
            System.exit(0);
        }

        MyPanel panel = new MyPanel();
        panel.setBounds(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);

        // 탭 기능있는 패널 -> 사용 용도는 추후 정하기
        menuPane = new JTabbedPane(JTabbedPane.BOTTOM);
        menuPane.setBounds(650, 50, 600, 600);
        //JPanel test2 = new JPanel();

        tab1Panel = new JPanel();
        addPanel = new JPanel();
        addPanel.setLayout(null);
        addPanel.setBackground(Color.DARK_GRAY);

        addBtn.setFont(new Font("DX빨간우체통B", Font.BOLD, 12));
        addBtn.setBounds(500, 20, 70, 35);
        addBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new addExpenseIncome();
            }
        });
        addPanel.add(addBtn);

        getDateLbl.setFont(new Font("DX빨간우체통B", Font.BOLD, 18));
        getDateLbl.setBounds(250, 20, 150, 35);
        addPanel.add(getDateLbl);

        incomeTable = new JTable(/*incomeHeader*/);
        incomePane = new JScrollPane(incomeTable);

        Dimension addPanelSize = addPanel.getPreferredSize();
        addPanelSize.height = 80;
        addPanel.setPreferredSize(addPanelSize);
        tab1Panel.setLayout(new BorderLayout());
        tab1Panel.add(addPanel, BorderLayout.NORTH);
        tab1Panel.add(incomePane,BorderLayout.CENTER);
        menuPane.addTab("수입/지출", tab1Panel);
        //menuPane.addTab("test2", test2);
        add(menuPane);

        // 달력 패널 만들
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
        setToday();
        showCal(); // 달력을 표시


        infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout());
        infoClock = new JLabel("", SwingConstants.RIGHT);
        infoClock.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(infoClock, BorderLayout.NORTH);
        selectedDate = new JLabel("<Html><font size=3>" + (today.get(Calendar.MONTH) + 1) + "/" + today.get(Calendar.DAY_OF_MONTH) + "/" + today.get(Calendar.YEAR) + "&nbsp;(Today)</html>", SwingConstants.LEFT);
        selectedDate.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));


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
        focusToday();


        layeredPane.add(panel);
        add(layeredPane);
        setVisible(true);
    }

    class MyPanel extends JPanel {
        public void paint(Graphics g) {
            g.drawImage(colorBackground, 0, 0, null);
        }
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

    public void moveMonth(int mon) { // ÇöÀç´Þ·Î ºÎÅÍ n´Þ ÀüÈÄ¸¦ ¹Þ¾Æ ´Þ·Â ¹è¿­À» ¸¸µå´Â ÇÔ¼ö(1³âÀº +12, -12´Þ·Î ÀÌµ¿ °¡´É)
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

            String dDayString = new String();
            int dDay = ((int) ((cal.getTimeInMillis() - today.getTimeInMillis()) / 1000 / 60 / 60 / 24));
            if (dDay == 0 && (cal.get(Calendar.YEAR) == today.get(Calendar.YEAR))
                    && (cal.get(Calendar.MONTH) == today.get(Calendar.MONTH))
                    && (cal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH))) dDayString = "Today";
            else if (dDay >= 0) dDayString = "D-" + (dDay + 1);
            else if (dDay < 0) dDayString = "D+" + (dDay) * (-1);

            selectedDate.setText("<Html><font size=3>" + (calMonth + 1) + "/" + calDayOfMon + "/" + calYear + "&nbsp;(" + dDayString + ")</html>");

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
}




