package com.zrp200.rkpd2.windows;

import com.watabou.utils.function.Function;
import com.zrp200.rkpd2.actors.hero.HeroClass;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.actors.hero.abilities.ArmorAbility;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.ItemSprite;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.ui.HeroIcon;
import com.zrp200.rkpd2.ui.TalentButton;
import com.zrp200.rkpd2.ui.TalentsPane;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class WndInfoArmorAbility extends WndTitledMessage {

	public WndInfoArmorAbility(ArmorAbility ability, Function<ArmorAbility, LinkedHashMap<Talent, Integer>> initializeArmorTalents){
		super( new HeroIcon(ability), Messages.titleCase(ability.name()), ability.desc());

		LinkedHashMap<Talent, Integer> talents = initializeArmorTalents.apply(ability);
		if(talents.isEmpty()) return;

		TalentsPane.TalentTierPane talentPane = new TalentsPane.TalentTierPane(talents, 4, TalentButton.Mode.INFO);
		talentPane.title.text( Messages.titleCase(Messages.get(WndHeroInfo.class, "talents")));
		addToBottom(talentPane, 5, 0);
	}
	public WndInfoArmorAbility(ArmorAbility ability) {
		this(ability, WndInfoArmorAbility::initializeTalents);
	}

	public static LinkedHashMap<Talent, Integer> initializeTalents(ArmorAbility ability) {
		ArrayList<LinkedHashMap<Talent, Integer>> talentList = Talent.initArmorTalents(ability);
		return talentList.size() < 4 ? new LinkedHashMap<>() : talentList.get(3);
	}
}
