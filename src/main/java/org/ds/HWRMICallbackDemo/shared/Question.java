package org.ds.HWRMICallbackDemo.shared;// Question.java
import java.io.Serializable;

public class Question implements Serializable {
    private static final long serialVersionUID = 1L;

    private String text;
    private String answer;

    public Question(String text, String answer) {
        this.text = text;
        this.answer = answer;
    }

    public String getText() {
        return text;
    }

    public String getAnswer() {
        return answer;
    }
}