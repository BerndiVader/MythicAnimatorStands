package com.gmail.berndivader.animatorstands;

import java.util.List;

public 
class 
MiFrame 
{
	public int format;
	public String created_in;
	public boolean is_model;
	public int tempo;
	public int length;
	public List<KeyFrame>keyframes;
	
	@Override
	public String toString() {
		
		StringBuilder output=new StringBuilder();
		output.append("interpolate\n");
		output.append("length "+(length+1)+"\n");
		
		int size=keyframes.size();
		
		Position[]main_position=new Position[length+1];
		Position[]middle=new Position[length+1];
		Position[]right_leg=new Position[length+1];
		Position[]left_leg=new Position[length+1];
		Position[]left_arm=new Position[length+1];
		Position[]right_arm=new Position[length+1];
		Position[]head=new Position[length+1];
		
		for(int i1=0;i1<size;i1++) {
			KeyFrame frame=keyframes.get(i1);
			if(frame.part_name!=null) {
				switch(frame.part_name) {
				case "body":
					middle[frame.position]=frame.values;
					break;
				case "head":
					head[frame.position]=frame.values;
					break;
				case "left_arm":
					left_arm[frame.position]=frame.values;
					break;
				case "right_arm":
					right_arm[frame.position]=frame.values;
					break;
				case "left_leg":
					left_leg[frame.position]=frame.values;
					break;
				case "right_leg":
					right_leg[frame.position]=frame.values;
					break;
				case "base":
					break;
				}
			} else {
				main_position[frame.position]=frame.values;
			}
		}
		
		for(int i1=0;i1<=length;i1++) {
			if(main_position[i1]!=null) {
				output.append("frame "+i1+"\n");
				output.append("Armorstand_Position "+main_position[i1].POS_X+" "+main_position[i1].POS_Y+" "+main_position[i1].POS_Z+" "+main_position[i1].ROT_X+"\n");
				if(middle[i1]!=null) {
					output.append("Armorstand_Middle "+middle[i1].ROT_X+" "+middle[i1].ROT_Y+" "+middle[i1].ROT_Z+"\n");
				}
				if(right_leg[i1]!=null) {
					output.append("Armorstand_Right_Leg "+right_leg[i1].ROT_X+" "+right_leg[i1].ROT_Y+" "+right_leg[i1].ROT_Z+"\n");
				}
				if(left_leg[i1]!=null) {
					output.append("Armorstand_Left_Leg "+left_leg[i1].ROT_X+" "+left_leg[i1].ROT_Y+" "+left_leg[i1].ROT_Z+"\n");
				}
				if(left_arm[i1]!=null) {
					output.append("Armorstand_Left_Arm "+left_arm[i1].ROT_X+" "+left_arm[i1].ROT_Y+" "+left_arm[i1].ROT_Z+"\n");
				}
				if(right_arm[i1]!=null) {
					output.append("Armorstand_Right_Arm "+right_arm[i1].ROT_X+" "+right_arm[i1].ROT_Y+" "+right_arm[i1].ROT_Z+"\n");
				}
				if(head[i1]!=null) {
					output.append("Armorstand_Head "+head[i1].ROT_X+" "+head[i1].ROT_Y+" "+head[i1].ROT_Z+"\n");
				}
			}
		}
		return output.toString();
	}
	
}

class
KeyFrame
{
	public int position;
	public String part_name;
	public Position values;
}

class
Position
{
	public double ROT_X;
	public double ROT_Y;
	public double ROT_Z;
	public double POS_X;
	public double POS_Y;
	public double POS_Z;
}
