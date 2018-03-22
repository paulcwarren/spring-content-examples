package examples.utils;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class EventuallyMatcher {

    public static <T> TypeSafeMatcher<Iterable<T>> eventually(Matcher<T> delegate) {
        return new TypeSafeMatcher<Iterable<T>>(){
//            private Matcher<T> delegate;
            public boolean matchesSafely(Iterable<T> ts) {
                for( T t : ts ){
                    if(delegate.matches(t)) return true;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Eventually ");
                delegate.describeTo(description);
            }
        };
    }
}
