package samples.powermockito.junit4.bridgemethod;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.bridgemethod.SubClass;
import samples.bridgemethod.SuperClass;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SuperClass.class, SubClass.class})
public class BridgeMethodTest extends BridgeMethodCases {
}
