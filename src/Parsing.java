import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parsing {
    Stack<String> statusStack;
    Stack<String> tokenStack;
    List<String> inputString;
    List<String> ACTION;
    List<String> GOTO;
    List<TAC> tacList;
    List<String> AList;
    List<Integer> RList;
    Analysis analysis;
    SLRChart slrChart;
    int TACcount = 0;
    Lex lex;

    public Parsing(Analysis analysis, Lex lex, SLRChart slrChart) {
        this.analysis = analysis;
        this.lex = lex;
        this.slrChart = slrChart;
        tokenStack = new Stack<>();
        statusStack = new Stack<>();
        inputString = new ArrayList<>();
        ACTION = new ArrayList<>();
        GOTO = new ArrayList<>();
        tacList = new ArrayList<>();
        RList = new ArrayList<>();
        AList = new ArrayList<>();

        tokenStack.push("#");
        statusStack.push("I0");

    }

    private void print(int count, String ACTIONstring, String GOTOstring) {
        String statusprintString = "";
        String tokenprintString = "";
        String inputprintString = "";
        for (String s :
                tokenStack) {
            tokenprintString += s;
        }
        for (String s :
                statusStack) {
            statusprintString += s;
        }
        for (String s :
                inputString) {
            inputprintString += s;
        }
        System.out.println(count + "                 "
                + statusprintString + "                 "
                + tokenprintString + "                 "
                + inputprintString + "                 "
                + ACTIONstring + "                 "
                + GOTOstring);
    }

    public int parsing() {
        int cnt = 0;
        inputString.addAll(lex.chart);
        inputString.add("#");

        while (true) {
            String tempInput = inputString.get(0);
            String status = statusStack.peek();

            String action;
            if (analysis.terminals.contains(tempInput)) {
                action = slrChart.ACTION.get(status).get(tempInput);
            } else {
                action = slrChart.ACTION.get(status).get(lex.Lex.get(tempInput));
            }

            try {
                if (action.contains("S")) {
                    print(++cnt, action, "");
                    ACTION.add(action);
                    statusStack.push(action.replace("S", "I"));
                    tokenStack.push(tempInput);
                    inputString.remove(0);

                } else if (action.contains("r")) {

                    ACTION.add(action);
                    String tokenOut = "";
                    int GOTOnum = Integer.parseInt(action.replace("r", ""));
                    for (int i = 0; i < analysis.productions.get(GOTOnum).getRight().length; i++) {
                        statusStack.pop();
                        String tmp = tokenStack.pop();
                        if (analysis.terminals.contains(tmp))
                            tokenOut += tmp;
                        else if (analysis.terminals.contains(lex.Lex.get(tmp)))
                            tokenOut += tmp;
                    }
                    String nonterminal = analysis.productions.get(GOTOnum).getLeft();
                    tokenStack.push(nonterminal);
                    String gotoString = slrChart.GOTO.get(statusStack.peek()).get(nonterminal);
                    statusStack.push(gotoString);

                    GOTO.add(gotoString);
                    print(++cnt, action, GOTO.get(GOTO.size() - 1));
                    switch (GOTOnum) {

                        case 2:
                            emit2();
                            break;


                        case 6:
                            emit6();
                            break;
                        case 7:
                            emit7();
                            break;
                        case 12:
                            emit12();
                            break;
                        case 13:
                            emit13(tokenOut);
                            break;
                        case 16:
                            emit16(tokenOut);
                            break;
                        case 17:
                            emit17();
                            break;


                        case 19:
                            emit19();
                            break;
                        case 20:
                            emit20();
                            break;
                        case 22:
                            emit22();
                            break;
                        case 23:
                        case 24:
                        case 25:
                            emit23(tokenOut);
                            break;


                    }

                } else if (action.equals("acc")) {
                    print(++cnt, "acc", "");
                    tacList.add(new TAC("out"));
                    printTAC();
                    return 1;
                }

            } catch (Exception e) {
                return 0;
            }
        }
    }

    private void printTAC() {
        System.out.println("\n----------------输出三地址码--------------");
        for (int i = 0; i < tacList.size(); i++) {
            TAC tac = tacList.get(i);
            System.out.println("(" + (i + 1) + ") " + tac.getExpr());
        }

    }



    private void emit2() {
        for (String string :
                AList) {
            TAC tac = new TAC(string);
            tacList.add(tac);
        }
        for (int i = tacList.size() - 1; i >= 0; i--) {
            String expr = tacList.get(i).getExpr();
            if (expr.contains("goto") && expr.contains("--")) {

                tacList.get(i).setExpr(expr.replace("--", "(" + (tacList.size() + 1) + ")"));
                break;
            }
        }
        AList.clear();
        RList.clear();
    }



    private void emit6() {
        TAC tac = new TAC("if " + AList.remove(AList.size() - 1) + " goto (" + (tacList.size() + 3) + ")");
        tacList.add(tac);
        tacList.add(new TAC("goto --"));
    }

    private void emit7() {

        for (String string :
                AList) {
            TAC tac = new TAC(string);
            tacList.add(tac);
        }

        for (int i = tacList.size() - 1; i >= 0; i--) {
            String expr = tacList.get(i).getExpr();
            if (expr.contains("goto") && expr.contains("--")) {
                tacList.get(i).setExpr(expr.replace("--", "(" + (tacList.size() + 2) + ")"));
                break;
            }
        }
        tacList.add(new TAC("goto --"));
        AList.clear();
        RList.clear();
    }


    private void emit12() {
        int e1 = RList.get(RList.size() - 1);
        int e2 = RList.get(RList.size() - 2);
        RList.remove(e1);
        RList.remove(e2);
        RList.add(e1 + e2);
    }

    private void emit13(String op) {
        String e1 = AList.get(AList.size() - 2);
        String e2 = AList.get(AList.size() - 1);
        AList.remove(e1);
        AList.remove(e2);
        AList.add(e1 + " " + op + " " + e2);
    }


    private void emit16(String token) {
        String[] tokens = token.split("");

        String e2 = AList.get(AList.size() - 1);

        AList.remove(e2);
        tacList.add(new TAC(tokens[tokens.length - 1] + " = " + e2));
    }


    private void emit17() {
        String e1 = AList.get(AList.size() - 2);
        String e2 = AList.get(AList.size() - 1);
        AList.remove(e1);
        AList.remove(e2);
        tacList.add(new TAC("T" + (++TACcount) + " = " + e1 + " + " + e2));
        AList.add("T" + TACcount);
    }


    private void emit19() {
        String e1 = AList.get(AList.size() - 2);
        String e2 = AList.get(AList.size() - 1);
        AList.remove(e1);
        AList.remove(e2);
        tacList.add(new TAC("T" + (++TACcount) + " = " + e1 + " - " + e2));
        AList.add("T" + TACcount);
    }

    private void emit20() {
        String e1 = AList.get(AList.size() - 2);
        String e2 = AList.get(AList.size() - 1);
        AList.remove(e1);
        AList.remove(e2);
        tacList.add(new TAC("T" + (++TACcount) + " = " + e1 + " * " + e2));
        AList.add("T" + TACcount);
    }

    private void emit22() {
        String e1 = AList.get(AList.size() - 2);
        String e2 = AList.get(AList.size() - 1);
        AList.remove(e1);
        AList.remove(e2);
        tacList.add(new TAC("T" + (++TACcount) + " = " + e1 + " / " + e2));
        AList.add("T" + TACcount);
    }

    private void emit23(String e) {
        AList.add(e);
    }


}
