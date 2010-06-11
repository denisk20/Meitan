package com.meitan.lubov.services.test;

	public class Item {
		String value;
		boolean valid;

		public Item(String value, boolean valid) {
			this.value = value;
			this.valid = valid;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public boolean isValid() {
			return valid;
		}

		public void setValid(boolean valid) {
			this.valid = valid;
		}
	}