package com.ost.ostsdk.models.entities;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OstBaseEntityTest {

//    @Test
//    public void testUserInvalidInsertion() throws JSONException {
//
//        //Test with empty json object
//        JSONObject jsonObject = new JSONObject();
//        testBaseEntityJsonException(jsonObject);
//
//        //Test with Id
//        jsonObject = new JSONObject();
//        jsonObject.put(OstBaseEntity.ID, 12);
//        testBaseEntityJsonException(jsonObject);
//
//        //Test Id for special characters
//        jsonObject = new JSONObject();
//        jsonObject.put(OstBaseEntity.ID, "12$+{(");
//        testBaseEntityJsonException(jsonObject);
//
//        //Test parent Id for special characters
//        jsonObject = new JSONObject();
//        jsonObject.put(OstBaseEntity.ID, "12");
//        jsonObject.put(OstBaseEntity.PARENT_ID, "12$+{(");
//        testBaseEntityJsonException(jsonObject);
//
//        //Test Status for invalid Status
//        jsonObject = new JSONObject();
//        jsonObject.put(OstBaseEntity.ID, "12");
//        jsonObject.put(OstBaseEntity.STATUS, "DELELD");
//        testBaseEntityJsonException(jsonObject);
//
//    }
//
//    @Test
//    public void testUserValidInsertion() throws JSONException {
//
//        JSONObject jsonObject;
//
//        //Test with Id
//        jsonObject = new JSONObject();
//        jsonObject.put(OstBaseEntity.ID, "12");
//        testBaseEntityJsonWithNoException(jsonObject);
//
//        //Test Id for special characters
//        jsonObject = new JSONObject();
//        jsonObject.put(OstBaseEntity.ID, "0x2q4234234");
//        testBaseEntityJsonWithNoException(jsonObject);
//
//        //Test parent Id for special characters
//        jsonObject = new JSONObject();
//        jsonObject.put(OstBaseEntity.ID, "0x2q4234234");
//        jsonObject.put(OstBaseEntity.PARENT_ID, "0xksjdleur348w");
//        testBaseEntityJsonWithNoException(jsonObject);
//
//        //Test Status for valid Status
//        jsonObject = new JSONObject();
//        jsonObject.put(OstBaseEntity.ID, "0x2q4234234");
//        jsonObject.put(OstBaseEntity.STATUS, "DELETED");
//        testBaseEntityJsonWithNoException(jsonObject);
//
//        //Test UPDATED_TIMESTAMP for invalid UPDATED_TIMESTAMP type
//        jsonObject = new JSONObject();
//        jsonObject.put(OstBaseEntity.ID, "0x2q4234234");
//        jsonObject.put(OstBaseEntity.UPDATED_TIMESTAMP, 123);
//        testBaseEntityJsonWithNoException(jsonObject);
//    }

    private void testBaseEntityJsonException(JSONObject jsonObject) {
        try {
            OstBaseEntity baseEntity = new OstBaseEntity(jsonObject);
            fail();
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    private void testBaseEntityJsonWithNoException(JSONObject jsonObject) {
        try {
            OstBaseEntity baseEntity = new OstBaseEntity(jsonObject);
            assertTrue(true);
        } catch (Exception ex) {
            fail();
        }
    }
}