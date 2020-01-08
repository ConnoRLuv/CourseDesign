import java.util.*;

public class Lex {
    List<String> chart = new ArrayList<>();
    public Map<String, String> Lex = new HashMap<>();
    private int textAt = 0;
    private int rowCount = 1;
    private String text;
    private String[] keys = {
            "main", "void", "bool", "int", "double", "char", "float", "printf",
            "class", "scanf", "else", "if", "return", "char", "public", "static"
            , "true", "false", "private", "while", "auto", "new",
            "continue", "break"
    };


    public Lex(String text) {
        this.text = text;
    }


    /**
     * 判断字符是否为字母
     *
     * @param ch 输入字符
     * @return 是字母为true
     */
    public boolean isCharacter(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    /**
     * 判断字符是否为数字
     *
     * @param ch 输入字符
     * @return 是数字为true
     */
    public boolean isNum(char ch) {
        return (ch >= '0' && ch <= '9');
    }

    /**
     * 判断字符是否为关键字
     *
     * @param string 输入字符串
     * @return 是关键字为true
     */
    public boolean isKey(String string) {
        for (String key : keys) {
            if (string.equals(key))
                return true;
        }
        return false;
    }

    /**
     * 开始对输入代码串进行分析
     */
    public void Scanner() {
        char ch;
        text = text + '\0';
        while (textAt < text.length() - 1) {
            ch = text.charAt(textAt);
            if (ch == ' ' || ch == '\t')
                textAt++;
            else if (ch == '\r' || ch == '\n') {
                textAt++;
                rowCount++;
            } else {
                textAt = ScannerAt(textAt);
            }
        }
    }

    /**
     * 逐个输入
     */
    public int ScannerAt(int textAt) {
        int i = textAt;
        char ch = text.charAt(i);
        String string;
        if (isCharacter(ch)) {
            string = "" + ch;
            return handleFirstCharacter(i, string);
        } else if (isNum(ch)) {
            string = "" + ch;
            return handleFirstNum(i, string);
        } else {
            string = "" + ch;
            switch (ch) {
                case ' ':
                case '\n':
                case '\r':
                case '\t':
                    return ++i;
                case '[':
                case ']':
                case '(':
                case ')':
                case '{':
                case '}':
                case ',':
                case '.':
                case ';':
                    printResult("界符", string);
                    return ++i;
                case '=':
                    if (text.charAt(i + 1) == '=') {
                        printResult("关系运算符", string + text.charAt(i + 1));
                        return i + 2;
                    } else {
                        printResult("赋值运算符", string);
                        return ++i;
                    }


                case '+':
                    return handlePlus(i, string);
                case '-':
                    return handleMinus(i, string);
                case '*':
                    return handleMulti(i, string);
                case '/':
                    if (text.charAt(i + 1) == '/') {
                        return handleSingleLineNote(i, string);
                    } else if (text.charAt(i + 1) == '*') {
                        return handleNote(i, string);
                    } else if (text.charAt(i + 1) == '=') {
                        return handleDiv(i, string);
                    } else
                        return handleDiv(i, string);
                case '<':
                case '>':
                case '!':
                    if (text.charAt(i + 1) == '=') {
                        printResult("关系运算符", string + text.charAt(i + 1));
                        return i + 2;
                    } else {
                        printResult("关系运算符", string);
                        return ++i;
                    }
                case '&':
                    if (text.charAt(i + 1) == '&') {
                        printResult("关系运算符", string + text.charAt(i + 1));
                        return i + 2;
                    } else {
                        printResult("关系运算符", string + text.charAt(i + 1));
                        return i + 2;
                    }
                case '|':
                    if (text.charAt(i + 1) == '|') {
                        printResult("关系运算符", string + text.charAt(i + 1));
                        return i + 2;
                    }


                case '\\':
                    if (text.charAt(i + 1) == 'n' || text.charAt(i + 1) == 't' ||
                            text.charAt(i + 1) == 'r' || text.charAt(i + 1) == '\\' ||
                            text.charAt(i + 1) == 'a' || text.charAt(i + 1) == 'v' ||
                            text.charAt(i + 1) == 'b' || text.charAt(i + 1) == 'f' ||
                            text.charAt(i + 1) == '\'' || text.charAt(i + 1) == '\"') {
                        printResult("转义符", string + text.charAt(i + 1));
                        return i + 2;
                    }
                case '\'':
                    return handleChar(i, string);
                case '\"':
                    return handleString(i, string);
                default:
                    printError("错误：暂时无法识别的标识符", string);
                    return ++i;
            }
        }
    }

    private int handleNote(int charAt, String string) {
        int i = charAt;
        char ch = text.charAt(++i);
        String st = string + ch;
        ch = text.charAt(++i);
        while (text.charAt(i) != '*' || (i + 1) < text.length() && text.charAt(i + 1) != '/') {
            st = st + ch;
            if (ch == '\n' || ch == '\r')
                rowCount++;
            else if (ch == '\0') {
                printError("错误：注释未闭合", st);
                return i;
            }
            ch = text.charAt(++i);
        }
        st = st + "*/";
        printResult("多行注释符", st);
        return i + 2;
    }

    private int handleSingleLineNote(int charAt, String string) {
        int i = charAt;
        char ch = text.charAt(++i);
        String st = string + ch;
        ch = text.charAt(++i);
        while (text.charAt(i) != '\n' && text.charAt(i) == '\r') {
            st = st + ch;
            ch = text.charAt(++i);
        }
        printResult("单行注释符", st);
        return ++i;
    }

    private int handleDiv(int charAt, String string) {
        int i = charAt;
        char ch = text.charAt(++i);
        String st = string;
        if (ch == '=') {
            st = st + ch;
            printResult("除法运算符", st);
            return ++i;

        } else {
            printResult("除法运算符", st);
            return i;
        }
    }

    private int handleMulti(int charAt, String string) {
        int i = charAt;
        char ch = text.charAt(++i);
        String st = string;
        if (ch == '=') {
            st = st + ch;
            printResult("乘法运算符", st);
            return ++i;

        } else {
            printResult("乘法运算符", st);
            return i;
        }
    }

    private int handleMinus(int charAt, String string) {
        int i = charAt;
        char ch = text.charAt(++i);
        String st = string;
        if (ch == '-') {
            st = st + ch;
            printResult("减法运算符", st);
            return ++i;
        } else if (ch == '=') {
            st = st + ch;
            printResult("减法运算符", st);
            return ++i;
        } else {
            printResult("减法运算符", st);
            return i;
        }
    }

    private int handlePlus(int charAt, String string) {
        int i = charAt;
        char ch = text.charAt(++i);
        String st = string;
        if (ch == '+') {
            st = st + ch;
            printResult("加法运算符", st);
            return ++i;
        } else if (ch == '=') {
            st = st + ch;
            printResult("加法运算符", st);
            return ++i;
        } else {
            printResult("加法运算符", st);
            return i;
        }
    }

    private int handleChar(int charAt, String string) {
        int i = charAt;
        char ch = text.charAt(++i);
        String st = string;
        while (ch != '\'') {
            if (ch == '\n' || ch == '\r')
                rowCount++;
            else if (ch == '\0') {
                printError("错误：单字符没有闭合", st);
                return i;
            }
            st = st + ch;
            ch = text.charAt(++i);
        }
        st = st + ch;
        if (st.length() == 3 || st.equals("\\'" + "\\" + "t" + "\\") || st.equals("\\'" + "\\" + "n" + "\\") || st.equals("\\'" + "\\" + "r" + "\\"))
            printResult("单字符", st);
        else
            printError("单字符溢出", st);
        return ++i;
    }

    private int handleString(int charAt, String string) {
        int i = charAt;
        char ch = text.charAt(++i);
        String st = string;
        while (ch != '\"') {
            if (ch == '\n' || ch == '\r')
                rowCount++;
            else if (ch == '\0') {
                printError("错误：字符串未闭合", st);
                return i;
            }
            st = st + ch;
            ch = text.charAt(++i);
        }
        st = st + ch;
        printResult("字符串", st);
        return ++i;
    }

    public int handleFirstCharacter(int charAt, String string) {
        int i = charAt;
        char ch = text.charAt(++i);
        String st = string;
        while (isCharacter(ch) || isNum(ch) || ch == '_') {
            st = st + ch;
            ch = text.charAt(++i);
        }
        if (st.length() == 1) {
            printResult("普通标识符", st);
            return i;
        }

        if (isKey(st)) {
            printResult("关键字", st);
            return i;
        } else {
            printResult("普通标识符", st);
            return i;
        }

    }

    public int handleFirstNum(int charAt, String string) {
        int i = charAt;
        char ch = text.charAt(++i);
        String st = string;
        while (isNum(ch)) {
            st = st + ch;
            ch = text.charAt(++i);
        }

        if (ch == ' ' || ch == ';' || ch == ',' || ch == '\n' || ch == '\r' || ch == '\0' ||
                ch == '\t' || ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%' ||
                ch == ')' || ch == ']') {
            printResult("整数", st);
            return i;
        } else if (ch == '.' && isNum(text.charAt(i + 1))) {
            st = st + ch;
            ch = text.charAt(++i);
            while (isNum(ch)) {
                st = st + ch;
                ch = text.charAt(++i);
            }
            if (ch == ' ' || ch == ';' || ch == ',' || ch == '\n' || ch == '\r' || ch == '\0' ||
                    ch == '\t' || ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%' ||
                    ch == ')' || ch == ']') {
                printResult("浮点数", st);
                return i;
            } else {
                do {
                    st = st + ch;
                    ch = text.charAt(++i);
                } while (ch != ' ' && ch != ';' && ch != ',' && ch != '\n' && ch != '\r' && ch != '\0' &&
                        ch != '\t' && ch != '+' && ch != '-' && ch != '*' && ch != '/' && ch != '%' &&
                        ch != ')' && ch != ']');
                printError("错误：输入不合法", st);
                return i;
            }
        } else {
            do {
                st = st + ch;
                ch = text.charAt(++i);
            } while (ch != ' ' && ch != ';' && ch != ',' && ch != '\n' && ch != '\r' && ch != '\0' &&
                    ch != '\t' && ch != '+' && ch != '-' && ch != '*' && ch != '/' && ch != '%' &&
                    ch != ')' && ch != ']');
            printError("错误：输入不合法", st);
            return i;
        }
    }

    public void printResult(String token, String string) {
        System.out.println(token + "：" + string);
        Lex.put(string, "<" + token + ">");
        chart.add(string);

    }

    public void printError(String error, String string) {
        System.out.println("第" + rowCount + "行" + error + "：" + string);
        System.exit(11111);
    }

}
