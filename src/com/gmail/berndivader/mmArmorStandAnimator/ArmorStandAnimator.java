package com.gmail.berndivader.mmArmorStandAnimator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;

import com.gmail.berndivader.mmArmorStandAnimator.NMS.NMSUtils;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.skills.AbstractSkill;
import io.lumine.xikage.mythicmobs.skills.Skill;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.SkillTargeter;
import io.lumine.xikage.mythicmobs.skills.SkillTrigger;
import io.lumine.xikage.mythicmobs.skills.targeters.ConsoleTargeter;
import io.lumine.xikage.mythicmobs.skills.targeters.IEntitySelector;
import io.lumine.xikage.mythicmobs.skills.targeters.ILocationSelector;
import io.lumine.xikage.mythicmobs.skills.targeters.MTOrigin;
import io.lumine.xikage.mythicmobs.skills.targeters.MTTriggerLocation;

public class ArmorStandAnimator {
	private static NMSUtils nmsutils = main.NMSUtils();
	/**
	 * This is a map containing the already loaded frames. This way we don't have to parse the same animation over and over.
	 */
	private static Map<String, Frame[]> animCache = new HashMap<String, Frame[]>();
	/**
	 * This is a list with all the animator instances. This makes it easy to update all the instances at one.
	 */
	private static Set<ArmorStandAnimator> animators = new HashSet<ArmorStandAnimator>();

	/** This void updates all the animator instances at once */
	public static void updateAll() {
		for (ArmorStandAnimator ani : animators) {
			ani.update();
		}
	}

	/** Returns all the animator instances */
	public static Set<ArmorStandAnimator> getAnimators() {
		return animators;
	}

	/** Clears the animation cache in case you want to update an animation */
	public static void clearCache() {
		animCache.clear();
	}

	/** The armor stand to animate */
	private ArmorStand armorStand;
	/** The amount of frames this animation has */
	private int length;
	/** All the frames of the animation */
	private Frame[] frames;
	/** Says when the animation is paused */
	private boolean paused = false;
	/** The current frame we're on */
	private int currentFrame;
	/** The start location of the animation */
	private Location startLocation;
	/** If this is true. The animator is going to guess the frames that aren't specified */
	private boolean interpolate = true;
	/** If this is true. The values of the frames will be negated. */
	private boolean negated = false;
	/** If true the ArmorStand will auto init the animator again if its destroyed. */
	private boolean autoInit = false;
	private String aiMobName;
	private File aniFile;
	private ActiveMob am,aiMob;
	private BukkitTask task;
	/**
	 * 
	 * create the aiMob for the ArmorStand
	 * 
	 */
	private void attachToAIMob() {
		if (this.aiMob!=null && !this.aiMob.isDead()) return;
		this.aiMob = MythicMobs.inst().getMobManager().spawnMob(this.aiMobName, this.armorStand.getLocation());
		PotionEffect pe = new PotionEffect(PotionEffectType.INVISIBILITY, 999999999, 2, false, false);
		this.aiMob.getLivingEntity().addPotionEffect(pe);
		String u1 = armorStand.getUniqueId().toString().substring(0, armorStand.getUniqueId().toString().length()/2);
		String u2 = armorStand.getUniqueId().toString().substring(armorStand.getUniqueId().toString().length()/2, armorStand.getUniqueId().toString().length());
        aiMob.getLivingEntity().setMetadata("aiMob", new FixedMetadataValue(main.inst(),u1));
        aiMob.getLivingEntity().setMetadata("aiMob1", new FixedMetadataValue(main.inst(),u2));
		Bukkit.getScheduler().runTaskLater(main.inst(), new Runnable() {
			@Override
			public void run() {
				aiMob.getLivingEntity().addPotionEffect(pe);
				armorStand.setInvulnerable(true);
				aiMob.getLivingEntity().setCanPickupItems(false);
				aiMob.getLivingEntity().getEquipment().clear();
			}
		}, 5);
		task = Bukkit.getScheduler().runTaskTimer(main.inst(), new Runnable() {
            @Override
            public void run() {
            	if (aiMob.isDead() || armorStand.isDead()) {
            		aiMob.getEntity().remove();
            		armorStand.remove();
            		Bukkit.getScheduler().cancelTask(task.getTaskId());
            	} else {
					nmsutils.SetNMSLocation(armorStand,
							aiMob.getEntity().getLocation().getX(),
							aiMob.getEntity().getLocation().getY(),
							aiMob.getEntity().getLocation().getZ(),
							aiMob.getEntity().getLocation().getYaw(),
							aiMob.getEntity().getLocation().getPitch());
            	}
            }
       }, 1, 1);
	}
	/**
	 * Constructor of the animator. Takes in the path to the file with the animation and the armor stand to animate.
	 * 
	 * @param aniFile
	 * @param armorStand
	 */
	public ArmorStandAnimator(File aniFile, ArmorStand armorStand, Object oi, Object mobtype) {
		// set all the stuff
		this.aniFile = aniFile;
		this.armorStand = armorStand;
		startLocation = armorStand.getLocation();
		if (oi!=null) this.autoInit = (Boolean)oi;
		this.am = MythicMobs.inst().getAPIHelper().getMythicMobInstance(armorStand);
		if (mobtype!=null) {
			this.aiMobName = (String)mobtype;
			this.attachToAIMob();
		}
		this.loadFrames();
		// register this instance of the animator
		animators.add(this);
	}
	
	public void changeAnim(File aniFile) {
		this.stop();
		this.aniFile = aniFile;
		this.loadFrames();
		this.play();
	}

	private void loadFrames() {
		// checks if the file has been loaded before. If so return the cached version
		if (animCache.containsKey(this.aniFile.getAbsolutePath())) {
			frames = new Frame[animCache.get(aniFile.getAbsolutePath()).length];
			frames = animCache.get(aniFile.getAbsolutePath());
//			this.autoInit = frames[0].autoInit;
//			this.aiMobName = frames[0].aiMobName;
			this.length = frames.length;
			this.currentFrame=0;
			this.paused=false;
			this.negated=false;
//			if (this.aiMobName.length()>0) this.attachToAIMob();
		} else {
			// File has not been loaded before so load it.
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(aniFile));
				String line = "";
				// create the current frame variable
				Frame currentFrame = null;
				while ((line = br.readLine()) != null) {
					// set the length
					if (line.startsWith("length")) {
						length = (int) Float.parseFloat(line.split(" ")[1]);
						frames = new Frame[length];
					} else if (line.startsWith("Animate_Negate")) {
						this.negated = true;
					} else if (line.startsWith("Animator_Auto_Init")) {
						this.autoInit = true;
					}
					// sets the current frame
					else if (line.startsWith("frame")) {
						if (currentFrame != null) {
							frames[currentFrame.frameID] = currentFrame;
						}
						int frameID = Integer.parseInt(line.split(" ")[1]);
						currentFrame = new Frame();
						currentFrame.frameID = frameID;
					}
					// sets the position and rotation or the main armor stand
					else if (line.contains("Armorstand_Position")) {
						currentFrame.x = Float.parseFloat(line.split(" ")[1]);
						currentFrame.y = Float.parseFloat(line.split(" ")[2]);
						currentFrame.z = Float.parseFloat(line.split(" ")[3]);
						currentFrame.r = Float.parseFloat(line.split(" ")[4]);
					}
					// sets the rotation for the middle
					else if (line.contains("Armorstand_Middle")) {
						float x = (float) Math.toRadians(Float.parseFloat(line.split(" ")[1]));
						float y = (float) Math.toRadians(Float.parseFloat(line.split(" ")[2]));
						float z = (float) Math.toRadians(Float.parseFloat(line.split(" ")[3]));
						if (this.negated) {
							x=-x;y=-y;z=-z;
						}
						currentFrame.middle = new EulerAngle(x, y, z);
					}
					// sets the rotation for the right leg
					else if (line.contains("Armorstand_Right_Leg")) {
						float x = (float) Math.toRadians(Float.parseFloat(line.split(" ")[1]));
						float y = (float) Math.toRadians(Float.parseFloat(line.split(" ")[2]));
						float z = (float) Math.toRadians(Float.parseFloat(line.split(" ")[3]));
						if (this.negated) {
							x=-x;y=-y;z=-z;
						}
						currentFrame.rightLeg = new EulerAngle(x, y, z);
					}
					// sets the rotation for the left leg
					else if (line.contains("Armorstand_Left_Leg")) {
						float x = (float) Math.toRadians(Float.parseFloat(line.split(" ")[1]));
						float y = (float) Math.toRadians(Float.parseFloat(line.split(" ")[2]));
						float z = (float) Math.toRadians(Float.parseFloat(line.split(" ")[3]));
						if (this.negated) {
							x=-x;y=-y;z=-z;
						}
						currentFrame.leftLeg = new EulerAngle(x, y, z);
					}
					// sets the rotation for the left arm
					else if (line.contains("Armorstand_Left_Arm")) {
						float x = (float) Math.toRadians(Float.parseFloat(line.split(" ")[1]));
						float y = (float) Math.toRadians(Float.parseFloat(line.split(" ")[2]));
						float z = (float) Math.toRadians(Float.parseFloat(line.split(" ")[3]));
									if (this.negated) {
							x=-x;y=-y;z=-z;
						}
						currentFrame.leftArm = new EulerAngle(x, y, z);
					}
					// sets the rotation for the right arm
					else if (line.contains("Armorstand_Right_Arm")) {
						float x = (float) Math.toRadians(Float.parseFloat(line.split(" ")[1]));
						float y = (float) Math.toRadians(Float.parseFloat(line.split(" ")[2]));
						float z = (float) Math.toRadians(Float.parseFloat(line.split(" ")[3]));
						if (this.negated) {
							x=-x;y=-y;z=-z;
						}
						currentFrame.rightArm = new EulerAngle(x, y, z);
					}
					// sets the rotation for the head
					else if (line.contains("Armorstand_Head")) {
						float x = (float) Math.toRadians(Float.parseFloat(line.split(" ")[1]));
						float y = (float) Math.toRadians(Float.parseFloat(line.split(" ")[2]));
						float z = (float) Math.toRadians(Float.parseFloat(line.split(" ")[3]));
						if (this.negated) {
							x=-x;y=-y;z=-z;
						}
						currentFrame.head = new EulerAngle(x, y, z);
					} else if (line.contains("executeSkill")) {
						currentFrame.doSkill = line.replaceFirst("executeSkill ", "");
					}
				}
				if (currentFrame != null) {
					frames[currentFrame.frameID] = currentFrame;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				// make sure to close the stream!
				if (br != null) {
					try {
						br.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			// add the animation to the cache, else adding the whole cache thing has no point.
			frames[0].autoInit=this.autoInit;
			frames[0].aiMobName=this.aiMobName;
			animCache.put(aniFile.getAbsolutePath(), frames);
		}
	}
	
	/**
	 * This method removes this instance from the animator instances list. When you don't want to use this instance any more, you can call this method.
	 */
	public void remove() {
		animators.remove(this);
		if (this.aiMob!=null && !this.aiMob.getEntity().isDead()) {
			this.aiMob.getEntity().remove();
		}
	}

	/** Pauses the animation */
	public void pause() {
		paused = true;
	}

	/**
	 * Pauses the animation and sets the current frame to 0. It also updates the animation one more time to set the armor stand to the first frame.
	 */
	public void stop() {
		// set the current frame to 0 and update the frame and set it to 0 again
		currentFrame = 0;
		update();
		currentFrame = 0;
		paused = true;
	}

	/** Plays the animation */
	public void play() {
		paused = false;
	}

	/** Updates the animation and goes to the next frame */
	public void update() {
		// make sure that the animation isn't paused
		if (!paused) {
			// makes sure that the frame is in bounds
			if (currentFrame >= (length - 1) || currentFrame < 0) {
				currentFrame = 0;
			}
			// get the frame
			Frame f = frames[currentFrame];
			//checks if we need to interpolate. If so interpolate.
			if(f == null) f = interpolate(currentFrame);
			// make sure it's not null
			if (f != null) {
				// get Entity instance for vehicle check
				Entity e = armorStand;
				// check for vehicle
				if (e.getVehicle()==null) {
					// get the new location
					if (this.aiMob==null) {
						Location newLoc = startLocation.clone().add(f.x, f.y, f.z);
						newLoc.setYaw(f.r + newLoc.getYaw());
						nmsutils.SetNMSLocation(e, newLoc.getX(), newLoc.getY(), newLoc.getZ(), newLoc.getYaw(), e.getLocation().getPitch());
					}
				} else {
					double xx,yy,zz;
					xx = e.getVehicle().getLocation().getX();
					yy = e.getVehicle().getLocation().getY();
					zz = e.getVehicle().getLocation().getZ();
					nmsutils.SetNMSLocation(e.getVehicle(), xx, yy, zz, e.getVehicle().getLocation().getYaw()+f.r, e.getVehicle().getLocation().getPitch());
				}
				// set all the values
				armorStand.setBodyPose(f.middle);
				armorStand.setLeftLegPose(f.leftLeg);
				armorStand.setRightLegPose(f.rightLeg);
				armorStand.setLeftArmPose(f.leftArm);
				armorStand.setRightArmPose(f.rightArm);
				armorStand.setHeadPose(f.head);
				
				//check for skill in frame:
				if (f.doSkill!=null) executeMythicMobsSkill(f.doSkill);
			}
			// go one frame higher
			currentFrame++;
		}
	}

	private void executeMythicMobsSkill(String skillName) {
		if (this.am==null) return;
	    Optional<SkillTargeter> maybeTargeter = Optional.empty();
        SkillMetadata data = new SkillMetadata(SkillTrigger.API, this.am, this.am.getEntity(), this.am.getLocation(), null, null, 1.0f);
	    String target = null;
	    String sname = skillName.split(" ")[0];
	    if (skillName.contains("@")) {
	    	String[] split = skillName.split("@");
	    	target = "@"+split[1].split(" ")[0];
	    	maybeTargeter = Optional.of(AbstractSkill.parseSkillTargeter(target));
	    }
	    if (maybeTargeter.isPresent()) {
            SkillTargeter targeter = maybeTargeter.get();
            if (targeter instanceof IEntitySelector) {
                data.setEntityTargets(((IEntitySelector)targeter).getEntities(data));
                ((IEntitySelector)targeter).filter(data, false);
            }
            if (targeter instanceof ILocationSelector) {
                data.setLocationTargets(((ILocationSelector)targeter).getLocations(data));
                ((ILocationSelector)targeter).filter(data);
            } else if (targeter instanceof MTOrigin) {
                data.setLocationTargets(((MTOrigin)targeter).getLocation(data.getOrigin()));
            } else if (targeter instanceof MTTriggerLocation) {
                HashSet<AbstractLocation> lTargets = new HashSet<AbstractLocation>();
                lTargets.add(data.getTrigger().getLocation());
                data.setLocationTargets(lTargets);
            }
            if (targeter instanceof ConsoleTargeter) {
                data.setEntityTargets(null);
                data.setLocationTargets(null);
            }
        } else {
        	if (am.hasThreatTable()) {
        		data.setEntityTarget(am.getThreatTable().getTopThreatHolder());
        	} else if (am.getEntity().getTarget()!=null) {
        		data.setEntityTarget(am.getEntity().getTarget());
        	} else {
        		data.setEntityTarget(am.getEntity());
        	}
        }
        Optional<Skill> maybeSkill = MythicMobs.inst().getSkillManager().getSkill(sname);
        if (!maybeSkill.isPresent()) return;
        Skill skill = maybeSkill.get();
        if (skill.usable(data, SkillTrigger.API)) skill.execute(data);
	}

	/** Returns the current frame */
	public int getCurrentFrame() {
		return currentFrame;
	}
	/** Returns the skill to execute after this frame */
	public String checkForSkill() {
		return this.frames[this.currentFrame].doSkill;
	}

	/** Sets the current frame */
	public void setCurrentFrame(int currentFrame) {
		this.currentFrame = currentFrame;
	}

	/** Returns the armor stand this instance animates */
	public ArmorStand getArmorStand() {
		return armorStand;
	}

	/** Returns the amount of frame this animation has */
	public int getLength() {
		return length;
	}

	/** Returns the list of frames */
	public Frame[] getFrames() {
		return frames;
	}

	/** Returns if the animation is paused */
	public boolean isPaused() {
		return paused;
	}

	/** Gets the start location */
	public Location getStartLocation() {
		return startLocation;
	}

	/**
	 * Sets the start location. If you want to teleport the armor stand this is the recommended function
	 * 
	 * @param location
	 */
	public void setStartLocation(Location location) {
		if (this.armorStand.getVehicle()==null) {
			startLocation = location;
		} else {
			startLocation = this.armorStand.getVehicle().getLocation().clone();
		}
	}

	/** Returns interpolate */
	public boolean isInterpolated() {
		return interpolate;
	}

	/** Sets interpolate */
	public void setInterpolated(boolean interpolate) {
		this.interpolate = interpolate;
	}

	/**Returns an interpolated frame*/
	private Frame interpolate(int frameID) {
		//get the minimum and maximum frames that are the closest
		Frame minFrame = null;
		for (int i = frameID; i >= 0; i--) {
			if (frames[i] != null) {
				minFrame = frames[i];
				break;
			}
		}
		Frame maxFrame = null;
		for (int i = frameID; i < frames.length; i++) {
			if (frames[i] != null) {
				maxFrame = frames[i];
				break;
			}
		}
		//make sure that those frame weren't the last one
		Frame res = null;

		if(maxFrame == null || minFrame == null) {
			if(maxFrame == null && minFrame != null) {
				return minFrame;
			}
			if(minFrame == null && maxFrame != null) {
				return maxFrame;
			}
			res = new Frame();
			res.frameID = frameID;
			return res;
		}
		//create the frame and interpolate
		res = new Frame();
		res.frameID = frameID;

		//this part calculates the distance the current frame is from the minimum and maximum frame and this allows for an easy linear interpolation
		float Dmin = frameID - minFrame.frameID;
		float D = maxFrame.frameID - minFrame.frameID;
		float D0 = Dmin / D;
		res = minFrame.mult(1 - D0, frameID).add(maxFrame.mult(D0, frameID), frameID);
		return res;
	}

	/**
	 * The frame class. This class holds all the information of one frame.
	 */
	public static class Frame {
		/**The Frame ID*/
		int frameID;
		//MythicMob aiMob
		String aiMobName;
		//do a skill at this frame
		String doSkill;
		//AutoInit after AnimatorInstance destroyed
		boolean autoInit;
		/**the location and rotation and pitch*/
		float x, y, z, r, p;
		/**The rotation of the body parts*/
		EulerAngle middle;
		EulerAngle rightLeg;
		EulerAngle leftLeg;
		EulerAngle rightArm;
		EulerAngle leftArm;
		EulerAngle head;
		/**This multiplies every value with another value.
		 * Used for interpolation
		 * @param a
		 * @param frameID
		 * @return
		 */
		public Frame mult(float a, int frameID) {
			Frame f = new Frame();
			f.frameID = frameID;
			f.x = f.x * a;
			f.y = f.y * a;
			f.z = f.z * a;
			f.r = f.r * a;
			f.middle = new EulerAngle(middle.getX() * a, middle.getY() * a, middle.getZ() * a);
			f.rightLeg = new EulerAngle(rightLeg.getX() * a, rightLeg.getY() * a, rightLeg.getZ() * a);
			f.leftLeg = new EulerAngle(leftLeg.getX() * a, leftLeg.getY() * a, leftLeg.getZ() * a);
			f.rightArm = new EulerAngle(rightArm.getX() * a, rightArm.getY() * a, rightArm.getZ() * a);
			f.leftArm = new EulerAngle(leftArm.getX() * a, leftArm.getY() * a, leftArm.getZ() * a);
			f.head = new EulerAngle(head.getX() * a, head.getY() * a, head.getZ() * a);
			return f;
		}
		/**This adds a value to every value.
		 * Used for interpolation
		 * @param a
		 * @param frameID
		 * @return
		 */
		public Frame add(Frame a, int frameID) {
			Frame f = new Frame();
			f.frameID = frameID;
			f.x = f.x + a.x;
			f.y = f.y + a.y;
			f.z = f.z + a.z;
			f.r = f.r + a.r;
			f.middle = new EulerAngle(middle.getX() + a.middle.getX(), middle.getY() + a.middle.getY(), middle.getZ() + a.middle.getZ());
			f.rightLeg = new EulerAngle(rightLeg.getX() + a.rightLeg.getX(), rightLeg.getY() + a.rightLeg.getY(), rightLeg.getZ() + a.rightLeg.getZ());
			f.leftLeg = new EulerAngle(leftLeg.getX() + a.leftLeg.getX(), leftLeg.getY() + a.leftLeg.getY(), leftLeg.getZ() + a.leftLeg.getZ());
			f.rightArm = new EulerAngle(rightArm.getX() + a.rightArm.getX(), rightArm.getY() + a.rightArm.getY(), rightArm.getZ() + a.rightArm.getZ());
			f.leftArm = new EulerAngle(leftArm.getX() + a.leftArm.getX(), leftArm.getY() + a.leftArm.getY(), leftArm.getZ() + a.leftArm.getZ());
			f.head = new EulerAngle(head.getX() + a.head.getX(), head.getY() + a.head.getY(), head.getZ() + a.head.getZ());
			return f;
		}
	}

}