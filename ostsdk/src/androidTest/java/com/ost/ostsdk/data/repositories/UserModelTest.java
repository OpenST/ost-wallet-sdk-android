package com.ost.ostsdk.data.repositories;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ost.ostsdk.data.models.entities.UserEntity;
import com.ost.ostsdk.data.models.DbProcessCallback;
import com.ost.ostsdk.data.models.Impls.DbUserModel;
import com.ost.ostsdk.data.models.UserModel;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class UserModelTest {

    static UserModel mUserRepository;

    @BeforeClass
    public static void  setUp() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        mUserRepository = new DbUserModel(appContext.getApplicationContext());
        mUserRepository.deleteAllUsers(null);
    }

    @Test
    public void testUserInsertion() {
        // Context of the app under test.
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setEconomyId(1);
        userEntity.setName("user");
        userEntity.setTokenHolderId(1);

        mUserRepository.insertUser(userEntity, new DbProcessCallback() {
            @Override
            public void onProcessComplete() {
                UserEntity userEntity = mUserRepository.getUserById(1);
                assertEquals("user",userEntity.getName());
            }
        });
    }
}
