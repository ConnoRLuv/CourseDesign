import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Analysis {

    HashMap<String, List<Production>> itemSet;
    Lex lex;
    List<Production> productions;
    List<String> terminals;
    List<String> nonterminals;
    List<Production> endList;
    private List<Object> leftEnd;
    private List<Object> rightEnd;
    HashMap<String, HashMap<String, String>> DFA;


    public Analysis(Lex lex) {
        this.lex = lex;
        terminals = new ArrayList<>();
        nonterminals = new ArrayList<>();
        productions = new ArrayList<>();
        itemSet = new HashMap<>();
        endList = new ArrayList<>();
        leftEnd = new ArrayList<>();
        rightEnd = new ArrayList<>();
        DFA = new HashMap<>();

        readFile();
        setTerminals();
        setItemSet();


    }


    private void readFile() {
        try {
            InputStream is = new FileInputStream("Gramma");

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String left;
            String[] rights;
            String line;
            int count = 0;
            List<Production> temp = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                left = line.split("->")[0].trim();
                String s = line.split("->")[1];

                rights = s.trim().split("\\|");
                for (String right :
                        rights) {
//                    if (right.contains("<")) {
//                        String s1 = "";
//                        List<String> strings = Arrays.asList(right.split(""));
//                        for (int i = 0; i < strings.size(); i++) {
//                            if (i >= strings.indexOf("<") && i <= strings.indexOf(">")) {
//                                s1 += (strings.get(i));
//                            }
//                        }
//
//                        String[] strings1 = lex.Lex.get(s1).toArray(new String[0]);
//                        if(strings1.length == 0){
//                            Production production = new Production(right, right.trim().split(" "), left + " -> " + right.trim());
//                            productions.add(production);
//                        }
//                        for (String op :
//                                strings1) {
//
//                            Production production = new Production(left,right.replace(s1,op).trim().split(" ") , left + " -> " + right.replace(s1,op).trim());
//                            productions.add(production);
//                        }
//
//
//                    } else {
                    Production production = new Production(left, right.trim().split(" "), left + " -> " + right.trim());
                    productions.add(production);

                }

                if (!nonterminals.contains(left)) {
                    nonterminals.add(left);
                }
            }
            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTerminals() {
        String[] right;
        for (Production production :
                productions) {
            right = production.getRight();
            for (String st :
                    right) {
                if (!(nonterminals.contains(st)) && !terminals.contains(st))
                    terminals.add(st);
            }
        }
        terminals.add("#");
    }

    private void setItemSet() {
        List<Production> closure = new ArrayList<>();
        List<String> rights;
        Production production;
        for (Production pro :
                productions) {
            if (pro.getLeft().equals(productions.get(0).getLeft())) {
                closure.add(pro);
            }
        }
        itemSet.put("I0", closure);
        for (int i = 0; i < itemSet.size(); i++) {
            closure = itemSet.get("I" + i);
            rights = new ArrayList<>();
            for (int j = 0; j < closure.size(); j++) {
                production = closure.get(j);
                if (production.getPosition() != production.getRight().length) {
                    String right_pos = production.getRight()[production.getPosition()];
                    if (nonterminals.contains(right_pos) && !rights.contains(right_pos)) {
                        closure.addAll(getProdByLeft(right_pos));
                        rights.add(right_pos);
                    }
                }

            }


            rights = new ArrayList<>();
            for (Production value : closure) {
                if (value.getPosition() != value.getRight().length && !rights.contains(value.getRight()[value.getPosition()])) {
                    rights.add(value.getRight()[value.getPosition()]);
                }
            }

            HashMap<String, String> DFAString = new HashMap<>();
            for (String right : rights) {
                List<Production> temp = new ArrayList<>();
                for (Production pro :
                        closure) {
                    if (pro.getPosition() != pro.getRight().length && pro.getRight()[pro.getPosition()].equals(right)) {
                        Production production1 = new Production(pro, pro.getPosition());
                        if (production1.getPosition() != production1.getRight().length) {
                            production1.setPosition(production1.getPosition() + 1);
                            temp.add(production1);
                        }
                    }

                }
                if (temp.size() > 0) {
                    if (!isExist(temp)) {
                        int size = itemSet.size();
                        itemSet.put("I" + size, temp);
                        DFAString.put(right, "I" + size);
                    } else {
                        String id = getID(temp);
                        DFAString.put(right, id);
                    }


                }
            }
            DFA.put("I" + i, DFAString);
            isEnd();
        }


    }

    private List<Production> getItem(List<Production> temp) {
        for (List<Production> list :
                itemSet.values()) {
            for (int i = 0; i < list.size(); i++) {
                if (!list.get(i).equals(temp.get(i)))
                    break;
                if (i == list.size() - 1) {
                    return list;
                }
            }
        }
        return null;
    }

    private void isEnd() {

        for (List<Production> tempList :
                itemSet.values()) {
            for (Production production :
                    tempList) {
                if (production.getPosition() == production.getRight().length) {
                    if (!(leftEnd.contains(production.getLeft()) && rightEnd.contains(production.getRight()))) {
                        leftEnd.add(production.getLeft());
                        rightEnd.add(production.getRight());
                        endList.add(production);
                    }
                }
            }
        }
    }


    private List<Production> getProdByLeft(String left) {
        List<Production> productionList = new ArrayList<>();
        for (Production production : productions) {
            if (production.getLeft().equals(left)) {
                productionList.add(production);
            }
        }
        return productionList;
    }

    public int getCount(Production production) {
        for (Production pro :
                productions) {
            if (pro.getLeft().equals(production.getLeft()) && Arrays.equals(pro.getRight(), production.getRight())) {
                return productions.indexOf(pro);
            }
        }
        return -1;
    }

    public String getID(List<Production> production) {
        for (String key :
                itemSet.keySet()) {
            int count = 0;
            for (int i = 0; i < itemSet.get(key).size(); i++) {
                for (Production value : production) {
                    if (itemSet.get(key).get(i).equals(value)) {
                        count++;
                    }
                }

            }

            if (count == production.size()) {
                return key;
            }

        }
        return null;
    }

    private boolean isExist(List<Production> temp) {

        for (List<Production> list :
                itemSet.values()) {
            if (list.get(0).equals(temp.get(0))) {
                return true;
            }
        }
        return false;

    }


}
