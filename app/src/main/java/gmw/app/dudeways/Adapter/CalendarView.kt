package gmw.app.dudeways.Adapter



import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class CustomCalendarView : LinearLayout {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttrs: Int) : super(context, attrs, defStyleAttrs) {
        init()
    }

    private fun init() {
        orientation = VERTICAL
        setBackgroundColor(Color.WHITE)

        // Example: Add a TextView displaying the current date
        val currentDateTextView = TextView(context)
        val currentDate = getCurrentDate()
        currentDateTextView.text = currentDate
        addView(currentDateTextView)
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}

