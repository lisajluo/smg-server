package org.smg.server.database.models;

import java.util.HashMap;
import java.util.Map;

public class Player {
	private Map<String, String> properties = new HashMap<String, String>();

	public static enum PlayerProperty {
		PLAYERID, HASHEDPASSWORD, ACCESSSIGNATURE, EMAIL, FIRSTNAME, LASTNAME, NICKNAME;

		public static PlayerProperty findByValue(String value) {
			for (PlayerProperty p : values()) {
				if (p.toString().equals(value)) {
					return p;
				}
			}
			return null;
		}
	}

	public Player() {

	}

	public String getProperty(PlayerProperty property) {
		if (property == null) {
			throw new IllegalArgumentException();
		}
		String p = properties.get(property.toString());
		return p == null ? "" : p;
	}

	public Map<String, String> getAllProperties() {
		return this.properties;
	}

	public boolean setProperty(PlayerProperty property, String value) {
		if (property == null) {
			throw new IllegalArgumentException();
		}
		if (this.validateProperty(property, value)) {
			properties.put(property.toString(), value);
			return true;
		}
		return false;
	}

	private boolean validateProperty(PlayerProperty property, String value) {
		return true;
	}

	public boolean isContain(Player subPlayer) {
		Map<String, String> subPlayerInfo = subPlayer.getAllProperties();
		for (String key : subPlayerInfo.keySet()) {
			if (!this.properties.containsKey(key)
					|| !this.properties.get(key).equals(subPlayerInfo.get(key))) {
				return false;
			}
		}
		return true;
	}

}
