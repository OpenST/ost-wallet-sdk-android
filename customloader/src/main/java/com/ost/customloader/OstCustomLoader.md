# Custom Loader Useage

## Setup
1. Copy `customLoader` folder in Application project.
2. Add `customloader` in setting.gralde file in project.
```
include ':customloader'
```
3. Add `customloader` dependency in application build.gradle

```
implementation project(':customloader')
```

4. Set LoaderManager of Custom loader in your application onCreate method
```java
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /* Add below line in you application onCreate */
        OstWalletUI.setLoaderManager(OstMockLoaderManager.getInstance());
    }
}
```

After performing above steps, you are good to go with custom loader.

## Customize Loader

You can customize icons and text for custom loader as per application need.

### 1. Loader gif:
To modfiy loader, Add your `.gif` file and rename as `ost_progress_image.gif`. After that, replace it with `customloader/src/main/res/drawable/ost_progress_image.gif`<br/>

### 2. Success and Failure Icon:
To modify Icons, open `customloader/src/main/res/drawable/` and replace `ost_success_icon.png` and `ost_failure_icon` with your application icons.

### 3. Modify success message:
Developer can modify success message by modifying `SUCCESS_MESSAGE` value in `CustomLoader/OstSdkMessages.json` file

### 4. Modify loader text:
To modify loader text, update language for key `text` under `initial_loader`, `loader` and `acknowledge` in ost_content_config.json <br/>
ost_content_config is a file, which you set for `setContentConfig` function.