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
	private static Map<String, Frame[]> animCache = new HashMap<String, Frame[]>();
	public static Set<ArmorStandAnimator> animators = new HashSet<ArmorStandAnimator>();

	public static void updateAll() {
		for (ArmorStandAnimator ani : animators) {
			ani.update();
		}
	}

	public static Set<ArmorStandAnimator> getAnimators() {
		return animators;
	}

	public static void clearCache() {
		animCache.clear();
	}

	private ArmorStand armorStand;
	private int length;
	private Frame[] frames;
	private boolean paused = false;
	private int currentFrame;
	private Location startLocation;
	private boolean interpolate = true;
	private boolean negated = false;
	private boolean autoInit = false;
	public String aiMobName;
	private File aniFile;
	public ActiveMob am,aiMob;
	private BukkitTask task;
	private double mcheck;
	private int lastaction;
	
	public ArmorStandAnimator(File aniFile, ArmorStand armorStand, Object oi, Object mobtype) {
		this.aniFile = aniFile;
		this.armorStand = armorStand;
        this.armorStand.setMetadata("asa", new FixedMetadataValue(main.inst(),true));
		startLocation = armorStand.getLocation();
		if (oi!=null) this.autoInit = (Boolean)oi;
		this.am = MythicMobs.inst().getAPIHelper().getMythicMobInstance(armorStand);
		if (mobtype!=null) {
			this.aiMobName = (String)mobtype;
			this.attachToAIMob();
		}
		this.loadFrames();
		animators.add(this);
		this.checkMovement();
	}
	
	public int checkMovement() {
		int action = 0;
		double chk=this.armorStand.getLocation().getX()
				+this.armorStand.getLocation().getY()
				+this.armorStand.getLocation().getZ();
		if (chk!=this.mcheck) {
			if (lastaction!=1) {
				action = 1;
				this.lastaction = 1;
			}
			this.mcheck=chk;
		} else {
			if (lastaction!=2) {
				action = 2;
				this.lastaction=2;
			}
		}
		return action;
	}
	
	public void reAttachAIMob() {
		this.createAIMob();
	}
	
	private void attachToAIMob() {
		if (this.aiMob!=null && !this.aiMob.isDead()) return;
		this.createAIMob();
        ArmorStandAnimator asa = this;
		task = Bukkit.getScheduler().runTaskTimer(main.inst(), new Runnable() {
            @Override
            public void run() {
            	ActiveMob aim = MythicMobs.inst().getMobManager().getMythicMobInstance(aiMob.getEntity());
            	ActiveMob aam = MythicMobs.inst().getMobManager().getMythicMobInstance(am.getEntity());
            	if (aam==null) {
            		if (aim!=null) aim.getEntity().remove();
            		armorStand.remove();
            		asa.remove();
            		Bukkit.getScheduler().cancelTask(task.getTaskId());
            		return;
            	}
            	if (aim==null) return;
            	if (aim.isDead() || aam.isDead()) {
            		asa.remove();
            		aim.setDead();
            		armorStand.remove();
            		Bukkit.getScheduler().cancelTask(task.getTaskId());
            	} else {
					int check = checkMovement();
					if (check==1) {
						aam.signalMob(null, "MOVESTART");
					} else if (check==2) {
						aam.signalMob(null, "MOVESTOPP");
					}
					float y = aim.getEntity().getLocation().getYaw();
					float p = aim.getEntity().getLocation().getPitch();
					Location l=null;
					if (lastaction==2 && aim.hasTarget()) {
						l = ArmorStandUtils.lookAt(armorStand.getLocation(), aim.getEntity().getTarget().getBukkitEntity().getLocation());
					} else {
						Location ll = ArmorStandUtils.getTargetBlock(aim.getLivingEntity(), 10);
						if (ll!=null) {
							l = ArmorStandUtils.lookAt(armorStand.getLocation(),ll);
						}
					}
					if (l!=null) {
						y = l.getYaw();
						p = l.getPitch();
					}
					nmsutils.SetNMSLocation(armorStand,
							aim.getEntity().getLocation().getX(),
							aim.getEntity().getLocation().getY(),
							aim.getEntity().getLocation().getZ(),
							y,
							p);
				}
            }
       }, 1, 1);
	}
	
	private void createAIMob() {
		this.aiMob = MythicMobs.inst().getMobManager().spawnMob(this.aiMobName, this.armorStand.getLocation());
		main.getEntityHider().hideEntity(this.aiMob.getEntity().getBukkitEntity());
		String u1 = armorStand.getUniqueId().toString().substring(0, armorStand.getUniqueId().toString().length()/2);
		String u2 = armorStand.getUniqueId().toString().substring(armorStand.getUniqueId().toString().length()/2, armorStand.getUniqueId().toString().length());
        aiMob.getLivingEntity().setMetadata("aiMob", new FixedMetadataValue(main.inst(),u1));
        aiMob.getLivingEntity().setMetadata("aiMob1", new FixedMetadataValue(main.inst(),u2));
		Bukkit.getScheduler().runTaskLater(main.inst(), new Runnable() {
			@Override
			public void run() {
				ActiveMob aim = MythicMobs.inst().getAPIHelper().getMythicMobInstance(aiMob.getLivingEntity());
				if (aim!=null) {
					aim.getLivingEntity().setNoDamageTicks(20);
					aim.getLivingEntity().setCanPickupItems(false);
				}
			}
		}, 15);
	}

	public void changeAnim(File aniFile) {
		this.stop();
		this.aniFile = aniFile;
		this.loadFrames();
		this.play();
	}

	private void loadFrames() {
		if (animCache.containsKey(this.aniFile.getAbsolutePath())) {
			frames = new Frame[animCache.get(aniFile.getAbsolutePath()).length];
			frames = animCache.get(aniFile.getAbsolutePath());
			this.length = frames.length;
			this.currentFrame=0;
			this.paused=false;
			this.negated=false;
		} else {
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
					else if (line.startsWith("frame")) {
						if (currentFrame != null) {
							frames[currentFrame.frameID] = currentFrame;
						}
						int frameID = Integer.parseInt(line.split(" ")[1]);
						currentFrame = new Frame();
						currentFrame.frameID = frameID;
					}
					else if (line.contains("Armorstand_Position")) {
						currentFrame.x = Float.parseFloat(line.split(" ")[1]);
						currentFrame.y = Float.parseFloat(line.split(" ")[2]);
						currentFrame.z = Float.parseFloat(line.split(" ")[3]);
						currentFrame.r = Float.parseFloat(line.split(" ")[4]);
					}
					else if (line.contains("Armorstand_Middle")) {
						float x = (float) Math.toRadians(Float.parseFloat(line.split(" ")[1]));
						float y = (float) Math.toRadians(Float.parseFloat(line.split(" ")[2]));
						float z = (float) Math.toRadians(Float.parseFloat(line.split(" ")[3]));
						if (this.negated) {
							x=-x;y=-y;z=-z;
						}
						currentFrame.middle = new EulerAngle(x, y, z);
					}
					else if (line.contains("Armorstand_Right_Leg")) {
						float x = (float) Math.toRadians(Float.parseFloat(line.split(" ")[1]));
						float y = (float) Math.toRadians(Float.parseFloat(line.split(" ")[2]));
						float z = (float) Math.toRadians(Float.parseFloat(line.split(" ")[3]));
						if (this.negated) {
							x=-x;y=-y;z=-z;
						}
						currentFrame.rightLeg = new EulerAngle(x, y, z);
					}
					else if (line.contains("Armorstand_Left_Leg")) {
						float x = (float) Math.toRadians(Float.parseFloat(line.split(" ")[1]));
						float y = (float) Math.toRadians(Float.parseFloat(line.split(" ")[2]));
						float z = (float) Math.toRadians(Float.parseFloat(line.split(" ")[3]));
						if (this.negated) {
							x=-x;y=-y;z=-z;
						}
						currentFrame.leftLeg = new EulerAngle(x, y, z);
					}
					else if (line.contains("Armorstand_Left_Arm")) {
						float x = (float) Math.toRadians(Float.parseFloat(line.split(" ")[1]));
						float y = (float) Math.toRadians(Float.parseFloat(line.split(" ")[2]));
						float z = (float) Math.toRadians(Float.parseFloat(line.split(" ")[3]));
									if (this.negated) {
							x=-x;y=-y;z=-z;
						}
						currentFrame.leftArm = new EulerAngle(x, y, z);
					}
					else if (line.contains("Armorstand_Right_Arm")) {
						float x = (float) Math.toRadians(Float.parseFloat(line.split(" ")[1]));
						float y = (float) Math.toRadians(Float.parseFloat(line.split(" ")[2]));
						float z = (float) Math.toRadians(Float.parseFloat(line.split(" ")[3]));
						if (this.negated) {
							x=-x;y=-y;z=-z;
						}
						currentFrame.rightArm = new EulerAngle(x, y, z);
					}
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
				if (br != null) {
					try {
						br.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			frames[0].autoInit=this.autoInit;
			frames[0].aiMobName=this.aiMobName;
			animCache.put(aniFile.getAbsolutePath(), frames);
		}
	}
	
	public void remove() {
		animators.remove(this);
		if (this.aiMob!=null && !this.aiMob.isDead()) {
			this.aiMob.getEntity().remove();
		}
	}

	public void pause() {
		paused = true;
	}

	public void stop() {
		currentFrame = 0;
		update();
		currentFrame = 0;
		paused = true;
	}

	public void play() {
		paused = false;
	}

	public void update() {
		if (!paused) {
			if (currentFrame >= (length - 1) || currentFrame < 0) {
				currentFrame = 0;
			}
			Frame f = frames[currentFrame];
			if(f == null) f = interpolate(currentFrame);
			if (f != null) {
				Entity e = armorStand;
				if (e.getVehicle()==null) {
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
				armorStand.setBodyPose(f.middle);
				armorStand.setLeftLegPose(f.leftLeg);
				armorStand.setRightLegPose(f.rightLeg);
				armorStand.setLeftArmPose(f.leftArm);
				armorStand.setRightArmPose(f.rightArm);
				armorStand.setHeadPose(f.head);
				
				if (f.doSkill!=null) executeMythicMobsSkill(f.doSkill);
			}
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

	public int getCurrentFrame() {
		return currentFrame;
	}
	
	public String checkForSkill() {
		return this.frames[this.currentFrame].doSkill;
	}

	public void setCurrentFrame(int currentFrame) {
		this.currentFrame = currentFrame;
	}

	public ArmorStand getArmorStand() {
		return armorStand;
	}

	public int getLength() {
		return length;
	}

	public Frame[] getFrames() {
		return frames;
	}

	public boolean isPaused() {
		return paused;
	}

	public Location getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(Location location) {
		if (this.armorStand.getVehicle()==null) {
			startLocation = location;
		} else {
			startLocation = this.armorStand.getVehicle().getLocation().clone();
		}
	}

	public boolean isInterpolated() {
		return interpolate;
	}

	public void setInterpolated(boolean interpolate) {
		this.interpolate = interpolate;
	}

	private Frame interpolate(int frameID) {
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
		res = new Frame();
		res.frameID = frameID;
		float Dmin = frameID - minFrame.frameID;
		float D = maxFrame.frameID - minFrame.frameID;
		float D0 = Dmin / D;
		res = minFrame.mult(1 - D0, frameID).add(maxFrame.mult(D0, frameID), frameID);
		return res;
	}

	public static class Frame {
		int frameID;
		String aiMobName;
		String doSkill;
		boolean autoInit;
		float x, y, z, r, p;
		EulerAngle middle;
		EulerAngle rightLeg;
		EulerAngle leftLeg;
		EulerAngle rightArm;
		EulerAngle leftArm;
		EulerAngle head;
		
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