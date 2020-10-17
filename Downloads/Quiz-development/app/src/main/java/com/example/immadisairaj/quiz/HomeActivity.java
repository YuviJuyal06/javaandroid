package com.example.immadisairaj.quiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.immadisairaj.quiz.api.Api;
import com.example.immadisairaj.quiz.api.ApiCount;
import com.example.immadisairaj.quiz.api.QuizQuestions;
import com.example.immadisairaj.quiz.api.Result;
import com.example.immadisairaj.quiz.question.Question;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//STARTING BASE CLASS
public class HomeActivity extends AppCompatActivity {
	Button start;
	Button filter;
	ProgressBar progressBar;
	Question q;
	String difficulty;
	String category;
	DBHelper db=new DBHelper(this);

	//CHECKS IF CLICK ON VIEW IS ON FILTER OR TAKE QUIZ BUTTON
		View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			//HOME_START ID OF TAKE QUIZ BUTTON
			if (view.getId() == R.id.home_start) {
				progressBar.setVisibility(View.VISIBLE);
				//CREATE QUESTION CLASS
				q = new Question(getApplicationContext());
				view.setClickable(false);
				//GET QUESTIONS FOR QUIZ
				fetchQuestionCount();
			//HOME_FILTER ID OF FILTER BUTTON
			} else if (view.getId() == R.id.home_filter) {
				//SETTINGS SCREEN
				Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		//CATEGORY AND DIFFICULTY
		setFilterDefaultValues();
		start = findViewById(R.id.home_start);
		filter = findViewById(R.id.home_filter);
		progressBar = findViewById(R.id.progressBar2);
		start.setOnClickListener(onClickListener);
		filter.setOnClickListener(onClickListener);

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		category = sharedPrefs.getString(
				getString(R.string.category_key),
				getString(R.string.medium_value)
		);

		difficulty = sharedPrefs.getString(
				getString(R.string.difficulty_key),
				getString(R.string.medium_value)
		);
	}

	private void setFilterDefaultValues() {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		difficulty = sharedPrefs.getString(
				getString(R.string.difficulty_key),
				null
		);
		category = sharedPrefs.getString(
				getString(R.string.category_key),
				null
		);
		if (difficulty == null) {
			sharedPrefs
					.edit()
					.putString(getString(R.string.difficulty_key), getString(R.string.easy_value))
					.apply();
		}
		if (category == null) {
			sharedPrefs
					.edit()
					.putString(getString(R.string.category_key), getString(R.string.any_category_value))
					.apply();
		}
	}

	private void fetchQuestionCount() {


		int category_value = Integer.valueOf(category);
		//REST API CLIENT TO ACCESS TRIVIA API
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(Api.BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		Api api = retrofit.create(Api.class);
		Call<ApiCount> call = api.getQuizQuestions(category_value);
		call.enqueue(new Callback<ApiCount>() {
			//GET QUESTION COUNT ACCORDING TO DIFFICULTY
			@Override
			public void onResponse(Call<ApiCount> call, Response<ApiCount> response) {
				switch (difficulty) {
					case "easy":
						fetchQuestionAPI(response.body().getCategoryQuestionCount().getTotalEasyQuestionCount());
						break;
					case "medium":
						fetchQuestionAPI(response.body().getCategoryQuestionCount().getTotalMediumQuestionCount());
						break;
					case "hard":
						fetchQuestionAPI(response.body().getCategoryQuestionCount().getTotalHardQuestionCount());
						break;
				}
			}

			@Override
			public void onFailure(Call<ApiCount> call, Throwable t) {
				Toast.makeText(getApplicationContext(), "Select Filter", Toast.LENGTH_SHORT).show();
				progressBar.setVisibility(View.INVISIBLE);
				start.setClickable(true);
			}
		});
	}
	//FETCH QUESTIONS
	public void fetchQuestionAPI(int categoryCount) {
		int category_value = Integer.valueOf(category);
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(Api.BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		Api api = retrofit.create(Api.class);
		Call<QuizQuestions> call = api.getQuizQuestions("url3986", categoryCount >= 10 ? 10 : categoryCount - 1, difficulty, category_value);
		call.enqueue(new Callback<QuizQuestions>() {
			@Override
			public void onResponse(Call<QuizQuestions> call, Response<QuizQuestions> response) {

				Log.v("url-----", call.request().url().toString());

				QuizQuestions quizQuestions = response.body();

				if (quizQuestions.getResponseCode() == 0) {

					q.results = quizQuestions.getResults();

					if (q.results != null) {
						for (Result r : q.results) {
							try {
								q.question.add(java.net.URLDecoder.decode(r.getQuestion(), "UTF-8"));
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							Random random = new Random();
							// For Boolean Type Questions, only 2 possible options (True/False)
							// For multiple choices, 4 options are required.
							int ran = r.getType().equals("boolean")
									? random.nextInt(2)
									: random.nextInt(4);
							setOptions(r, ran);
							q.Answer.add(ran + 1);
						}
						Log.v("answers", q.Answer.toString());
					}
				}
				progressBar.setVisibility(View.INVISIBLE);
				start.setClickable(true);
				EditText e=(EditText)findViewById(R.id.namebox);
				String username=e.getText().toString();

				db.insertUser(username,-1);
				//NEXT CALL QUIZ SCREEN
				Intent intent = new Intent(HomeActivity.this, QuizActivity.class);
				intent.putExtra("question", q);
				intent.putExtra("username",username);
				startActivity(intent);
			}

			@Override
			public void onFailure(Call<QuizQuestions> call, Throwable t) {
				Toast.makeText(getApplicationContext(), "Select Category", Toast.LENGTH_SHORT).show();
				progressBar.setVisibility(View.INVISIBLE);
				start.setClickable(true);
			}
		});
	}
	public void displayRank(View view){
		ArrayList<String> a=db.getAllUsers();
		String[] a1=  a.toArray(new String[a.size()]);
		String[] a2={"","",""};
		a2[0]=a1[0]+"                                "+a1[1];
		a2[1]=a1[2]+"                                 "+a1[3];
		a2[2]=a1[4]+"                                 "+a1[5];
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("High Scores");
		builder.setItems(a2, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				// Do something with the selection

			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	void setOptions(Result r, int ran) {
		List<String> wrong;
		switch (ran) {
			case 0:
				try {
					q.optA.add(java.net.URLDecoder.decode(r.getCorrectAnswer(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				wrong = r.getIncorrectAnswers();
				try {
					q.optB.add(java.net.URLDecoder.decode(wrong.get(0), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				// Options C, D are not applicable for Boolean Type Questions.
				if (r.getType().equals("boolean")) {
					q.optC.add("false");
					q.optD.add("false");
					return;
				}
				try {
					q.optC.add(java.net.URLDecoder.decode(wrong.get(1), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				try {
					q.optD.add(java.net.URLDecoder.decode(wrong.get(2), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
			case 1:
				try {
					q.optB.add(java.net.URLDecoder.decode(r.getCorrectAnswer(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				wrong = r.getIncorrectAnswers();
				try {
					q.optA.add(java.net.URLDecoder.decode(wrong.get(0), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				// Options C, D are not applicable for Boolean Type Questions.
				if (r.getType().equals("boolean")) {
					q.optC.add("false");
					q.optD.add("false");
					return;
				}
				try {
					q.optC.add(java.net.URLDecoder.decode(wrong.get(1), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				try {
					q.optD.add(java.net.URLDecoder.decode(wrong.get(2), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
			case 2:
				try {
					q.optC.add(java.net.URLDecoder.decode(r.getCorrectAnswer(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				wrong = r.getIncorrectAnswers();
				try {
					q.optA.add(java.net.URLDecoder.decode(wrong.get(0), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				try {
					q.optB.add(java.net.URLDecoder.decode(wrong.get(1), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				try {
					q.optD.add(java.net.URLDecoder.decode(wrong.get(2), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
			case 3:
				try {
					q.optD.add(java.net.URLDecoder.decode(r.getCorrectAnswer(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				wrong = r.getIncorrectAnswers();
				try {
					q.optA.add(java.net.URLDecoder.decode(wrong.get(0), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				try {
					q.optB.add(java.net.URLDecoder.decode(wrong.get(1), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				try {
					q.optC.add(java.net.URLDecoder.decode(wrong.get(2), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
		}
	}
}