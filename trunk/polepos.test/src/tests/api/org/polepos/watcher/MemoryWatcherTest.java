package tests.api.org.polepos.watcher;

import junit.framework.*;

import org.polepos.watcher.*;

public class MemoryWatcherTest extends TestCase {
	private MemoryWatcher _watcher;

	protected void setUp() throws Exception {
		_watcher = new MemoryWatcher();
	}

	public void test() throws Exception {
		final int MEMORY_CONSUMPTION = 1024*1024*10;
		_watcher.start();
		byte[] data = new byte[MEMORY_CONSUMPTION];
		for(int i = 0; i < data.length; ++i) {
			data[i] = 42;
		}
		_watcher.stop();
		Long value = (Long) _watcher.value();
		long memoryConsumed = value.longValue();
		// The following assertions are not safe in theory, but it works
		// practically.
		assertTrue(memoryConsumed < data.length * 1.5);
		assertTrue(memoryConsumed > data.length/2);
	}
}
