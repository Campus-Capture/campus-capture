package com.github.campus_capture.bootcamp.utils;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import android.Manifest;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.github.campus_capture.bootcamp.activities.MainActivity;
import com.github.campus_capture.bootcamp.utils.PermissionUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@RunWith(AndroidJUnit4.class)
public class PermissionsUtilsTest {
    @Before
    public void removePermissions(){
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("pm revoke com.github.campus_capture.bootcamp android.permission.ACCESS_FINE_LOCATION");
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("pm revoke com.github.campus_capture.bootcamp android.permission.ACCESS_COARSE_LOCATION");
        //InstrumentationRegistry.getInstrumentation().getUiAutomation().revokeRuntimePermission("com.github.campus_capture.bootcamp", "android.permission.ACCESS_COARSE_LOCATION");
        //InstrumentationRegistry.getInstrumentation().getUiAutomation().revokeRuntimePermission("com.github.campus_capture.bootcamp", "android.permission.ACCESS_FINE_LOCATION");
    }

    public void grantPermission(){
        Instrumentation ir = InstrumentationRegistry.getInstrumentation();
        UiObject uo = UiDevice.getInstance(ir).findObject(new UiSelector().textContains("While using the app"));

        if(uo.exists()){
            try {
                uo.click();
            } catch (UiObjectNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void testRequestLocationPermissionGrant() throws Exception {
        ActivityScenario.launch(MainActivity.class);
        if (ContextCompat.checkSelfPermission(getTargetContext().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fail("Test should not have access to location. Has the @Before ran?");
        }

        Thread.sleep(2000);
        //app has no access to permission, which is excepted
        grantPermission();
        Thread.sleep(2000);

        if(ContextCompat.checkSelfPermission(getTargetContext().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            return;
        }

        fail("nique toi");
    }

    @Test
    public void testRequestLocationPermissionDeny() throws Exception {
        //check that no perm
        //request permission
        //deny
        //check that perm is not granted
    }
}