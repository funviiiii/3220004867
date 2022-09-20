//题目：论文查重
//
//描述如下：
//
//设计一个论文查重算法，给出一个原文文件和一个在这份原文上经过了增删改的抄袭版论文的文件，在答案文件中输出其重复率。
//
//原文示例：今天是星期天，天气晴，今天晚上我要去看电影。
//抄袭版示例：今天是周天，天气晴朗，我晚上要去看电影。
//要求输入输出采用文件输入输出，规范如下：
//
//从命令行参数给出：论文原文的文件的绝对路径。
//从命令行参数给出：抄袭版论文的文件的绝对路径。
//从命令行参数给出：输出的答案文件的绝对路径。
//
//注意：答案文件中输出的答案为浮点型，精确到小数点后两位

// 关键点：
// 1、只看文字，不看标点符号（装入数组时丢掉标点符号）
//    !问题！这样的是整篇论文的总字数拿来判断，而不是判断句子间的重复，容易出现误判
//    解决：装入数组时判断标点符号，以一个句号/感叹号/冒号等划分句子，句子后结束装入
// 2、看字的重复率，不计较顺序（解决办法：把文章一个一个字装入数组）

import java.io.*;
import java.util.Scanner;

public class PaperPassCount {
    public static void main(String[] args) {

        String originalTextPath; // 用于比较的原文路径
        String comparePath; // 需要查重的论文路径
        String answerPath; // 答案文件路径
        String[] originalArray = new String[500];
        String[] compareArray = new String[500];

        Scanner in = new Scanner(System.in);
        System.out.println("请输入论文原文路径:");
        originalTextPath = in.nextLine();

        originalArray = TxtArray(originalTextPath);
        System.out.println("请输入需要查重的论文路径:");
        comparePath = in.nextLine();

        compareArray = TxtArray(comparePath);
        System.out.println("请输入答案储存路径:");
        answerPath = in.nextLine();
    }

    // 判断字符类型
    private static int JudgeType(int tempchar) {
        if ((char) tempchar == '。' || (char) tempchar == '!' || (char) tempchar == '？' || (char) tempchar == '\n' || (char) tempchar == ';' || (char) tempchar == '>') {
            return 1;   // 判定为一个句子
        }
        else return 2; // 不是一个句子
    }

    // 论文放入数组
    private static String[] TxtArray(String paperPath) {
        String[] sentenceArray = new String[2000];
        try {
            Reader reader = null;
            reader = new InputStreamReader(new FileInputStream(new File(paperPath)));
            int tempchar;
            int n = 0;
            String sentence = "";
            while ((tempchar = reader.read()) != -1) {
                switch (JudgeType(tempchar)) {
                    case 1:
                        if (sentence.equals("")) break;
                        if (sentence.length() > 5) sentenceArray[n++] = sentence;
                        sentence = "";
                        break;
                    case 2:
                        sentence = sentence + (char) (tempchar);
                    default:
                        break;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sentenceArray;
    }
}