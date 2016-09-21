package dk.magenta.eark.erms.extraction;

public class Dummy implements Runnable {

	@Override
	public void run() {
		long now = System.currentTimeMillis();
		long t = System.currentTimeMillis();
		while (t < now + 10000) {
			t = System.currentTimeMillis();
		}

	}

}
