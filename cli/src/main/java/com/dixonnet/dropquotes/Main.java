package com.dixonnet.dropquotes;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.IOException;
import java.util.List;

/**
 * Created by mark on 9/20/15.
 */
public class Main {
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
}
