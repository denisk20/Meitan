package com.meitan.lubov.services;

import com.meitan.lubov.services.dao.Dao;

import java.util.HashMap;

/**
 * Date: Nov 29, 2010
 * Time: 10:27:57 AM
 *
 * @author denisk
 */
public class FlowEntitiesManager {
	private HashMap<String, String> selectFlows = new HashMap<String, String>();
	private HashMap<String, String> editFlows = new HashMap<String, String>();
	private HashMap<String, Dao> daos = new HashMap<String, Dao>();

	public HashMap<String, String> getSelectFlows() {
		return selectFlows;
	}

	public void setSelectFlows(HashMap<String, String> selectFlows) {
		this.selectFlows = selectFlows;
	}

	public HashMap<String, String> getEditFlows() {
		return editFlows;
	}

	public void setEditFlows(HashMap<String, String> editFlows) {
		this.editFlows = editFlows;
	}

	public HashMap<String, Dao> getDaos() {
		return daos;
	}

	public void setDaos(HashMap<String, Dao> daos) {
		this.daos = daos;
	}

	public String getEditFlow(String type) {
		return editFlows.get(type);
	}
}
