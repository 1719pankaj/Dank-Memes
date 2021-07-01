package com.example.dankmemes.Fragments

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dankmemes.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.fragment_onboarding.*
import kotlinx.android.synthetic.main.fragment_onboarding.view.*


class OnboardingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_onboarding, container, false)

        view.nextWpermission.setOnClickListener { dexter() }
        view.nextWOpermission.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingFragment_to_mainFragment)
            onBoardingFinished()
        }

        return view
    }


    private fun nextWithPermission() {
        if(isReadStoragePermissionGranted() && isWriteStoragePermissionGranted()) {
            findNavController().navigate(R.id.action_onboardingFragment_to_mainFragment)
            onBoardingFinished()
        } else {
            onboardingMessageTV.text = "If you want to continue W/O permission click continue without functionality Or grant permission"
        }
    }

    fun isReadStoragePermissionGranted(): Boolean {
        if (PermissionChecker.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
            Log.v("TAG", "READ permission granted")
        }
        else {
            Log.v("TAG", "no READ permission, permission requested")
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 3)
            }
        if (PermissionChecker.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
            Log.v("TAG", "READ permission granted")
            return true
        } else {
            Log.v("TAG", "READ permission revoked")
            return false
        }
    }


    fun isWriteStoragePermissionGranted(): Boolean {
        if (PermissionChecker.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
            Log.v("TAG", "WRITE permission granted")
        }
        else {
            Log.v("TAG", "no WRITE permission, permission requested")
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 4)
        }
        if (PermissionChecker.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
            Log.v("TAG", "WRITE permission granted")
            return true
        } else {
            Log.v("TAG", "WRITE permission revoked")
            return false
        }
    }

    fun dexter() {
        Dexter.withContext(context).withPermissions(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport){
                    if (p0.areAllPermissionsGranted()) {
                        findNavController().navigate(R.id.action_onboardingFragment_to_mainFragment)
                        onBoardingFinished()
                    } else {
                        onboardingMessageTV.text = "If you want to continue without permission click continue without functionality Or grant permission"
                        nextWOpermission.visibility = View.VISIBLE
                    }
                }

                override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, p1: PermissionToken?) {
                    p1?.continuePermissionRequest()
                }
            }).check()
    }

    private fun onBoardingFinished() {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }

}