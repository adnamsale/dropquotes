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
    public EntropyScorer() throws IOException {
        ArpaNgramReader anr = new ArpaNgramReader();
        InputStream is = getClass().getClassLoader().getResourceAsStream("lm_giga_20k_nvp_2gram.arpa.zip");
        ZipInputStream zis = new ZipInputStream(is);
        ZipEntry ze = zis.getNextEntry();
        model = anr.read(zis);
    }

    public double computeCrossEntropy(String sentence) {
        sentence = sentence.toLowerCase();

        TextStreamSentenceReader tssr = new TextStreamSentenceReader(new ByteArrayInputStream(sentence.getBytes()));

        for (String[] sent : tssr) {
            model.getWordEntropies(sent);
        }

        final float log2 = (float)Math.log10(2);

        String result = model.printReport();
        int start = result.indexOf("1-gram");
        int finish = result.indexOf('%', start);
        double unigram = Double.parseDouble(result.substring(start + 7, finish));
        start = result.indexOf("2-gram");
        finish = result.indexOf('%', start);
        double bigram = Double.parseDouble(result.substring(start + 7, finish));
        return unigram * 1000 + bigram;
    }

    private NgramLM model;
}
