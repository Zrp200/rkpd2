package com.watabou.utils.function;

/** implementation of a SAM interface that takes one argument and returns nothing. **/
public interface Consumer<Argument> {
    void accept(Argument argument);
}
