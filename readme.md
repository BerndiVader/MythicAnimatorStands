# AnimateStands 4 MythicMobs
build up on Bram Stout's ArmorStandAnimator Class, thx alot to him. Requires MythicMobs 4.0.0 or higher

### How to install?
Stop your server and copy the mmArmorStandAnimator.jar file into your plugins folder. After that restart the server.

### Usage?
1. You need an MythicMobs with the type of armor_stand. How to setup an armor_stand MythicMobs look here: http://www.mythicmobs.net/manual/doku.php/databases/mobs/options#armor_stands
#####
2. You need atleast one animation that you can apply to your mob. You find one example in the folder "MythicMobs/Anims/" This is btw the folder where all your animations should be stored.
#####
3. Setup the skills to make the MythicMobs an AnimateStand:
######
###Init the MythicMob:
The first thing to do is, initialize the MythicMobs as an AnimateStand. This can be done with the skill 
```
- asInit{anim=Example.anim;plate=false} @self ~onSpawn 1
```
This skill will setup the AnimateStand and assign the animation "Example.anim" to it. The option plate is optional. If false the armorstand will have no plate. The animation will only be loaded if it wasnt already cached befor. Now the mob is ready to be animated.
#### anim, or a = filename. The animfile have to be in the folder MythicMobs/Anims/Filename.anim
#### plate or base: true if the armorstand have a baseplate or false if not.
######
###Animate the MythicMob:
```
  - asAnimate{anim=Example.anim;r=20;d=0;plate=false} @self ~onTimer:20 1
```
This skill will now play the animation Example.anim for (r=20) 20 times with a delay of (d=0) 0 ticks. There is an ~onTimer:20 1 trigger which will restart the skill again. The AnimateStand timer should not last longer than the MythicMobs timer. It was nessercary for an buildin Timer because MythicMobs clocks only ticks every 4 ticks. This might be a bit to slow for the animations.
###### anim or a: again the name of the animation.
###### r or repeat: how many times should the animation repeat.
###### d or delay: how fast the animation should be played. where 0 is every tick.
######
### Pause the AnimateStand:
With this skill you can pause the animation.
```
 - asPause @self
```
Simple as that. There are no other options.
######
### Restart the Animation again:
```
- asRun @self
```
Also pretty simple. No other options here neither.
######
### TargetConditions: - AnimateStandPaused
```
 - AnimateStandPaused
```
Meet the condition if the animation is paused or not if not.
######
### TargetConditions: - isAnimateStand
```
 - isAnimateStand
```
Meet the condition if the MythicMob is an AnimateStand or false if not.
#
#
Examples:
```
animator:
  Type: armor_stand
  Options:
    HasArms: true
  Skills:
  - asinit{anim=Example.anim;plate=false} @self ~onSpawn 1
  - asanimate{anim=Example.anim;r=20;d=0} @self ~onTimer:20 1
```
