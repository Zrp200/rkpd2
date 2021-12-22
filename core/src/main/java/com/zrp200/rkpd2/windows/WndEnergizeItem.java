package com.zrp200.rkpd2.windows;

import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.ShatteredPixelDungeon;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.mobs.npcs.Shopkeeper;
import com.zrp200.rkpd2.items.EnergyCrystal;
import com.zrp200.rkpd2.items.EquipableItem;
import com.zrp200.rkpd2.items.Gold;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.AlchemyScene;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.ItemSprite;
import com.zrp200.rkpd2.sprites.ItemSpriteSheet;
import com.zrp200.rkpd2.ui.RedButton;

public class WndEnergizeItem extends WndInfoItem {

	private static final int BTN_HEIGHT	= 18;

	private WndBag owner;

	public WndEnergizeItem(Item item, WndBag owner) {
		super(item);

		this.owner = owner;

		if (item.quantity() == 1) {
			addToBottom(new RedButton( Messages.get(this, "energize", item.energyVal()) ) {
				@Override
				protected void onClick() {
					energize( item );
					hide();
				}
				{
					setHeight(BTN_HEIGHT);
					icon(new ItemSprite(ItemSpriteSheet.ENERGY));
				}
			});
		} else {

			int energyAll = item.energyVal();
			addToBottom(
					new RedButton( Messages.get(this, "energize_1", energyAll / item.quantity()) ) {
						@Override
						protected void onClick() {
							energizeOne( item );
							hide();
						}
						{
							setHeight(BTN_HEIGHT);
							icon(new ItemSprite(ItemSpriteSheet.ENERGY));
						}
					},
					new RedButton( Messages.get(this, "energize_all", energyAll ) ) {
						@Override
						protected void onClick() {
							energize( item );
							hide();
						}
						{
							setHeight(BTN_HEIGHT);
							icon(new ItemSprite(ItemSpriteSheet.ENERGY));
						}
					}
			);
		}
	}

	@Override
	public void hide() {

		super.hide();

		if (owner != null) {
			owner.hide();
			openItemSelector();
		}
	}

	public static void energize( Item item ) {

		Hero hero = Dungeon.hero;

		if (item.isEquipped( hero ) && !((EquipableItem)item).doUnequip( hero, false )) {
			return;
		}
		item.detachAll( hero.belongings.backpack );

		if (ShatteredPixelDungeon.scene() instanceof AlchemyScene){

			Dungeon.energy += item.energyVal();
			((AlchemyScene) ShatteredPixelDungeon.scene()).createEnergy();

		} else {

			//selling items in the sell interface doesn't spend time
			hero.spend(-hero.cooldown());

			new EnergyCrystal(item.energyVal()).doPickUp(hero);

		}
	}

	public static void energizeOne( Item item ) {

		if (item.quantity() <= 1) {
			energize( item );
		} else {

			Hero hero = Dungeon.hero;

			item = item.detach( hero.belongings.backpack );

			if (ShatteredPixelDungeon.scene() instanceof AlchemyScene){

				Dungeon.energy += item.energyVal();
				((AlchemyScene) ShatteredPixelDungeon.scene()).createEnergy();

			} else {

				//selling items in the sell interface doesn't spend time
				hero.spend(-hero.cooldown());

				new EnergyCrystal(item.energyVal()).doPickUp(hero);
			}
		}
	}

	public static WndBag openItemSelector(){
		if (ShatteredPixelDungeon.scene() instanceof GameScene) {
			return GameScene.selectItem( selector );
		} else {
			WndBag window = WndBag.getBag( selector );
			ShatteredPixelDungeon.scene().addToFront(window);
			return window;
		}
	}

	public static WndBag.ItemSelector selector = new WndBag.ItemSelector() {
		@Override
		public String textPrompt() {
			return Messages.get(WndEnergizeItem.class, "prompt");
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item.energyVal() > 0;
		}

		@Override
		public void onSelect(Item item) {
			if (item != null) {
				WndBag parentWnd = openItemSelector();
				if (ShatteredPixelDungeon.scene() instanceof GameScene) {
					GameScene.show(new WndEnergizeItem(item, parentWnd));
				} else {
					ShatteredPixelDungeon.scene().addToFront(new WndEnergizeItem(item, parentWnd));
				}
			}
		}
	};

}
