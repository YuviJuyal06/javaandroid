package com.example.immadisairaj.quiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.immadisairaj.quiz.question.Question;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuizActivity extends AppCompatActivity {

	Question qAndA = new Question();

	int ques, score, ans, nextC;
	float percentage;
	boolean submit;
	boolean timeup=false;
	ArrayList<Integer> Answers;
	String username;
	DBHelper db=new DBHelper(this);
	String q_nos;

	@BindView(R.id.q_numbers)
	TextView q_no;

	@BindView(R.id.question)
	TextView questions;

	@BindView(R.id.optionA)
	RadioButton opA;

	@BindView(R.id.optionB)
	RadioButton opB;

	@BindView(R.id.optionC)
	RadioButton opC;

	@BindView(R.id.optionD)
	RadioButton opD;

	@BindView(R.id.options)
	RadioGroup optionsGroup;

	@BindView(R.id.prev)
	Button prevButton;
	public int counter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz);

		ButterKnife.bind(this);
		Intent i = getIntent();
		//GET QUESTION OBJECT AND USERNAME FROM HOME ACTIVITY SCREEN
		qAndA = (Question) i.getSerializableExtra("question");
		username=i.getStringExtra("username");
		q_nos = "Question: " + 1 + "/" + qAndA.question.size();
		questions = findViewById(R.id.question);
		questions.setText("Quiz");
		prevButton.setVisibility(View.GONE);
		Answers = new ArrayList<>();
		startTimer();
		ques = -1;
		score = 0;
		ans = 0;
		nextC = 0;
		submit = true;
		goNext();
	}
	public void startTimer() {
		final TextView counttimer=findViewById(R.id.timer);
		final Button next1=findViewById(R.id.submit);
		new CountDownTimer(60000,1000){
			@Override
			public void onTick(long millisUntilFinished){
				counttimer.setText(String.valueOf(counter));
				counter++;
			}
			@Override
			public void onFinish()
			{
				counttimer.setText("Time up!");
				timeup=true;
				next1.performClick();
			}
		}.start();
	}
	public void goNext() {
		ques++;

		if (ques >= qAndA.question.size()) {
			ques = qAndA.question.size() - 1;
		}

		q_no.setVisibility(View.VISIBLE);
		opA.setVisibility(View.VISIBLE);
		opB.setVisibility(View.VISIBLE);

		if (qAndA.results.get(ques).getType().equals("boolean")) {
			opC.setVisibility(View.GONE);
			opD.setVisibility(View.GONE);
		} else {
			opC.setVisibility(View.VISIBLE);
			opD.setVisibility(View.VISIBLE);
		}

		q_nos = "Question: " + (ques + 1) + "/" + qAndA.question.size();
		q_no.setText(q_nos);
		questions.setText(qAndA.question.get(ques));
		opA.setText(qAndA.optA.get(ques));
		opB.setText(qAndA.optB.get(ques));
		opC.setText(qAndA.optC.get(ques));
		opD.setText(qAndA.optD.get(ques));
		try {
			if (Answers.get(ques) == 1) {
				opA.setChecked(true);
			} else if (Answers.get(ques) == 2) {
				opB.setChecked(true);
			} else if (Answers.get(ques) == 3) {
				opC.setChecked(true);
			} else if (Answers.get(ques) == 4) {
				opD.setChecked(true);
			} else {
				optionsGroup.clearCheck();
			}
		} catch (Exception e) {
			optionsGroup.clearCheck();
		}
	}

	public void clickPrev(View view) {

		int selectedId = optionsGroup.getCheckedRadioButtonId();
		switch (selectedId) {
			case R.id.optionA:
				ans = 1;
				break;
			case R.id.optionB:
				ans = 2;
				break;
			case R.id.optionC:
				ans = 3;
				break;
			case R.id.optionD:
				ans = 4;
				break;
			default:
				ans = 0;
		}
		if (ques >= 0) {
			try {
				Answers.set(ques, ans);
			} catch (Exception e) {
				Answers.add(ques, ans);
			}
		}
		if (ques < qAndA.question.size() - 1) {
			goPrev();
		} else if (ques == qAndA.question.size() - 1) {
			Button button = findViewById(R.id.next);
			button.setVisibility(View.VISIBLE);
			button = findViewById(R.id.submit);
			button.setVisibility(View.INVISIBLE);
			optionsGroup.clearCheck();
			goPrev();
		}
		if (ques == 0)
			prevButton.setVisibility(View.GONE);
		nextC--;
		ans = 0;
	}

	public void goPrev() {
		ques--;

		q_no.setVisibility(View.VISIBLE);
		opA.setVisibility(View.VISIBLE);
		opB.setVisibility(View.VISIBLE);

		if (qAndA.results.get(ques).getType().equals("boolean")) {
			opC.setVisibility(View.GONE);
			opD.setVisibility(View.GONE);
		} else {
			opC.setVisibility(View.VISIBLE);
			opD.setVisibility(View.VISIBLE);
		}

		q_nos = "Question: " + (ques + 1) + "/" + qAndA.question.size();
		q_no.setText(q_nos);
		questions.setText(qAndA.question.get(ques));
		opA.setText(qAndA.optA.get(ques));
		opB.setText(qAndA.optB.get(ques));
		opC.setText(qAndA.optC.get(ques));
		opD.setText(qAndA.optD.get(ques));
		try {
			if (Answers.get(ques) == 1) {
				opA.setChecked(true);
			} else if (Answers.get(ques) == 2) {
				opB.setChecked(true);
			} else if (Answers.get(ques) == 3) {
				opC.setChecked(true);
			} else if (Answers.get(ques) == 4) {
				opD.setChecked(true);
			} else {
				optionsGroup.clearCheck();
			}
		} catch (Exception e) {
			optionsGroup.clearCheck();

		}
	}

	public void clickSubmit(final View view) {
		if(timeup==false) {
			AlertDialog.Builder alertConfirm = new AlertDialog.Builder(this);
			alertConfirm.setTitle("Confirm Submission");
			alertConfirm.setMessage("Do you want to submit quiz?");
			alertConfirm.setCancelable(true);
			alertConfirm.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			alertConfirm.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					submit(view);
				}
			});
			AlertDialog dialog = alertConfirm.create();
			dialog.show();
		}
		else{
			submit(view);
		}
	}

	public void submit(View view) {
		clickNext(view);
		if (submit)
			checkScore();
		submit = false;
		percentage = (float) (score * 100) / qAndA.question.size();
		prevButton.setVisibility(View.INVISIBLE);
		opA.setClickable(false);
		opB.setClickable(false);
		opC.setClickable(false);
		opD.setClickable(false);
		LayoutInflater inflater = getLayoutInflater();
		View alertLayout = inflater.inflate(R.layout.alert_dialog, null);
		final ImageView imgview= alertLayout.findViewById(R.id.badge);


		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("RESULT");
		int oldscore=db.getScore(username);
		db.updateUser(username,score);
		if(oldscore <= score && oldscore!=-1) {
			imgview.setImageResource(R.drawable.winner2);
			alert.setMessage(username + ", " + "You scored " + score + " out of " + qAndA.question.size() + " questions.You are doing better than ever!");

		}
		if(oldscore > score && oldscore != -1) {
			imgview.setImageResource(R.drawable.winner1);
			alert.setMessage(username + ", " + "You scored " + score + " out of " + qAndA.question.size() + " questions.You can do better next time!!Good Luck!!");

		}
		else{
			imgview.setImageResource(R.drawable.winner2);
			alert.setMessage(username + ", " + "You scored " + score + " out of " + qAndA.question.size() + " questions.You are doing good for your first try!");
		}
		alert.setView(alertLayout);
		alert.setCancelable(false);

		alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				moveTaskToBack(true);
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(1);
			}
		});

		alert.setPositiveButton("View Solutions", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				clickSolutions();
			}
		});
		AlertDialog dialog = alert.create();
		dialog.show();

	}

	public void clickNext(View view) {

		int selectedId = optionsGroup.getCheckedRadioButtonId();
		switch (selectedId) {
			case R.id.optionA:
				ans = 1;
				break;
			case R.id.optionB:
				ans = 2;
				break;
			case R.id.optionC:
				ans = 3;
				break;
			case R.id.optionD:
				ans = 4;
				break;
			default:
				ans = 0;
		}

		if (ques >= 0) {
			try {
				Answers.set(ques, ans);
			} catch (Exception e) {
				Answers.add(ques, ans);
			}
		}
		if (ques < qAndA.question.size() - 2) {
			optionsGroup.clearCheck();
			goNext();
		} else if (ques == qAndA.question.size() - 2) {
			Button button = findViewById(R.id.next);
			button.setVisibility(View.INVISIBLE);
			button = findViewById(R.id.submit);
			button.setVisibility(View.VISIBLE);
			optionsGroup.clearCheck();
			goNext();
		}
		if (ques > 0)
			prevButton.setVisibility(View.VISIBLE);

		nextC++;
		ans = 0;
	}

	public void checkScore() {
		if (ques != -1)
			for (int i = 0; i < Answers.size(); i++) {
				if (qAndA.Answer.get(i).equals(Answers.get(i))) {
					score++;
				}
			}
	}

	public void clickSolutions() {
		Intent solutions = new Intent(this, SolutionActivity.class);
		solutions.putIntegerArrayListExtra("Answer", Answers);
		solutions.putStringArrayListExtra("Question", (ArrayList<String>) qAndA.question);
		solutions.putStringArrayListExtra("optA", (ArrayList<String>) qAndA.optA);
		solutions.putStringArrayListExtra("optB", (ArrayList<String>) qAndA.optB);
		solutions.putStringArrayListExtra("optC", (ArrayList<String>) qAndA.optC);
		solutions.putStringArrayListExtra("optD", (ArrayList<String>) qAndA.optD);
		solutions.putIntegerArrayListExtra("Answers", (ArrayList<Integer>) qAndA.Answer);
		startActivity(solutions);
	}

	@Override
	public void onBackPressed() {
		Toast.makeText(this, "Back Press is not allowed", Toast.LENGTH_LONG).show();
	}
}
