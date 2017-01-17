package org.powermock.api.easymock.annotation;

import org.powermock.core.classloader.annotations.PowerMockListener;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be placed on those fields in your test class that should
 * be mocked. This eliminates the need to setup and tear-down mocks manually
 * which minimizes repetitive test code and makes the test more readable. In
 * order for PowerMock to control the life-cycle of the mocks you must supply
 * the {@link PowerMockListener} annotation to the class-level of the test case.
 * For example:
 * 
 * <pre>
 * ...
 * &#064;PowerMockListener(AnnotationEnabler.class)
 * public class PersonServiceTest {
 * 
 * 	&#064;Mock
 * 	private PersonDao personDaoMock;
 * 
 * 	private PersonService classUnderTest;
 * 
 * 	&#064;Before
 * 	public void setUp() {
 * 		classUnderTest = new PersonService(personDaoMock);
 * 	}
 *  ...
 * }
 * </pre>
 * <p>
 * 
 * Note that you can also create partial mocks by using the annotation. Let's
 * say that the PersonService has a method called "getPerson" and another method
 * called "savePerson" and these are the only two methods that you'd like to
 * mock. Rewriting the previous example to accommodate this will give us the
 * following test:
 * 
 * <pre>
 * ...
 * &#064;PowerMockListener(AnnotationEnabler.class)
 * public class PersonServiceTest {
 * 
 * 	&#064;Mock({&quot;getPerson&quot;, &quot;savePerson&quot;})
 * 	private PersonDao personDaoMock;
 * 
 * 	private PersonService classUnderTest;
 * 
 * 	&#064;Before
 * 	public void setUp() {
 * 		classUnderTest = new PersonService(personDaoMock);
 * 	}
 *  ...
 * }
 * </pre>
 * <p>
 */

@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mock {
	String[] value() default "";

	/**
	 * Name of the test subject field to which this mock will be assigned. Use to disambiguate the case where
     * a mock may be assigned to multiple fields of the same type.
	 * When set, this mock will be assigned to the given field name in any test subject with a matching field name.
	 * If not set, injection is to all type-compatible fields in all test subjects.
	 * A given field name may only be used once, and there must be a matching field in at least one test subject.
	 *
	 * @return name of the field to inject to
	 **/
	String fieldName() default "";
}
