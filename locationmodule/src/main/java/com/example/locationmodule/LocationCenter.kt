package com.example.locationmodule

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

/**
 * Created by ayush on 9/12/17.
 */
class LocationCenter {

    fun isGooglePlayServicesAvailable(context: Context) : Boolean{

        val googleApiAvailability = GoogleApiAvailability.getInstance()
        var googlePlayServicesAvailable = googleApiAvailability.isGooglePlayServicesAvailable(context)
        if (googlePlayServicesAvailable != ConnectionResult.SUCCESS){

            if (googleApiAvailability.isUserResolvableError(googlePlayServicesAvailable)) googleApiAvailability.getErrorDialog(context,
                                                                                                googlePlayServicesAvailable,
                                                                                                1000).show()
            return false
        }
        return true
    }


}