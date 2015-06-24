package com.example.stropheum.androidworkouttimer.view;

import java.util.Calendar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.example.stropheum.androidworkouttimer.R;
import com.example.stropheum.androidworkouttimer.view.TimePicker.OnTimeChangedListener;

public class MyTimePickerDialog extends AlertDialog implements OnClickListener,
        OnTimeChangedListener {

    @Override
    public void onTimeChanged(com.example.stropheum.androidworkouttimer.view.TimePicker view, int minute, int seconds) {
        updateTitle(minute, seconds);
    }

    /**
     * The callback interface used to indicate the user is done filling in
     * the time (they clicked on the 'Set' button).
     */
    public interface OnTimeSetListener {

        /**
         * @param view The view associated with this listener.
         * @param minute The minute that was set.
         */
        void onTimeSet(TimePicker view, int minute, int seconds);
    }

    private static final String MINUTE = "minute";
    private static final String SECONDS = "seconds";
    private static final String IS_24_HOUR = "is24hour";

    private final TimePicker mTimePicker;
    private final OnTimeSetListener mCallback;
    private final Calendar mCalendar;
    private final java.text.DateFormat mDateFormat;

    int mInitialMinute;
    int mInitialSeconds;
    boolean mIs24HourView;

    /**
     * @param context Parent.
     * @param callBack How parent is notified.
     * @param is24HourView Whether this is a 24 hour view, or AM/PM.
     */
    public MyTimePickerDialog(Context context,
                              OnTimeSetListener callBack, boolean is24HourView) {

        this(context, 0,
                callBack, is24HourView);
    }

    /**
     * @param context Parent.
     * @param theme the theme to apply to this dialog
     * @param callBack How parent is notified.
     * @param is24HourView Whether this is a 24 hour view, or AM/PM.
     */
    public MyTimePickerDialog(Context context,
                              int theme,
                              OnTimeSetListener callBack,boolean is24HourView) {
        super(context, theme);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mCallback = callBack;
        mInitialMinute = 0;
        mInitialSeconds = 0;
        mIs24HourView = is24HourView;

        mDateFormat = DateFormat.getTimeFormat(context);
        mCalendar = Calendar.getInstance();
        updateTitle(mInitialMinute, mInitialSeconds);

        setButton(context.getText(R.string.time_set), this);
        setButton2(context.getText(R.string.cancel), (OnClickListener) null);

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.time_picker_dialog, null);
        setView(view);
        mTimePicker = (TimePicker) view.findViewById(R.id.timePicker);

        // initialize state
        mTimePicker.setCurrentMinute(mInitialMinute);
        mTimePicker.setCurrentSecond(mInitialSeconds);
        mTimePicker.setIs24HourView(mIs24HourView);
        mTimePicker.setOnTimeChangedListener(this);
    }

    public void onClick(DialogInterface dialog, int which) {
        if (mCallback != null) {
            mTimePicker.clearFocus();
            mCallback.onTimeSet(mTimePicker, mTimePicker.getCurrentMinute(),
                    mTimePicker.getCurrentSeconds());
        }
    }

    private void updateTitle(int minute, int seconds) {
        mCalendar.set(Calendar.MINUTE, minute);
        mCalendar.set(Calendar.SECOND, seconds);
        setTitle("Workout Time: " + String.format("%d", minute) + ":" + String.format("%02d", seconds));
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(MINUTE, mTimePicker.getCurrentMinute());
        state.putInt(SECONDS, mTimePicker.getCurrentSeconds());
        state.putBoolean(IS_24_HOUR, mTimePicker.is24HourView());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int minute = savedInstanceState.getInt(MINUTE);
        int seconds = savedInstanceState.getInt(SECONDS);
        mTimePicker.setCurrentMinute(minute);
        mTimePicker.setCurrentSecond(seconds);
        mTimePicker.setIs24HourView(savedInstanceState.getBoolean(IS_24_HOUR));
        mTimePicker.setOnTimeChangedListener(this);
        updateTitle(minute, seconds);
    }
}
