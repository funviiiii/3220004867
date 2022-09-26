//题目：论文查重
//
//test
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
import java.util.HashMap;
import java.util.Map;
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

        PaperPass(originalArray, compareArray, answerPath);
    }

    // 判断字符类型
    private static int JudgeType(int tempChar) {
        if ((char) tempChar == '。' || (char) tempChar == '!' || (char) tempChar == '？' || (char) tempChar == '\n' || (char) tempChar == ';' || (char) tempChar == '>') {
            return 1;   // 判定为一个句子
        }
        else return 2; // 不是一个句子
    }

    // 论文放入数组
    private static String[] TxtArray(String paperPath) {
        String[] sentenceArray = new String[2000];
        try {
            Reader reader = null; // 字节输入流读取文件
            reader = new InputStreamReader(new FileInputStream(paperPath));

            int tempChar;
            int n = 0;
            String sentence = "";
            while ((tempChar = reader.read()) != -1) { // 还有字可以读取时
                switch (JudgeType(tempChar)) {
                    case 1:
                        if (sentence.equals("")) break; // 句子里面无内容
                        if (sentence.length() > 5) sentenceArray[n++] = sentence; // 句子里面有内容即装入数组
                        sentence = ""; // 重置
                        break;
                    case 2:
                        sentence = sentence + (char) (tempChar); // 不是一个句子，逐字装入数组
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

    // 比较函数
    private static void PaperPass(String[] originalArray, String[] compareArray, String answerPath) {
        double similarityPercentage = 0; // 重复率
        double sentencePercentage; //
        double wordNum = 0; // 总字数
        for (String paper1 : originalArray) {
            sentencePercentage = 0;
            if (paper1 == null) break; // 原文内容为空
            wordNum += paper1.length();

            for (String paper2 : compareArray) {
                if (paper2 == null) break; // 需要查重的论文为空
                Map<Character, int[]> algMap = new HashMap<>();
                for (int i = 0; i < paper1.length(); i++) {
                    char d1 = paper1.charAt(i); // 返回i处的字符
                    int[] fq = algMap.get(d1); // 返回字符对应的值
                    if (fq != null && fq.length == 2) {
                        fq[0]++; // 出现次数+1
                    } else {
                        fq = new int[2];
                        fq[0] = 1;
                        fq[1] = 0;
                        algMap.put(d1, fq);
                    }
                }
                for (int i = 0; i < paper2.length(); i++) {
                    char d2 = paper2.charAt(i);
                    int[] fq = algMap.get(d2);
                    if (fq != null && fq.length == 2) {
                        fq[1]++;
                    } else {
                        fq = new int[2];
                        fq[0] = 0;
                        fq[1] = 1;
                        algMap.put(d2, fq);
                    }
                }
                double sqdoc1 = 0;
                double sqdoc2 = 0;
                double denominator = 0;
                for (Map.Entry entry : algMap.entrySet()) {
                    int[] c = (int[]) entry.getValue(); // 放入两篇文章文字出现次数
                    denominator += c[0] * c[1];
                    sqdoc1 += c[0] * c[0];
                    sqdoc2 += c[1] * c[1];
                }
                double similarPercentage = denominator / Math.sqrt(sqdoc1 * sqdoc2);
                if (similarPercentage > sentencePercentage) {
                    sentencePercentage = similarPercentage;
                }
            }
            similarityPercentage += (sentencePercentage * paper1.length());
        }
        similarityPercentage = similarityPercentage / wordNum * 100; // 计算重复率
        similarityPercentage = (double)(Math.round(similarityPercentage*100)/100.0); // 保留两位小数
        System.out.println("与原文重复率为" + similarityPercentage + "%"); // 命令行输出结果

        // 写入文件
        File file = new File(answerPath);
        try {
            Writer writer = new FileWriter(file,false);
            writer.write("与原文重复率为" + similarityPercentage + "%");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}