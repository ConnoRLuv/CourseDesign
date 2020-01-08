import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("**********编译原理课程设计程序**********" + "\n源程序代码为：");
        String text = "";
        String line;
        InputStream is = new FileInputStream("Code");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine();
        while (line != null) {
            System.out.println(line);
            text += line;
            line = reader.readLine();
        }

        reader.close();
        is.close();

        Lex lex = new Lex(text);
        lex.Scanner();
        Analysis analysis = new Analysis(lex);
        SLRChart slrChart = new SLRChart(analysis);
        Parsing parsing = new Parsing(analysis, lex, slrChart);
        if (parsing.parsing() == 0) {
            System.out.println("出错");
        }

    }
}
