package ost.com.ostsdkui.uicomponents.uiutils.content;

import org.json.JSONObject;

public class StringConfig {

    private final String name;

    StringConfig(JSONObject stringObject) {
        this.name = stringObject.optString("name");
        int startIndexOfPlaceHolder = name.indexOf("<");
        int endIndexOfPlaceHolder = name.indexOf(">", startIndexOfPlaceHolder);
        String placeholderString = this.name.substring(startIndexOfPlaceHolder, endIndexOfPlaceHolder).trim();

    }

    public String getString() {
        return this.name;
    }
}
