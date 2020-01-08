import consoletable.ConsoleTable;
import consoletable.table.Cell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SLRChart {
    private HashMap<String, List<String>> firsts;
    private HashMap<String, List<String>> follows;
    private List<Production> MoveinList;
    private List<Production> GuiyueList;
    private List<String> CanBeNull;
    Analysis analysis;
    HashMap<String, HashMap<String, String>> ACTION;
    HashMap<String, HashMap<String, String>> GOTO;

    public SLRChart(Analysis analysis) {
        this.analysis = analysis;
        firsts = new HashMap<>();
        follows = new HashMap<>();
        CanBeNull = new ArrayList<>();
        ACTION = new HashMap<>();
        GOTO = new HashMap<>();


        setFirsts();
        setFollows();
        setGOTO();
        setACTION();

        printChart();
    }

    private void printChart() {
        List<Cell> header = new ArrayList<>();
        header.add(new Cell("编号"));
        for (String String :
                analysis.terminals) {
            header.add(new Cell(String));
        }

        for (String String :
                analysis.nonterminals) {
            header.add(new Cell(String));
        }

        List<List<Cell>> body = new ArrayList<>();
        List<Cell> bodyCell;
        for (int i = 0; i < ACTION.size(); i++) {
            bodyCell = new ArrayList<>();
            bodyCell.add(new Cell("I" + i));

            for (int j = 1; j <= analysis.terminals.size(); j++) {
                Cell cell = header.get(j);
                int cnt = 0;
                for (String string :
                        ACTION.get("I" + i).keySet()) {
                    if (cell.getValue().equals(string)) {
                        bodyCell.add(new Cell(ACTION.get("I" + i).get(string)));
                    } else
                        cnt++;
                }
                if (cnt == ACTION.get("I" + i).keySet().size())
                    bodyCell.add(new Cell(""));


            }

            for (int j = analysis.terminals.size() + 1; j < header.size(); j++) {
                Cell cell = header.get(j);
                int cnt = 0;
                for (String string :
                        GOTO.get("I" + i).keySet()) {
                    if (cell.getValue().equals(string)) {
                        bodyCell.add(new Cell(GOTO.get("I" + i).get(string)));
                    } else
                        cnt++;
                }
                if (cnt == GOTO.get("I" + i).keySet().size())
                    bodyCell.add(new Cell(""));


            }


            body.add(bodyCell);
        }

        new ConsoleTable.ConsoleTableBuilder()
                .addHeaders(header)
                .addRows(body)
                .build()
                .print();
    }

    private void setGOTO() {
        for (String key :
                analysis.itemSet.keySet()) {
            GOTO.put(key, new HashMap<>());
        }

        HashMap<String, String> tempGOTO;
        for (int i = 0; i < analysis.DFA.size(); i++) {
            tempGOTO = new HashMap<>();
            for (String key :
                    analysis.DFA.get("I" + i).keySet()) {
                if (analysis.nonterminals.contains(key)) {
                    tempGOTO.put(key, analysis.DFA.get("I" + i).get(key));
                    GOTO.put("I" + i, tempGOTO);
                }
            }

        }

    }

    private void setACTION() {
        for (String key :
                analysis.itemSet.keySet()) {
            ACTION.put(key, new HashMap<>());
        }

        HashMap<String, String> tempACTION;
        for (int i = 0; i < analysis.itemSet.size(); i++) {
            tempACTION = new HashMap<>();
            for (String key :
                    analysis.DFA.get("I" + i).keySet()) {
                if (analysis.terminals.contains(key)) {
                    if (isMovein(i, key)) {
                        tempACTION.put(key, analysis.DFA.get("I" + i).get(key).replace("I", "S"));

                    } else if (isContradictory(analysis.itemSet.get("I" + i))) {
                        List<Production> tempList = new ArrayList<>(analysis.itemSet.get("I" + i));
                        List<String> movein = new ArrayList<>();
                        for (Production pro1 :
                                MoveinList) {
                            if (analysis.terminals.contains(pro1.getRight()[pro1.getPosition()]))
                                movein.add(pro1.getRight()[pro1.getPosition()]);
                        }

                        for (String string :
                                movein) {
                            tempACTION.put(string, analysis.DFA.get("I" + i).get(string).replace("I", "S"));

                        }

                        for (Production pro2 :
                                GuiyueList) {
                            for (String string : follows.get(pro2.getLeft())) {

                                tempACTION.put(string, "r" + analysis.getCount(pro2));

                            }
                        }
                    }
                }
            }
            if (analysis.getCount((analysis.itemSet.get("I" + i).get(0))) == 0
                    && analysis.itemSet.get("I" + i).get(0).getPosition() == analysis.itemSet.get("I" + i).get(0).getRight().length) {
                tempACTION.put("#", "acc");
            } else if (analysis.DFA.get("I" + i).size() == 0) {
                for (String terminal :
                        analysis.terminals) {
                    tempACTION.put(terminal, "r" + analysis.getCount(analysis.itemSet.get("I" + i).get(0)));
                }
            }


            ACTION.put("I" + i, tempACTION);

        }

    }


    private boolean isContradictory(List<Production> list) {
        MoveinList = new ArrayList<>();
        GuiyueList = new ArrayList<>();
        List<Production> tempList = new ArrayList<>(list);

        for (Production production :
                tempList) {
            if (production.getPosition() == production.getRight().length) {
                GuiyueList.add(production);
            } else
                MoveinList.add(production);
        }
        return GuiyueList.size() > 1 || (GuiyueList.size() > 0 && MoveinList.size() > 0);
    }


    private boolean isMovein(int i, String key) {
        for (Production production :
                analysis.itemSet.get("I" + i)) {
            if (production.getPosition() == production.getRight().length)
                return false;
        }
        return true;
    }

    private void setFollows() {

        List<String> follow;
        for (String nonterminal :
                analysis.nonterminals) {
            follow = new ArrayList<>();
            follows.put(nonterminal, follow);
        }

        follows.get(analysis.productions.get(0).getLeft()).add("#");

        for (String nonterminal :
                analysis.nonterminals) {
            follow = new ArrayList<>(setFollow(nonterminal));
            follows.get(nonterminal).removeAll(follow);
            follows.get(nonterminal).addAll(follow);

        }


    }

    private List<String> setFollow(String nonterminal) {

        String left;
        String[] rights;
        List<String> follow = new ArrayList<>(follows.get(nonterminal));

        for (Production production :
                analysis.productions) {
            left = production.getLeft();
            rights = production.getRight();
            for (int i = 0; i < rights.length; i++) {
                if (nonterminal.equals(rights[i])) {
                    if (i < rights.length - 1) {

                        List<String> first = new ArrayList<>(firsts.get(rights[i + 1]));
                        for (int temp = i + 1; temp < rights.length; temp++) {
                            first.removeAll(firsts.get(rights[temp]));
                            first.addAll(firsts.get(rights[temp]));
                            if (!first.contains("null")) {
                                break;
                            }
                        }

                        if (first.contains("null")) {
                            follow.removeAll(first);
                            follow.addAll(first);
                            follow.remove("null");
                            List<String> temp = setFollow(left);
                            follow.removeAll(temp);
                            follow.addAll(temp);
                        } else {
                            follow.removeAll(first);
                            follow.addAll(first);
                        }

                    } else if (i == rights.length - 1 && !rights[i].equals(left)) {
                        List<String> temp = setFollow(left);
                        follow.removeAll(temp);
                        follow.addAll(temp);
                    }

                }
            }
        }
        return follow;
    }

    private void setFirsts() {
        List<String> first;
        for (String terminal :
                analysis.terminals) {
            first = new ArrayList<>();
            first.add(terminal);
            firsts.put(terminal, first);
        }
        for (String nonterminal :
                analysis.nonterminals) {
            first = new ArrayList<>();
            firsts.put(nonterminal, first);

        }

        for (String nonterminal :
                analysis.nonterminals) {
            first = setFirst(nonterminal);
            if (CanBeNull.contains(nonterminal))
                first.add("null");
            firsts.put(nonterminal, first);
        }
    }

    private List<String> setFirst(String nonterminal) {
        String left;
        String[] rights;
        List<String> first = new ArrayList<>();

        for (Production production :
                analysis.productions) {
            if (production.getLeft().equals(nonterminal) && !production.getRight()[0].equals(nonterminal)) {
                left = nonterminal;
                rights = production.getRight();
                first = firsts.get(left);
                if (analysis.terminals.contains(rights[0])) {
                    if (!first.contains(rights[0]))
                        first.add(rights[0]);
                } else if (!CanBeNull.contains(rights[0])) {
                    List<String> temp = setFirst(rights[0]);
                    first.removeAll(temp);
                    first.addAll(temp);
                } else if (CanBeNull.contains(rights[0])) {
                    List<String> temp = setFirst(rights[0]);
                    temp.remove("null");
                    first.removeAll(temp);
                    first.addAll(temp);
                }
            }
        }
        return first;
    }


}
