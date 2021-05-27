package com.zrp200.rkpd2.effects;

import com.watabou.noosa.Image;
import com.zrp200.rkpd2.ui.Icons;

public class SelectableCell extends Image {
    public SelectableCell(Image sprite) {
        super(Icons.get(Icons.TARGET));
        point( sprite.center(this) );
        sprite.parent.addToFront(this);
    }
}
