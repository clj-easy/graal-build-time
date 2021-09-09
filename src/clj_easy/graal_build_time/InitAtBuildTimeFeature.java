import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;
import com.oracle.svm.core.annotate.AutomaticFeature;

@AutomaticFeature
public class InitAtBuildTimeFeature implements Feature {

    @Override
    public void duringSetup(DuringSetupAccess access) {
        System.out.println("Registering build time packages.");
        RuntimeClassInitialization.initializeAtBuildTime("clojure", "");
    }

}
