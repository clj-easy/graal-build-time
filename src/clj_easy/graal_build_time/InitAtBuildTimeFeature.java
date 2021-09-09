import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;
import com.oracle.svm.core.annotate.AutomaticFeature;

@AutomaticFeature
public class InitAtBuildTimeFeature implements Feature {

    @Override
    public void duringSetup(DuringSetupAccess access) {
        String[] packages = clj_easy.graal_build_time.packageList();
        String packagesStr = clj_easy.graal_build_time.packageListStr(packages);
        System.out.println("Registering packages for build time initialization: " + packagesStr);
        RuntimeClassInitialization.initializeAtBuildTime(packages);
    }

}
