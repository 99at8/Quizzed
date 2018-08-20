package com.example.android.quizzed;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    QuestionBank allQuestions = new QuestionBank();
    String pickedAnswer = "";
    int questionNumber = 0;
    int score = 0;
    final int numberOfQuestions = allQuestions.list.size();
    boolean noRadioSelected = false;
    TextView questionLabel, scoreLabel, progressLabel;
    View progressBar;
    LinearLayout buttonLayout, checkBoxLayout;
    RadioGroup radioGroup;
    Button submitButton;
    EditText answerField;
    RadioButton radioButtonA, radioButtonB, radioButtonC, radioButtonD;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        questionLabel = findViewById(R.id.question_textview);
        scoreLabel = findViewById(R.id.score_label);
        progressLabel = findViewById(R.id.progress_label);
        progressBar = findViewById(R.id.progress_bar);
        buttonLayout = findViewById(R.id.button_layout);
        radioGroup = findViewById(R.id.radio_layout);
        checkBoxLayout = findViewById(R.id.checkbox_layout);
        submitButton = findViewById(R.id.submit_button);
        answerField = findViewById(R.id.answer_field);
        radioButtonA = findViewById(R.id.radiobutton_a);
        radioButtonB = findViewById(R.id.radiobutton_b);
        radioButtonC = findViewById(R.id.radiobutton_c);
        radioButtonD = findViewById(R.id.radiobutton_d);
        if (savedInstanceState != null) {
            allQuestions.list = (ArrayList<Question>) savedInstanceState.getSerializable("questionList");
            score = savedInstanceState.getInt("score");
            questionNumber = savedInstanceState.getInt("questionNumber");
        } else {
            Collections.shuffle(allQuestions.list);
        }
        hideAll();
        nextQuestion();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("questionList", allQuestions.list);
        outState.putInt("score", score);
        outState.putInt("questionNumber", questionNumber);
    }

    // Get Screen Width for Progress Bar
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    // Hide all the Input Views
    private void hideAll() {
        buttonLayout.setVisibility(View.INVISIBLE);
        radioGroup.setVisibility(View.INVISIBLE);
        checkBoxLayout.setVisibility(View.INVISIBLE);
        submitButton.setVisibility(View.INVISIBLE);
        answerField.setVisibility(View.INVISIBLE);
    }

    // Display Button Input View
    private void showButtons() {
        buttonLayout.setVisibility(View.VISIBLE);
    }
    private void showCheckBoxes() {
        checkBoxLayout.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.VISIBLE);
    }

    // Display Radio Buttons
    private void showRadioButtons() {
        radioGroup.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.VISIBLE);
    }

    // Display Text Entry and Submit Button
    private void showTextEntry() {
        answerField.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.VISIBLE);
    }

    // Display next question and enable appropriate input mechanism. Else if game is over display alert
    private void nextQuestion() {
        if (questionNumber <= numberOfQuestions - 1) {
            String fullQuestion = allQuestions.list.get(questionNumber).questionSet.get("question").toString();
            fullQuestion += "\n\na) " + allQuestions.list.get(questionNumber).questionSet.get("a");
            fullQuestion += "\nb) " + allQuestions.list.get(questionNumber).questionSet.get("b");
            fullQuestion += "\nc) " + allQuestions.list.get(questionNumber).questionSet.get("c");
            fullQuestion += "\nd) " + allQuestions.list.get(questionNumber).questionSet.get("d");
            QuestionType type = (QuestionType) allQuestions.list.get(questionNumber).questionSet.get("format");
            switch (type) {
                case BUTTON:
                    showButtons();
                    break;
                case RADIO:
                    showRadioButtons();
                    break;
                case CHECKBOX:
                    showCheckBoxes();
                    break;
                case TEXTENTRY:
                    showTextEntry();
                    fullQuestion = allQuestions.list.get(questionNumber).questionSet.get("question").toString();
                    break;
            }
            questionLabel.setText(fullQuestion);
            updateUI();
        } else {
            hideAll();
            scoreLabel.setText("Score: " + score);
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Awesome!");
            alertDialog.setMessage("You scored " + score * 100 / numberOfQuestions + "% Try again?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Restart",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            restart();
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

    // Verify answer for all questions
    public void submitPressed(View view) {
        String correctAnswer = allQuestions.list.get(questionNumber).questionSet.get("answer").toString();
        QuestionType type = (QuestionType) allQuestions.list.get(questionNumber).questionSet.get("format");
        if (type == QuestionType.TEXTENTRY) {
            EditText answerField = findViewById(R.id.answer_field);
            String proposedAnswer = answerField.getText().toString();
            pickedAnswer = proposedAnswer.toLowerCase();
            checkAnswer(correctAnswer.toLowerCase());
            answerField.setText("");
        } else if (type == QuestionType.CHECKBOX) {
            int checkBoxSum = 0;
            CheckBox buttonA = findViewById(R.id.checkbox_a);
            CheckBox buttonB = findViewById(R.id.checkbox_b);
            CheckBox buttonC = findViewById(R.id.checkbox_c);
            CheckBox buttonD = findViewById(R.id.checkbox_d);
            if (buttonA.isChecked()) {
                checkBoxSum += 1;
                buttonA.setChecked(false);
            }
            if (buttonB.isChecked()) {
                checkBoxSum += 2;
                buttonB.setChecked(false);
            }
            if (buttonC.isChecked()) {
                checkBoxSum += 4;
                buttonC.setChecked(false);
            }
            if (buttonD.isChecked()) {
                checkBoxSum += 8;
                buttonD.setChecked(false);
            }
            pickedAnswer = "" + checkBoxSum;
            checkAnswer(correctAnswer);

        } else if (type == QuestionType.RADIO) {
            checkRadioButtons();
            if (noRadioSelected) {
                AlertDialog dialog = new AlertDialog.Builder(this).create();
                dialog.setTitle("Nothing Selected");
                dialog.setMessage("Please pick an answer!");
                dialog.show();
                noRadioSelected = false;
                return;
            } else {
                checkAnswer(correctAnswer);
            }
        } else if (type == QuestionType.BUTTON) {
            switch (view.getId()) {
                case R.id.button_a:
                    pickedAnswer = "a";
                    break;
                case R.id.button_b:
                    pickedAnswer = "b";
                    break;
                case R.id.button_c:
                    pickedAnswer = "c";
                    break;
                case R.id.button_d:
                    pickedAnswer = "d";
                    break;
                default:
                    break;
            }
            checkAnswer(correctAnswer);
        }
        hideAll();
        questionNumber += 1;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextQuestion();
            }
        }, 2000);
    }

    private void checkRadioButtons() {
        if (radioButtonA.isChecked()) {
            pickedAnswer = "a";
        } else if (radioButtonB.isChecked()) {
            pickedAnswer = "b";
        } else if (radioButtonC.isChecked()) {
            pickedAnswer = "c";
        } else if (radioButtonD.isChecked()) {
            pickedAnswer = "d";
        } else {
            noRadioSelected = true;
        }
        radioGroup.clearCheck();
    }

    // Verify if Answer was correct and inform user then add score
    private void checkAnswer(String correctAnswer) {
        if (correctAnswer.equals(pickedAnswer)) {
            showToast(true);
            score += 1;
        } else {
            showToast(false);
        }
    }

    // Inform user that answer is correct or not
    private void showToast(boolean value) {
        if (value) {
            Toast toast = Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Wrong!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    // Update scores and other UI Elements
    private void updateUI() {
        scoreLabel.setText("Score: " + score);
        progressLabel.setText(questionNumber + 1 + " / " + numberOfQuestions);
        int parentWidth = ((View) progressBar.getParent()).getMeasuredWidth();
        if (parentWidth == 0) {
            parentWidth = getScreenWidth();
        }
        ValueAnimator anim = ValueAnimator.ofInt(progressBar.getMeasuredWidth(), parentWidth / numberOfQuestions * (questionNumber + 1));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = progressBar.getLayoutParams();
                layoutParams.width = val;
                progressBar.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(500);
        anim.start();
    }

    // Restart the game for another attempt.
    private void restart() {
        questionNumber = 0;
        score = 0;
        Collections.shuffle(allQuestions.list);
        nextQuestion();
    }
}