package no.hiof.toyopoly.userManagment

import android.text.Editable
import android.text.NoCopySpan
import android.text.TextWatcher
import android.widget.EditText

class DateInputMask (val input: EditText): NoCopySpan {

    fun listen() {
        input.addTextChangedListener(dateEntryWatcher)
    }

    private val dateEntryWatcher = object : TextWatcher {
        var edited = false
        val dividerCharacter = "/"

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (edited) {
                edited = false
                return
            }

            var working = getEditText()

            working = manageDateDivider(working, 2, start, before)
            working = manageDateDivider(working, 5, start, before)

            edited = true
            input.setText(working)
            input.setSelection(input.text.length)
        }

        private fun manageDateDivider(working: String, position : Int, start: Int, before: Int) : String{
            if (working.length == position) {
                return if (before <= position && start < position)
                    working + dividerCharacter
                else
                    working.dropLast(1)
            }
            return working
        }

        private fun getEditText() : String {
            return if (input.text.length >= 10)
                input.text.toString().substring(0,10)
            else
                input.text.toString()
        }

        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    }



}