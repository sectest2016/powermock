package samples.powermockito.junit4.rule.objenesis;

import org.junit.Rule;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import samples.bridgemethod.SubClass;
import samples.bridgemethod.SuperClass;
import samples.powermockito.junit4.bridgemethod.BridgeMethodCases;

@PrepareForTest({SuperClass.class, SubClass.class})
public class BridgeMethodTest extends BridgeMethodCases {
    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();
}
