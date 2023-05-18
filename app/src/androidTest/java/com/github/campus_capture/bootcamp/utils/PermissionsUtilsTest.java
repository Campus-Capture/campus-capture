package com.github.campus_capture.bootcamp.utils;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import android.Manifest;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.widget.RadioButton;

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
    public void removePermissions() throws IOException, InterruptedException {
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + getTargetContext().getApplicationContext().getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        getTargetContext().getApplicationContext().startActivity(i);

        Instrumentation ir = InstrumentationRegistry.getInstrumentation();
        UiObject perm = UiDevice.getInstance(ir).findObject(new UiSelector().textContains("Permissions"));

        try {
            perm.click();
        } catch (UiObjectNotFoundException e) {
            throw new RuntimeException(e);
        }

        Thread.sleep(2000);

        UiObject loc = UiDevice.getInstance(ir).findObject(new UiSelector().textContains("Location"));
        try {
            loc.click();
        } catch (UiObjectNotFoundException e) {
            throw new RuntimeException(e);
        }
        Thread.sleep(2000);
        UiObject allow = UiDevice.getInstance(ir).findObject(new UiSelector().className(RadioButton.class.getName()).index(2));
        try {
            allow.click();
        } catch (UiObjectNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void closeSettingsApp() throws IOException, RemoteException {
        Instrumentation ir = InstrumentationRegistry.getInstrumentation();
        UiDevice uiD = UiDevice.getInstance(ir);
        for(int i = 0; i < 10; i++){
            uiD.pressBack();
        }
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

    public void denyPermission(){
        Instrumentation ir = InstrumentationRegistry.getInstrumentation();
        UiObject uo = UiDevice.getInstance(ir).findObject(new UiSelector().textContains("Don't allow"));

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
            fail("App should not have location permission. Has @Before ran?");
        }
        Thread.sleep(2000);
        //app has no access to permission, which is excepted
        grantPermission();
        Thread.sleep(2000);

        if(ContextCompat.checkSelfPermission(getTargetContext().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            return;
        }

        fail("Something went wrong, permission was not granted");
    }

    @Test
    public void testRequestLocationPermissionDeny() throws Exception {
        ActivityScenario.launch(MainActivity.class);
        if (ContextCompat.checkSelfPermission(getTargetContext().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fail("App should not have location permission. Has @Before ran?");
        }
        Thread.sleep(2000);
        //app has no access to permission, which is excepted
        denyPermission();
        Thread.sleep(2000);

        if (ContextCompat.checkSelfPermission(getTargetContext().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            return;
        }

        fail("Something went wrong, permission was granted");
    }
}