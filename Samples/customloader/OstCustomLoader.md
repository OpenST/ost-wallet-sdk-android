# Custom Loader Usage

## Setup
1. Application project should have `ost-wallet-sdk-android` dependency.
2. Copy `customloader` directory in Application project `src` directory.
3. Define `customloader` resources and assets directory in application build.gradle.
```
android {
        sourceSets {
                main.java.srcDirs += 'src/customloader/src'
                main.assets.srcDirs += 'src/customloader/assets'
                main.res.srcDirs += 'src/customloader/res'
        }
}
```
4. Add resource import statements in `GIFView.java` and `OstMockLoaderFragment.java`
```
import <Your application pacakge name>.R
```

4. Set LoaderManager of Custom loader in your application onCreate method
```java
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /* Add below line in you application onCreate */
        OstWalletUI.setLoaderManager(customloader.src.OstMockLoaderManager.getInstance());
    }
}
```

After performing above steps, you are good to go with custom loader.

## Customize Loader

You can customize icons and text for custom loader as per application need.

### 1. Loader gif:
To modfiy loader, Add your `.gif` file and rename as `ost_progress_image.gif`. After that, replace it with `src/customloader/res/drawable/ost_progress_image.gif`<br/>

### 2. Success and Failure Icon:
To modify Icons, open `src/customloader/res/drawable/` and replace `ost_success_icon.png` and `ost_failure_icon` with your application icons.

### 3. Modify success message:
Developer can modify success message by modifying `SUCCESS_MESSAGE` value in `src/customloader/assets/OstSdkMessages.json` file

### 4. Modify loader text:
To modify loader text, update language for key `text` under `initial_loader`, `loader` and `acknowledge` in ost_content_config.json <br/>
ost_content_config is a file, which you set for `setContentConfig` function.
