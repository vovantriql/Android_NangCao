package vovantri.com.tipcalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.NumberFormat;

import vuthanhtutrang.com.tipcalculator.R;

public class MainActivity extends AppCompatActivity {

    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private static final NumberFormat percentFormat = NumberFormat.getPercentInstance();

    private double billAmount = 0.0; // số tiền hóa đơn được nhập bởi người dùng
    private double percent = 0.15; // phần trăm tiền boa ban đầu
    private TextView amountTextView; // hiển thị số tiền hóa đơn được định dạng
    private TextView percentTextView; // cho thấy tỷ lệ đầu
    private TextView tipTextView; // hiển thị số tiền boa được tính
    private TextView totalTextView; // hiển thị tổng số tiền hóa đơn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // lấy các tham chiếu đến các TextView được thao tác theo chương trình
        amountTextView = (TextView) findViewById(R.id.amountTextView);
        percentTextView = (TextView) findViewById(R.id.percentTextView);
        tipTextView = (TextView) findViewById(R.id.tipTextView);
        totalTextView = (TextView) findViewById(R.id.totalTextView);
        tipTextView.setText(currencyFormat.format(0));
        totalTextView.setText(currencyFormat.format(0));

        // đặt amountEditText's TextWatcher
        EditText amountEditText = (EditText) findViewById(R.id.amountEditText);
        amountEditText.addTextChangedListener(amountEditTextWatcher);

        // đặt percentSeekBar's OnSeekBarChangeListener
        SeekBar percentSeekBar = (SeekBar) findViewById(R.id.percentSeekBar);
        percentSeekBar.setOnSeekBarChangeListener(seekBarListener);
    }

    // tính toán và hiển thị tiền boa và tổng số tiền
    private void calculate() {
        // định dạng phần trăm và hiển thị trong phần trăm
        percentTextView.setText(percentFormat.format(percent));

        // tính tiền boa và tổng
        double tip = billAmount * percent;
        double total = billAmount + tip;

        // hiển thị tiền boa và tổng số được định dạng là tiền tệ
        tipTextView.setText(currencyFormat.format(tip));
        totalTextView.setText(currencyFormat.format(total));
    }

    // đối tượng người nghe cho sự kiện thay đổi tiến trình của SeekBar
    private final SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            percent = progress / 100.0; // đặt phần trăm dựa trên tiến độ
            calculate(); // tính toán và đầu hiển thị và tổng
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    // đối tượng người nghe cho các sự kiện thay đổi văn bản của EditText
    private final TextWatcher amountEditTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            try { // lấy số tiền hóa đơn và hiển thị giá trị tiền tệ được định dạng
                billAmount = Double.parseDouble(s.toString()) / 100.0;
                amountTextView.setText(currencyFormat.format(billAmount));
        }
            catch (NumberFormatException e) { // nếu s trống hoặc không phải là số
                amountTextView.setText("");
                billAmount = 0.0;
            }

            calculate(); // cập nhật mẹo và tổng số TextViews
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}
