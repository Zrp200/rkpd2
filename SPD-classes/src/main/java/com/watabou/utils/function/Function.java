package com.watabou.utils.function;

/** SAM interface that takes a single argument and results something. **/
public interface Function<Argument, Result> {
    Result apply(Argument argument);
}
