package com.ost.ostsdk.models.entities;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class UserTest {

    @Test
    public void testUserInvalidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any User  attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        testUserJsonException(jsonObject);

        //Test Id with partial User attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        jsonObject.put(User.TOKEN_ID, "123");
        jsonObject.put(User.NAME, "status");
        testUserJsonException(jsonObject);
    }

    @Test
    public void testUserValidInsertion() throws JSONException {

        //Init json object
        JSONObject jsonObject;

        //Test without any User  attribute
        jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, "ID");
        jsonObject.put(User.TOKEN_ID, "0x2901239");
        jsonObject.put(User.TOKEN_HOLDER_ID, "123");
        jsonObject.put(User.NAME, "name");
        jsonObject.put(User.MULTI_SIG_ID, "1");

        User user = new User(jsonObject);
        assertEquals("0x2901239", user.getTokenId());
        assertEquals("123", user.getTokenHolderId());
        assertEquals("name", user.getName());
        assertEquals("ID", user.getId());
        assertEquals("1", user.getMultiSigId());
    }


    private void testUserJsonException(JSONObject jsonObject) {
        try {
            User user = new User(jsonObject);
            fail();
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testUserJsonWithNoException(JSONObject jsonObject) {
        try {
            User user = new User(jsonObject);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}