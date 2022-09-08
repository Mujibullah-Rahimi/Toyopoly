package no.hiof.toyopoly

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController


class HomeFragment : Fragment(), View.OnClickListener{
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val legoButton = view.findViewById<Button>(R.id.legoButton)
        legoButton.setOnClickListener(this)

        val dollsButton = view.findViewById<Button>(R.id.dollsButton)
        dollsButton.setOnClickListener(this)

        val carsButton = view.findViewById<Button>(R.id.carsButton)
        carsButton.setOnClickListener(this)
    }



    override fun onClick(v: View?) {
        val navController = v?.findNavController()
        val legoButton = v?.findViewById<Button>(R.id.legoButton)
        val dollsButton = v?.findViewById<Button>(R.id.dollsButton)
        val carsButton = v?.findViewById<Button>(R.id.carsButton)

        val action = HomeFragmentDirections.actionHomeFragmentToCategoryFragment()

        when (v?.id){
            R.id.legoButton -> {
                action.category = legoButton?.text.toString()
                navController?.navigate(action)
            }
            R.id.dollsButton -> {
                action.category = dollsButton?.text.toString()
                navController?.navigate(action)
            }
            R.id.carsButton -> {
                action.category = carsButton?.text.toString()
                navController?.navigate(action)
            }
        }
    }
}