package com.ractoc.fs.components.ai;

public class AiConstants {
	
	// global
	static final String ASSET_MANAGER_PROPERTY = "assetManager";
	static final String SHIP_ENTITY_PROPERTY = "shipEntity";
	
	// ApproachComponent
	static final String DESTROYED_EXIT = "destroyed";
	static final String APPROACH_RANGE_PROPERTY = "range";
	
	// RangeTriggerComponent
	static final String IN_RANGE_EXIT = "inRange";
	static final String RANGE_PROPERTY = "range";
	
	// ShootComponent
	static final String BOOM_EXIT = "boom";

	// SpawnShipComponent
	static final String SHIP_TEMPLATE_PROPERTY = "shipTemplate";
	static final String SPAWNED_EXIT = "spawned";
	
	// SubScriptComponent
	static final String SUB_SCRIPTS_EXIT = "exit";
	static final String SCRIPTS_PROPERTY = "scripts";
	
	// TimerComponent
	static final String TIME_EXIT = "time";
	static final String INTERVALPROPERTY = "interval";
	
	private AiConstants() {
		// this is a utility class and should never be instantiated.
	}
	
}
