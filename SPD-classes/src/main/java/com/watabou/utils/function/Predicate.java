package com.watabou.utils.function;

/** takes one argument and returns a boolean **/
public interface Predicate<Argument> {
    boolean test(Argument argument);
}
