package samples.powermockito.junit4.bridgemethod;

import org.junit.Ignore;
import org.junit.Test;
import samples.bridgemethod.SomeInterface;
import samples.bridgemethod.SubClass;
import samples.bridgemethod.SubImpl;
import samples.bridgemethod.SuperClass;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public abstract class BridgeMethodCases {

    @Test
    @Ignore
    public void shouldMockBridgeMethodWithDifferentParameters() {
        SuperClass subClass = mock(SubClass.class);
        when(subClass.sayYes(any(SomeInterface.class))).thenCallRealMethod();

        SubImpl sub = new SubImpl("A");
        assertThat(subClass.sayYes(sub)).isEqualTo("Yes, sir");
    }

    @Test
    @Ignore
    public void shouldMockBridgeMethodWithDifferentOut() {
        SuperClass subClass = mock(SubClass.class);
        when(subClass.calculate(anyInt())).thenReturn(2);

        assertThat(subClass.calculate(10)).isEqualTo(2);
    }

}
