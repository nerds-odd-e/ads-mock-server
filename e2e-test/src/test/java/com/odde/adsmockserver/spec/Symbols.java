package com.odde.adsmockserver.spec;

import com.github.leeonky.jfactory.Spec;
import com.odde.adsmockserver.object.Symbol;

public class Symbols {

    public static class SymbolSpec extends Spec<Symbol> {
        @Override
        protected String getName() {
            return "Symbol";
        }

        @Override
        public void main() {
            property("type").value("INT");
            property("value").value(42);
        }
    }
}
