package org.codenergic.theskeleton.core.data;

@FunctionalInterface
public interface Activeable {
	enum Status {
		INACTIVE(0), ACTIVE(1);

		private int stat;

		Status(int stat) {
			this.stat = stat;
		}

		public int getStatus() {
			return stat;
		}
	}

	int getStatus();
}
