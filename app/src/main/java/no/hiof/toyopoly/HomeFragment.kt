package no.hiof.toyopoly

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController


class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val legoButton  = view.findViewById<Button>(R.id.legoButton)
        legoButton.setOnClickListener{
            val navController = it.findNavController()

            val action = HomeFragmentDirections.actionHomeFragmentToCategoryFragment()
            action.category = legoButton.text.toString()

            navController.navigate(action)
        }
    }
}