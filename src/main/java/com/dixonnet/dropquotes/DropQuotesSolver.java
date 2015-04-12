package com.dixonnet.dropquotes;

import au.id.bjf.dlx.DLX;
import au.id.bjf.dlx.DLXResult;
import au.id.bjf.dlx.DLXResultProcessor;
import au.id.bjf.dlx.data.ColumnObject;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class DropQuotesSolver {
    public static void main(String[] args) throws IOException {
        OptionParser parser = new OptionParser();
        OptionSpec<Dictionary.DictSize> size = parser.accepts("d", "dictionary size (s/m/l)").withRequiredArg().ofType(Dictionary.DictSize.class).defaultsTo(Dictionary.DictSize.m);
        OptionSet options = null;
        try {
            options = parser.parse(args);
            DropQuotesSolver dqs = new DropQuotesSolver();
            List<?> extraArgs = options.nonOptionArguments();
            dqs.solve(options.valueOf(size), extraArgs.isEmpty() ? "" : extraArgs.get(0).toString());
        }
        catch (Exception e) {
            parser.printHelpOn(System.out);
        }
    }

    private void solve(Dictionary.DictSize size, String path) throws IOException {
        InputStream is = null;
        if (path.isEmpty()) {
            is = getClass().getClassLoader().getResourceAsStream("test.html");
        }
        else {
            is = new FileInputStream(path);
        }
        Document doc = Jsoup.parse(is, "UTF-8", "");
        Element container = doc.select("#gametable tr:eq(1) td").first();
        clues = new ArrayList<>();
        template = new ArrayList<>();
        Hole curHole = null;
        int curPos = 0;
        for (Element child : container.children()) {
            if (child.hasAttr("align")) {
                author = child.text();
            }
            else if (child.hasClass("lettertop")) {
                String s = child.text();
                if (s.isEmpty()) {
                    clues.add(new char[0]);
                }
                else {
                    String[] letters = s.split(" ");
                    char[] ac = new char[letters.length];
                    for (int i = 0; i < ac.length; ++i) {
                        ac[i] = letters[i].charAt(0);
                    }
                    clues.add(ac);
                }
            }
            else if (child.hasClass("textbox")) {
                if (null == curHole) {
                    curHole = new Hole(curPos % clues.size(), 1);
                    template.add(curHole);
                }
                else {
                    ++curHole.length;
                }
                ++curPos;
            }
            else if (child.hasClass("boxblack")) {
                curHole = null;
                ++curPos;
            }
        }
        computeConstraints();

        Dictionary dict = new Dictionary(size);
        for (int i = 0 ; i < template.size() ; ++i) {
            addRows(dict, template.get(i), i);
        }

        ColumnObject h;
        h = DLX.buildSparseMatrix(matrix.toArray(new byte[][]{}), constraintLabels);
        DLX.solve(h, true, new ResultProcessor());
    }

    private void computeConstraints() {
        letterConstraintCount = 0;
        for (char[] arr : clues) {
            letterConstraintCount += arr.length;
        }
        constraintOffsets = new int[clues.size()];
        constraintOffsets[0] = 0;
        for (int i = 1 ; i < clues.size() ; ++i) {
            constraintOffsets[i] = constraintOffsets[i - 1] + clues.get(i - 1).length;
        }
        holeConstraintCount = template.size();
        constraintLabels = new String[letterConstraintCount + holeConstraintCount];
        int idx = 0;
        for (int i = 0 ; i < clues.size() ; ++i) {
            for (int j = 0 ; j < clues.get(i).length ; ++j) {
                constraintLabels[idx++] = "Column " + i + ": " + clues.get(i)[j];
            }
        }
        for (int i = 0 ; i < template.size() ; ++i) {
            constraintLabels[letterConstraintCount + i] = "Hole " + i;
        }
    }

    private void addRows(Dictionary dictionary, Hole hole, int holeNumber) throws IOException {
        int[] indexes = new int[hole.length];
        char[] candidate = new char[hole.length];

        int checked = 0;
        candidate[checked] = clues.get((hole.startColumn + checked) % (clues.size()))[indexes[checked]];

        mainLoop: while (true) {
            if (dictionary.isPrefix(candidate)) {
                if (hole.length - 1 == checked) {
                    byte[] row = new byte[letterConstraintCount + holeConstraintCount];
                    for (int i = 0 ; i < hole.length ; ++i) {
                        row[constraintOffsets[(hole.startColumn + i) % (clues.size())] + indexes[i]] = 1;
                    }
                    row[letterConstraintCount + holeNumber] = 1;
                    matrix.add(row);
                }
                else {
                    ++checked;
                    indexes[checked] = -1;
                }
            }
            ++indexes[checked];
            while (indexes[checked] == clues.get((hole.startColumn + checked) % (clues.size())).length) {
                candidate[checked] = '\0';
                if (0 == checked) {
                    break mainLoop;
                }
                --checked;
                ++indexes[checked];
            }
            candidate[checked] = clues.get((hole.startColumn + checked) % (clues.size()))[indexes[checked]];
        }
    }

    private class ResultProcessor implements DLXResultProcessor {
        ResultProcessor() throws IOException {
            scorer = new EntropyScorer();
            bestScore = 0;
        }

        @Override
        public boolean processResult(DLXResult result) {
            String[] answer = new String[template.size()];

            Iterator<List<Object>> walk = result.rows();
            while (walk.hasNext()) {
                int holeNumber = -1;
                List<Object> row = walk.next();
                for (int i = 0 ; i < row.size() ; ++i) {
                    if (row.get(i).toString().startsWith("Hole")) {
                        holeNumber = Integer.parseInt(row.get(i).toString().substring(5));
                        break;
                    }
                }
                if (-1 == holeNumber) {
                    throw new IllegalStateException("No matching template position for row");
                }
                char[] word = new char[template.get(holeNumber).length];
                for (int i = 0 ; i < row.size() ; ++i) {
                    String s = row.get(i).toString();
                    if (s.startsWith("Column")) {
                        int numEnd = s.indexOf(':');
                        int col = Integer.parseInt(s.substring(7, numEnd));
                        int pos = (col - template.get(holeNumber).startColumn);
                        if (pos < 0) {
                            pos += clues.size();
                        }
                        word[pos] = s.charAt(s.length() - 1);
                    }
                }
                answer[holeNumber] = new String(word);
            }
            StringBuilder sb = new StringBuilder();
            sb.append(answer[0]);
            for (int i = 1 ; i < answer.length ; ++i) {
                sb.append(" ");
                sb.append(answer[i]);
            }
            if (!answerCache.contains(sb.toString())) {
                String s = sb.toString();
                answerCache.add(s);
                double entropy = scorer.computeCrossEntropy(s);
                if (bestScore < entropy) {
                    System.out.println(entropy + " " + s);
                    bestScore = entropy;
                }
            }
            return true;
        }

        private Set<String> answerCache = new HashSet<>();
        private EntropyScorer scorer;
        private double bestScore;
    }

    private List<char[]> clues;

    private class Hole {
        Hole(int startColumn, int length) {
            this.startColumn = startColumn;
            this.length = length;
        }

        int startColumn;
        int length;
    }

    private String author;

    private List<Hole> template;

    private List<byte[]> matrix = new ArrayList<byte[]>();

    private int letterConstraintCount;
    private int holeConstraintCount;
    private int[] constraintOffsets;
    private String[] constraintLabels;
}
