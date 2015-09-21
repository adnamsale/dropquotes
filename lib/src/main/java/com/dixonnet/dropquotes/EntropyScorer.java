package com.dixonnet.dropquotes;

import kylm.model.ngram.NgramLM;
import kylm.model.ngram.reader.ArpaNgramReader;
import kylm.reader.TextStreamSentenceReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class EntropyScorer {
    public EntropyScorer() {
        ArpaNgramReader anr = new ArpaNgramReader();
        InputStream is = getClass().getClassLoader().getResourceAsStream("lm_giga_5k_nvp_2gram.arpa.zip");
        ZipInputStream zis = new ZipInputStream(is);
        try {
            ZipEntry ze = zis.getNextEntry();
            model = anr.read(zis);
        }
        catch (IOException e) {
            System.out.println("Error loading model: " + e.getMessage());
        }
    }

    public double computeScore(String sentence) {
        sentence = sentence.toLowerCase();

        TextStreamSentenceReader tssr = new TextStreamSentenceReader(new ByteArrayInputStream(sentence.getBytes()));

        for (String[] sent : tssr) {
            model.getWordEntropies(sent);
        }

        String result = model.printReport();
        int start = result.indexOf("1-gram");
        int finish = result.indexOf('%', start);
        double unigram = Double.parseDouble(result.substring(start + 7, finish));
        start = result.indexOf("2-gram");
        finish = result.indexOf('%', start);
        double bigram = Double.parseDouble(result.substring(start + 7, finish));
        return Math.round(unigram) * 1000 + Math.round(bigram);
    }

    private NgramLM model;
}
