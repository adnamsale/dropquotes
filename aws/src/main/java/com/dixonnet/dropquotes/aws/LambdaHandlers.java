package com.dixonnet.dropquotes.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.dixonnet.dropquotes.Dictionary;
import com.dixonnet.dropquotes.DropQuotesSolver;
import org.json.simple.JSONObject;

import java.io.ByteArrayInputStream;

/**
 * Created by mark on 9/20/15.
 */
public class LambdaHandlers {
    public static class RequestData {
        public String getSrc() {
            return this.src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        private String src;
    }

    public static class ResponseData {
        public String getError() {
            return this.error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getAnswer() {
            return this.answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        private String error;
        private String answer;
    }

    public ResponseData solve(RequestData pd, Context context) {
        ResponseData ret = new ResponseData();
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(pd.getSrc().getBytes("UTF-8"));
            DropQuotesSolver dqs = new DropQuotesSolver();
            Dictionary dict = new Dictionary(Dictionary.DictSize.m);
            ret.setAnswer(dqs.solve(dict, bis));
        }
        catch (Throwable t) {
            ret.setError(t.getMessage());
        }
        return ret;
    }
}
