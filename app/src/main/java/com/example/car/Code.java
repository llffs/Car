package com.example.car;
//
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//
//import java.util.Random;
//
//
//public class Code {
//    private static final char[] CHARS = { '1', '2', '3', '4', '5', '6', '7',
//            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'l',
//            'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
//            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
//            'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };	 //预定随机数库
//
//    private static Code bmpCode;
//
//    public static Code getInstance() {
//        if(bmpCode == null)
//            bmpCode = new Code();
//        return bmpCode;
//    }
//    private static final int CodeLength = 4; // 随机数个数
//    private static final int LineNumber =8;	 //线条数目
//    private static final int WIDTH =140, HEIGHT = 80; // 位图长、宽
//    private static final int FontSize = 40;	 //随机数字体大小
//    private static int base_padding_left ;
//    private static final int random_padding_left = 23,
//            base_padding_top = 45, random_padding_top = 10;
//    private static Random random = new Random();
//
//    private static String code;
//
//    /*********************************************************************************
//     *	方  法 名：createRandomBitmap
//     *	功能描述：生成随机验证码视图
//     *  Data     ：2015-6-6[J]
//     *********************************************************************************/
//    public static Bitmap createRandomBitmap(){
//        /**
//         * (1)生成一组随机数
//         * */
//        code = createRandomText();	 //生成4个随机数
//        /***
//         * (2)创建位图Bitmap,画布Canvas,初始化画笔Paint
//         * */
//        Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);	//创建位图，并指定其长、宽
//        Canvas canvas = new Canvas(bitmap);	 //创建指定位图的画布
//        canvas.drawColor(Color.WHITE);	 //设置画布的背景为白色
//        Paint paint = new Paint();	 //定义画笔paint
//        paint.setTextSize(FontSize);	 //设置画笔字体大小
//        /**
//         * (3)生成四个随机数风格各异(颜色、位置、形状)的位图
//         * */
//        base_padding_left = 20;
//        for(int i=0;i<code.length();i++){
//            //设置一个随机数的风格
//            int color = RandomColor();
//            paint.setColor(color);	 //设置(画笔)该随机数的颜色
//            paint.setFakeBoldText(false);//设置画笔为非粗体
//            float skewX = random.nextInt(11)/10;
//            skewX = random.nextBoolean() ? skewX :(-skewX);
//            paint.setTextSkewX(skewX);	//设置字体倾斜方向(负数表示右斜,整数表示左斜)
//            //设置一个随机数位置
////	 int padding_left = base_padding_left + random.nextInt(random_padding_left);
//            int padding_top =base_padding_top + random.nextInt(random_padding_top);
//            //绘制该随机数
//            canvas.drawText(code.charAt(i)+"" ,base_padding_left, padding_top, paint);
//            base_padding_left += random_padding_left;
//        }
//        /**
//         * (4)绘制线条
//         **/
//        for(int i=0;i<LineNumber;i++){
//            mdrawLine(canvas, paint);
//        }
//        canvas.save();	//保存
//        canvas.restore();
//        return bitmap;
//    }
//
//    public String getCode() {
//        return code;
//    }
//
//    /*********************************************************************************
//     *	方  法 名：createRandomText
//     *	功能描述：
//     *  Data     ：2015-6-6[J]
//     *********************************************************************************/
//    private static String createRandomText(){
//        StringBuilder buffer = new StringBuilder();
//        for(int i=0;i<CodeLength;i++){
//            buffer.append(CHARS[random.nextInt(CHARS.length)]);	//CHARS下标限定在0~CodeLength之间
//        }
//        return buffer.toString();	 //生成4个随机数
//    }
//    /********************************************************************************
//     *	方  法 名：RandomColor
//     *	功能描述：生成一个随机颜色
//     *  Data     ：2015-6-6[J]
//     *********************************************************************************/
//    private static int RandomColor(){
//        int red = random.nextInt(256);	 //红色：0~256之间
//        int green = random.nextInt(256);	 //绿色：0~256之间
//        int blue = random.nextInt(256);	 //蓝色：0~256之间
//        return Color.rgb(red, green, blue);	//返回生成随机颜色值
//    }
//    /*********************************************************************************
//     *	方  法 名：mdrawLine
//     *	功能描述：绘制一条线条,参数：当前画布，当前画笔
//     *  Data     ：2015-6-6[J]
//     **********************************************************************************/
//    private static void mdrawLine(Canvas canvas,Paint paint){
//        //a.设置该线条颜色
//        int color = RandomColor();
//        paint.setColor(color);
//        //b.设置该随机数的位置(起点和终点,0~WIDTH,0~HEIGHT)
//        int startX = random.nextInt(WIDTH);
//        int startY = random.nextInt(HEIGHT);
//        int stopX = random.nextInt(WIDTH);
//        int stopY = random.nextInt(HEIGHT);
//        canvas.drawLine(startX, startY, stopX, stopY, paint);
//    }
//}


//--------------------------------------------------------------------------------------------------

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

public class Code {
    /**
     * 随机数数组
     * 去除了易混淆的 数字 0 和 字母 o O
     * 数字 1 和 字母 i I l L
     * 数字 6 和 字母 b
     * 数字 9 和 字母 q
     * 字母 c C 和 G
     * 字母 t （经常和随机线混在一起看不清）
     */
    private static final char[] CHARS = {
            '2', '3', '4', '5', '7', '8',
            'a', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm',
            'n', 'p', 'r', 's', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'D', 'E', 'F', 'H', 'J', 'K', 'M',
            'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    private static Code bmpCode;

    public static Code getInstance() {
        if (bmpCode == null)
            bmpCode = new Code();
        return bmpCode;
    }

    //default settings
    //验证码默认随机数的个数
    private static final int DEFAULT_CODE_LENGTH = 4;
    //默认字体大小
    private static final int DEFAULT_FONT_SIZE = 25;
    //默认线条的条数
    private static final int DEFAULT_LINE_NUMBER = 5;
    //padding值
    private static final int BASE_PADDING_LEFT = 10, RANGE_PADDING_LEFT = 15, BASE_PADDING_TOP = 15, RANGE_PADDING_TOP = 20;
    //验证码的默认宽高
    private static final int DEFAULT_WIDTH = 100, DEFAULT_HEIGHT = 40;

    //canvas width and height
    private int width = DEFAULT_WIDTH, height = DEFAULT_HEIGHT;

    //random word space and pading_top
    private int base_padding_left = BASE_PADDING_LEFT, range_padding_left = RANGE_PADDING_LEFT,
            base_padding_top = BASE_PADDING_TOP, range_padding_top = RANGE_PADDING_TOP;

    //number of chars, lines; font size
    private int codeLength = DEFAULT_CODE_LENGTH, line_number = DEFAULT_LINE_NUMBER, font_size = DEFAULT_FONT_SIZE;

    //variables
    private String code;
    private int padding_left, padding_top;
    private Random random = new Random();

    //验证码图片
    public Bitmap createBitmap() {
        padding_left = 0;

        Bitmap bp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bp);

        code = createCode();

        c.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(font_size);
        //画验证码
        for (int i = 0; i < code.length(); i++) {
            randomTextStyle(paint);
            randomPadding();
            c.drawText(code.charAt(i) + "", padding_left, padding_top, paint);
        }
        //画线条
        for (int i = 0; i < line_number; i++) {
            drawLine(c, paint);
        }

        c.save();//保存
        c.restore();//
        return bp;
    }

    public String getCode() {
        return code;
    }

    //生成验证码
    private String createCode() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            buffer.append(CHARS[random.nextInt(CHARS.length)]);
        }
        return buffer.toString();
    }

    //画干扰线
    private void drawLine(Canvas canvas, Paint paint) {
        int color = randomColor();
        int startX = random.nextInt(width);
        int startY = random.nextInt(height);
        int stopX = random.nextInt(width);
        int stopY = random.nextInt(height);
        paint.setStrokeWidth(1);
        paint.setColor(color);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    //生成随机颜色
    private int randomColor() {
        return randomColor(1);
    }

    private int randomColor(int rate) {
        int red = random.nextInt(256) / rate;
        int green = random.nextInt(256) / rate;
        int blue = random.nextInt(256) / rate;
        return Color.rgb(red, green, blue);
    }

    //随机生成文字样式，颜色，粗细，倾斜度
    private void randomTextStyle(Paint paint) {
        int color = randomColor();
        paint.setColor(color);
        paint.setFakeBoldText(random.nextBoolean());  //true为粗体，false为非粗体
        float skewX = random.nextInt(11) / 10;
        skewX = random.nextBoolean() ? skewX : -skewX;
        paint.setTextSkewX(skewX); //float类型参数，负数表示右斜，整数左斜
        //paint.setUnderlineText(true); //true为下划线，false为非下划线
        //paint.setStrikeThruText(true); //true为删除线，false为非删除线
    }

    //随机生成padding值
    private void randomPadding() {
        padding_left += base_padding_left + random.nextInt(range_padding_left);
        padding_top = base_padding_top + random.nextInt(range_padding_top);
    }
}