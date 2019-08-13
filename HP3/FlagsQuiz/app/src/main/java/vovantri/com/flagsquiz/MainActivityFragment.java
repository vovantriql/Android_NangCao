package vuthanhtutrang.com.flagsquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;

import vovantri.com.flagsquiz.GuessButtonListener;

public class MainActivityFragment extends Fragment {

    private SecureRandom random;
    private Animation shakeAnimation;
    private LinearLayout quizLinearLayout;
    private TextView questionNumberTextView;
    private ImageView flagImageView;
    private LinearLayout[] guessLinearLayout;
    private TextView answerTextView;
    private vuthanhtutrang.com.flagsquiz.QuizViewModel quizViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.quizViewModel = ViewModelProviders.of(getActivity()).get(vuthanhtutrang.com.flagsquiz.QuizViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        View.OnClickListener guessButtonListener = new GuessButtonListener(this);
        LinearLayout answersLinearLayout = view.findViewById(R.id.answersLinearLayout);

        this.random = new SecureRandom();
        this.shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.incorrect_shake);
        this.shakeAnimation.setRepeatCount(3);
        this.quizLinearLayout = view.findViewById(R.id.quizLinearLayout);
        this.questionNumberTextView = view.findViewById(R.id.questionNumberTextView);
        this.flagImageView = view.findViewById(R.id.flagImageView);

        this.guessLinearLayout = new LinearLayout[4];
        this.answerTextView = view.findViewById(R.id.answerTextView);



        for (int i = 0; i < answersLinearLayout.getChildCount(); i++) {
            try {
                if (answersLinearLayout.getChildAt(i) instanceof LinearLayout) {
                    this.guessLinearLayout[i] = (LinearLayout) answersLinearLayout.getChildAt(i);
                }
            } catch (ArrayStoreException e) {
                Log.e(vuthanhtutrang.com.flagsquiz.QuizViewModel.getTag(),
                        "Error getting button rows on loop #" + String.valueOf(i), e);
            }
        }

        for (LinearLayout row : this.guessLinearLayout) {
            for (int column = 0; column < row.getChildCount(); column++) {
                (row.getChildAt(column)).setOnClickListener(guessButtonListener);
            }
        }

        this.questionNumberTextView.setText(
                getString(R.string.question, 1, vuthanhtutrang.com.flagsquiz.QuizViewModel.getFlagsInQuiz()));
        return view;
    }

    public void updateGuessRows() {

        int numberOfGuessRows = this.quizViewModel.getGuessRows();
        for (LinearLayout layout : this.guessLinearLayout) {
            layout.setVisibility(View.GONE);
        }
        for (int row = 0; row < numberOfGuessRows; row++) {
            guessLinearLayout[row].setVisibility(View.VISIBLE);
        }
    }

    public void resetQuiz() {
        this.quizViewModel.clearFileNameList();
        this.quizViewModel.setFileNameList(getActivity().getAssets());
        this.quizViewModel.resetTotalGuesses();
        this.quizViewModel.resetCorrectAnswers();
        this.quizViewModel.clearQuizCountriesList();

        int flagCounter = 1;
        int numberOfFlags = this.quizViewModel.getFileNameList().size();
        while (flagCounter <= vuthanhtutrang.com.flagsquiz.QuizViewModel.getFlagsInQuiz()) {
            int randomIndex = this.random.nextInt(numberOfFlags);

            String filename = this.quizViewModel.getFileNameList().get(randomIndex);

            if (!this.quizViewModel.getQuizCountriesList().contains(filename)) {
                this.quizViewModel.getQuizCountriesList().add(filename);
                ++flagCounter;
            }
        }

        this.updateGuessRows();
        this.loadNextFlag();
    }

    private void loadNextFlag() {
        AssetManager assets = getActivity().getAssets();
        String nextImage = this.quizViewModel.getNextCountryFlag();
        String region = nextImage.substring(0, nextImage.indexOf('-'));

        this.quizViewModel.setCorrectAnswer(nextImage);
        answerTextView.setText("");

        questionNumberTextView.setText(getString(R.string.question,
                (quizViewModel.getCorrectAnswers() + 1), vuthanhtutrang.com.flagsquiz.QuizViewModel.getFlagsInQuiz()));

        try (InputStream stream = assets.open(region + "/" + nextImage + ".png")) {
            Drawable flag = Drawable.createFromStream(stream, nextImage);
            flagImageView.setImageDrawable(flag);
            animate(false);
        } catch (IOException e) {
            Log.e(vuthanhtutrang.com.flagsquiz.QuizViewModel.getTag(), "Error Loading " + nextImage, e);
        }

        this.quizViewModel.shuffleFilenameList();

        for (int row = 0; row < this.quizViewModel.getGuessRows(); row++) {
            for (int column = 0; column < guessLinearLayout[row].getChildCount(); column++) {
                Button guessButton = (Button) guessLinearLayout[row].getChildAt(column);
                guessButton.setEnabled(true);
                String filename = this.quizViewModel.getFileNameList()
                        .get((row * 2) + column)
                        .substring(this.quizViewModel.getFileNameList()
                                .get((row * 2) + column).indexOf('-') + 1)
                        .replace('_', ' ');
                guessButton.setText(filename);
            }
        }

        int row = this.random.nextInt(this.quizViewModel.getGuessRows());
        int column = this.random.nextInt(2);
        LinearLayout randomRow = guessLinearLayout[row];
        ((Button) randomRow.getChildAt(column)).setText(this.quizViewModel.getCorrectCountryName());
    }

    public void animate(boolean animateOut) {
        if (this.quizViewModel.getCorrectAnswers() == 0) {
            return;
        }
        int centreX = (quizLinearLayout.getLeft() + quizLinearLayout.getRight()) / 2;
        int centreY = (quizLinearLayout.getTop() + quizLinearLayout.getBottom()) / 2;
        int radius = Math.max(quizLinearLayout.getWidth(), quizLinearLayout.getHeight());
        Animator animator;
        if (animateOut) {
            animator = ViewAnimationUtils.createCircularReveal(
                    quizLinearLayout, centreX, centreY, radius, 0);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    loadNextFlag();
                }
            });
        } else {
            animator = ViewAnimationUtils.createCircularReveal(
                    quizLinearLayout, centreX, centreY, 0, radius);
        }

        animator.setDuration(500);
        animator.start();
    }

    public void incorrectAnswerAnimation(){
        flagImageView.startAnimation(shakeAnimation);

        answerTextView.setText(R.string.incorrect_answer);
        answerTextView.setTextColor(getResources().getColor(R.color.incorrect_answer));
    }

    public void disableButtons() {
        for (LinearLayout row : this.guessLinearLayout) {
            for (int column = 0; column < row.getChildCount(); column++) {
                (row.getChildAt(column)).setEnabled(false);
            }
        }
    }

    public TextView getAnswerTextView() {
        return answerTextView;
    }

    public vuthanhtutrang.com.flagsquiz.QuizViewModel getQuizViewModel() {
        return quizViewModel;
    }
}
