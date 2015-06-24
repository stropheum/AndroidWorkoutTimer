package com.example.stropheum.androidworkouttimer.view;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;

import com.example.stropheum.androidworkouttimer.R;

public class TimePicker extends FrameLayout {

    /**
     * A no-op callback used in the constructor to avoid null checks
     * later in the code.
     */
    private static final OnTimeChangedListener NO_OP_CHANGE_LISTENER = new OnTimeChangedListener() {
        public void onTimeChanged(TimePicker view, int minute, int seconds) {
        }
    };

    public static final NumberPicker.Formatter TWO_DIGIT_FORMATTER =
            new Formatter() {

                @Override
                public String format(int value) {
                    // TODO Auto-generated method stub
                    return String.format("%02d", value);
                }
            };

    // state
    private int mCurrentHour = 0; // 0-23
    private int mCurrentMinute = 0; // 0-59
    private int mCurrentSeconds = 0; // 0-59
    private Boolean mIs24HourView = false;
    private boolean mIsAm;

    // ui components
//    private final NumberPicker mHourPicker;
    private final NumberPicker mMinutePicker;
    private final NumberPicker mSecondPicker;
    //    private final Button mAmPmButton;
    private final String mAmText;
    private final String mPmText;

    // callbacks
    private OnTimeChangedListener mOnTimeChangedListener;

    /**
     * The callback interface used to indicate the time has been adjusted.
     */
    public interface OnTimeChangedListener {

        /**
         * @param view The view associated with this listener.
         * @param minute The current minute.
         * @param seconds The current second.
         */
        void onTimeChanged(TimePicker view, int minute, int seconds);
    }

    public TimePicker(Context context) {
        this(context, null);
    }

    public TimePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.time_picker_widget,
                this, // we are the parent
                true);

        // digits of minute
        mMinutePicker = (NumberPicker) findViewById(R.id.minute);
        mMinutePicker.setMinValue(0);
        mMinutePicker.setMaxValue(59);
        mMinutePicker.setFormatter(TWO_DIGIT_FORMATTER);
        mMinutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker spinner, int oldVal, int newVal) {
                mCurrentMinute = newVal;
                onTimeChanged();
            }
        });

        // digits of seconds
        mSecondPicker = (NumberPicker) findViewById(R.id.seconds);
        mSecondPicker.setMinValue(0);
        mSecondPicker.setMaxValue(59);
        mSecondPicker.setFormatter(TWO_DIGIT_FORMATTER);
        mSecondPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mCurrentSeconds = newVal;
                onTimeChanged();

            }
        });

        // initialize to current time
        Calendar cal = Calendar.getInstance();
        setOnTimeChangedListener(NO_OP_CHANGE_LISTENER);

        // by default we're not in 24 hour mode
        setCurrentMinute(cal.get(Calendar.MINUTE));
        setCurrentSecond(cal.get(Calendar.SECOND));

        mIsAm = (mCurrentHour < 12);

        /* Get the localized am/pm strings and use them in the spinner */
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] dfsAmPm = dfs.getAmPmStrings();
        mAmText = dfsAmPm[Calendar.AM];
        mPmText = dfsAmPm[Calendar.PM];

        if (!isEnabled()) {
            setEnabled(false);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mMinutePicker.setEnabled(enabled);
    }

    /**
     * Used to save / restore state of time picker
     */
    private static class SavedState extends BaseSavedState {

        private final int mHour;
        private final int mMinute;

        private SavedState(Parcelable superState, int hour, int minute) {
            super(superState);
            mHour = hour;
            mMinute = minute;
        }

        private SavedState(Parcel in) {
            super(in);
            mHour = in.readInt();
            mMinute = in.readInt();
        }

        public int getMinute() {
            return mMinute;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mHour);
            dest.writeInt(mMinute);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, mCurrentHour, mCurrentMinute);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
//        setCurrentHour(ss.getHour());
        setCurrentMinute(ss.getMinute());
    }

    /**
     * Set the callback that indicates the time has been adjusted by the user.
     * @param onTimeChangedListener the callback, should not be null.
     */
    public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener) {
        mOnTimeChangedListener = onTimeChangedListener;
    }

    /**
     * @return The current hour (0-23).
     */
    public Integer getCurrentHour() {
        return mCurrentHour;
    }

    /**
     * Set whether in 24 hour or AM/PM mode.
     * @param is24HourView True = 24 hour mode. False = AM/PM.
     */
    public void setIs24HourView(Boolean is24HourView) {
        if (mIs24HourView != is24HourView) {
            mIs24HourView = is24HourView;
        }
    }

    /**
     * @return true if this is in 24 hour view else false.
     */
    public boolean is24HourView() {
        return mIs24HourView;
    }

    /**
     * @return The current minute.
     */
    public Integer getCurrentMinute() {
        return mCurrentMinute;
    }

    /**
     * Set the current minute (0-59).
     */
    public void setCurrentMinute(Integer currentMinute) {
        this.mCurrentMinute = currentMinute;
        updateMinuteDisplay();
    }

    /**
     * @return The current minute.
     */
    public Integer getCurrentSeconds() {
        return mCurrentSeconds;
    }

    /**
     * Set the current second (0-59).
     */
    public void setCurrentSecond(Integer currentSecond) {
        this.mCurrentSeconds = currentSecond;
        updateSecondsDisplay();
    }

    private void onTimeChanged() {
        mOnTimeChangedListener.onTimeChanged(this, getCurrentMinute(), getCurrentSeconds());
    }

    /**
     * Set the state of the spinners appropriate to the current minute.
     */
    private void updateMinuteDisplay() {
        mMinutePicker.setValue(mCurrentMinute);
        mOnTimeChangedListener.onTimeChanged(this, getCurrentMinute(), getCurrentSeconds());
    }

    /**
     * Set the state of the spinners appropriate to the current second.
     */
    private void updateSecondsDisplay() {
        mSecondPicker.setValue(mCurrentSeconds);
        mOnTimeChangedListener.onTimeChanged(this, getCurrentMinute(), getCurrentSeconds());
    }
}
