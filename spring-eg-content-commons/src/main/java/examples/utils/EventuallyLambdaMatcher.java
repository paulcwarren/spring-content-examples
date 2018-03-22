package examples.utils;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

public class EventuallyLambdaMatcher<T> extends TypeSafeMatcher<Supplier<T>> {
    private static Duration defaultTimeout = Duration.ofMillis(5000);
    private final Matcher<T> matcher;
    private final Duration timeout;

    private EventuallyLambdaMatcher(Matcher<T> matcher) {
        this(matcher, defaultTimeout);
    }

    private EventuallyLambdaMatcher(Matcher<T> matcher, Duration timeout) {
        this.matcher = matcher;
        this.timeout = timeout;
    }

    public static <T> EventuallyLambdaMatcher<T> eventuallyEval(Matcher<T> matcher) {
        return new EventuallyLambdaMatcher<T>(matcher);
    }

    public static <T> EventuallyLambdaMatcher<T> eventuallyEval(Matcher<T> matcher, Duration timeout) {
        return new EventuallyLambdaMatcher<T>(matcher, timeout);
    }

    private T val = null;
    @Override
    protected boolean matchesSafely(Supplier<T> item) {
        Instant start = Instant.now();
        while(Duration.between(start, Instant.now()).compareTo(timeout) < 0) {
            try {
                val = item.get();
            } catch (Exception ignored) {}
            if(val != null && matcher.matches(val)) return true;
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendDescriptionOf(matcher);
    }

    @Override
    public void describeMismatchSafely(Supplier<T> lambda, Description mismatchDescription) {
        mismatchDescription.appendText(String.valueOf(val));
    }
}
