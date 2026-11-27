package com.holub.life;

/**
 * Holds the rule that all cells of ONE Universe follow.
 */
final class RuleSlot {

    private LifeRule rule = LifeRule.CONWAY;

    LifeRule getRule() {
        return rule;
    }

    void setRule(LifeRule rule) {
        this.rule = rule;
    }
}
