package com.icegame.gm.value;

import com.icegame.gm.entity.JBehavior;

/**
 * @author absir
 *
 */
public enum EBehaviorType {

	GET_NPC {
		@Override
		public String getDescription(JBehavior behavior) {
			// TODO Auto-generated method stub
			return "获取弟子，" + behavior.getValue();
		}
	},

	GET_MAGIC {
		@Override
		public String getDescription(JBehavior behavior) {
			// TODO Auto-generated method stub
			return "获取法宝，" + behavior.getValue();
		}
	},

	GET_EQUIP {
		@Override
		public String getDescription(JBehavior behavior) {
			// TODO Auto-generated method stub
			return "获取装备，" + behavior.getValue();
		}
	},

	GET_HORSE {
		@Override
		public String getDescription(JBehavior behavior) {
			// TODO Auto-generated method stub
			return "获取坐骑，" + behavior.getValue();
		}
	},

	TOP_UP {
		@Override
		public String getDescription(JBehavior behavior) {
			// TODO Auto-generated method stub
			return "充值了" + behavior.getValue() + "元，获得" + behavior.getDollar() + "元宝";
		}
	},

	BUY {
		@Override
		public String getDescription(JBehavior behavior) {
			// TODO Auto-generated method stub
			return "购买了" + behavior.getValue() + "，" + behavior.getMoreInfo() + "个，消费了" + behavior.getDollar() + "元宝";
		}
	},

	LOT {
		@Override
		public String getDescription(JBehavior behavior) {
			// TODO Auto-generated method stub
			return "抽卡" + (behavior.getTypeId() == 0 ? "十里挑一" : behavior.getTypeId() == 1 ? "百里挑一" : "千里挑一") + "，获得了"
					+ behavior.getValue();
		}
	},

	BUY_REVIVE {
		@Override
		public String getDescription(JBehavior behavior) {
			// TODO Auto-generated method stub
			return "购买复活次数，花费" + behavior.getDollar() + "元宝";
		}
	},

	BUY_STAMINA {
		@Override
		public String getDescription(JBehavior behavior) {
			// TODO Auto-generated method stub
			return "购买体力，第" + behavior.getTypeId() + "次，花费" + behavior.getDollar() + "元宝";
		}
	},

	BUY_GOD_STAMINA {
		@Override
		public String getDescription(JBehavior behavior) {
			// TODO Auto-generated method stub
			return "购买神力，第" + behavior.getTypeId() + "次，花费" + behavior.getDollar() + "元宝";
		}
	},

	BUY_QI {
		@Override
		public String getDescription(JBehavior behavior) {
			// TODO Auto-generated method stub
			return "购买元气，第" + behavior.getTypeId() + "次，花费" + behavior.getDollar() + "元宝";
		}
	},

	BUY_LEVEL_TIMES {
		@Override
		public String getDescription(JBehavior behavior) {
			// TODO Auto-generated method stub
			return "购买关卡攻击次数，花费" + behavior.getDollar() + "元宝";
		}
	},

	CLEAR_LEVEL_CD {
		@Override
		public String getDescription(JBehavior behavior) {
			// TODO Auto-generated method stub
			return "清除关卡扫荡CD，花费" + behavior.getDollar() + "元宝";
		}
	},

	EVOLUTION {
		@Override
		public String getDescription(JBehavior behavior) {
			// TODO Auto-generated method stub
			return "化神弟子，" + behavior.getValue();
		}
	},

	COMBINE {
		@Override
		public String getDescription(JBehavior behavior) {
			// TODO Auto-generated method stub
			return "融合弟子，" + behavior.getValue();
		}
	},

	COMBINE_FORCE {
		@Override
		public String getDescription(JBehavior behavior) {
			// TODO Auto-generated method stub
			return "强制融合弟子，" + behavior.getValue() + "，花费" + behavior.getDollar() + "元宝";
		}
	},

	CATCH_HORSE {
		@Override
		public String getDescription(JBehavior behavior) {
			// TODO Auto-generated method stub
			return "捕捉坐骑，" + behavior.getValue() + "，花费" + behavior.getDollar() + "元宝";
		}
	},

	REFRESH_HORSE_GENIUS {
		@Override
		public String getDescription(JBehavior behavior) {
			// TODO Auto-generated method stub
			return behavior.getValue() + "刷新天赋，花费" + behavior.getDollar() + "元宝，获得天赋" + behavior.getMoreInfo();
		}
	},
	//Avenger 20160116  添加日志内容
	FRIST_RECHARGE {
		@Override
		public String getDescription(JBehavior behavior) {
			return "领取首充奖励,获得"+ behavior.getDollar() + "元宝";
		}
	},
	SECOND_RECHARGE {
		@Override
		public String getDescription(JBehavior behavior) {
			// TODO Auto-generated method stub
			return "领取次充奖励,获得"+ behavior.getDollar() + "元宝";
		}
	},
	EVERYDAY_RECHARGE {
		@Override
		public String getDescription(JBehavior behavior) {
			// TODO Auto-generated method stub
			return "领取每日充值奖励,获得"+ behavior.getDollar() + "元宝";
		}
	},
	
    //Avenger 20160116
	;

	public abstract String getDescription(JBehavior behavior);

}
