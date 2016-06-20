package projb.dissystems.aueb.vassilis.nycheckins;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Vassilis on 13/5/2016.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    TextView mapsActivityText;

    public void setMapsActivityText(TextView textView) {
        mapsActivityText = textView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        String date_format = year + "-" + (month + 1) + "-" + day;
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = formatDate.parse(date_format);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = formatDate.format(date);
        mapsActivityText.setText(formattedDate);
    }
}
