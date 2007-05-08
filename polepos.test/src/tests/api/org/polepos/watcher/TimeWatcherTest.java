package tests.api.org.polepos.watcher;

import junit.framework.*;

import org.polepos.watcher.*;

public class TimeWatcherTest extends TestCase {
	private TimeWatcher _watcher;

	protected void setUp() throws Exception {
		_watcher = new TimeWatcher();
	}

	public void test() throws Exception {
		_watcher.start();
		Thread.sleep(1000);
		_watcher.stop();
		Long value = (Long) _watcher.value();
		long timeElapsed = value.longValue();
		// The following assertions are not safe in theory, but it works
		// practically.
		assertTrue(timeElapsed < 1500);
		assertTrue(timeElapsed > 500);
	}
}
