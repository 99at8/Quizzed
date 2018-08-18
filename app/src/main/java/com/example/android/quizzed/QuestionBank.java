package com.example.android.quizzed;

import java.util.ArrayList;

// An array of questions for the quiz.
class QuestionBank {

    ArrayList<Question> list = new ArrayList<>();

    QuestionBank() {
        list.add(new Question("Use column addition to find 2876 + 6049?",
                "8925",
                "8915",
                "8825",
                "8815",
                "a",
                QuestionType.BUTTON));
        list.add(new Question("If y = 13, then what is the value of  5(y − 7) ?",
                "25",
                "30",
                "35",
                "58",
                "b",
                QuestionType.RADIO));
        list.add(new Question("For the number 6,074,269, what does the 0 mean?",
                "0 Millions",
                "0 Hundred-thousands",
                "0 Ten-thousands",
                "0 Thousands",
                "b",
                QuestionType.BUTTON));
        list.add(new Question("Convert 326% to a decimal.",
                "326",
                "32.6",
                "3.26",
                "0.326",
                "c",
                QuestionType.BUTTON));
        list.add(new Question("Use long multiplication to calculate 384 × 367?",
                "140,868",
                "140,898",
                "141,928",
                "292,992",
                "d",
                QuestionType.RADIO));
        list.add(new Question("Who is known as the Prince of Indian Mathematics?", "", "", "", "",
                "srinivasa ramanujan",
                QuestionType.TEXTENTRY));
        list.add(new Question("How many years is 144 months?",
                "24/2",
                "12",
                "5",
                "14",
                "3",
                QuestionType.CHECKBOX));
    }
}