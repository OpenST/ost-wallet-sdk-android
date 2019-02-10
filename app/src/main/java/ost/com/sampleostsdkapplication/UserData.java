package ost.com.sampleostsdkapplication;

import org.json.JSONObject;

public class UserData {
    private final String name;
    private final String mobile;
    private final String description;
    private final String id;

    public UserData(String id, String name, String mobile, String description) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.description = description;
    }

    public static UserData parse(JSONObject jsonObject) {
        String id = jsonObject.optString(Constants.OST_USER_ID, "");
        String name = jsonObject.optString(Constants.USER_NAME, "");
        String mobile = jsonObject.optString(Constants.MOBILE_NUMBER,"");
        String description = jsonObject.optString(Constants.DESCRIPTION, "");
        return new UserData(id, name, mobile, description);
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }
}
