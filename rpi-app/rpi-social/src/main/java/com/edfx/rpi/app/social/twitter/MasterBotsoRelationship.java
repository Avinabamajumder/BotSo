package com.edfx.rpi.app.social.twitter;

/**
 * Class {@code MasterBotsoRelationship}
 * 
 * @author Tapas Bose
 * @since RPI V1.0
 */
public class MasterBotsoRelationship {

	public final boolean masterFollowBotso;
	public final boolean botsoFollowMaster;

	/**
	 * Constructor {@code MasterBotsoRelationship}
	 * 
	 * @author Tapas Bose
	 * @since RPI V1.0
	 * @param masterFollowBotso
	 * @param botsoFollowMaster
	 */
	public MasterBotsoRelationship(boolean masterFollowBotso, boolean botsoFollowMaster) {
		this.masterFollowBotso = masterFollowBotso;
		this.botsoFollowMaster = botsoFollowMaster;
	}
}
