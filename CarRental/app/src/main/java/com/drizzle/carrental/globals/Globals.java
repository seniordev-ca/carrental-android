package com.drizzle.carrental.globals;


import com.drizzle.carrental.enumerators.ServiceArea;
import com.drizzle.carrental.models.Claim;
import com.drizzle.carrental.models.Coverage;
import com.drizzle.carrental.models.MyProfile;
import com.drizzle.carrental.models.VehicleType;

import lombok.Getter;
import lombok.Setter;

public class Globals {

    public static boolean isLoggedIn = false;

    public static MyProfile profile = new MyProfile();

    public static VehicleType  selectedVehicleType = null;
    public static ServiceArea selectedServiceArea = null;

    public static int paymentId = 0;

    public static Coverage coverage = null; //current active coverage

    public static String stringPhoneNumber = ""; //string phone number

    public static boolean isSignUpOrLoginRequest = true;

    public static Claim selectedClaim = new Claim();

}
