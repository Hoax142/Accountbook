package codes;

/**
 *main 클래스
 * 화면의 크기를 고정
 */

public class Main {

    /* 큰화면 : 1280 * 720 // 작은 화면 : 600 * 400 */
    public static final int BIG_SCREEN_HEIGHT = 720; // 큰 화면 고정 높이
    public static final int BIG_SCREEN_WIDTH = 1280; // 큰 화면 고정 넓이

    public static final int SMALL_SCREEN_HEIGHT = 600; // 작은 화면 고정 높이
    public static final int SMALL_SCREEN_WIDTH = 400; // 작은 화면 고정 넓이

    public static void main(String[] args) {
        new Start();
    }

}