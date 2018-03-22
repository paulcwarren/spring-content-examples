package examples.utils;

import java.time.Clock;
import java.time.Duration;
import java.util.Iterator;
import java.util.function.Supplier;

public class RealTimeSeries<T> implements Iterable<T> {

    private Supplier<T> thing;
    private Duration duration = Duration.ofSeconds(30);

    public RealTimeSeries(Supplier<T> thing) {
        this.thing = thing;
    }

    public static <T> RealTimeSeries<T> sample(Supplier<T> thing) {
        return new RealTimeSeries(thing);
    }

    public RealTimeSeries<T> duration(long duration) {
        this.duration = Duration.ofSeconds(duration);
        return this;
    }

    @Override
    public String toString() {
        return this.thing.get().toString() + " (after " + this.duration.getSeconds() + " seconds)";
    }

    public Iterator<T> iterator() {
        return new SampleIterator(thing, duration);
    }

    private class SampleIterator implements Iterator {

        private Supplier<T> thing;

        private Clock clock = Clock.systemUTC();
        private Duration max;

        private Duration interval = Duration.ofSeconds(1);

        private boolean firstSample = true;
        private long expectedEnd;

        public SampleIterator(Supplier<T> thing, Duration max) {
            this.thing = thing;
            this.max = max;
        }

        public boolean hasNext() {
            if (firstSample) {
                return true;
            }
            final long endOfNextSample = clock.millis() + interval.toMillis();
            return endOfNextSample <= expectedEnd;
        }

        public T next() {
            if (firstSample) {
                expectedEnd = clock.millis() + max.toMillis();
                firstSample = false;
            } else {
                try {
                    Thread.currentThread().sleep(interval.toMillis());
                } catch (InterruptedException e) {
                    return thing.get();
                }
            }
            return thing.get();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
